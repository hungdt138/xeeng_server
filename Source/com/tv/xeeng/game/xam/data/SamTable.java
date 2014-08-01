/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.xam.data;

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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tuanda
 */
public class SamTable extends SimpleTable {

    private ArrayList<SamPlayer> playings = new ArrayList<>();
    private ArrayList<SamPlayer> waitings = new ArrayList<>();
    private SamPlayer currPlayer;
    public boolean isNewRound;
    private long samBaoID;
    public ArrayList<SamPlayer> samBaoList = new ArrayList<>();

    public boolean hasBaoSam() {
//        return samBaoList.size() > 0;
        return samBaoID > 0;
    }
    /*
     * 1: Sam thanh cong
     * 2: Sam thanh cong dac biet
     */
    private byte SAM_THANH_CONG = 1;
    private byte SAM_THANH_CONG_DAC_BIET = 2;
    private byte SAM_THAT_BAI = 3;
    private byte endMatchState = 0;//binh thuong
    //private ArrayList<Poker> currCards;

    private byte GAME_STATE_BEGIN = 1;
    private byte GAME_STATE_BAT_CAI = 2;
    private byte GAME_STATE_BAO_SAM = 3;
    private byte GAME_STATE_PLAY_CARDS = 4;
    private byte GAME_STATE_END = 5;
    private byte gameState = 0;

    public SamTable(SamPlayer owner, long money, long matchId) {
        this.matchID = matchId;
        this.owner = owner;
        this.playings.add(owner);
        this.firstCashBet = money;
        owner.isGiveUp = false;  //owner doesnt bet
        logdir = "Sam";
    }

    public SamTable() {
    }

    @Override
    public boolean isFullTable() {
        return getTableSize() >= maximumPlayer; // Maximum player = 5 players
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

    public ArrayList<SamPlayer> getPlayings() {
        return playings;
    }

    public ArrayList<SamPlayer> getWaitings() {
        return waitings;
    }

    public boolean isAllReady() {
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            SamPlayer t = this.playings.get(i);
//            mLog.error("---THANGTD START DEBUG---" + t.username + " is ready: " + t.isReady);
            if (t.id != this.owner.id && !t.isReady) {
                return false;
            }
        }

        return true;
    }

    public boolean containPlayer(long id) {
        for (int i = 0; i < playings.size(); i++) {
            if (playings.get(i).id == id) {
                return true;
            }
        }
        for (int i = 0; i < waitings.size(); i++) {
            if (waitings.get(i).id == id) {
                return true;
            }
        }
        return false;
    }

    public void join(SamPlayer player) throws BusinessException {
        if (isFullTable()) {
            throw new BusinessException("Full Player");
        }
//        player.setLastActivated(System.currentTimeMillis());
        if (isPlaying) {
            waitings.add(player);
            player.isObserve = true;
//            player.isMonitor = true;
        } else {
            playings.add(player);
            player.isObserve = false;
//            player.isMonitor = false;
        }

        mLog.debug("---THANGTD JOIN DEBUG SAM---" + player.username + " is ready: " + player.isReady);
        outCodeSB.append("player: ").append(player.username).append(" join").append(NEW_LINE);
    }

    @Override
    protected void joinResponse(JoinResponse joinResponse) {
        //if(isPlaying) {
        joinResponse.isObserve = isPlaying;
        //}
        joinResponse.zoneID = ZoneID.SAM;
        if (isPlaying) {
            joinResponse.turn = currPlayer.id;
            joinResponse.cards = cardsToString(lastCard);
        }
    }

