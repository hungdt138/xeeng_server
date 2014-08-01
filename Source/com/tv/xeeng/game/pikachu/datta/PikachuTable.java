package com.tv.xeeng.game.pikachu.datta;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelResponse;
import com.tv.xeeng.base.protocol.messages.EndMatchResponse;
import com.tv.xeeng.base.protocol.messages.KickOutRequest;
import com.tv.xeeng.base.protocol.messages.OutResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.data.*;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.protocol.MessageFactory;
import org.json.JSONException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tuanda
 */
public class PikachuTable extends SimpleTable {

    public int pikaLevel;
//	public Room room;
    public long winID;
    public ArrayList<PikachuPlayer> playings = new ArrayList<PikachuPlayer>();
    public ArrayList<PikachuPlayer> waitings = new ArrayList<PikachuPlayer>();
    protected int MAX_NUMBER_PLAYER = 4;
    private boolean isSingleMode = false;

    public PikachuTable(PikachuPlayer owner, long money, long matchId, Room room) {
        this.matchID = matchId;
        this.owner = owner;
        this.playings.add(owner);
        //this.waitings.add(owner);
        this.firstCashBet = money;
        owner.isGiveUp = false; // owner doesnt bet
        this.setRoom(room);
        this.winID = owner.id;
        logdir = "Pikachu";
    }

    public void changeMode(long uid) throws BusinessException, JSONException, ServerException {
        if (uid == owner.id) {
            endMatch(0, 0);
        } else {
            throw new BusinessException("Bạn không phải chủ phòng rồi!");
        }
    }

    public void join(SimplePlayer player) throws BusinessException {
        if (isFullTable()) {
            throw new BusinessException("Full Player");
        }

        player.setLastActivated(System.currentTimeMillis());
        if (isPlaying) {
            waitings.add((PikachuPlayer) player);
            player.isMonitor = true;
        } else {
            playings.add((PikachuPlayer) player);
            player.isMonitor = false;
        }
        outCodeSB.append("player: ").append(player.username).append(" join").append(NEW_LINE);
    }

    @Override
    public boolean isFullTable() {
        return getTableSize() >= MAX_NUMBER_PLAYER;
    }

    private boolean isAllStop() {
        for (PikachuPlayer p : this.playings) {
            if (!p.isStop) {
                return false;
            }
        }
        return false;
    }

    /*public PikachuPlayer getPlayer(long uid) {
     for (PikachuPlayer p : this.waitings) {
     if (p.id == uid) {
     return p;
     }
     }
     return null;
     }*/
    @Override
    public List<? extends SimplePlayer> getNewPlayings() {
        return playings;
    }

    @Override
    public List<? extends SimplePlayer> getNewWaitings() {
        return waitings;
    }

    /*public void broadCast(IResponseMessage obj) throws ServerException {
     for (PikachuPlayer p : this.waitings) {
     if (p.currentSession != null) {
     obj.setSession(p.currentSession);
     p.currentSession.write(obj);
     }
     }
     }

     public void broaadCast(IResponseMessage obj, long exceptID)
     throws ServerException {
     for (PikachuPlayer p : this.waitings) {
     if (p.id != exceptID) {
     if (p.currentSession != null) {
     obj.setSession(p.currentSession);
     p.currentSession.write(obj);
     }
     }
     }
     }*/
    public long allOutExceptOne() {
        int i = 0;
        long res = 0;
        if (isSingleMode) {
            return 0;
        }
        for (PikachuPlayer p : this.playings) {
            if (!p.isOut) {
                i++;
                res = p.id;
            }
        }
        if (i == 1) {
            return res;
        } else {
            return 0;
        }
    }

    private boolean isInPlaying(long uid) {
        for (int i = 0; i < playings.size(); i++) {
            if (playings.get(i).id == uid) {
                return true;
            }
        }
        return false;
    }

