/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.baucuatomca.data;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.*;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.data.*;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.protocol.MessageFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author tuanda
 */
public class BauCuaTomCaTable extends SimpleTable {

    // Danh sach player trong ban
    private List<BauCuaTomCaPlayer> playings = new ArrayList<BauCuaTomCaPlayer>();
    private List<BauCuaTomCaPlayer> waitings = new ArrayList<BauCuaTomCaPlayer>();

    private static final int AUTO_BAU_CUA = 35000;
    private static final int AUTO_CANCEL_SHOWHAND = 20000;
    private static final int TIME_BEETWEEN_TWO_TIMEOUT = 5000;

    private boolean hasShowHand = false;
    private boolean isSentBetInfo = false;
    private boolean isUpdatingDB = false;

    int[] results = new int[3];
    private final static int BAUCUA_LOG_TYPE = 10012;

    public static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BauCuaTomCaTable.class);

    public boolean isAllReady() {
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            BauCuaTomCaPlayer t = this.playings.get(i);
//            mLog.error("---THANGTD START DEBUG---" + t.username + " is ready: " + t.isReady);
            if (t.id != this.owner.id && !t.isReady) {
                return false;
            }
        }

        return true;
    }

    public BauCuaTomCaTable(BauCuaTomCaPlayer owner, long money, long matchId) {
        this.matchID = matchId;
        this.owner = owner;
        this.playings.add(owner);
        this.firstCashBet = money;
        owner.isGiveUp = false;  //owner not bet
        logdir = "Baucuatomca";
    }

    @Override
    public boolean isFullTable() {
        return getTableSize() >= getMaximumPlayer();
    }

    @Override
    public int getTableSize() {
        return playings.size() + getWaitings().size();
    }

    public List<BauCuaTomCaPlayer> getPlayings() {
        return playings;
    }

    public void join(BauCuaTomCaPlayer player) throws BauCuaTomCaException {
        if (isFullTable()) {
            throw new BauCuaTomCaException(Messages.FULL_PLAYER_MSG);
        }

        player.setLastActivated(System.currentTimeMillis());

        if (isPlaying) {
            this.getWaitings().add(player);
            player.isMonitor = true;
        } else {
            playings.add(player);
            player.isMonitor = false;
        }

        outCodeSB.append("player: ").append(player.username).append(" join").append(NEW_LINE);
    }

    @Override
    public void removePlayer(SimplePlayer player) {
        this.remove((BauCuaTomCaPlayer) player);
    }

    public void remove(BauCuaTomCaPlayer player) {
        try {
            if (player != null) {
                BauCuaTomCaPlayer removePlayer;
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

    public int onlinePlayers() {
        int size = this.waitings.size();

        for (int i = 0; i < this.playings.size(); i++) {
            if (!playings.get(i).isOut) {
                size++;
            }
        }

        return size;
    }

    public BauCuaTomCaPlayer ownerQuit() {
        for (int i = 0; i < playings.size(); i++) {
            BauCuaTomCaPlayer p = playings.get(i);
            if (!p.notEnoughMoney() && !p.isOut) {
                owner = p;
                ownerSession = owner.currentSession;
                return p;
            }
        }

        for (int i = 0; i < waitings.size(); i++) {
            BauCuaTomCaPlayer p = waitings.get(i);
            if (!p.notEnoughMoney()) {
                owner = p;
                ownerSession = owner.currentSession;
                return p;
            }
        }
        return null;
    }

    public void kickout(long userKickoutid, KickOutRequest rqKickOut) throws BauCuaTomCaException {
        if (userKickoutid != owner.id) {
            throw new BauCuaTomCaException(Messages.NOT_OWNER_PERSON);
        }

        BauCuaTomCaPlayer player = findPlayer(rqKickOut.uid);
        if (player == null) {
            throw new BauCuaTomCaException(Messages.PLAYER_OUT);
        }

        if (this.isPlaying) {
            throw new BauCuaTomCaException(Messages.PLAYING_TABLE);
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
    }

    public CancelResponse cancel(long uid) throws ServerException, JSONException, BauCuaTomCaException {
        BauCuaTomCaPlayer player = findPlayer(uid);

        if (player == null) {
            throw new BauCuaTomCaException(Messages.NONE_EXISTS_PLAYER);
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

        outCodeSB.append("Table playing :").append(this.isPlaying).append(NEW_LINE);

        if (this.isPlaying) {
            if (player.isMonitor) {
                this.remove(player);
                broadcastMsg(resMatchCancel, playings, waitings, player, false);
            } else {
                if (uid == owner.id) {
                    for (int i = 0; i < this.playings.size(); i++) {
                        BauCuaTomCaPlayer bcPlayer = this.playings.get(i);
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
                BauCuaTomCaPlayer newOwner = this.ownerQuit();

                if (newOwner != null) {
                    resMatchCancel.newOwner = newOwner.id;
                    this.setOwnerId(newOwner.id);
                }
            }
            broadcastMsg(resMatchCancel, playings, waitings, player, false);
        }
//        broadcastMsg(resMatchCancel, playings, waitings, player, false);

        return resMatchCancel;
    }

    @Override
    public BauCuaTomCaPlayer findPlayer(long uid) {
        for (int i = 0; i < this.playings.size(); i++) {
            BauCuaTomCaPlayer player = this.playings.get(i);
            if (player.id == uid) {
                return player;
            }
        }

        for (int i = 0; i < this.waitings.size(); i++) {
            BauCuaTomCaPlayer player = this.waitings.get(i);
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

    public void resetPlayers() {
//		System.out.println("Reset players now!");
        List<BauCuaTomCaPlayer> removedPlayer = new ArrayList<BauCuaTomCaPlayer>();

        for (int i = 0; i < this.playings.size(); i++) {
//            if (playings.get(i).isOut || playings.get(i).notEnoughMoney() || playings.get(i).currentSession.isExpiredNew())
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
        this.waitings = new ArrayList<BauCuaTomCaPlayer>();

        resetAutoKickOut();

        for (int i = 0; i < playings.size(); i++) {
            BauCuaTomCaPlayer player = playings.get(i);
            player.isReady = false;
            player.setWonMoney(0);
            player.resetBet();
            player.isWin = false;
        }

        owner.isReady = true;
        this.isPlaying = false;
        hasShowHand = false;
        isSentBetInfo = false;
        isUpdatingDB = false;
    }

    public void start() {
        resetPlayers();
        lastActivated = System.currentTimeMillis();
        this.isPlaying = true;

        this.isUpdatingDB = false;
        synchronized (waitings) {
            this.playings.addAll(waitings);
            this.waitings = new ArrayList<BauCuaTomCaPlayer>();
        }

        //setup all player to giveup if player does bet, he willn't give up.
        for (int i = 0; i < this.playings.size(); i++) {
            this.playings.get(i).isGiveUp = true;
            this.playings.get(i).setWonMoney(0);
        }

        setCurrentTimeOut(AUTO_BAU_CUA);
    }

    private int getShowHands(int showhandls, long money) {
        if (money == -1) {
            return showhandls + 1;
        }

        return showhandls;
    }

    private long getBetShowHand(long money, long partShowHands) {
        if (money == -1) {
            return -partShowHands;
        }
        return money;
    }

    private long getBetMoney(long money) {
        if (money == -1) {
            return 0;
        }
        return money;
    }

    public void bet(long playerId, long holo, long tom, long cua, long ca, long ga, long huu) throws BauCuaTomCaException, JSONException, ServerException {
        BauCuaTomCaPlayer player = findPlayer(playerId);
        if (player == null) {
            throw new BauCuaTomCaException(Messages.NONE_EXISTS_PLAYER);
        }

        if (player.id == owner.id) {
            outCodeSB.append(Messages.NOT_ALLOW_OWNER).append(NEW_LINE);
            throw new BauCuaTomCaException(Messages.NOT_ALLOW_OWNER);
        }

        long maxNormalBet = getBetMoney(holo);
        maxNormalBet += getBetMoney(tom);
        maxNormalBet += getBetMoney(cua);
        maxNormalBet += getBetMoney(ca);
        maxNormalBet += getBetMoney(ga);
        maxNormalBet += getBetMoney(huu);

        if (player.cash < maxNormalBet) {
            outCodeSB.append(Messages.NOT_ALLOW_USER_MONEY_BET).append(NEW_LINE);
            throw new BauCuaTomCaException(Messages.NOT_ALLOW_USER_MONEY_BET);
        }

        outCodeSB.append("player: ").append(player.username).append(" bet bau: ").append(holo).
                append(" bet tom: ").append(tom).append(" bet cua: ").append(cua).
                append(" bet ca: ").append(ca).append(" bet holo: ").append(ga).
                append(" bet huou: ").append(huu).append(NEW_LINE);

        //find number show hands
        int numShowHands = 0;
        numShowHands = getShowHands(numShowHands, holo);
        numShowHands = getShowHands(numShowHands, tom);
        numShowHands = getShowHands(numShowHands, cua);
        numShowHands = getShowHands(numShowHands, ca);
        numShowHands = getShowHands(numShowHands, ga);
        numShowHands = getShowHands(numShowHands, huu);

        long partShowHand = 0;

        if (numShowHands > 0) {
            long moneyShowHand = owner.cash / (this.playings.size() - 1);
            // moneyShowHand/=3; //multiple 3 if has the same result in 3 sides
            partShowHand = (player.cash - maxNormalBet) / numShowHands;
            if (moneyShowHand < partShowHand) {
                partShowHand = moneyShowHand;
            }

            player.setShowHand(true);
            player.setMoneyShowHand(partShowHand);
            outCodeSB.append("Money show hand").append(partShowHand).append(NEW_LINE);
        }

        holo = getBetShowHand(holo, partShowHand);
        tom = getBetShowHand(tom, partShowHand);
        cua = getBetShowHand(cua, partShowHand);
        ca = getBetShowHand(ca, partShowHand);
        ga = getBetShowHand(ga, partShowHand);
        huu = getBetShowHand(huu, partShowHand);

        player.setHolo(holo);
        player.setTom(tom);
        player.setCua(cua);
        player.setCa(ca);
        player.setGa(ga);
        player.setHuou(huu);

        player.isGiveUp = false; //this user did bet
        sendBets();
//        checkAllBetPlayers();
    }

    private JSONObject getShowHandInfo(BauCuaTomCaPlayer player) throws JSONException {
        JSONObject jO = new JSONObject();
        jO.put("uid", player.id);
        if (player.getHolo() < 0) {
            jO.put("money", -player.getHolo());
            jO.put("cua", "holo");
        } else if (player.getTom() < 0) {
            jO.put("money", -player.getTom());
            jO.put("cua", "tom");
        } else if (player.getCua() < 0) {
            jO.put("money", -player.getCua());
            jO.put("cua", "cua");
        } else if (player.getCa() < 0) {
            jO.put("money", -player.getCa());
            jO.put("cua", "ca");
        } else if (player.getGa() < 0) {
            jO.put("money", -player.getGa());
            jO.put("cua", "ga");
        } else if (player.getHuou() < 0) {
            jO.put("money", -player.getHuou());
            jO.put("cua", "huou");
        }

        return jO;
    }

    private JSONObject getUserBetInfo(BauCuaTomCaPlayer player) throws JSONException {
        JSONObject jO = new JSONObject();
        jO.put("uid", player.id);
        jO.put("holo", player.getHolo());
        jO.put("tom", player.getTom());
        jO.put("cua", player.getCua());
        jO.put("ca", player.getCa());
        jO.put("ga", player.getGa());
        jO.put("huou", player.getHuou());
        long money = player.getHolo() + player.getTom() + player.getCua() + player.getCa() + player.getHuou() + player.getGa();
        jO.put("money", money);
        return jO;
    }

    private JSONObject getShowHandJSonObject() throws JSONException {
        JSONObject encodingObj = new JSONObject();
        encodingObj.put("mid", MessagesID.SHOW_HAND);
        encodingObj.put("code", ResponseCode.SUCCESS);

        JSONArray playersArr = new JSONArray();

        for (int i = 0; i < this.playings.size(); i++) {
            BauCuaTomCaPlayer player = this.playings.get(i);
            if (!player.isOut && player.id != owner.id && player.isShowHand()) {
                playersArr.put(getShowHandInfo(player));
                hasShowHand = true;
            }
        }
        encodingObj.put("playings", playersArr);

        return encodingObj;
    }

    public boolean checkAllBetPlayers() throws JSONException, ServerException {
        for (int i = 0; i < this.playings.size(); i++) {
            BauCuaTomCaPlayer player = playings.get(i);
            if (player.isGiveUp && !player.isOut && player.id != owner.id) // this player still doesn't bet
            {
                return false;
            }
        }

        //send showhand to owner if there 's someone who bet all
        JSONObject showHandObj = getShowHandJSonObject();

        if (hasShowHand) {
            //write info to owner;
            outCodeSB.append("Send show hand to owner").append(NEW_LINE);

            setCurrentTimeOut(AUTO_CANCEL_SHOWHAND);
            setLastActivated(System.currentTimeMillis());
            MessageFactory msgFactory = getNotNullSession().getMessageFactory();
            ShowHandResponse resShowHand = (ShowHandResponse) msgFactory.getResponseMessage(MessagesID.SHOW_HAND);

            resShowHand.setSuccess(showHandObj);

            owner.currentSession.write(resShowHand);
        } else {
            sendBets();
        }

        return true;
    }

    private String converstResultsToString() {
        StringBuilder sb = new StringBuilder();
        int lastElement = results.length - 1;
        for (int i = 0; i < results.length; i++) {
            sb.append(results[i]);
            if (i != lastElement) {
                sb.append("#");
            }
        }

        return sb.toString();

    }

    public void cancelShowHand(List<BauCuaTomCaPlayer> lstBetPlayers) throws ServerException, BauCuaTomCaException {
        outCodeSB.append("cancel show hand").append(NEW_LINE);
        //send end message to client
        MessageFactory msgFactory = getNotNullSession().getMessageFactory();

        if (!this.isPlaying || isSentBetInfo) {
            throw new BauCuaTomCaException(Messages.UNSUCCESSFULLY_CANCEL_SHOW_HAND);
        }

        for (int i = 0; i < this.playings.size(); i++) {
            BauCuaTomCaPlayer player = this.playings.get(i);

            if (player.id != owner.id) {
                for (int j = 0; j < lstBetPlayers.size(); j++) {
                    BauCuaTomCaPlayer betPlayer = lstBetPlayers.get(j);

                    if (player.id == betPlayer.id && !player.isOut) {
                        StringBuilder info = new StringBuilder();
                        info.append("Chủ bàn hủy tố tất cả ở");
                        if (betPlayer.getHolo() < 0 && player.getHolo() < 0) {
                            info.append(" cửa hồ lô");
                            player.setHolo(firstCashBet);
                        }
                        if (betPlayer.getTom() < 0 && player.getTom() < 0) {
                            info.append(" cửa tôm");
                            player.setTom(firstCashBet);
                        }
                        if (betPlayer.getCua() < 0 && player.getCua() < 0) {
                            info.append(" cửa cua");
                            player.setCua(firstCashBet);
                        }
                        if (betPlayer.getCa() < 0 && player.getCa() < 0) {
                            info.append(" cửa cá");
                            player.setCa(firstCashBet);
                        }
                        if (betPlayer.getGa() < 0 && player.getGa() < 0) {
                            info.append(" cửa gà");
                            player.setGa(firstCashBet);
                        }
                        if (betPlayer.getHuou() < 0 && player.getHuou() < 0) {
                            info.append(" cửa hươu");
                            player.setHuou(firstCashBet);
                        }

                        CancelShowHandResponse cancelShowHandRes = (CancelShowHandResponse) msgFactory.getResponseMessage(MessagesID.CANCEL_SHOW_HAND);
                        // set the result

                        cancelShowHandRes.setSuccess(info.toString());

                        player.currentSession.write(cancelShowHandRes);
                        break;
                    }
                }
            }
        }
    }

    public void sendBets() throws ServerException, JSONException {
        isSentBetInfo = false;

        int sentBetPlayers = 0;
        // Check all player did bet or not
        for (int i = 0; i < this.playings.size(); i++) {
            BauCuaTomCaPlayer player = playings.get(i);
            mLog.debug("---THANGTD BAU CUA DEBUG--- Player " + player.username + " give up = " + player.isGiveUp);
            if (!player.isOut && player.id != owner.id) {
                if (!player.isGiveUp)
                    sentBetPlayers++;
            }
        }
        
        if (sentBetPlayers == this.playings.size() - 1)
            isSentBetInfo = true;

        mLog.debug("---THANGTD BAU CUA DEBUG--- SentBet Players = " + sentBetPlayers + " playing size = " + (this.playings.size() - 1));
        mLog.debug("---THANGTD BAU CUA DEBUG--- isSentBetInfo = " + isSentBetInfo);
        
        //write to log
        outCodeSB.append(converstResultsToString()).append(NEW_LINE);

        MessageFactory msgFactory = getNotNullSession().getMessageFactory();

        long holoMoney = 0;
        long tomMoney = 0;
        long cuaMoney = 0;
        long caMoney = 0;
        long gaMoney = 0;
        long huouMoney = 0;

        JSONArray betInfo = new JSONArray();
        for (int i = 0; i < this.playings.size(); i++) {
            BauCuaTomCaPlayer player = this.playings.get(i);
            if (!player.isOut && player.id != owner.id) {
                //convert to real bet>0
                holoMoney += Math.abs(player.getHolo());
                cuaMoney += Math.abs(player.getCua());
                tomMoney += Math.abs(player.getTom());
                caMoney += Math.abs(player.getCa());
                gaMoney += Math.abs(player.getGa());
                huouMoney += Math.abs(player.getHuou());
//                player.setHolo(Math.abs(player.getHolo()));
//                player.setTom(Math.abs(player.getTom()));
//                player.setCua(Math.abs(player.getCua()));
//                player.setCa(Math.abs(player.getCa()));
//                player.setGa(Math.abs(player.getGa()));
//                player.setHuou(Math.abs(player.getHuou()));
//                betInfo.put(getUserBetInfo(player));
            }
        }

        JSONObject jO = new JSONObject();
        jO.put("holo", holoMoney);
        jO.put("cua", cuaMoney);
        jO.put("tom", tomMoney);
        jO.put("ca", caMoney);
        jO.put("ga", gaMoney);
        jO.put("huou", huouMoney);
        betInfo.put(jO);

//       outCodeSB.append("Bet info " +  betInfo.toString());
        for (int i = 0; i < this.playings.size(); i++) {
            BauCuaTomCaPlayer player = this.playings.get(i);
            if (!player.isOut) {
                BetResponse betRes = (BetResponse) msgFactory.getResponseMessage(MessagesID.BET);
                betRes.setSuccess(ResponseCode.SUCCESS, player.id, betInfo, ZoneID.BAU_CUA_TOM_CA);
                betRes.setSession(player.currentSession);
                player.currentSession.write(betRes);
            }
        }
        
        if (isSentBetInfo) {
            lastActivated = System.currentTimeMillis();
            setCurrentTimeOut(TIME_BEETWEEN_TWO_TIMEOUT);
            
            mLog.debug("begin Sendbet and create end results");
            outCodeSB.append("begin Sendbet and create end results");

            generateResults();
            outCodeSB.append("Results:");
        }
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
     * @return the waiting
     */
    public List<BauCuaTomCaPlayer> getWaitings() {
        return waitings;
    }

    @Override
    protected List<SimplePlayer> removeNotEnoughMoney() {
        List<SimplePlayer> removedPlayers = new ArrayList<SimplePlayer>();
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
            playings.remove(removedPlayers.get(i));
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

    public void updateCash() throws ServerException {
        Connection con = DBPoolConnection.getConnection();
        try {
            String desc = "BauCua: " + matchID;
            UserDB userDb = new UserDB(con);
            long ownerWonMoney = 0;

            boolean havingMinusBalance = false;

            List<BauCuaTomCaPlayer> lstWinner = new ArrayList<BauCuaTomCaPlayer>();
            List<BauCuaTomCaPlayer> lstLoser = new ArrayList<BauCuaTomCaPlayer>();

            for (int i = 0; i < playings.size(); i++) {
                BauCuaTomCaPlayer player = this.playings.get(i);
                if (player.id != owner.id) {
                    if (player.isWin) {
                        lstWinner.add(player);
                        
                        // Check for in-game event for winner
                        player.checkEvent(true);
                        
//                       havingMinusBalance = havingMinusBalance ||updateUserCash(userDb, player, owner, player.getMoneyForBetDouble(), desc);
//                       ownerWonMoney += -player.getMoneyForBetDouble();
                    } else {
                        lstLoser.add(player);
                        
                        // Check for in-game event for loser
                        player.checkEvent(false);
                    }
                }
            }

            // Cập nhật tiền của những người thua cuộc - ThangTD
            int loserSize = lstLoser.size();
            for (int i = 0; i < loserSize; i++) {
                BauCuaTomCaPlayer loser = lstLoser.get(i);
                ownerWonMoney -= loser.getMoneyForBetDouble();
                loser.cash = userDb.updateUserMoney(-(long) loser.getMoneyForBetDouble(), false, loser.id, desc, 0, BAUCUA_LOG_TYPE);

                if (loser.cash < 0) {
                    ownerWonMoney += loser.cash;
                    outCodeSB.append("having minus balance").append(NEW_LINE);
                    havingMinusBalance = true;
                    loser.setMoneyForBetDouble(loser.getMoneyForBetDouble() + loser.cash);
                    loser.cash = 0;
                }

                loser.setWonMoney((long) loser.getMoneyForBetDouble());
            }

            int winnerSize = lstWinner.size();
            for (int i = 0; i < winnerSize; i++) {
                BauCuaTomCaPlayer winner = lstWinner.get(i);
                if (ownerWonMoney >= winner.getMoneyForBetDouble()) {
                    ownerWonMoney -= winner.getMoneyForBetDouble();
                    winner.cash = userDb.updateUserMoney((long) (winner.getMoneyForBetDouble() * REAL_GOT_MONEY), true, winner.id, desc, 0, BAUCUA_LOG_TYPE);
                } else {
                    if (ownerWonMoney > 0) {
                        owner.cash = userDb.updateUserMoney((long) (winner.getMoneyForBetDouble() - ownerWonMoney), false, owner.id, desc, 0, BAUCUA_LOG_TYPE);

                        if (owner.cash < 0)//avoid hack
                        {
                            winner.setMoneyForBetDouble(winner.getMoneyForBetDouble() + owner.cash);
                        }

                        winner.cash = userDb.updateUserMoney((long) (winner.getMoneyForBetDouble() * REAL_GOT_MONEY), true, winner.id, desc, 0, BAUCUA_LOG_TYPE);
                        ownerWonMoney -= winner.getMoneyForBetDouble();
                    } else {
                        ownerWonMoney -= winner.getMoneyForBetDouble();
                        havingMinusBalance = havingMinusBalance || updateUserCash(userDb, winner, owner, winner.getMoneyForBetDouble(), desc);
                    }
                }

                winner.setWonMoney((long) (winner.getMoneyForBetDouble() * REAL_GOT_MONEY));
//                if(owner.cash>0)
//                {
//                    ownerWonMoney -= winner.moneyForBet;
//                    havingMinusBalance = havingMinusBalance || updateUserCash(userDb, winner, owner, winner.moneyForBet, desc);
//                }
            }

            if (ownerWonMoney > 0) {
                ownerWonMoney *= REAL_GOT_MONEY;
                owner.cash = userDb.updateUserMoney(ownerWonMoney, true, owner.id, desc, 0, BAUCUA_LOG_TYPE);
            }

            //update 
            owner.setWonMoney(ownerWonMoney);
//            for(int i=0; i< playings.size(); i++)
//            {
//                BauCuaTomCaPlayer player = this.playings.get(i);
//                if(player.id != owner.id)
//                {
//                    if(player.isWin)
//                    {
//                       havingMinusBalance = havingMinusBalance ||updateUserCash(userDb, player, owner, player.getMoneyForBetDouble(), desc);
//                       ownerWonMoney += -player.getMoneyForBetDouble();
//                    }
//                    else
//                    {
//                        ownerWonMoney += (long)(player.getMoneyForBetDouble() * (-1) * REAL_PERCENT);
//                        havingMinusBalance = havingMinusBalance ||updateUserCash(userDb, owner, player, -player.getMoneyForBetDouble(), desc);
//                        
//                    }
//                }
//            }
//            
//            owner.setWonMoney(ownerWonMoney);

            if (havingMinusBalance) {
                userDb.notMinus();
            }
        } catch (DBException ex) {
            outCodeSB.append(ex.getMessage()).append(ex.getStackTrace()).append(NEW_LINE);
        } catch (SQLException ex) {
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

    private String getEndValue(long newOwnerId) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(Long.toString(newOwnerId)).append(AIOConstants.SEPERATOR_BYTE_1);

        String type = "";
        if (isSentBetInfo) {
            type = converstResultsToString();
        }
        sb.append(type).append(AIOConstants.SEPERATOR_BYTE_3);

        int playingSize = this.playings.size();

        for (int i = 0; i < playingSize; i++) {
            BauCuaTomCaPlayer player = this.playings.get(i);
            sb.append(Long.toString(player.id)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(player.getWonMoney())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(player.cash)).append(AIOConstants.SEPERATOR_BYTE_1);
            //sb.append((player.currentSession.isExpiredNew() || player.isOut) ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.isOut ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            boolean notEnoughMoney = player.notEnoughMoney();
            sb.append(notEnoughMoney ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_2);
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    private JSONObject getEndJSonObject(long newOwnerId) throws JSONException {
        JSONObject encodingObj = new JSONObject();
        encodingObj.put("mid", MessagesID.MATCH_END);
        encodingObj.put("code", ResponseCode.SUCCESS);

        if (newOwnerId > 0) {
            encodingObj.put("newOwner", newOwnerId);
        }

        if (isSentBetInfo) {
            encodingObj.put("types", converstResultsToString());
        }

        JSONArray playersArr = new JSONArray();

        for (int i = 0; i < this.playings.size(); i++) {
            BauCuaTomCaPlayer player = this.playings.get(i);
            JSONObject jO = new JSONObject();
            jO.put("uid", player.id);
            jO.put("wonMOney", player.getWonMoney());
            jO.put("cash", player.cash);
            jO.put("isOut", player.isOut);
            playersArr.put(jO);
        }
        encodingObj.put("playings", playersArr);

        JSONArray waitingsArr = new JSONArray();
        for (int i = 0; i < this.waitings.size(); i++) {
            BauCuaTomCaPlayer p = this.waitings.get(i);
            JSONObject jO = new JSONObject();
            jO.put("uid", p.id);
            jO.put("cash", p.cash);
            waitingsArr.put(jO);
        }

        encodingObj.put("waitings", waitingsArr);

        return encodingObj;
    }

    public long endMatch() throws ServerException, JSONException {
        if (!isUpdatingDB) {
            isUpdatingDB = true;
            long newOwnerId = 0;

            for (int i = 0; i < playings.size(); i++) {
                BauCuaTomCaPlayer player = this.playings.get(i);
                if (player.isOut) {
                    player.isWin = false;
                    player.setMoneyForBetDouble(-firstCashBet);
                }
            }

            for (int i = 0; i < this.playings.size(); i++) {
                BauCuaTomCaPlayer player = this.playings.get(i);
                if (player.id == owner.id) {
                    continue; // don't compare with owner
                }

                if (!player.isOut) {
                    if (owner.isOut && !this.isSentBetInfo) {
                        player.isWin = true;
                        player.setMoneyForBetDouble(firstCashBet);
                    } else {
                        player.calculateBetMoney(results, firstCashBet);
                    }
                }

                outCodeSB.append("--player: ").append(player.username).append(" wonMOney: ").append(player.getMoneyForBetDouble()).append(NEW_LINE);

                if (player.getMoneyForBetDouble() > 0) {
                    player.isWin = true;
                }
            }

            updateCash();

            if (owner.isOut || owner.notEnoughMoney()) {
                BauCuaTomCaPlayer newOwner = this.ownerQuit();
                if (newOwner != null) {
                    newOwnerId = newOwner.id;
                }
            }

            String endValue = getEndValue(newOwnerId);

            MessageFactory msgFactory = getNotNullSession().getMessageFactory();
            EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory.getResponseMessage(MessagesID.MATCH_END);
            // set the result
            endMatchRes.setZoneID(ZoneID.BAU_CUA_TOM_CA);
            endMatchRes.setSuccess(endValue);

            //send end message to client
            broadcastMsg(endMatchRes, playings, waitings, owner, true);
            this.isPlaying = false;

            /*for(int i = 0; i< playings.size(); i++)
             {
             BauCuaTomCaPlayer player = this.playings.get(i);
             if(!player.isOut)
             {

             //endMatchRes.newOwner = newOwnerId;
             player.currentSession.write(endMatchRes);
             }
             }

             for(int i = 0; i< this.waitings.size(); i++)
             {
             BauCuaTomCaPlayer waiter = this.waitings.get(i);
             waiter.currentSession.write(endMatchRes);
             }*/
            //     this.removeNotEnoughMoney(this.getRoom());
            this.supRemOldVer(newOwnerId, AIOConstants.PROTOCOL_MXH);
            this.resetPlayers();
            return newOwnerId;
        }

        return 0;
    }

    public void generateResults() {
        for (int i = 0; i < results.length; i++) {
            Random rand = new Random((i + 1) * System.currentTimeMillis());
            results[i] = Math.abs((int) (rand.nextLong() % 6));
        }
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
                        BauCuaTomCaPlayer bcPlayer = this.playings.get(i);
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

                            BauCuaTomCaPlayer currOwner = findPlayer(owner.id);
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
                mLog.error("xocdia Kick time out error " + matchID, ex);
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
            BauCuaTomCaPlayer player = this.playings.get(i);
            if (!player.isOut && player.currentSession != null && player.currentSession.getMessageFactory() != null) {
                return player.currentSession;
            }
        }

        return null;
    }

    public void doTimeout() {
        outCodeSB.append("do timeout").append(NEW_LINE);
        mLog.debug("bau tom cua do timeout");
        lastActivated = System.currentTimeMillis();

        try {
            endMatch();
        } catch (ServerException ex) {
            outCodeSB.append("error do time end match ").append(ex.getMessage()).append(NEW_LINE);
            mLog.error("error do time out  end match matchId: " + matchID, ex);
        } catch (JSONException ex) {
            outCodeSB.append("error do time end match ").append(ex.getMessage()).append(NEW_LINE);
            mLog.error("error do time out  end match matchId: " + matchID, ex);
        }
        
//        if (isSentBetInfo) {
//            try {
//                endMatch();
//            } catch (ServerException ex) {
//                outCodeSB.append("error do time end match ").append(ex.getMessage()).append(NEW_LINE);
//                mLog.error("error do time out  end match matchId: " + matchID, ex);
//            } catch (JSONException ex) {
//                outCodeSB.append("error do time end match ").append(ex.getMessage()).append(NEW_LINE);
//                mLog.error("error do time out  end match matchId: " + matchID, ex);
//            }
//        } else {
//            try {
//                sendBets();
//            } catch (ServerException ex) {
//                outCodeSB.append("error do time out send bets ").append(ex.getMessage()).append(NEW_LINE);
//                mLog.error("error do time out send bau cua bets matchId: " + matchID, ex);
//
//            } catch (JSONException ex) {
//                outCodeSB.append("error do time out send bets  matchId: ").append(matchID).append(ex.getMessage()).append(NEW_LINE);
//                mLog.error("error do time out send bau cua bets  matchId: " + matchID, ex);
//            }
//        }
    }
}
