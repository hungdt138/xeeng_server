/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.newbacay.data;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BetRequest;
import com.tv.xeeng.base.protocol.messages.CancelChallengeRequest;
import com.tv.xeeng.base.protocol.messages.CancelChallengeResponse;
import com.tv.xeeng.base.protocol.messages.CancelResponse;
import com.tv.xeeng.base.protocol.messages.ChallengeOtherPlayerResponse;
import com.tv.xeeng.base.protocol.messages.EndMatchResponse;
import com.tv.xeeng.base.protocol.messages.GetPokerResponse;
import com.tv.xeeng.base.protocol.messages.KickOutRequest;
import com.tv.xeeng.base.protocol.messages.OutResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.Couple;
import com.tv.xeeng.game.data.Messages;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.data.Triple;
import com.tv.xeeng.game.data.Utils;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.protocol.MessageFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

/**
 *
 * @author tuanda
 */
public class NewBaCayTable extends SimpleTable {
    //private XocDiaPlayer owner;

    private List<NewBaCayPlayer> playings = new ArrayList<>();
    private List<NewBaCayPlayer> waitings = new ArrayList<>();
    private Map<SimplePlayer, NewBaCayPlayer> pairBetTogether = new HashMap<>();//key invited player with betMoney, 
    private static final String NO_CARDS = "0#0#0";
//    
//    private final Object lockPlayings = new Object();
//    private final Object lockWaitings = new Object();
    private static final int AUTO_CHIA_BAI = 20000;
    private static final int AUTO_LAT_BAI = 20000;
    private static final int AUTO_CANCEL_SHOWHAND = 30000;
//	private static final double REAL_PERCENT = 0.9;
    private boolean isChiaBai = false;
    private boolean isSendCancel = false;
    private boolean isSendPoker = false;
//    private static final int LIMIT_PLAYER = 7;
    private final String FULL_PLAYER_MSG = "Phòng đã đầy";
    //private static final String BET_AFTER_DELIVER_POKER = "Bạn không được phép đặt cược khi bài đã chia";
    //private boolean hasShowHand = false;
    private final static int BACAY_LOG_TYPE = 10011;
    private boolean isUpdatingDB = false;
    public static final Logger mLogIn = LoggerContext.getLoggerFactory().getLogger(NewBaCayTable.class);

    public NewBaCayTable(NewBaCayPlayer owner, long money, long matchId) {
        this.matchID = matchId;
        this.owner = owner;
        this.playings.add(owner);
        this.firstCashBet = money;
        owner.isGiveUp = false;  //owner doesnt bet

        logdir = "Ba_cay";
    }

    @Override
    public boolean isFullTable() {
        return getTableSize() >= getMaximumPlayer(); // Maximum player = 4 player 
    }

    @Override
    public int getTableSize() {
        return playings.size() + getWaitings().size();
    }

    public List<NewBaCayPlayer> getPlayings() {
        return playings;
    }

    public void join(NewBaCayPlayer player) throws NewBaCayException {

        if (isFullTable()) {
            throw new NewBaCayException(FULL_PLAYER_MSG);
        }

        player.setLastActivated(System.currentTimeMillis());
        if (isPlaying) {
//                synchronized(this.lockWaitings)
//                {
            this.getWaitings().add(player);
            player.isMonitor = true;
//                }
        } else {
//                synchronized(this.lockPlayings)
//                {
            playings.add(player);
            player.isMonitor = false;
//                }
        }

        outCodeSB.append("player: ").append(player.username).append(" join").append(NEW_LINE);
    }