    public CancelResponse cancel(long uid) throws Exception, ServerException, JSONException {
        PikachuPlayer p = findPlayer(uid);

        if (p == null) {
            throw new Exception(NONE_EXISTS_PLAYER);
        }

        outCodeSB.append("cancel player:").append(p.username).append(NEW_LINE);

        CancelResponse resMatchCancel;
        MessageFactory msgFactory = p.currentSession.getMessageFactory();
        resMatchCancel = (CancelResponse) msgFactory.getResponseMessage(MessagesID.MATCH_CANCEL);
        resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);

        p.isOut = true;

        if (this.onlinePlayers() == 0) {
            return resMatchCancel;
        }

        long newOwnerId = 0;

        if (this.isPlaying) {
            if (!isInPlaying(uid)) {
                removePlayerFromWaitings(uid);
                broadcastMsg(resMatchCancel, playings, waitings, p, false);
                return resMatchCancel;
            }
            p.isWin = false;
            p.isStop = true;

            long winID = allOutExceptOne();
            if (winID > 0) {
                this.endMatch(winID, 0);
                /*
                 * if (playings.get(0).id == uid) {
                 * this.endMatch(playings.get(1).id, 0); } else {
                 * this.endMatch(playings.get(0).id, 0); }
                 */
                removePlayer(uid);
            }
            // removePlayerFromPlayings(uid);

            if (uid == this.owner.id) {
                PikachuPlayer newOwner = this.ownerQuit();
                if (newOwner != null) {
                    this.setOwnerId(newOwner.id);
                    newOwnerId = newOwner.id;
                }
            } else {
                newOwnerId = this.owner.id;
            }

        } else {
            if (uid == owner.id) {
                PikachuPlayer newOwner = this.ownerQuit();
                if (newOwner != null) {
                    newOwnerId = newOwner.id;
                    this.setOwnerId(newOwner.id);
                } else {
                    return resMatchCancel;
                }

            }

            removePlayer(uid);
            broadcastMsg(resMatchCancel, playings, waitings, p, false);
            mLog.debug("waiting size: " + waitings.size());
        }

