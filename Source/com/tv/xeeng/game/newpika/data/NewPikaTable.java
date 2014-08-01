/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.newpika.data;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.*;
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
public class NewPikaTable extends SimpleTable {

    public long moneyForAdven = 1000;
    public static final int SINGLE_MODE = 1;
    public static final int ADVENTURE_MODE = 2;
    public static final int MUTIL_MODE = 3;
    private List<NewPikaPlayer> playings = new ArrayList<>();
    private List<NewPikaPlayer> waitings = new ArrayList<>();
    protected int MAX_NUMBER_PLAYER = 4;
    public int mode = 0;
    public NewPikaPlayer winner = null;
    public int typeClientPlay = 0;//
    public int typeMatrix = 1; // 4 level
    public static int maxAdLevel = 15;
    public int dificultLevel = 1;
    public void changeModePlay(int mode, int typeM, long uid)  throws SimpleException {
        if (uid == owner.id && !isPlaying) {
            this.mode = mode;
            typeMatrix = typeM;
        } else {
            throw new SimpleException("wrong mode!");
        }
    }
    
    public void init(boolean isAdventureMode, int matrixSize, int level) {
        mode = (isAdventureMode)?ADVENTURE_MODE:SINGLE_MODE;
        if(!isAdventureMode) {
            dificultLevel = level;
        }
        typeMatrix = matrixSize;
    }
    
    private void nextLevelAdventure() throws SimpleException {
        if (mode == ADVENTURE_MODE) {
            lastActivated = System.currentTimeMillis();
            if (((NewPikaPlayer) owner).levelAdvanture++ == maxAdLevel) {
                isPlaying = false;
                //TODO: make gift for User
            }
        } else {
            throw new SimpleException("wrong mode!");
        }
    }

    public NewPikaTable(NewPikaPlayer owner, long money) {
        this.owner = owner;
        this.playings.add(owner);
        this.firstCashBet = money;
        owner.isGiveUp = false;  //owner doesnt bet
        logdir = "New_Pika";
        mode = SINGLE_MODE;
    }

    @Override
    public boolean isFullTable() {
        return getTableSize() >= maximumPlayer; // Maximum player = 5 player 
    }

    @Override
    public int getTableSize() {
        return playings.size() + waitings.size();
    }

    @Override
    public List<? extends SimplePlayer> getNewPlayings() {
        return playings;
    }

    @Override
    public List<? extends SimplePlayer> getNewWaitings() {
        return waitings;
    }

    @Override
    protected void join(SimplePlayer player) throws BusinessException {

        if (isFullTable()) {
            throw new BusinessException("Full Player");
        }

        player.setLastActivated(System.currentTimeMillis());
        if (isPlaying) {
            waitings.add((NewPikaPlayer) player);
            player.isMonitor = true;
        } else {
            playings.add((NewPikaPlayer) player);
            player.isMonitor = false;

        }

        outCodeSB.append("player: ").append(player.username).append(" join").append(NEW_LINE);
    }

    @Override
    protected void joinResponse(JoinResponse joinResponse) {
        joinResponse.isObserve = isPlaying;
        joinResponse.zoneID = ZoneID.NEW_PIKA;
    }

    public void kickout(long userKickoutid, KickOutRequest rqKickOut) throws BusinessException {
        if (userKickoutid != owner.id) {
            throw new BusinessException(Messages.NOT_OWNER_PERSON);
        }

        NewPikaPlayer player = findPlayer(rqKickOut.uid);
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
        /*
         UserDB db = new UserDB();
         if (db.checkBotUser(rqKickOut.uid)) {
         this.isKickoutBot = true;
         }
         */
    }

    public CancelResponse cancel(long uid) throws BusinessException, ServerException, JSONException {


        NewPikaPlayer player = findPlayer(uid);
        mLog.debug("Cancel uid =" + uid);
        if (player == null) {
            throw new BusinessException(NONE_EXISTS_PLAYER);
        }

        outCodeSB.append("cancel player:").append(player.username).append(NEW_LINE);

        CancelResponse resMatchCancel;
        MessageFactory msgFactory = getNotNullSession().getMessageFactory();
        resMatchCancel = (CancelResponse) msgFactory
                .getResponseMessage(MessagesID.MATCH_CANCEL);
        resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);

        player.isOut = true;