    private String cardsToString(ArrayList<Poker> cards) {
        StringBuilder sb = new StringBuilder();
        for (Poker p : cards) {
            sb.append(p.toInt()).append("#");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public void kickout(long userKickoutid, KickOutRequest rqKickOut) throws BusinessException {
        if (userKickoutid != owner.id) {
            throw new BusinessException(Messages.NOT_OWNER_PERSON);
        }

        SamPlayer player = findPlayer(rqKickOut.uid);
        if (player == null) {
            throw new BusinessException(Messages.PLAYER_OUT);
        }

        if (this.isPlaying) {
            throw new BusinessException(Messages.PLAYING_TABLE);
        }

        player.currentSession.setLastFP(System.currentTimeMillis() - 20000); //for fast play

        MessageFactory msgFactory = getNotNullSession().getMessageFactory();
        OutResponse broadcastMsg = (OutResponse) msgFactory.getResponseMessage(MessagesID.OUT);
        broadcastMsg.setSuccess(ResponseCode.SUCCESS, rqKickOut.uid, player.username + " bị chủ bàn đá ra ngoài", player.username, 0);

        broadcastMsg(broadcastMsg, playings, waitings, player, true);

        this.remove(player);
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
        SamPlayer player = findPlayer(uid);
        if (player == null) {
            throw new BusinessException(NONE_EXISTS_PLAYER);
        }

        outCodeSB.append("cancel player:").append(player.username).append(NEW_LINE);

        CancelResponse resMatchCancel;
        MessageFactory msgFactory = getNotNullSession().getMessageFactory();
        resMatchCancel = (CancelResponse) msgFactory.getResponseMessage(MessagesID.MATCH_CANCEL);
        resMatchCancel.setZone(ZoneID.SAM);
        resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);

        player.isOut = true;

        if (this.onlinePlayers() == 0) {
            //player.currentSession.write(resMatchCancel);
            return resMatchCancel;
        }
        if (this.isPlaying) {
            if (player.isMonitor) {
                this.remove(player);
                broadcastMsg(resMatchCancel, playings, waitings, player, false);
            } else {
                if (onlinePlayingPlayer() == 1) {
                    lastCard.clear();
                    if (samBaoID > 0 && samBaoID != winner.id) {
                        denLang(winner.id);
                    } else {
                        endMatch(winner.id);
                    }
                } else if (player.id == currPlayer.id) {
                    if (isSentBaoSam) {
                        doTimeout();
                    }
                }
            }
        } else {
            this.remove(player);
            if (uid == owner.id) {
                SamPlayer newOwner = this.ownerQuit();

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
        this.remove((SamPlayer) player);
    }

    public void remove(SamPlayer player) {
        try {
            if (player != null) {
                SamPlayer removePlayer;
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

     public SamPlayer ownerQuit() {
        for (int i = 0; i < playings.size(); i++) {
            SamPlayer p = playings.get(i);
            if (!p.notEnoughMoney() && !p.isOut) {
                owner = p;
                ownerSession = owner.currentSession;
                return p;
            }
        }

        for (int i = 0; i < waitings.size(); i++) {
            SamPlayer p = waitings.get(i);
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
    public SamPlayer findPlayer(long uid) {
        for (int i = 0; i < this.playings.size(); i++) {
            SamPlayer player = this.playings.get(i);
            if (player.id == uid) {
                return player;
            }
        }

        for (int i = 0; i < this.waitings.size(); i++) {
            SamPlayer player = this.waitings.get(i);
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
        List<SamPlayer> removedPlayer = new ArrayList<>();

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
            SamPlayer player = playings.get(i);
            player.reset();
        }

        newRound();
        //isNewRound = true;
        samBaoID = 0;
        owner.isReady = true;
        this.isPlaying = false;
    }

    public SamPlayer winner = null;

    public void start() throws ServerException, JSONException, BusinessException {
        gameState = GAME_STATE_BEGIN;
//        setCurrentTimeOut(20000);
        resetPlayers();
        //superUsers.clear();

        int playingSize = playings.size();
        for (int i = 0; i < playingSize; i++) {
            SamPlayer p = playings.get(i);
            if (com.tv.xeeng.game.data.Utils.isSuperUser(p.id)) {
                p.isSuper = true;
            } else {
                p.isSuper = false;
            }
        }
        //chiabaiFlash();
        if (playings.size() < 2) {
            throw new BusinessException("Chưa có người chơi cùng!");
        }

        isPlaying = true;
        isSentBaoSam = false;
        isNewRound = true;
        lastActivated = System.currentTimeMillis();
        uidNotPlayGreatestCard = 0;
        has1Card = false;
        samBaoList.clear();
        perfectType = 0;

        if (winner == null) {
//            winner = (SamPlayer) owner;
            batCai();
            return;
        } else {
            SamPlayer winnerPlayer = findPlayer(winner.id);
            if (winnerPlayer == null) {
//                winner = (SamPlayer) owner;
                batCai();
                return;
            } else {
                winner = winnerPlayer;
            }
        }

        letsStart();
    }

    public void letsStart() throws ServerException, JSONException, BusinessException {
        gameState = GAME_STATE_BAO_SAM;

        setCurrentTimeOut(20000);
        lastActivated = System.currentTimeMillis();

        currPlayer = winner;

        findCurrIndex();
        chia();
        //chiaFix();
        sendStart();
        //lastCard = new ArrayList<>();
    }

    public void batCai() {
        gameState = GAME_STATE_BAT_CAI;

        setCurrentTimeOut(10000);
        lastActivated = System.currentTimeMillis();

        ArrayList<Integer> caiList = getRandomCaiList();

        if ((this.playings.size() <= 5) && (this.playings.size() > 1)) {
            for (int i = 0; i < playings.size(); i++) {
                SamPlayer p = this.playings.get(i);
                p.setCaiCard(new Poker(caiList.get(i)));
                outCodeSB.append(p.username).append(" bat cai: ").append(p.caiCard.toString()).append(NEW_LINE);
                mLog.debug("---THANGTD DEBUG SAM---BAT CAI: " + p.username + " " + p.caiCard.toString());
            }

            winner = this.playings.get(0);
            for (int i = 0; i < playings.size(); i++) {
                SamPlayer player = this.playings.get(i);
                if (player.caiCard.isGreater(winner.caiCard)) {
                    winner = player;
                }
            }

            winner.isCai = true;

            mLog.debug("---THANGTD DEBUG SAM---BAT CAI WINNER: " + winner.username);

            MessageFactory msgFactory = getNotNullSession().getMessageFactory();
            BatCaiResponse batCaiRespone = (BatCaiResponse) msgFactory.getResponseMessage(MessagesID.BAT_CAI);
            batCaiRespone.setSuccess(ResponseCode.SUCCESS, playings);

            broadcastMsg(batCaiRespone, playings, waitings, this.owner, true);
        } else {
            mLog.debug("Sai ne!");
        }

//        ArrayList<Poker> caiCards = new ArrayList<Poker>();
//        for (int i = 0; i < 5; i++) {
//            Poker caiCard = new Poker(caiList.get(i));
//            caiCards.add(caiCard);
//            mLog.debug("---THANGTD DEBUG SAM---BAT CAI: player #" + i + " " + caiCard.toString());
//        }
//
//        int winnerIdx = 0;
//        Poker highestPoker = caiCards.get(0);
//
//        for (int i = 0; i < caiCards.size(); i++) {
//            Poker poker1 = caiCards.get(i);
//
//            if (poker1.isGreater(highestPoker)) {
//                winnerIdx = i;
//                highestPoker = poker1;
//            }
//        }
//
//        mLog.debug("---THANGTD DEBUG SAM---BAT CAI WINNER: player #" + winnerIdx);
    }

    public boolean baoSam(long uid) throws BusinessException, DBException, JSONException, SQLException, ServerException {
//        if (samBaoID > 0) {
//            throw new BusinessException("Đã có người chơi khác báo sâm trước bạn rồi!");
//        }
//
//        samBaoID = uid;
//        SamPlayer p = findPlayer(uid);
//        perfectType = p.samDacBiet();
//        isSentBaoSam = true;
//        if (perfectType > 0) {
//            endMatchState = SAM_THANH_CONG_DACBIET;
//            endMatch(uid);
//            return true;
//        } else {
//            currPlayer = p;
//            findCurrIndex();
//            //currIndex = 0;
//            currentTimeOut = 30000;
//            lastActivated = System.currentTimeMillis();
//            return false;
//        }

        SamPlayer player = findPlayer(uid);

        mLog.debug("---THANGTD DEBUG SAM---BAO SAM PLAYER " + player.username + " TRY TO BAO SAM");

        if (gameState != GAME_STATE_BAO_SAM) {
            throw new BusinessException("Không phải lượt báo Sâm!");
        }

        for (SamPlayer p : samBaoList) {
            if (p.id == player.id) {
                throw new BusinessException("Bạn đã báo Sâm rồi!");
            }
        }

        if (samBaoList.add(player)) {
            mLog.debug("---THANGTD DEBUG SAM---BAO SAM: " + player.username);
        } else {
            mLog.debug("---THANGTD DEBUG SAM---BAO SAM ERROR: " + player.username);
            return false;
        }

        return true;
    }

    private void giveUp(long uid) throws BusinessException {
        if (uid == currPlayer.id) {
            //int numberGiveUp = 0;
            if (currPlayer.thoi) {
                throw new BusinessException("You have give up!");
            }
            currPlayer.thoi = true;
            next();
        } else {
            throw new BusinessException("Not your turn!");
        }
    }

    private void newRound() {
        if (lastCard != null) {
            lastCard.clear();
        } else {
            lastCard = new ArrayList<>();
        }

        if (!fightInfo.isEmpty()) {
            long data[];
            data = fightInfo.get(fightInfo.size() - 1);
            SamPlayer lasP = findPlayer(data[0]);
            SamPlayer cuP = findPlayer(data[1]);
            long fightMoney = data[2];
            if (data[3] > 0) {
                cuP.comment.add("Tứ quý " + numToNumPoker(data[3]) + " chặt heo");
                lasP.comment.add("Bị tứ quý " + numToNumPoker(data[3]) + " chặt heo");
            } else {
                cuP.comment.add("Tứ quý chặt chồng");
                lasP.comment.add("Bị tứ quý chặt chồng");
            }
            lasP.cashLost.add(fightMoney);
            cuP.cashWin.add(fightMoney);
        }
        fightInfo.clear();
        //currCards.clear();
        //isNewRound = false;
        for (SamPlayer p : playings) {
            p.thoi = false;
        }
    }

    private String numToNumPoker(long i) {
        int in = (int) i;
        switch (in) {
            case 1:
            case 14:
                return "Át";
            case 11:
                return "J";
            case 12:
                return "Q";
            case 13:
                return "K";
            default:
                return "" + i;
        }
    }

    private int findPlayerIndex(long uid) {
        for (int i = 0; i < playings.size(); i++) {
            if (playings.get(i).id == uid) {
                return i;
            }
        }

        return -1;
    }

    private void findCurrIndex() {
        for (int i = 0; i < playings.size(); i++) {
            if (playings.get(i).id == currPlayer.id) {
                currIndex = i;
                return;
            }
        }
    }
    private int currIndex = 0;

    private void next() throws BusinessException {
        //int temp = currIndex;
        if (currIndex == playings.size() - 1) {
            currIndex = 0;
        } else if (currIndex < playings.size() - 1) {
            currIndex++;
        }

        while (playings.get(currIndex).thoi) {
            /*if (currIndex == temp) {
             isNewRound = true;
             newRound();
             break;
             }*/
            if (currIndex == playings.size() - 1) {
                currIndex = 0;
            } else if (currIndex < playings.size() - 1) {
                currIndex++;
            }
        }

        currPlayer = playings.get(currIndex);
        if (currPlayer.id == lasPlayer.id) {
            isNewRound = true;
            newRound();
        }
    }

    public long getCurrID() {
        return currPlayer.id;
    }

    @Override
    public SamPlayer getCurrPlayer() {
        return currPlayer;
    }

    public SamPlayer lasPlayer;
    public boolean fightOccur = false;
    public ArrayList<long[]> fightInfo = new ArrayList(); // lưu thông tin khi xảy ra chặt
    private ArrayList<Poker> lastCard;
    private long lastUID = 0;

    public String lastCardToString() {
        StringBuilder res = new StringBuilder();
        if (lastCard != null && !lastCard.isEmpty()) {
            for (Poker p : lastCard) {
                res.append(p.toInt()).append("#");
            }
            res.deleteCharAt(res.length() - 1);
        }
        return res.toString();
    }

    private boolean is2Cuoi(GroupCard gP) {
        //System.out.println("type:" + gP.type.toString() + "-" + gP.cards.size());
        if (gP.isHeo() || gP.isBoHeo()) {
            //System.out.println("card size:" + gP.cards.size() + "-" + currPlayer.playingCards.size());
            if (gP.cards.size() == currPlayer.playingCardSize()) {
                return true;
            }
        }
        return false;

    }

    private boolean isTatCaThoi2() {
        for (SamPlayer p : playings) {
            if (!p.de2CuoiDung) {
                return false;
            }
        }
        return true;
    }

    public void chuyenLuotKhiUserPhamLuat(long uid, TurnRequest rq) throws BusinessException {
        mLog.debug("---THANGTD DEBUG SAM---INVALID TURN " + currPlayer.username + " play " + rq.tienlenCards + " over " + lastCardToString());

        giveUp(uid);
        rq.tienlenCards = "";
        rq.isGiveup = true;
        if (lastCard.isEmpty() || (lastUID == currPlayer.id)) {
            isNewRound = true;
            newRound();
        }
    }
    public long uidNotPlayGreatestCard = 0;
    public boolean has1Card = false;

    public boolean play(long uid, TurnRequest rq) throws BusinessException, ServerException, JSONException {
        String cards = rq.tienlenCards;
        mLog.debug("---THANGTD DEBUG SAM---IN GAME " + currPlayer.username + " play " + rq.tienlenCards);
        boolean isGiveUp = rq.isGiveup;
        mLog.debug("---THANGTD DEBUG SAM---IN GAME " + currPlayer.username + " is give up " + rq.isGiveup);

        if (gameState != GAME_STATE_PLAY_CARDS) {
            throw new BusinessException("Chưa đến lúc đánh bài!");
        }

//        if (!isSentBaoSam) {
//            throw new BusinessException("Bạn hãy chờ người chơi khác báo Sâm nhé!");
//        }
        lastActivated = System.currentTimeMillis();

        if (isGiveUp) {
            if (isNewRound)
                throw new BusinessException("Bạn không được bỏ lượt!");
            
            giveUp(uid);
            return false;
        }

        isNewRound = false;

        ArrayList<Poker> playCard = stringToPokers(cards);
        if (uid == currPlayer.id) {
            lasPlayer = currPlayer;
            GroupCard gP = new GroupCard(playCard);
            if ((is2Cuoi(gP)) || currPlayer.cardIsAll2()) {
                currPlayer.de2CuoiDung = true;
                if (isTatCaThoi2()) { // Tất cả đều để 2 cuối cùng
                    endMatch(uid);
                    return true;
                }

                chuyenLuotKhiUserPhamLuat(uid, rq);
                return false;
            }

            try {
                currPlayer.play(playCard, lastCard, lastUID, rq, has1Card);
            } catch (Exception ex) {
                ex.printStackTrace();
                chuyenLuotKhiUserPhamLuat(uid, rq);
                return false;
            }

            lastCard = playCard;
            if (samBaoID > 0 && samBaoID != uid) {
                denLang(uid);
                return true;
            }

            mLog.debug("---THANGTD DEBUG SAM---IN GAME " + currPlayer.username + " number of rest cards: " + currPlayer.playingCardSize());
            if (currPlayer.playingCardSize() == 0) {
                if (playCard.size() == 1 && uidNotPlayGreatestCard > 0 && uidNotPlayGreatestCard != currPlayer.id) { //Den lang
                    int numbetCards = 0;
                    for (SamPlayer p : playings) {
                        numbetCards += p.playingCardSize();
                        //p.playingCards.clear();
                    }

                    SamPlayer punPlayer = findPlayer(uidNotPlayGreatestCard);
                    punPlayer.cashLost.add(numbetCards * firstCashBet);
                    punPlayer.comment.add("Phạt do không đánh cây cao nhất!");
                } else {
                    uidNotPlayGreatestCard = 0;
                }

                endMatch(uid);
                return true;
            } else {
                lastUID = uid;
                next();
                return false;
            }
        } else {
            throw new BusinessException("Not your turn!");
        }
    }

    private void denLang(long uid) throws BusinessException, ServerException, JSONException {
        for (SamPlayer p : playings) {
            p.cashLost.clear();
            p.cashWin.clear();
            //p.playingCards.clear();
            if (p.id == samBaoID) {
                p.comment.add("Phạt đền làng");
                p.cashLost.add((playings.size() - 1) * firstCashBet * 30);
            } else if (p.id == uid) {
                p.comment.add("Được đền làng");
                p.cashWin.add(firstCashBet * 30);
            }
        }

        endMatch(uid);
    }

    private ArrayList<Poker> stringToPokers(String str) throws BusinessException {
        if (str.equals("")) {
            return new ArrayList<>();
        }

        ArrayList<Poker> res = new ArrayList<>();

        String[] strCards = str.split("#");
        for (String s : strCards) {
            res.add(new Poker(Integer.parseInt(s)));
        }
        return res;
    }

    private void chiaFix() {
        ArrayList<Poker> cards = new ArrayList<>();
        cards.add(new Poker(5, PokerType.Co));
        cards.add(new Poker(5, PokerType.Ro));
        cards.add(new Poker(2, PokerType.Co));
        cards.add(new Poker(6, PokerType.Co));
        cards.add(new Poker(6, PokerType.Ro));
        cards.add(new Poker(11, PokerType.Pic));
        cards.add(new Poker(7, PokerType.Co));
        cards.add(new Poker(7, PokerType.Ro));
        cards.add(new Poker(7, PokerType.Pic));
        cards.add(new Poker(7, PokerType.Tep));
        this.playings.get(0).setCards(cards);
        //cards.clear();
        ArrayList<Poker> cards1 = new ArrayList<>();
        cards1.add(new Poker(8, PokerType.Co));
        cards1.add(new Poker(8, PokerType.Ro));
        cards1.add(new Poker(8, PokerType.Pic));
        cards1.add(new Poker(8, PokerType.Tep));
        cards1.add(new Poker(9, PokerType.Ro));
        cards1.add(new Poker(9, PokerType.Pic));
        cards1.add(new Poker(4, PokerType.Co));
        cards1.add(new Poker(4, PokerType.Ro));
        cards1.add(new Poker(4, PokerType.Pic));
        cards1.add(new Poker(2, PokerType.Pic));
        this.playings.get(1).setCards(cards1);
    }

    private void chia() {
        ArrayList<Integer> currList = getRandomList();

        if ((this.playings.size() <= 5) && (this.playings.size() > 1)) {
            for (int i = 0; i < playings.size(); i++) {
                SamPlayer p = this.playings.get(i);
                ArrayList<Poker> cards = new ArrayList<>();
                for (int j = 10 * i; j < 10 * (i + 1); j++) {
                    cards.add(new Poker(currList.get(j)));
                }
                p.setCards(cards);
                outCodeSB.append(p.username).append(":");
                for (int k = 0; k < cards.size(); k++) {
                    outCodeSB.append(" ").append(cards.get(k).toString());
                }
                outCodeSB.append(NEW_LINE);
            }
        } else {
            mLog.debug("Sai ne!");
        }
    }

    private ArrayList<Integer> getRandomList() {
        ArrayList<Integer> res = new ArrayList<>();
        ArrayList<Integer> currList = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            currList.add(i, i + 1);
        }
        int len = currList.size();

        for (int i = 0; i < len; i++) {
            int index = getRandomNumber(currList, res);
            currList.remove(index);
        }
        return res;
    }

    private ArrayList<Integer> getRandomCaiList() {
        ArrayList<Integer> res = new ArrayList<>();
        ArrayList<Integer> currList = new ArrayList<>();

        int rand = (int) Math.round(Math.random() * 3) + 1;
        int idx = 0;
        for (int i = 13 * (rand - 1); i < 13 * rand; i++) {
            currList.add(idx, i + 1);
            idx++;
        }
        int len = currList.size();

        for (int i = 0; i < len; i++) {
            int index = getRandomNumber(currList, res);
            currList.remove(index);
        }
        return res;
    }

    public static void main(String[] args) {
        SamTable t = new SamTable();
        t.batCai();
        /*for(int i = 1; i<53; i++){
         Poker p = new Poker(i);
         System.out.print("i = "+i+":");
         System.out.print("p = "+p.toInt()+":");
         System.out.println(p.toString());
         }*/
    }

    private int getRandomNumber(ArrayList<Integer> input, ArrayList<Integer> result) {
        int lengh = input.size();
        long maxSize = System.currentTimeMillis() * lengh;
        Random rand = new Random(maxSize);
        int index = (int) (Math.abs(rand.nextLong()) % lengh);
        result.add(input.get(index));
        return index;
    }

    public void sendStart() throws ServerException, JSONException {
        MessageFactory msgFactory = getNotNullSession().getMessageFactory();

        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            SamPlayer player = this.playings.get(i);
            if (!player.isOut) {
                try {
                    GetPokerResponse getPoker = (GetPokerResponse) msgFactory.getResponseMessage(MessagesID.GET_POKER);
                    getPoker.setSuccess(ResponseCode.SUCCESS, player.id, player.username);
                    getPoker.setBeginID(winner.id);
                    getPoker.setSamCards(player.getPlayingCards());
                    getPoker.setSamPerfectType(player.samDacBiet());
                    getPoker.session = player.currentSession;

                    getPoker.zoneId = ZoneID.SAM;
                    player.currentSession.write(getPoker);
                    sendToSuper(player);
                } catch (Exception ex) {
                    mLog.error(ex.getMessage(), ex);
                }
            }
        }
    }

    private void sendToSuper(SamPlayer player) {
        MessageFactory msgFactory = getNotNullSession().getMessageFactory();
        GetOtherPokerResponse getOtherPoker = (GetOtherPokerResponse) msgFactory.getResponseMessage(MessagesID.GET_OTHER_POKER);
        getOtherPoker.setSuccess(ResponseCode.SUCCESS, player.id, true);
        getOtherPoker.setSamCard(player.cardsToString());
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            SamPlayer p = playings.get(i);
            if (p.id != player.id && p.isSuper && p.currentSession != null) {
                try {
                    p.currentSession.write(getOtherPoker);
                } catch (Throwable e) {
                }
            }
        }
    }
    public int perfectType = 0;

    private String getEndValues(long newOwnerID) {
        String seperator_element = AIOConstants.SEPERATOR_BYTE_1;
        String seperator_dif_element = AIOConstants.SEPERATOR_BYTE_3;
        String seperator_ar = AIOConstants.SEPERATOR_BYTE_2;

        StringBuilder sb = new StringBuilder();
        sb.append(winner.id).append(seperator_element);
        sb.append(perfectType).append(seperator_element);
        sb.append(0).append(seperator_element);

        if (perfectType == 0) {
            sb.append(lastCardToString()).append(seperator_element);
            sb.append(Long.toString(newOwnerID)).append(seperator_dif_element);

            int resultSize = playings.size();
            for (int i = 0; i < resultSize; i++) {
                SamPlayer o = playings.get(i);
                long uid = o.id;
                sb.append(uid).append(seperator_element);
                sb.append(o.getWonMoney()).append(seperator_element);
                sb.append(o.comment()).append(seperator_element);
                sb.append(o.cardsToString()).append(seperator_element);
                sb.append(o.isOut ? "1" : "0").append(seperator_element);
                sb.append(o.notEnoughMoney() ? "1" : "0").append(seperator_element);
                sb.append(o.cash).append(seperator_ar);
            }

            sb.deleteCharAt(sb.length() - 1);
            if (fightInfo != null && !fightInfo.isEmpty()) {
                long[] data = fightInfo.get(0);
                sb.append(data[0]).append(seperator_element);
                sb.append(data[1]).append(seperator_element);
                sb.append(data[2]).append(seperator_element);
                if (data.length == 5) {
                    sb.append(1).append(seperator_element);
                    sb.append(data[3]).append(seperator_element);
                    sb.append(data[4]).append(seperator_element);
                } else {
                    sb.append(0).append(seperator_element);
                }
                sb.deleteCharAt(sb.length() - 1);
            }
        } else {
            sb.deleteCharAt(sb.length() - 1);
            sb.append(seperator_dif_element);
            int resultSize = playings.size();
            // int count = 0;
            for (int i = 0; i < resultSize; i++) {
                SamPlayer o = playings.get(i);
                sb.append(o.id).append(seperator_element);
                sb.append(o.getWonMoney()).append(seperator_element);
                sb.append(o.comment()).append(seperator_element);
                sb.append(o.cardsToString()).append(seperator_element);
                sb.append(o.isOut ? "1" : "0").append(seperator_element);
                sb.append(o.notEnoughMoney() ? "1" : "0").append(seperator_element);
                sb.append(o.cash).append(seperator_ar);
            }

            sb.deleteCharAt(sb.length() - 1);
            sb.append(seperator_dif_element);
            sb.append(Long.toString(newOwnerID)).append(seperator_dif_element);
        }

        return sb.toString();
    }
    //private ArrayList<SamResultEntity> result = new ArrayList<>();

    /*private String getEndJSonValue(long newOwnerId) throws JSONException {
     StringBuilder sb = new StringBuilder();
     sb.append(Long.toString(newOwnerId)).append(AIOConstants.SEPERATOR_BYTE_3);

     int playingSize = this.playings.size();

     for (int i = 0; i < playingSize; i++) {
     try {
     SamPlayer player = this.playings.get(i);
     sb.append(Long.toString(player.id)).append(AIOConstants.SEPERATOR_BYTE_1);
     sb.append(Long.toString(player.getWonMoney())).append(AIOConstants.SEPERATOR_BYTE_1);
     sb.append(Long.toString(player.cash)).append(AIOConstants.SEPERATOR_BYTE_1);
     sb.append(player.isOut ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
     sb.append(player.notEnoughMoney() ? "1" : "0");
     sb.append(AIOConstants.SEPERATOR_BYTE_2);
     } catch (Exception ex) {
     outCodeSB.append("error end van khi chua chia bai").append(NEW_LINE);
     }
     }

     sb.deleteCharAt(sb.length() - 1);


     return sb.toString();
     }*/
    private long endMatch(long winID) throws ServerException, JSONException {
        gameState = GAME_STATE_END;

        resetAutoKickOut();
        this.isPlaying = false;
        winner = findPlayer(winID);

        if (winID == samBaoID || perfectType > 0) {
            // Báo Sâm thành công hoặc tới trắng
            try {
                UserDB userDb = new UserDB();
                userDb.updateGameEvent(winID, ZoneID.SAM);
            } catch (Throwable e) {
            }

            for (SamPlayer p : playings) {
                if (p.id != winID) {
                    //p.playingCards.clear();
                    endMatchState = SAM_THAT_BAI;

                    if (perfectType == 5) {
                        p.cashLost.add(40 * firstCashBet);
                    } else {
                        p.cashLost.add(30 * firstCashBet);
                    }

                    if (perfectType > 0) {
                        p.comment.add("Bị phạt tới trắng!");
                    } else {
                        p.comment.add("Bị phạt sâm thành công!");
                    }
                } else {
                    if (endMatchState != SAM_THANH_CONG_DAC_BIET) {
                        endMatchState = SAM_THANH_CONG;

                        if (perfectType > 0) {
                            p.comment.add("Tới trắng thành công!");
                        } else {
                            p.comment.add("Sâm thành công!");
                        }
                    }
                }
            }
        }

        long newOwnerId = 0;
        updateCash();

        //send end message to client
        MessageFactory msgFactory = getNotNullSession().getMessageFactory();

        if (owner.isOut || owner.notEnoughMoney()) {
            SamPlayer newOwner = this.ownerQuit();
            if (newOwner != null && newOwnerId == 0) {
                newOwnerId = newOwner.id;
                this.setOwnerId(newOwnerId);
            }
        }

        for (SamPlayer p : playings) {
            mLog.debug(p.username + " has wonMoney " + p.getWonMoney());
        }

        String endValue = getEndValues(newOwnerId);
        EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory.getResponseMessage(MessagesID.MATCH_END);
        // set the result
        endMatchRes.setZoneID(ZoneID.SAM);
        endMatchRes.setSuccess(endValue);

        broadcastMsg(endMatchRes, playings, waitings, winner, true);

        outCodeSB.append("------------end match").append(NEW_LINE);
        saveLogToFile();
//       this.removeNotEnoughMoney(this.getRoom());
        this.resetPlayers();
        return newOwnerId;
    }

    @Override
    public boolean newContainPlayer(long id) {
        for (int i = 0; i < this.playings.size(); i++) {
            if (playings.get(i).id == id) {
                return true;
            }
        }

        return false;
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

    public void updateCash() {
        Connection con = DBPoolConnection.getConnection();
        try {
            String desc = "Sam:" + matchID;
            UserDB userDb = new UserDB(con);

            boolean havingMinusBalance = false;

            long totalWinMoney = 0;
            for (int i = 0; i < playings.size(); i++) {
                SamPlayer p = playings.get(i);
                if (p.id != winner.id) {
                    /*
                     * if (lost) res > 0
                     * else res < 0
                     */
                    long res = p.cashLost(firstCashBet);
                    if (res > p.cash) {
                        res = p.cash;
                    }

                    if (res > winner.cash) {
                        res = winner.cash;
                    }

                    totalWinMoney += res;
                    if (res < 0) {
                        res *= REAL_GOT_MONEY;
                    }

                    p.setWonMoney(-res);
                    p.cash = userDb.updateUserMoney(res, false, p.id, desc, p.getExperience(), 20);
                    if (p.cash < 0) {
                        totalWinMoney += p.cash;
                    }
                    
                    // Check for in-game event for loser
                    p.checkEvent(false);
                }
            }
            
            // Check for in-game event for winner
            winner.checkEvent(true);
            
            /*
             if (totalWinMoney > winner.cash) {
             totalWinMoney = winner.cash;
             }
             */
            mLog.debug("---THANGTD DEBUG SAM---TABLE: " + matchID + " TOTAL WIN MONEY: " + totalWinMoney);
            if (totalWinMoney > 0) {
                totalWinMoney *= REAL_GOT_MONEY;
            }
            mLog.debug("---THANGTD DEBUG SAM---TABLE: " + matchID + " TOTAL WIN MONEY AFTER TAX: " + totalWinMoney);
            if (endMatchState == 0) {
                winner.comment.add("Thắng");
            }
            winner.setWonMoney(totalWinMoney);
            winner.cash = userDb.updateUserMoney(totalWinMoney, true, winner.id, desc, winner.getExperience(), 20);
//            havingMinusBalance = updatePairBetOther(userDb, desc);

            if (havingMinusBalance) {
                userDb.notMinus();
            }
        } catch (Throwable ex) {
            outCodeSB.append(ex.getMessage()).append(ex.getStackTrace()).append(NEW_LINE);
            mLog.error(ex.getMessage(), ex);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                outCodeSB.append(ex.getStackTrace()).append(NEW_LINE);
                mLog.error(ex.getMessage(), ex);
            }
        }

        saveLogToFile();
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
                        SamPlayer bcPlayer = this.playings.get(i);
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

                            SamPlayer currOwner = findPlayer(owner.id);
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
                mLog.error("Sam Kick time out error matchId " + matchID, ex);
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
            SamPlayer player = this.playings.get(i);
            if (!player.isOut && player.currentSession != null && player.currentSession.getMessageFactory() != null) {
                return player.currentSession;
            }
        }

        return null;
    }

    public SimplePlayer getNotNullSessionPlayer() {
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            SamPlayer player = this.playings.get(i);
            if (!player.isOut && player.currentSession != null && player.currentSession.getMessageFactory() != null) {
                return player;
            }
        }

        return null;
    }
    private boolean isSentBaoSam = false;

    public void huyBaoSam(long uid) throws BusinessException, ServerException, JSONException {
        SamPlayer p = findPlayer(uid);
        p.huyBaoSam();
        if (isAllCancelSam()) {
            sentBaoSam();
        }
    }

    private boolean isAllCancelSam() {
        for (SamPlayer p : playings) {
            if (!p.huyBaoSam) {
                return false;
            }
        }

        return true;
    }

    private void sentBaoSam() throws ServerException, JSONException {
//        MessageFactory msgFactory = ownerSession.getMessageFactory();
//        BaoSamResponse resAn = (BaoSamResponse) msgFactory.getResponseMessage(MessagesID.BAOSAM);
//        resAn.setSuccess(winner.id, false);
//        broadcastMsg(resAn, playings, waitings, this.owner, true);

        boolean isBaoSam = false;
        if (samBaoList.isEmpty()) {
            currPlayer = winner;
        } else {
            if (samBaoList.indexOf(winner) != -1) {
                currPlayer = winner;
            } else {
                int caiIndex = findPlayerIndex(winner.id);
                
                for (int i = 0; i < samBaoList.size() - 1; i++) {
                    for (int j = i + 1; j < samBaoList.size(); j++) {
                        SamPlayer p1 = samBaoList.get(i);
                        SamPlayer p2 = samBaoList.get(j);

                        if (findPlayerIndex(p1.id) != -1 && findPlayerIndex(p2.id) != -1) {
                            int firstValue = findPlayerIndex(p1.id) - caiIndex;
                            int secondValue = findPlayerIndex(p2.id) - caiIndex;
                            if (firstValue < 0 && secondValue < 0) {
                                if (Math.abs(secondValue) > Math.abs(firstValue)) {
                                    SamPlayer tmp = p2;
                                    p1 = p2;
                                    p2 = tmp;
                                }
                            } else {
                                if (Math.abs(secondValue) < Math.abs(firstValue)) {
                                    SamPlayer tmp = p2;
                                    p1 = p2;
                                    p2 = tmp;
                                }
                            }
                        }
                    }
                }

                currPlayer = samBaoList.get(0);
            }

            samBaoID = currPlayer.id;

            isBaoSam = true;
        }

        gameState = GAME_STATE_PLAY_CARDS;

        MessageFactory msgFactory = getNotNullSession().getMessageFactory();
        BaoSamResponse resAn = (BaoSamResponse) msgFactory.getResponseMessage(MessagesID.BAOSAM);
        resAn.setSuccess(currPlayer.id, isBaoSam, true);
        broadcastMsg(resAn, playings, waitings, this.owner, true);

        isSentBaoSam = true;

        perfectType = currPlayer.samDacBiet();
        if (perfectType > 0) {
            endMatchState = SAM_THANH_CONG_DAC_BIET;
            endMatch(currPlayer.id);
        } else {
            findCurrIndex();
            //currIndex = 0;
            currentTimeOut = 30000;
            lastActivated = System.currentTimeMillis();
        }
    }

    @Override
    public void doTimeout() {
        try {
            if (isPlaying) {
//                if (!isBatCai) {
//                    batCai();
//                } else if (isBatCai && !isChiaBai) {
//                    try {
//                        letsStart();
//                    } catch (ServerException ex) {
//                        mLog.error(ex.getMessage(), ex);
//                    } catch (JSONException ex) {
//                        mLog.error(ex.getMessage(), ex);
//                    } catch (BusinessException ex) {
//                        mLog.error(ex.getMessage(), ex);
//                    }
//                } else if (isChiaBai && !isSentBaoSam) {
//                    sentBaoSam();
//                } else {
//                    this.currPlayer.autoPlay(this);
//                    currentTimeOut = 30000;
//                    lastActivated = System.currentTimeMillis();
//                }
                if (gameState == GAME_STATE_BAT_CAI) {
                    try {
                        letsStart();
                    } catch (ServerException ex) {
                        mLog.error(ex.getMessage(), ex);
                    } catch (JSONException ex) {
                        mLog.error(ex.getMessage(), ex);
                    } catch (BusinessException ex) {
                        mLog.error(ex.getMessage(), ex);
                    }
                } else if (gameState == GAME_STATE_BAO_SAM) {
                    sentBaoSam();
                } else if (gameState == GAME_STATE_PLAY_CARDS) {
                    this.currPlayer.autoPlay(this);
                    currentTimeOut = 30000;
                    lastActivated = System.currentTimeMillis();
                }
            }
        } catch (Exception ex) {
            try {
                this.isPlaying = false;
                mLog.error(ex.getMessage(), ex);
            } catch (Exception exx) {
            }
        } catch (ServerException ex) {
            Logger.getLogger(SamTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