    public int getOwnerIndex() {
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            NewBaCayPlayer player = this.playings.get(i);
            if (player.id == owner.id) {
                return i;
            }
        }
        return 0;
    }

    public void kickout(long userKickoutid, KickOutRequest rqKickOut) throws NewBaCayException {
        if (userKickoutid != owner.id) {
            throw new NewBaCayException(Messages.NOT_OWNER_PERSON);
        }

        NewBaCayPlayer player = findPlayer(rqKickOut.uid);
        if (player == null) {
            throw new NewBaCayException(Messages.PLAYER_OUT);
        }

        if (this.isPlaying) {
            throw new NewBaCayException(Messages.PLAYING_TABLE);
        }

        player.currentSession.setLastFP(System.currentTimeMillis() - 20000); //for fast play

        MessageFactory msgFactory = getNotNullSession().getMessageFactory();
        OutResponse broadcastMsg = (OutResponse) msgFactory.getResponseMessage(MessagesID.OUT);
        broadcastMsg.setSuccess(ResponseCode.SUCCESS, rqKickOut.uid, player.username + " bị chủ bàn đá ra ngoài", player.username, 0);

        broadcastMsg(broadcastMsg, playings, waitings, player, true);
        remove(player);
        Room room = player.currentSession.leftRoom(matchID);

        if (room != null) {
            room.left(player.currentSession);
        }

        player.currentSession.setRoom(null);

        UserDB db = new UserDB();
        if (db.checkBotUser(rqKickOut.uid)) {
            this.isKickoutBot = true;
        }
    }

    public CancelResponse cancel(long uid) throws NewBaCayException, ServerException, JSONException {
        NewBaCayPlayer player = findPlayer(uid);
        if (player == null) {
            throw new NewBaCayException(NONE_EXISTS_PLAYER);
        }

        outCodeSB.append("cancel player:").append(player.username).append(NEW_LINE);

        CancelResponse resMatchCancel;
        MessageFactory msgFactory = getNotNullSession().getMessageFactory();
        resMatchCancel = (CancelResponse) msgFactory.getResponseMessage(MessagesID.MATCH_CANCEL);
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
                if (uid == owner.id) {
                    for (int i = 0; i < this.playings.size(); i++) {
                        NewBaCayPlayer bcPlayer = this.playings.get(i);
                        if (!bcPlayer.isOut && bcPlayer.id != owner.id) {
                            bcPlayer.isWin = true;
                        }
                    }
                    resMatchCancel.newOwner = this.endMatch();
                } else {
                    if (this.onlinePlayers() == 1)//there are two players. One player(not owner) leave tables
                    {
                        player.isWin = false;
                        this.endMatch();
                    }
                }
            }
        } else {
            this.remove(player);
            if (uid == owner.id) {
                NewBaCayPlayer newOwner = this.ownerQuit();

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
        this.remove((NewBaCayPlayer) player);
    }

    public void remove(NewBaCayPlayer player) {
        try {
            if (player != null) {
                NewBaCayPlayer removePlayer;
                outCodeSB.append("Remove player ").append(player.id).append(NEW_LINE);
//                synchronized (lockPlayings) {

                for (int i = 0; i < this.playings.size(); i++) {
                    removePlayer = this.playings.get(i);
                    if (removePlayer.id == player.id) {
                        this.playings.remove(removePlayer);
                        break;
                    }
                }

//                }
//                synchronized(lockWaitings)
//                {
                for (int i = 0; i < this.waitings.size(); i++) {
                    removePlayer = this.waitings.get(i);
                    if (removePlayer.id == player.id) {
                        this.waitings.remove(removePlayer);
                        break;
                    }
                }
//                }
            }
        } catch (Exception e) {
            outCodeSB.append("Remove player !!!error ").append(player.id).append(NEW_LINE);
            mLogIn.error(e.getMessage() + " remove player: ", e.getStackTrace());
        }
    }

    public int onlinePlayers() {
        int size = this.waitings.size();

        for (int i = 0; i < this.playings.size(); i++) {
            if (!playings.get(i).isOut) {
                size++;
            }
        }

        return size;
    }

    public NewBaCayPlayer ownerQuit() {
        NewBaCayPlayer newOwner = null;
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            NewBaCayPlayer p = playings.get(i);
            if (!p.notEnoughMoney() && !p.isOut && (newOwner == null || newOwner.cash < p.cash)) {
                newOwner = p;
//                           owner = p;
//                           ownerSession = owner.currentSession;
//                          return p;
            }
        }

        for (int i = 0; i < waitings.size(); i++) {
            NewBaCayPlayer p = waitings.get(i);
            if (!p.notEnoughMoney() && (newOwner == null || newOwner.cash < p.cash)) {
                newOwner = p;
//                           owner = p;
//                           owner.setLastActivated(System.currentTimeMillis());
//                           ownerSession = owner.currentSession;
//                          return p;
            }
        }

        if (newOwner != null) {
            owner = newOwner;
            ownerSession = newOwner.currentSession;
        }

        return newOwner;
    }

    @Override
    public NewBaCayPlayer findPlayer(long uid) {
        for (int i = 0; i < this.playings.size(); i++) {
            NewBaCayPlayer player = this.playings.get(i);
            if (player.id == uid) {
                return player;
            }
        }

        for (int i = 0; i < this.waitings.size(); i++) {
            NewBaCayPlayer player = this.waitings.get(i);
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
        List<NewBaCayPlayer> removedPlayer = new ArrayList<>();
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            NewBaCayPlayer p = playings.get(i);
            if (p.isOut || p.notEnoughMoney()) {
                removedPlayer.add(p);
            }
        }
//            mLog.debug("remove size: " + removedPlayer.size());
//            synchronized(playings)
//            {
        int removeSize = removedPlayer.size();
        for (int i = 0; i < removeSize; i++) {
            playings.remove(removedPlayer.get(i));
        }

        for (int i = 0; i < this.waitings.size(); i++) {
            this.waitings.get(i).isMonitor = false;
        }

        this.playings.addAll(this.waitings);
        this.waitings = new ArrayList<>();
//            }
        mLogIn.debug("playings size: " + playings.size());
        resetAutoKickOut();

        for (int i = 0; i < playings.size(); i++) {
            NewBaCayPlayer player = playings.get(i);
            player.isReady = false;
            player.latbai = false;
            player.setMultiBetMoney(0);
            player.point = 0;
            player.isGiveUp = true;
            player.isChiaBai = false;
            player.moneyForBet = this.firstCashBet;
            player.isBet = false;
            player.resetPoker();
        }

        isUpdatingDB = false;

        isSendPoker = false;
        isSendCancel = false;
        owner.isReady = true;
        this.isPlaying = false;
        this.pairBetTogether = new HashMap<>();
        resetBotCheck();
    }

    public boolean isAllReady() {
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            NewBaCayPlayer t = this.playings.get(i);
//            mLog.error("---THANGTD START DEBUG---" + t.username + " is ready: " + t.isReady);
            if (t.id != this.owner.id && !t.isReady) {
                return false;
            }
        }

        return true;
    }

    public void start() {
        lastActivated = System.currentTimeMillis();
        setCurrentTimeOut(AUTO_CHIA_BAI);
        resetPlayers();
        isUpdatingDB = false;

        this.isChiaBai = false;

//        synchronized(lockWaitings)
//        {
        this.playings.addAll(waitings);
        this.waitings = new ArrayList<>();
//        }

        //setup all player to giveup if player does bet, he willn't give up.
        for (int i = 0; i < this.playings.size(); i++) {
            this.playings.get(i).isGiveUp = true;
            this.playings.get(i).setWonMoney(0);
        }

        //chiabaiFlash();
        this.isPlaying = true;
    }

    public void bet(BetRequest request) throws NewBaCayException, JSONException, ServerException {
        outCodeSB.append("betInfo uid ").append(request.uid).append(NEW_LINE);
        NewBaCayPlayer player = findPlayer(request.uid);
        if (player == null) {
            throw new NewBaCayException(NONE_EXISTS_PLAYER);
        }

//        if(player.id == owner.id)
//        {
//            throw new NewBaCayException(NOT_ALLOW_OWNER_BET);
//        }
        if (request.money < this.firstCashBet) {
            throw new NewBaCayException(REQUIRED_MONEY);
        }

        if (player.isBet) {
            throw new NewBaCayException(DONE_BET);
        }

        long maxOwnerBet = owner.cash / this.playings.size();

        if (this.playings.size() > 1) {
            maxOwnerBet = owner.cash / (this.playings.size() - 1);
        }

        if (request.money > maxOwnerBet) {
            throw new NewBaCayException(NOT_ALLOW_OWNER_MONEY_BET);
        }

        if (request.money > player.cash) {
            throw new NewBaCayException(NOT_ALLOW_USER_MONEY_BET);
        }

        if (!this.isPlaying) {
            throw new NewBaCayException(NOT_PLAYING_TABLE);
        }

//        if(isChiaBai)
//        {
//            throw new NewBaCayException(BET_AFTER_DELIVER_POKER);
//        }
//        
        player.isGiveUp = false; //this user did bet
        player.moneyForBet = request.money;
        player.isBet = true;

        if (player.id != owner.id) {
            player.setMultiBetMoney(request.money + player.getMultiBetMoney());
            owner.setMultiBetMoney(request.money + owner.getMultiBetMoney());
        }
        if (request.playings != null) {
            for (int i = 0; i < request.playings.size(); i++) {
                SimplePlayer inviteBetPlayer = request.playings.get(i);
                NewBaCayPlayer inviteBCBetPlayer = findPlayer(inviteBetPlayer.id);
                inviteBCBetPlayer.setMultiBetMoney(inviteBCBetPlayer.getMultiBetMoney() + inviteBetPlayer.getBetOther());

                if (inviteBCBetPlayer.id == request.uid) {
                    outCodeSB.append("you can't bet with u").append(inviteBCBetPlayer.id).append(NEW_LINE);
                    continue;
                }

                /* if(inviteBCBetPlayer == null)
                 {
                 outCodeSB.append("bet other player doesn't exist uid").append(inviteBCBetPlayer.id).append(NEW_LINE);
                 continue;
                 }*/
                long betMoney = inviteBetPlayer.getBetOther() + player.getMultiBetMoney();

                player.setMultiBetMoney(betMoney);
                pairBetTogether.put(inviteBetPlayer, player);
            }
        }
        if (!isChiaBai) {
            checkAllBetPlayers();
        }
    }

    private void processChallengeOtherPlayer() throws JSONException, ServerException {
        int pairBetSize = pairBetTogether.size();
        if (pairBetSize > 0) {
            //prepare for send poker
            MessageFactory msgFactory = getNotNullSession().getMessageFactory();

            for (int i = 0; i < playings.size(); i++) {
                NewBaCayPlayer player = this.playings.get(i);

                if (!player.isOut) {
                    boolean hasBetOther = false;
                    JSONObject encodingObj = new JSONObject();
                    encodingObj.put("mid", MessagesID.CHALLENGE_OTHER_PLAYER);
                    encodingObj.put("code", ResponseCode.SUCCESS);
                    JSONArray playersArr = new JSONArray();
                    for (SimplePlayer smpPlayer : pairBetTogether.keySet()) //key invited player with betMoney, 
                    {

                        if (smpPlayer.id == player.id) {
                            NewBaCayPlayer challengePlayer = pairBetTogether.get(smpPlayer);
                            JSONObject jO = new JSONObject();
                            jO.put("uid", challengePlayer.id);
                            jO.put("money", smpPlayer.getBetOther());
                            hasBetOther = true;

                            setCurrentTimeOut(AUTO_CANCEL_SHOWHAND);
                            setLastActivated(System.currentTimeMillis());

                            playersArr.put(jO);
                        }
                    }

                    if (hasBetOther) {
                        //there are some players which bet u. We will send it to client

                        encodingObj.put("playings", playersArr);
                        ChallengeOtherPlayerResponse resChallenge = (ChallengeOtherPlayerResponse) msgFactory
                                .getResponseMessage(MessagesID.CHALLENGE_OTHER_PLAYER);
                        resChallenge.setSuccess(encodingObj);
                        outCodeSB.append("Send challenge to  player name: ").append(player.username).append(NEW_LINE);

                        player.currentSession.write(resChallenge);
                    }
                }
            }
        }
    }

    private boolean contain(long[] arrUid, long uid) {
        for (int i = 0; i < arrUid.length; i++) {
            if (arrUid[i] == uid) {
                return true;
            }
        }

        return false;
    }

    private void sendCancelChallenge(List<SimplePlayer> removeBetOther, NewBaCayPlayer cancelBcPlayer) throws ServerException {
        if (!isSendCancel) {
            isSendCancel = true;
            MessageFactory msgFactory = getNotNullSession().getMessageFactory();

            for (int i = 0; i < removeBetOther.size(); i++) {
                NewBaCayPlayer bcPlayer = pairBetTogether.get(removeBetOther.get(i));
                if (!bcPlayer.isOut) {
                    CancelChallengeResponse cancelRes = (CancelChallengeResponse) msgFactory.getResponseMessage(MessagesID.CANCEL_CHALLENGE_OTHER_PLAYER);
                    cancelRes.setFailure(cancelBcPlayer.username + " hủy chơi cận biên với bạn");
                    bcPlayer.currentSession.write(cancelRes);

                }

                pairBetTogether.remove(removeBetOther.get(i));
            }
        }
    }

    public void cancelChallenge(CancelChallengeRequest request, long cancelUid) throws ServerException, NewBaCayException {
        outCodeSB.append("cancel challenge").append(NEW_LINE);
        //send end message to client

        if (!this.isPlaying) {
            throw new NewBaCayException(Messages.UNSUCCESSFULLY_CANCEL_CHALLENGE);
        }

        NewBaCayPlayer cancelBcPlayer = findPlayer(cancelUid);
        if (cancelBcPlayer == null) {
            outCodeSB.append("Cancel challenge error null player").append(NEW_LINE);
            throw new NewBaCayException(Messages.NONE_EXISTS_PLAYER);
        }

        String[] arrStrUid = request.lstPlayerId.split("#");
        long[] arrUid = new long[arrStrUid.length];
        for (int i = 0; i < arrStrUid.length; i++) {
            arrUid[i] = Long.parseLong(arrStrUid[i]);
        }

        List<SimplePlayer> removeBetOther = new ArrayList<>();

        for (SimplePlayer smpPlayer : pairBetTogether.keySet()) //key invited player with betMoney, 
        {
            if (smpPlayer.id == cancelUid) {
                NewBaCayPlayer bcPlayer = pairBetTogether.get(smpPlayer);
                if (contain(arrUid, bcPlayer.id)) {
                    removeBetOther.add(smpPlayer);
                    cancelBcPlayer.setMultiBetMoney(cancelBcPlayer.getMultiBetMoney() - smpPlayer.getBetOther());//decrease money bet together
                    bcPlayer.setMultiBetMoney(bcPlayer.getMultiBetMoney() - smpPlayer.getBetOther());//decrease money bet together
                }
            }
        }

        sendCancelChallenge(removeBetOther, cancelBcPlayer);

    }

    public void checkAllBetPlayers() throws JSONException, ServerException {
//        synchronized(lockPlayings) // to make sure action chiabai does only one times
//        {
        for (int i = 0; i < this.playings.size(); i++) {
            NewBaCayPlayer player = playings.get(i);
            if (!player.isOut && player.isGiveUp) // this player still doesn't bet
            {
                return;
            }
        }

//        }
        if (!isChiaBai) {
            processChallengeOtherPlayer();
            if (pairBetTogether.isEmpty()) {
                //there 's no show hand and bet together so we will deliver poker immediately
                chiabai();
            }
        }

    }

    private void checkCancelChallenge() throws ServerException {
        for (int i = 0; i < this.playings.size(); i++) {
            NewBaCayPlayer player = this.playings.get(i);
            if (!player.isOut && player.getMultiBetMoney() > player.cash) {
                //we should have to decrease this person multibet
                List<SimplePlayer> removeBetOther = new ArrayList<>();

                for (SimplePlayer smpPlayer : pairBetTogether.keySet()) //key invited player with betMoney, 
                {
                    if (smpPlayer.id == player.id) {
                        NewBaCayPlayer bcPlayer = pairBetTogether.get(smpPlayer);

                        removeBetOther.add(smpPlayer);
                        player.setMultiBetMoney(player.getMultiBetMoney() - smpPlayer.getBetOther());//decrease money bet together
                        bcPlayer.setMultiBetMoney(bcPlayer.getMultiBetMoney() - smpPlayer.getBetOther());//decrease money bet together
                    }
                }

                sendCancelChallenge(removeBetOther, player);
            }
        }
    }
    private int beatCode = 0; // 1: an, 11: sap, 8: 8p, 9: 9p, 10: 10p
    private long beatUid = 0;
    private long superUid = 0;

    private void resestSuper() {
        beatCode = 0;
        beatUid = 0;
        superUid = 0;
    }

    public void beat(long uid, int bCode, long dUid) {
        if (Utils.isSuperUser(uid)) {
            superUid = uid;
            beatCode = bCode;
            beatUid = dUid;
        }
    }

    private boolean chiaCheat() {
        try {
            ChiaBai chia = new ChiaBai();
            switch (beatCode) {
                case 1: { // an
                    Triple<Poker[], Poker[], ArrayList<Poker>> res = chia.anDe();
                    NewBaCayPlayer superP = findPlayer(superUid);
                    NewBaCayPlayer beatP = findPlayer(beatUid);
                    superP.setPokers(res.e1);
                    outCodeSB.append(": ").append(superP.username).append(" poker: ").append(superP.pokersToString()).append(NEW_LINE);
                    beatP.setPokers(res.e2);
                    outCodeSB.append(": ").append(beatP.username).append(" poker: ").append(beatP.pokersToString()).append(NEW_LINE);
                    int len = playings.size();
                    int count = 0;
                    for (int i = 0; i < len; i++) {
                        NewBaCayPlayer p = playings.get(i);
                        if (p.id != superUid && p.id != beatUid) {
                            Poker[] cards = new Poker[3];
                            cards[0] = res.e3.get(count++);
                            cards[1] = res.e3.get(count++);
                            cards[2] = res.e3.get(count++);
                            p.setPokers(cards);
                            outCodeSB.append(": ").append(p.username).append(" poker: ").append(p.pokersToString()).append(NEW_LINE);
                        }
                    }
                    resestSuper();
                    return true;
                }
                default: {
                    Couple<Poker[], ArrayList<Poker>> res = chia.cheat(beatCode);
                    NewBaCayPlayer superP = findPlayer(superUid);
                    superP.setPokers(res.e1);
                    outCodeSB.append(": ").append(superP.username).append(" poker: ").append(superP.pokersToString()).append(NEW_LINE);
                    int len = playings.size();
                    int count = 0;
                    for (int i = 0; i < len; i++) {
                        NewBaCayPlayer p = playings.get(i);
                        if (p.id != superUid) {
                            Poker[] cards = new Poker[3];
                            cards[0] = res.e2.get(count++);
                            cards[1] = res.e2.get(count++);
                            cards[2] = res.e2.get(count++);
                            p.setPokers(cards);
                            outCodeSB.append(": ").append(p.username).append(" poker: ").append(p.pokersToString()).append(NEW_LINE);
                        }
                    }
                    resestSuper();
                    return true;

                }
            }

        } catch (Throwable e) {
            resestSuper();
            return false;
        }
    }

    // Chia bài thường
    private void chiaThuong() {
        int numberPlayers = this.playings.size();
        checkBotUser();
        ArrayList<Poker[]> res;
        if (botPlayer != null) {
            res = chiaCheat(numberPlayers);
        } else {
            res = chia(numberPlayers);
        }

        for (int i = 0; i < numberPlayers; i++) {
            NewBaCayPlayer player = this.playings.get(i);

            if (!player.isOut) {
                player.isChiaBai = true;
            }

            player.setPokers(res.get(i));
            String s = "";
            for (Poker p : res.get(i)) {
                s += p.toString() + ",";
            }

            mLogIn.debug("Chia:" + s);
            outCodeSB.append(": ").append(player.username).append(" poker: ").append(player.pokersToString()).append(NEW_LINE);
        }
    }

    private void chiabai() throws ServerException {
        if (!this.isChiaBai) {
            this.isChiaBai = true;

            checkCancelChallenge();

            if (beatCode > 0) {
                if (!chiaCheat()) {
                    chiaThuong();
                }
            } else {
                chiaThuong();
            }

            int numberPlayers = this.playings.size();
            for (int i = 0; i < numberPlayers; i++) {
                NewBaCayPlayer bcPlayer = this.playings.get(i);
                if (bcPlayer.id != owner.id) {
                    bcPlayer.isWin = bcPlayer.isWin((NewBaCayPlayer) owner);
                }
            }

            setCurrentTimeOut(AUTO_LAT_BAI);
            lastActivated = System.currentTimeMillis();
            //createLogFile();
        }
    }

    public void sendPokers() throws ServerException, JSONException {
        if (!isSendPoker) {
            isSendPoker = true;

            MessageFactory msgFactory = getNotNullSession().getMessageFactory();

            JSONArray betInfo = new JSONArray();
            for (int i = 0; i < this.playings.size(); i++) {
                NewBaCayPlayer player = this.playings.get(i);
                if (!player.isOut && player.id != owner.id) {
                    JSONObject jO = new JSONObject();
                    jO.put("uid", player.id);
                    jO.put("moneyBet", player.getMultiBetMoney());
                    betInfo.put(jO);
                }
            }

            for (int i = 0; i < this.playings.size(); i++) {
                NewBaCayPlayer player = this.playings.get(i);
                if (!player.isOut) {
                    GetPokerResponse getPoker = (GetPokerResponse) msgFactory.getResponseMessage(MessagesID.GET_POKER);
                    getPoker.session = player.currentSession;
                    String cards = player.pokersToString();
                    mLogIn.debug("Send:" + cards);
                    mLogIn.debug("String:" + player.pokersToStringNew());
                    getPoker.setNewBacaySuccess(player.id, cards, betInfo);
                    player.currentSession.write(getPoker);
                }
            }
        }
    }

    public NewBaCayPlayer latbai(long uid) throws NewBaCayException {
        if (!isChiaBai) {
            throw new NewBaCayException("Lật bài khi ván chưa bắt đầu");
        }

        NewBaCayPlayer player = findPlayer(uid);
        if (player == null) {
            throw new NewBaCayException(NONE_EXISTS_PLAYER);
        }
        player.latbai = true;
        return player;
    }

    public boolean isAllLatBai() {
        for (int i = 0; i < playings.size(); i++) {
            NewBaCayPlayer player = this.playings.get(i);
            if (!player.isOut && !player.latbai) {
                return false;
            }
        }

        return true;
    }

    //To avoid number of do the same function
    private String getEndValue(long newOwnerId) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(Long.toString(newOwnerId)).append(AIOConstants.SEPERATOR_BYTE_3);

        int playingSize = this.playings.size();

        for (int i = 0; i < playingSize; i++) {
            NewBaCayPlayer player = this.playings.get(i);
            sb.append(Long.toString(player.id)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(player.getWonMoney())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(player.cash)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(player.point)).append(AIOConstants.SEPERATOR_BYTE_1);

            try {
                sb.append(player.pokersToString()).append(AIOConstants.SEPERATOR_BYTE_1);

            } catch (Exception ex) {
                sb.append(NO_CARDS).append(AIOConstants.SEPERATOR_BYTE_1);
            }
//            sb.append((player.currentSession.isExpiredNew() || player.isOut) ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.isOut ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            boolean notEnoughMOney = player.notEnoughMoney();

            sb.append(notEnoughMOney ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_2);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public long endMatch() throws ServerException, JSONException {
//       resetAutoKickOut();
        if (!isUpdatingDB) {
            isUpdatingDB = true;
            long newOwnerId = 0;

            for (int i = 0; i < playings.size(); i++) {
                NewBaCayPlayer player = this.playings.get(i);
                if (player.isOut && player.isChiaBai) {
                    player.isWin = false;
                } else {
                    if (player.isOut && !player.isChiaBai) {
                        player.isWin = false;
                        player.moneyForBet = 0; //this player is out and dont'' bet
                    }
                }
            }

            updateCash();

            //send end message to client
            MessageFactory msgFactory = getNotNullSession().getMessageFactory();
            List<NewBaCayPlayer> lstTenPlayers = new ArrayList<>();
            for (int i = 0; i < this.playings.size(); i++) {
                NewBaCayPlayer player = this.playings.get(i);
                if (player.point == 10 && !player.isOut) {
                    lstTenPlayers.add(player);
                }
            }

            if (lstTenPlayers.size() > 0) {
                NewBaCayPlayer newOwner = lstTenPlayers.get(0);
                for (int i = 1; i < lstTenPlayers.size(); i++) {
                    NewBaCayPlayer player = lstTenPlayers.get(i);
                    if (player.isWin(newOwner)) {
                        newOwner = player;
                    }
                }

                if (newOwner.id != owner.id) {
                    newOwnerId = newOwner.id;
                    owner = newOwner;
                    ownerSession = owner.currentSession;
                }
            }

            if (owner.isOut || owner.notEnoughMoney()) {
                NewBaCayPlayer newOwner = this.ownerQuit();

                if (newOwner != null && newOwnerId == 0) {
                    newOwnerId = newOwner.id;
                }
            }

            String endValue = getEndValue(newOwnerId);
            EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory.getResponseMessage(MessagesID.MATCH_END);
            // set the result
            endMatchRes.setZoneID(ZoneID.NEW_BA_CAY);
            endMatchRes.setSuccess(endValue);

            //       for(int i = 0; i< playings.size(); i++)
            //       {
            //           NewBaCayPlayer player = this.playings.get(i);
            //           if(!player.isOut)
            //           {
            //               
            //                //endMatchRes.newOwner = newOwnerId;
            //                player.currentSession.write(endMatchRes);
            //           }
            //       }
            //       
            //       for(int i = 0; i< this.waitings.size(); i++)
            //       {
            //           NewBaCayPlayer waiter = this.waitings.get(i);
            //           waiter.currentSession.write(endMatchRes);
            //       }
            broadcastMsg(endMatchRes, playings, waitings, owner, true);

            this.isPlaying = false;
            //       this.removeNotEnoughMoney(this.getRoom());
            this.supRemOldVer(newOwnerId, AIOConstants.PROTOCOL_MXH);
            this.resetPlayers();
            return newOwnerId;
        }

        return 0;

    }

    private ArrayList<Poker[]> chia(int numberPlayers) {
        ArrayList<Poker[]> res = new ArrayList<>();
        ArrayList<Integer> currList = getRandomList(numberPlayers);
        for (int i = 0; i < numberPlayers; i++) {
            Poker[] p = new Poker[3];
            for (int j = 0; j < 3; j++) {
                p[j] = new Poker(currList.get(3 * i + j));
            }
            res.add(p);
        }
        return res;
    }

    private ArrayList<Poker[]> chiaCheat(int numberPlayers) {
        ArrayList<Poker[]> res = new ArrayList<>();
        ArrayList<Integer> currList = getRandomListCheat(numberPlayers);
        for (int i = 0; i < numberPlayers; i++) {
            Poker[] p = new Poker[3];
            for (int j = 0; j < 3; j++) {
                p[j] = new Poker(currList.get(3 * i + j));
            }
            res.add(p);
        }
        return res;
    }

    private ArrayList<Integer> getRandomList(int numberPlayers) {
        ArrayList<Integer> res = new ArrayList<>();
        ArrayList<Integer> currList = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < 52; i++) {
            int i1 = i + 1;
            int num = Poker.getNum(i1);
            if (num != 10 && num != 11 && num != 12 & num != 13) {
                currList.add(count++, i1);
            }
        }

        int numCards = numberPlayers * 3;

        for (int i = 0; i < numCards; i++) {
            int index = getRandomNumber(currList, res);
            currList.remove(index);
        }

        return res;
    }

    private ArrayList<Integer> getRandomListCheat(int numberPlayers) {
        ArrayList<Integer> res = new ArrayList<>();
        ArrayList<Integer> currList = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < 52; i++) {
            int i1 = i + 1;
            int num = Poker.getNum(i1);
            if (num != 10 && num != 11 && num != 12 & num != 13) {
                currList.add(count++, i1);
            }
        }

        int numCards = numberPlayers * 3;

        int indexCheat = -1;
        for (int i = 0; i < numberPlayers; i++) {
            if (this.playings.get(i) == botPlayer) {
                indexCheat = i;
                break;
            }
        }

        int notRandInd = -1;//cheat index
        if (indexCheat > -1) {
            notRandInd = indexCheat * 3 + 2;
        }

        for (int i = 0; i < numCards; i++) {
            if (i == notRandInd) {
                NewBaCayPlayer player = new NewBaCayPlayer();
                Poker[] p = new Poker[3];
                p[0] = new Poker(res.get(notRandInd - 2));
                p[1] = new Poker(res.get(notRandInd - 1));
                int countRetry = 0;
                ArrayList<Integer> resTemp = new ArrayList<>();

                int index = getRandomNumber(currList, resTemp);
                p[2] = new Poker(currList.get(index));
                player.setPokers(p);
                player.compute();
                int point = player.point;
                while (countRetry < MAX_RETRY_BOT && player.point < 8) {
                    int nextIndex = getRandomNumber(currList, resTemp);
                    p[2] = new Poker(currList.get(nextIndex));
                    player.compute();
                    if (player.point > point) {
                        index = nextIndex;
                        point = player.point;
                    }

                    countRetry++;
                }

                res.add(currList.get(index));

                currList.remove(index);

            } else {
                int index = getRandomNumber(currList, res);
                currList.remove(index);
            }
        }

        return res;
    }

    private int getRandomNumber(ArrayList<Integer> input, ArrayList<Integer> result) {
        int lengh = input.size() - 1;
        int index = (int) Math.round(Math.random() * lengh);
        result.add(input.get(index));
        return index;
    }

    public boolean containPlayer(long id) {
        for (int i = 0; i < this.playings.size(); i++) {
            if (playings.get(i).id == id) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the waitings
     */
    public List<NewBaCayPlayer> getWaitings() {
        return waitings;
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
            if (removedPlayers.get(i).id == owner.id) {
                isChangeOwner = true;
            }
            playings.remove((NewBaCayPlayer) removedPlayers.get(i));
        }

        if (isChangeOwner) {
            int playingSize = playings.size();
            if (playingSize > 0) {
                NewBaCayPlayer newOwner = playings.get(0);
                for (int i = 1; i < playingSize; i++) {
                    NewBaCayPlayer p = playings.get(i);
                    if (p.cash > newOwner.cash) {
                        newOwner = p;
                    }
                }
                owner = newOwner;
                ownerSession = owner.currentSession;

                resetAutoKickOut();
            }
        }
        this.setOwnerId(owner.id);

        return removedPlayers;
    }

    @SuppressWarnings("unused")
    private boolean updatePairBetOther(UserDB userDb, String desc) throws DBException, SQLException {
        boolean havingMinusBalance = false;
        for (SimplePlayer keyPlayer : pairBetTogether.keySet()) {
            NewBaCayPlayer bcPlayer = findPlayer(keyPlayer.id);
            NewBaCayPlayer competitor = pairBetTogether.get(keyPlayer);

            if (bcPlayer == null) {
                outCodeSB.append("null player when save to db").append(NEW_LINE);
            } else {
                outCodeSB.append("save pair player").append(bcPlayer.username).append(" with competitor ").append(competitor.username).append(NEW_LINE);

                long betMoney = keyPlayer.getBetOther();
                if (bcPlayer.point == 10 || competitor.point == 10) {
                    betMoney *= 2;
                } else if (bcPlayer.point == 11 || competitor.point == 11) {
                    betMoney *= 3;
                }

                if (!bcPlayer.isOut && bcPlayer.isWin(competitor)) {
                    havingMinusBalance = updateUserCash(userDb, bcPlayer, competitor, betMoney, desc);

                } else {
                    havingMinusBalance = updateUserCash(userDb, competitor, bcPlayer, betMoney, desc);
                }

            }
        }
        return havingMinusBalance;
    }

    public void updateCash() {
        Connection con = DBPoolConnection.getConnection();
        try {
            String desc = "BaCay: " + matchID;
            UserDB userDb = new UserDB(con);
            long ownerWonMoney = 0;
            boolean havingMinusBalance = false;
            NewBaCayPlayer ownerTable = (NewBaCayPlayer) owner;
            List<SimplePlayer> lstWinner = new ArrayList<>();
            List<SimplePlayer> lstLoser = new ArrayList<>();

            for (int i = 0; i < playings.size(); i++) {
                NewBaCayPlayer player = this.playings.get(i);
                if (player.id != owner.id) {
                    if (player.point == 10 || ownerTable.point == 10) //10 diem nhan doi
                    {
                        player.moneyForBet *= 2;
                    } else {
                        if (player.point == 11 || ownerTable.point == 11) //sap nhan 3
                        {
                            player.moneyForBet *= 3;
                        }
                    }

                    if (player.moneyForBet == 0) {
                        continue;
                    }

                    if (player.cash < player.moneyForBet) {
                        player.moneyForBet = player.cash;
                    }

                    if (player.isWin) {
                        lstWinner.add(player);
                        
                        // Check for in-game event for winner
                        player.checkEvent(true);
                        //                       havingMinusBalance = havingMinusBalance ||updateUserCash(userDb, player, owner, player.moneyForBet, desc);
                        //                       ownerWonMoney += -player.moneyForBet;
                    } else {
                        lstLoser.add(player);
                        
                        // Check for in-game event for loser
                        player.checkEvent(false);
                        //                        ownerWonMoney += player.moneyForBet * REAL_PERCENT;
                        //                        havingMinusBalance = havingMinusBalance ||updateUserCash(userDb, owner, player, player.moneyForBet, desc);
                    }
                }
            }

            int loserSize = lstLoser.size();
            for (int i = 0; i < loserSize; i++) {
                SimplePlayer loser = lstLoser.get(i);
                ownerWonMoney += loser.moneyForBet;
                loser.cash = userDb.updateUserMoney(loser.moneyForBet, false, loser.id, desc, 0, BACAY_LOG_TYPE);

                if (loser.cash < 0) {
                    ownerWonMoney += loser.cash;
                    outCodeSB.append("having minus balance").append(NEW_LINE);
                    havingMinusBalance = true;
                    loser.moneyForBet += loser.cash;
                    loser.cash = 0;
                }

                loser.setWonMoney(-loser.moneyForBet);
            }

            //            if(ownerWonMoney> ownerLostMoney)
            //            {
            //                
            //            }
            //update owner money
            //            if(ownerWonMoney>0)
            //                userDb.updateUserMoney(ownerWonMoney, true, owner.id, desc, 0, BACAY_LOG_TYPE);
            int winnerSize = lstWinner.size();
            for (int i = 0; i < winnerSize; i++) {
                SimplePlayer winner = lstWinner.get(i);
                if (ownerWonMoney >= winner.moneyForBet) {
                    ownerWonMoney -= winner.moneyForBet;
                    winner.cash = userDb.updateUserMoney((long) (winner.moneyForBet * REAL_GOT_MONEY), true, winner.id, desc, 0, BACAY_LOG_TYPE);
                } else {
                    if (ownerWonMoney > 0) {
                        owner.cash = userDb.updateUserMoney(winner.moneyForBet - ownerWonMoney, false, owner.id, desc, 0, BACAY_LOG_TYPE);

                        if (owner.cash < 0)//avoid hack
                        {
                            winner.moneyForBet += owner.cash;
                        }

                        winner.cash = userDb.updateUserMoney((long) (winner.moneyForBet * REAL_GOT_MONEY), true, winner.id, desc, 0, BACAY_LOG_TYPE);
                        ownerWonMoney -= winner.moneyForBet;
                    } else {
                        ownerWonMoney -= winner.moneyForBet;
                        havingMinusBalance = havingMinusBalance || updateUserCash(userDb, winner, owner, winner.moneyForBet, desc);
                    }
                }

                winner.setWonMoney((long) ((double) winner.moneyForBet * REAL_GOT_MONEY));
                //                if(owner.cash>0)
                //                {
                //                    
                //                    
                //                    ownerWonMoney -= winner.moneyForBet;
                //                    havingMinusBalance = havingMinusBalance || updateUserCash(userDb, winner, owner, winner.moneyForBet, desc);
                //                }
            }

            if (ownerWonMoney > 0) {
                ownerWonMoney *= REAL_GOT_MONEY;
                owner.cash = userDb.updateUserMoney(ownerWonMoney, true, owner.id, desc, 0, BACAY_LOG_TYPE);
            }

            //update 
            owner.setWonMoney(ownerWonMoney);
            //            havingMinusBalance = updatePairBetOther(userDb, desc);

            if (havingMinusBalance) {
                userDb.notMinus();
            }

            int playingSize = this.playings.size();
            //reset bet money for error not enough money
            for (int i = 0; i < playingSize; i++) {
                NewBaCayPlayer player = this.playings.get(i);
                player.moneyForBet = this.firstCashBet;
            }
        } catch (Throwable ex) {
            outCodeSB.append(ex.getMessage()).append(ex.getStackTrace()).append(NEW_LINE);
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
    public void kickTimeout(Room room) {
        try {
            if (!this.isPlaying) {
                if (this.playings.size() > 1) {
                    long now = System.currentTimeMillis();
                    //check user which does nothing when he comes to table
                    boolean isAllJoinReady = true;

                    //Room room = bacayZone.findRoom(matchId);
                    for (int i = 0; i < this.playings.size(); i++) {

                        NewBaCayPlayer bcPlayer = this.playings.get(i);

                        if (!bcPlayer.isReady && bcPlayer.id != owner.id) {
                            isAllJoinReady = false;
                            //does this user over time out
                            if (now - bcPlayer.getLastActivated() > AUTO_KICKOUT_TIMEOUT) {
                                // kich him
                                kickTimeout(room, bcPlayer, 0);
                                this.remove(bcPlayer);
                                bcPlayer.isOut = true;
                                String kickOutMessage = "Auto kick out " + bcPlayer.username;
                                mLogIn.debug(kickOutMessage);
                                outCodeSB.append(kickOutMessage).append(NEW_LINE);
                            }

                        }
                    }

                    if (isAllJoinReady) {
                        //start game
                        if (now - owner.getLastActivated() > AUTO_KICKOUT_OWNER_TIMEOUT) {
                            SimplePlayer oldOwner = owner.clone();

                            NewBaCayPlayer currOwner = findPlayer(owner.id);
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
                mLogIn.error("bacay Kick time out error matchId " + matchID, ex);
                outCodeSB.append("Kick out error").append(NEW_LINE);
                cancel(playings);
                room.allLeft();
                this.destroy();
            } catch (Exception exx) {
            }
        }
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
    public ISession getNotNullSession() {
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            NewBaCayPlayer player = this.playings.get(i);
            if (!player.isOut && player.currentSession != null && player.currentSession.getMessageFactory() != null) {
                return player.currentSession;
            }
        }

        return null;
    }

    @Override
    public void doTimeout() {
        try {
            lastActivated = System.currentTimeMillis();
            outCodeSB.append("do timeout").append(NEW_LINE);
            if (!isChiaBai) {
                try {
                    //Auto chia bai
                    chiabai();
                    this.sendPokers();
                    return;
                } catch (ServerException ex) {
                    mLogIn.error(ex.getMessage(), ex);
                }
            }

//            this.isPlaying = false; // to make sure no error in following code
            //Auto lat bai
            endMatch();
        } catch (Throwable ex) {
            mLogIn.error(ex.getMessage(), ex);
        }
    }

    /**
     * @return the isChiaBai
     */
    public boolean isIsChiaBai() {
        return isChiaBai;
    }

    /**
     * @param isChiaBai the isChiaBai to set
     */
    public void setIsChiaBai(boolean isChiaBai) {
        this.isChiaBai = isChiaBai;
    }
}