        if (this.onlinePlayers() == 0) {
            //player.currentSession.write(resMatchCancel);
            return resMatchCancel;
        }
        if (this.isPlaying) {
            if (player.isMonitor) {
                remove(player);
                broadcastMsg(resMatchCancel, playings, waitings, player, false);
            } else {
                if (onlinePlayingPlayer() == 1) {
                    endMatch(winner.id, 1);
                }
            }
        } else {
            remove(player);
            if (uid == owner.id) {
                NewPikaPlayer newOwner = this.ownerQuit();

                if (newOwner != null) {
                    resMatchCancel.newOwner = newOwner.id;
                    this.setOwnerId(newOwner.id);
                }
            }

            broadcastMsg(resMatchCancel, playings, waitings, player, false);

        }
        return resMatchCancel;
    }

    @Override
    public void removePlayer(SimplePlayer player) {
        this.remove((NewPikaPlayer) player);
    }

    public void remove(NewPikaPlayer player) {
        try {
            if (player != null) {
                NewPikaPlayer removePlayer;
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
//                if(winner != null && player.id == winner.id) {
//                    winner = null;
//                }
            }
        } catch (Exception e) {
            outCodeSB.append("Remove player !!!error ").append(player.id).append(NEW_LINE);
            mLog.error(e.getMessage() + " remove player: ", e.getStackTrace());

        }
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

    public NewPikaPlayer ownerQuit() {

        for (int i = 0; i < playings.size(); i++) {
            NewPikaPlayer p = playings.get(i);
            if (!p.notEnoughMoney() && !p.isOut) {

                owner = p;
                ownerSession = owner.currentSession;
                return p;
            }
        }

        for (int i = 0; i < waitings.size(); i++) {
            NewPikaPlayer p = waitings.get(i);
            if (!p.notEnoughMoney()) {
                owner = p;
                owner.setLastActivated(System.currentTimeMillis());
                ownerSession = owner.currentSession;
                return p;
            }
        }


        return null;
    }

    @Override
    public NewPikaPlayer findPlayer(long uid) {
        for (int i = 0; i < this.playings.size(); i++) {
            NewPikaPlayer player = this.playings.get(i);
            if (player.id == uid) {
                return player;
            }
        }

        for (int i = 0; i < this.waitings.size(); i++) {
            NewPikaPlayer player = this.waitings.get(i);
            if (player.id == uid) {
                return player;
            }
        }
        return null;
    }

    //Reset auto kickout user
    public void resetAutoKickOut() {
        long timeActivated = System.currentTimeMillis() + SLEEP_BEETWEEN_MATCH_TIMEOUT;
        for (int i = 0; i < this.playings.size(); i++) {
            this.playings.get(i).setLastActivated(timeActivated);

        }
        for (int i = 0; i < this.waitings.size(); i++) {
            this.waitings.get(i).setLastActivated(timeActivated);
        }
        owner.setLastActivated(timeActivated);
    }

    /**
     * *************************************************
     */
    public void resetPlayers() {
//		System.out.println("Reset players now!");

        List<NewPikaPlayer> removedPlayer = new ArrayList<>();

        for (int i = 0; i < this.playings.size(); i++) {
            if (playings.get(i).isOut || playings.get(i).notEnoughMoney()) {
                removedPlayer.add(this.playings.get(i));
            }

        }

        //synchronized (playings) {
        for (int i = 0; i < removedPlayer.size(); i++) {

            playings.remove(removedPlayer.get(i));

        }

        for (int i = 0; i < this.waitings.size(); i++) {
            this.waitings.get(i).isMonitor = false;
        }


        this.playings.addAll(this.waitings);
        this.waitings.clear();
        //}

        resetAutoKickOut();


        for (int i = 0; i < playings.size(); i++) {
            NewPikaPlayer player = playings.get(i);
            player.reset();
        }
        owner.isReady = true;
        this.isPlaying = false;

    }

    public void pikaEnd(long uid) throws SimpleException, ServerException, JSONException {
        if (isPlaying) {
            if (mode == ADVENTURE_MODE) {
                nextLevelAdventure();
            } else {
                endMatch(uid, 1);
            }
        } else {
            throw new SimpleException("Ván chơi đã kết thúc!");
        }
    }

    public void start() {
        isPlaying = true;
        if (mode == SINGLE_MODE && playings.size() > 1) {
            mode = MUTIL_MODE;
        } else if (mode == MUTIL_MODE && playings.size() == 1) {
            mode = SINGLE_MODE;
        } else if (mode == ADVENTURE_MODE) {
            setCurrentTimeOut(60000);
            try {
                UserDB db = new UserDB();
                db.updateUserMoney(moneyForAdven, false, owner.id, "Trừ tiền chơi New-Pika Adventure", 0, 104);
            } catch (Throwable e) {
            }
        }
        typeClientPlay = makeRandomNum(6) + 1;
    }

    private String getEndValue(long newOwnerId) {
        StringBuilder sb = new StringBuilder();
        sb.append(Long.toString(newOwnerId)).append(AIOConstants.SEPERATOR_BYTE_3);
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {

            NewPikaPlayer player = this.playings.get(i);
            long wonMOney = player.getWonMoney();
            if (!player.isWin) {
                wonMOney = -wonMOney;
            }

            sb.append(Long.toString(player.id)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(wonMOney)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(player.cash))/*.append(AIOConstants.SEPERATOR_BYTE_1);
                     sb.append(player.isOut?"1":"0").append(AIOConstants.SEPERATOR_BYTE_1);
                     sb.append(player.notEnoughMoney()?"1":"0")*/.append(AIOConstants.SEPERATOR_BYTE_2);
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();

    }

    public long endMatch(long uid, int number) throws ServerException,
            JSONException {
        this.isPlaying = false;
        long newOwnerId = 0;
        if (this.mode == MUTIL_MODE) {
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
            long wonMoney = 0;
            winner = findPlayer(uid);
            for (int i = 0; i < playings.size(); i++) {
                NewPikaPlayer p = this.playings.get(i);
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
                NewPikaPlayer newOwner = this.ownerQuit();
                if (newOwner != null) {
                    newOwnerId = newOwner.id;
                }
            }
            //JSONObject endJson = getEndJSonObject(newOwnerId);
            EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory
                    .getResponseMessage(MessagesID.MATCH_END);
            // set the result
            endMatchRes.setZoneID(ZoneID.NEW_PIKA);
            endMatchRes.mCode = ResponseCode.SUCCESS;
            endMatchRes.value = getEndValue(newOwnerId);
            //endMatchRes.setSuccess(endJson);
            broadcastMsg(endMatchRes, playings, waitings, owner, true);
        }
        removePlayerOut();
        //this.removeNotEnoughMoney(this.room);
        this.resetPlayers();
        return newOwnerId;
    }

    // DB
    private void removePlayerOut() {
        ArrayList<NewPikaPlayer> res = new ArrayList<>();
        for (NewPikaPlayer p : this.playings) {
            if (p.isOut) {
                res.add(p);
            }
        }
        for (NewPikaPlayer p : res) {
            System.out.println("Remove Out: " + p.username);
            remove(p);
            //removePlayerFromWaitings(p.id);
        }
    }

    public void updateCash(int mult) {
        Connection con = DBPoolConnection.getConnection();
        try {

            String desc = "Pikachu:" + matchID;
            UserDB userDb = new UserDB(con);
            // long ownerWonMoney = 0;

            boolean havingMinusBalance = false;
            // PikachuPlayer winPlayer = getPlayer(winID);
            long moneyWin = winner.getWonMoney();
            for (int i = 0; i < playings.size(); i++) {
                NewPikaPlayer player = this.playings.get(i);
                if (player.id != winner.id) {
                    // System.out.println(player.username +" has :" +
                    // player.isWin);
                    player.cash = userDb.updateUserMoney(player.getWonMoney(),
                            player.isWin, player.id, desc,
                            player.getExperience(), 11111);
                    if (player.cash < 0) {
                        moneyWin += player.cash;
                        player.cash = 0;
                    }
                }
                /*
                 * if (!player.isWin) { havingMinusBalance =
                 * updateUserCash(userDb, winPlayer, player, this.firstCashBet *
                 * mult, desc); } int temp = player.hint + player.revert; if
                 * (temp > 0) { havingMinusBalance = updateUserCash(userDb,
                 * player, false, (this.firstCashBet * temp) / 10, desc); }
                 */
            }
            // Save for winner
            winner.setWonMoney((int) (moneyWin * REAL_GOT_MONEY));
            winner.cash = userDb.updateUserMoney(winner.getWonMoney(), true,
                    winner.id, desc, winner.getExperience(), 11111);

            if (havingMinusBalance) {
                userDb.notMinus();
            }
        } catch (DBException | SQLException ex) {
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

    @Override
    protected List<SimplePlayer> removeNotEnoughMoney() {
        List<SimplePlayer> removedPlayers = new ArrayList<>();
        for (int i = 0; i < playings.size(); i++) {
            playings.get(i).moneyForBet = this.firstCashBet;
            if (playings.get(i).notEnoughMoney()) {
                removedPlayers.add(playings.get(i));
            }
        }

        boolean isChangeOwner = false;
        for (int i = 0; i < removedPlayers.size(); i++) {
            SimplePlayer p = removedPlayers.get(i);
            if (p.id == owner.id) {
                isChangeOwner = true;
            }
            playings.remove(i);
        }

        if (isChangeOwner) {
            if (playings.size() > 0) {
                owner = playings.get(0);

                resetAutoKickOut();
            }
        }
        this.setOwnerId(owner.id);

        return removedPlayers;
    }

    @Override
    public void kickTimeout(Room room) {
        try {

            if (!this.isPlaying) {
                if (this.playings.size() > 1) {
                    long now = System.currentTimeMillis();
                    //check user which does nothing when he comes to table
                    boolean isAllJoinReady = true;

                    //Room room = bacayZone.findRoom(matchId);

                    for (int i = 0; i < this.playings.size(); i++) {

                        NewPikaPlayer bcPlayer = this.playings.get(i);

                        if (!bcPlayer.isReady && bcPlayer.id != owner.id) {
                            isAllJoinReady = false;
                            //does this user over time out
                            if (now - bcPlayer.getLastActivated() > AUTO_KICKOUT_TIMEOUT) {
                                // kich him
                                kickTimeout(room, bcPlayer, 0);
                                this.remove(bcPlayer);
                                bcPlayer.isOut = true;
                                String kickOutMessage = "Auto kick out " + bcPlayer.username;
                                mLog.debug(kickOutMessage);
                                outCodeSB.append(kickOutMessage).append(NEW_LINE);
                            }

                        }
                    }


                    if (isAllJoinReady) {
                        //start game
                        if (now - owner.getLastActivated() > AUTO_KICKOUT_OWNER_TIMEOUT) {
                            SimplePlayer oldOwner = owner.clone();

                            NewPikaPlayer currOwner = findPlayer(owner.id);
                            currOwner.isOut = true;
                            owner = this.ownerQuit();
                            kickTimeout(room, oldOwner, owner.id);
                            //autoStartGame(owner);


                            this.resetPlayers();
                        }

                    }


                }
            }
        } catch (Exception ex) {
            try {

                mLog.error("bacay Kick time out error matchId " + matchID, ex);
                outCodeSB.append("Kick out error").append(NEW_LINE);
                cancel(playings);
                room.allLeft();
                this.destroy();
            } catch (Exception exx) {
            }
        }
    }

//    @Override
//    protected void sendReadyMessage(SimplePlayer player, IResponseMessage msg)
//    {
//        broadcastMsg(msg, playings, waitings, player, true);
//    }
    @Override
    public ISession getNotNullSession() {
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            NewPikaPlayer player = this.playings.get(i);
            if (!player.isOut && player.currentSession != null && player.currentSession.getMessageFactory() != null) {
                return player.currentSession;
            }
        }

        return null;
    }

    public SimplePlayer getNotNullSessionPlayer() {
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            NewPikaPlayer player = this.playings.get(i);
            if (!player.isOut && player.currentSession != null && player.currentSession.getMessageFactory() != null) {
                return player;
            }
        }

        return null;
    }

    private int makeRandomNum(int n) {
        return (int) Math.round(Math.random() * n);
    }

    @Override
    public void doTimeout() {
        try {
            if (isPlaying && mode == ADVENTURE_MODE) {
                isPlaying = false;
                //TODO: send feedback for user
            }
        } catch (Exception ex) {
            try {
                this.isPlaying = false;
                mLog.error(ex.getMessage(), ex);
            } catch (Exception exx) {
            }
        }
    }
}