        resMatchCancel.newOwner = newOwnerId;
        return resMatchCancel;
    }

    public void remove(PikachuPlayer player) {
        try {
            if (player != null) {
                PikachuPlayer removePlayer;
                outCodeSB.append("Remove player ").append(player.id).append(NEW_LINE);
                int playingSize = this.playings.size();
                for (int i = 0; i < playingSize; i++) {
                    removePlayer = this.playings.get(i);
                    if (removePlayer.id == player.id) {
                        this.playings.remove(removePlayer);
                        break;
                    }
                }

                int waitingSize = this.waitings.size();

                for (int i = 0; i < waitingSize; i++) {
                    removePlayer = this.waitings.get(i);
                    if (removePlayer.id == player.id) {
                        this.waitings.remove(removePlayer);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            outCodeSB.append("Remove player !!!error ").append(player.id).append(NEW_LINE);
            mLog.error(e.getMessage() + " remove player: ", e.getStackTrace());

        }
    }

    @Override
    public void removePlayer(SimplePlayer player) {
        this.remove((PikachuPlayer) player);
    }

    @Override
    public PikachuPlayer findPlayer(long uid) {
        for (int i = 0; i < this.playings.size(); i++) {
            PikachuPlayer player = this.playings.get(i);
            if (player.id == uid) {
                return player;
            }
        }

        for (int i = 0; i < this.waitings.size(); i++) {
            PikachuPlayer player = this.waitings.get(i);
            if (player.id == uid) {
                return player;
            }
        }
        return null;
    }

    public void removePlayerFromWaitings(long uid) {
        for (int i = 0; i < this.waitings.size(); i++) {
            PikachuPlayer p = this.waitings.get(i);
            if (p.id == uid) {
                this.waitings.remove(p);
                return;
            }
        }
    }

    public void removePlayerFromPlayings(long uid) {
        for (int i = 0; i < this.playings.size(); i++) {
            PikachuPlayer p = this.playings.get(i);
            if (p.id == uid) {
                this.playings.remove(p);
                return;
            }
        }
    }

    public void removePlayer(long uid) {
        for (int i = 0; i < this.playings.size(); i++) {
            PikachuPlayer p = this.playings.get(i);
            if (p.id == uid) {
                this.playings.remove(p);
                return;
            }
        }

        for (int i = 0; i < this.waitings.size(); i++) {
            PikachuPlayer p = this.waitings.get(i);
            if (p.id == uid) {
                this.waitings.remove(p);
                return;
            }
        }
    }

    public void start() {
        this.isPlaying = true;

        if (playings.size() == 1) {
            isSingleMode = true;
        } else {
            isSingleMode = false;
        }
        pikaLevel = makeRandomNum(10) + 1;

    }

    private PikachuPlayer winner;

    private String getEndValue(long newOwnerId) {
        StringBuilder sb = new StringBuilder();
        sb.append(Long.toString(newOwnerId)).append(AIOConstants.SEPERATOR_BYTE_3);

        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            PikachuPlayer player = this.playings.get(i);
            long wonMOney = player.getWonMoney();
            if (!player.isWin) {
                wonMOney = -wonMOney;
            }

            sb.append(Long.toString(player.id)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(wonMOney)).append(AIOConstants.SEPERATOR_BYTE_1);
            //sb.append(Long.toString(player.cash)).append(AIOConstants.SEPERATOR_BYTE_2);
            sb.append(Long.toString(player.cash)).append(AIOConstants.SEPERATOR_BYTE_1);
//            sb.append((player.currentSession.isExpiredNew() || player.isOut) ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.isOut ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.notEnoughMoney() ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_2);
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();

    }

    public long endMatch(long uid, int number) throws ServerException,
            JSONException {
        this.isPlaying = false;
        long newOwnerId = 0;

        if (isSingleMode) {
            MessageFactory msgFactory = owner.currentSession.getMessageFactory();
            EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory
                    .getResponseMessage(MessagesID.MATCH_END);
            // set the result
            endMatchRes.setZoneID(ZoneID.PIKACHU);
            endMatchRes.value = getEndValue(0);
            endMatchRes.mCode = ResponseCode.SUCCESS;
            broadcastMsg(endMatchRes, playings,
                    waitings, owner, true);

        } else {
            this.winID = uid;
            long wonMoney = 0;
            winner = findPlayer(uid);

            for (int i = 0; i < playings.size(); i++) {
                PikachuPlayer p = this.playings.get(i);
                if (p.id == uid) {
                    p.isWin = true;
                } else {
                    if (p.cash < this.firstCashBet) {
                        p.setWonMoney(p.cash);
                        wonMoney += p.cash;
                    } else {
                        wonMoney += firstCashBet;
                        p.setWonMoney(firstCashBet);
                    }
                    p.isWin = false;
                }
            }
            if (winner != null) {
                if (winner.cash < wonMoney) {
                    winner.setWonMoney(winner.cash);
                } else {
                    winner.setWonMoney(wonMoney);
                }
            }

            int mul = 1;
            if (number == 10) {
                mul = 2;
            } else if (number == 11) {
                mul = 3;
            } else if (number == 12) {
                mul = 4;
            } else if (number == 13) {
                mul = 10;
            }

            updateCash(mul);
            // send end message to client
            MessageFactory msgFactory = owner.currentSession.getMessageFactory();
            if (owner.isOut) {
                PikachuPlayer newOwner = this.ownerQuit();
                if (newOwner != null) {
                    newOwnerId = newOwner.id;
                }
            }
            //JSONObject endJson = getEndJSonObject(newOwnerId);
            EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory
                    .getResponseMessage(MessagesID.MATCH_END);
            // set the result
            endMatchRes.setZoneID(ZoneID.PIKACHU);
            endMatchRes.mCode = ResponseCode.SUCCESS;
            endMatchRes.value = getEndValue(newOwnerId);
            //endMatchRes.setSuccess(endJson);
            broadcastMsg(endMatchRes, playings, waitings, owner, true);
        }

        removePlayerOut();
        this.removeNotEnoughMoney(this.getRoom());
        this.resetPlayers();
        return newOwnerId;
    }

    private void removePlayerOut() {
        ArrayList<PikachuPlayer> res = new ArrayList<PikachuPlayer>();
        for (PikachuPlayer p : this.playings) {
            if (p.isOut) {
                res.add(p);
            }
        }
        for (PikachuPlayer p : res) {
            System.out.println("Remove Out: " + p.username);
            remove(p);
        }
    }

    public void updateCash(int mult) throws ServerException {
        Connection con = DBPoolConnection.getConnection();
        try {

            String desc = "Pikachu:" + matchID;
            UserDB userDb = new UserDB(con);

            boolean havingMinusBalance = false;
            long moneyWin = winner.getWonMoney();
            for (int i = 0; i < playings.size(); i++) {
                PikachuPlayer player = this.playings.get(i);
                if (player.id != this.winID) {
                    player.cash = userDb.updateUserMoney(player.getWonMoney(),
                            player.isWin, player.id, desc,
                            player.getExperience(), 11111);
                    if (player.cash < 0) {
                        moneyWin += player.cash;
                        player.cash = 0;
                    }

                    // Check for in-game event for loser
                    player.checkEvent(false);
                }
            }
            
            // Check for in-game event for winner
            winner.checkEvent(true);
            
            // Save for winner
            winner.setWonMoney((int) (moneyWin * REAL_GOT_MONEY));
            winner.cash = userDb.updateUserMoney(winner.getWonMoney(), true,
                    winner.id, desc, winner.getExperience(), 11111);

            if (havingMinusBalance) {
                userDb.notMinus();
            }
        } catch (DBException ex) {
            outCodeSB.append(ex.getMessage()).append(ex.getStackTrace())
                    .append(NEW_LINE);
        } catch (SQLException ex) {
            outCodeSB.append(ex.getMessage()).append(ex.getStackTrace())
                    .append(NEW_LINE);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                outCodeSB.append(ex.getStackTrace()).append(NEW_LINE);
            }
        }
        saveLogToFile();
    }

    public boolean containPlayer(long id) {
        for (PikachuPlayer p : this.playings) {
            if (p.id == id) {
                return true;
            }
        }
        for (PikachuPlayer p : this.waitings) {
            if (p.id == id) {
                return true;
            }
        }

        return false;
    }

    public void resetPlayers() {
        List<PikachuPlayer> removedPlayer = new ArrayList<>();

        for (int i = 0; i < this.playings.size(); i++) {
//            if (playings.get(i).isOut || playings.get(i).notEnoughMoney() || playings.get(i).currentSession.isExpiredNew()) {
            if (playings.get(i).isOut || playings.get(i).notEnoughMoney()) {
                removedPlayer.add(this.playings.get(i));
            }
        }

        for (int i = 0; i < removedPlayer.size(); i++) {
            playings.remove(removedPlayer.get(i));
        }

        for (int i = 0; i < this.waitings.size(); i++) {
            this.waitings.get(i).isMonitor = false;
        }

        this.playings.addAll(this.waitings);
        this.waitings.clear();

//        resetAutoKickOut();
        for (int i = 0; i < playings.size(); i++) {
            PikachuPlayer player = playings.get(i);
            player.reset();
        }
        owner.isReady = true;
        this.isPlaying = false;
    }

    public PikachuPlayer ownerQuit() {

        for (int i = 0; i < playings.size(); i++) {
            PikachuPlayer p = playings.get(i);
            if (!p.notEnoughMoney() && !p.isOut) {

                owner = p;
                ownerSession = owner.currentSession;
                return p;
            }
        }

        for (int i = 0; i < waitings.size(); i++) {
            PikachuPlayer p = waitings.get(i);
            if (!p.notEnoughMoney()) {
                owner = p;
                owner.setLastActivated(System.currentTimeMillis());
                ownerSession = owner.currentSession;
                return p;
            }
        }

        return null;

    }

    public void stop(long uid) throws ServerException, JSONException {
        findPlayer(uid).isStop = true;
        int point = -1;
        if (isAllStop()) {
            winID = playings.get(0).id;
            for (PikachuPlayer p : playings) {
                if (p.point >= point) {
                    winID = p.id;
                }
            }
            this.endMatch(winID, 0);
            // if(isOut)
            // removePlayer(uid);
        }

    }

    private int makeRandomNum(int n) {
        return (int) Math.round(Math.random() * n);
    }

    @Override
    public int getTableSize() {
        return this.waitings.size() + playings.size();
    }

    @Override
    protected List<SimplePlayer> removeNotEnoughMoney() {
        List<SimplePlayer> removedPlayers = new ArrayList<SimplePlayer>();
        boolean isChangeOwner = false;
        for (PikachuPlayer p : this.playings) {
            p.moneyForBet = this.firstCashBet;
            if (p != null && p.notEnoughMoney()) {
                if (p.id == this.owner.id) {
                    isChangeOwner = true;
                }
                removedPlayers.add(p);
            }
        }

        if (isChangeOwner) {
            PikachuPlayer player = ownerQuit();
            if (player != null) {
                this.owner = player;
                ownerSession = owner.currentSession;
            } else {
                return removedPlayers;
            }
        }
        this.setOwnerId(owner.id);

        return removedPlayers;
    }

    public void kickout(long userKickoutid, KickOutRequest rqKickOut) throws BusinessException {
        if (userKickoutid != owner.id) {
            throw new BusinessException(Messages.NOT_OWNER_PERSON);
        }

        PikachuPlayer player = findPlayer(rqKickOut.uid);
        if (player == null) {
            throw new BusinessException(Messages.PLAYER_OUT);
        }

        if (isPlaying) {
            throw new BusinessException(Messages.PLAYING_TABLE);
        }

        player.currentSession.setLastFP(System.currentTimeMillis() - 20000); //for fast play

        MessageFactory msgFactory = getNotNullSession().getMessageFactory();
        OutResponse broadcastMsg = (OutResponse) msgFactory.getResponseMessage(MessagesID.OUT);
        broadcastMsg.setSuccess(ResponseCode.SUCCESS,
                rqKickOut.uid,
                player.username
                + " bị chủ bàn đá ra ngoài", player.username, 0);

        broadcastMsg(broadcastMsg, playings, waitings, player, true);

        remove(player);
        Room room = player.currentSession.leftRoom(matchID);

        if (room != null) {
            room.left(player.currentSession);
        }

        player.currentSession.setRoom(null);
    }

    public int onlinePlayers() {
        int size = this.waitings.size();
        long uid = 0;
        for (int i = 0; i < this.playings.size(); i++) {
            if (!playings.get(i).isOut) {
                uid = playings.get(i).id;
                size++;
            }
        }
        if (size == 1) {
            winner = findPlayer(uid);
        }
        return size;
    }

    public int onlinePlayingPlayer() {
        int size = 0;
        long uid = 0;
        for (int i = 0; i < this.playings.size(); i++) {
            if (!playings.get(i).isOut) {
                uid = playings.get(i).id;
                size++;
            }
        }
        if (size == 1) {
            winner = findPlayer(uid);
        }
        return size;
    }

    @Override
    public ISession getNotNullSession() {
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            PikachuPlayer player = this.playings.get(i);
            if (!player.isOut && player.currentSession != null && player.currentSession.getMessageFactory() != null) {
                return player.currentSession;
            }
        }
        return null;
    }

}
