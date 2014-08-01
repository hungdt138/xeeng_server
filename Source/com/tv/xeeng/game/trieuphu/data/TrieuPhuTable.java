package com.tv.xeeng.game.trieuphu.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelResponse;
import com.tv.xeeng.base.protocol.messages.EndMatchResponse;
import com.tv.xeeng.base.protocol.messages.KickOutRequest;
import com.tv.xeeng.base.protocol.messages.OutResponse;
import com.tv.xeeng.base.protocol.messages.SendAdvResponse;
import com.tv.xeeng.base.protocol.messages.SendQuestionResponse;
import com.tv.xeeng.base.protocol.messages.TrieuPhuAnswerResponse;
import com.tv.xeeng.base.protocol.messages.TrieuPhuHelpResponse;
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
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.protocol.MessageFactory;

import java.util.AbstractList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrieuPhuTable extends SimpleTable {

    public ArrayList<TrieuPhuPlayer> playings;
    public ArrayList<TrieuPhuPlayer> waitings;
    private ArrayList<Integer> excepts;
    public boolean isSingleMode = true;
    private ArrayList<Question> question = new ArrayList<Question>(15);
    private int MAX_PLAYER = 4;
    public long winID;
    private int timeOut = 30000;
    private int SLEEP_BEF_NEW_QUES = 3000;
    private int BONUS_TIME_OUT = 10000;
    private int point;
    private TrieuPhuPlayer winner;
    private boolean isSentAnswer = true;
    private boolean isBonusTimeout = false;
    private boolean isNoOneWin = true;
    // private Room room;
    // private GameStat stat;

    @Override
    public boolean isFullTable() {
        return playings.size() + waitings.size() >= getMaximumPlayer();
    }

    @Override
    protected void join(SimplePlayer p) throws BusinessException {
        if (isPlaying) {
            p.isMonitor = true;
            waitings.add((TrieuPhuPlayer) p);
        } else {
            p.isMonitor = false;
            this.playings.add((TrieuPhuPlayer) p);
        }
        outCodeSB.append("player: ").append(p.username).append(" join").append(NEW_LINE);
    }

    @Override
    public List<? extends SimplePlayer> getNewPlayings() {
        return playings;
    }

    @Override
    public List<? extends SimplePlayer> getNewWaitings() {
        return waitings;
    }

    public TrieuPhuPlayer findPlayer(long uid) {
        for (int i = 0; i < playings.size(); i++) {
            if (playings.get(i).id == uid) {
                return playings.get(i);
            }
        }
        for (int i = 0; i < waitings.size(); i++) {
            if (waitings.get(i).id == uid) {
                return waitings.get(i);
            }
        }
        return null;
        // throw new BusinessException("Khong tim thay nguoi choi: "+ uid);
    }

    private void removePlaying(long uid) {
        for (int i = 0; i < playings.size(); i++) {
            if (playings.get(i).id == uid) {
                playings.remove(i);
                return;
            }
        }
    }

    private void removeWaiting(long uid) {
        for (int i = 0; i < waitings.size(); i++) {
            if (waitings.get(i).id == uid) {
                waitings.remove(i);
                return;
            }
        }
    }

    public void remove(TrieuPhuPlayer player) {
        try {

            if (player != null) {
                TrieuPhuPlayer removePlayer;
                outCodeSB.append("Remove player ").append(player.id).append(NEW_LINE);
                int playingSize = this.playings.size();
                for (int i = 0; i < playingSize; i++) {
                    removePlayer = this.playings.get(i);
                    if (removePlayer.id == player.id) {
                        this.playings.remove(removePlayer);
                        return;
                    }
                }

                int waitingSize = this.waitings.size();

                for (int i = 0; i < waitingSize; i++) {
                    removePlayer = this.waitings.get(i);
                    if (removePlayer.id == player.id) {
                        this.waitings.remove(removePlayer);
                        return;
                    }
                }

            }
        } catch (Exception e) {
            outCodeSB.append("Remove player !!!error ").append(player.id).append(NEW_LINE);
            mLog.error(e.getMessage() + " remove player: ", e.getStackTrace());

        }
    }

    public CancelResponse cancel(long uid) throws ServerException, JSONException, BusinessException {
        TrieuPhuPlayer player = findPlayer(uid);
        if (player == null) {
            throw new BusinessException(NONE_EXISTS_PLAYER);
        }

        outCodeSB.append("cancel player:").append(player.username).append(NEW_LINE);

        MessageFactory msgFactory = getNotNullSession().getMessageFactory();
        CancelResponse resMatchCancel = (CancelResponse) msgFactory.getResponseMessage(MessagesID.MATCH_CANCEL);
        resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);

        player.isOut = true;

        if (this.onlinePlayers() == 0) {
            return resMatchCancel;
        }

        if (this.isPlaying) {
            if (player.isMonitor) {
                this.remove(player);
                broadcastMsg(resMatchCancel, playings, waitings, player, false);
            } else {

//                /*       1 cai het luon //for event
//                        if(this.realPlaying() == 1)//there are two players. One player(not owner) leave tables
//                        {
//                            player.isWin = false;
//                            this.endMatch();
//
//                        }
//                        else
//                        {
                if (isAllAnswer()) {
                    doTimeout();
                }
//                        }

//                         * 
//                         */

                /*
                 //not for event
                 if(this.realPlaying() == 1)//there are two players. One player(not owner) leave tables
                 {
                 player.isWin = false;
                 this.endMatch();

                 }
                 else
                 {
                 if(isAllAnswer())
                 {
                 doTimeout();
                 }
                 }
                 */
            }

        } else {
            this.remove(player);
            if (uid == owner.id) {
                TrieuPhuPlayer newOwner = this.ownerQuit();

                if (newOwner != null) {
                    resMatchCancel.newOwner = newOwner.id;
                    this.setOwnerId(newOwner.id);
                }
            }

            broadcastMsg(resMatchCancel, playings, waitings, player, false);
        }

        return resMatchCancel;
    }

    /*
     * a = 0,1,2,3,4 0: xin dung cuoc choi
     */
    public boolean answer(long uid, String answer) throws BusinessException {
        TrieuPhuPlayer p = findPlayer(uid);
        if (p == null) {
            throw new BusinessException("Không tìm thấy người chơi: " + uid);
        }

        int a = Integer.parseInt(answer);
        // System.out.println("a =" +p.isStop);
        if (a == 0) {
            p.xitop();
            return false;
        } else {
            // System.out.println("best =" +question.get(point - 1).best);
            if (a == 5) //bot answer true
            {
                UserDB db = new UserDB();
                if (db.checkBotUser(uid)) {
                    a = question.get(point - 1).best;
                }
            }

            boolean isRightAnswer = a == question.get(point - 1).best;

            p.answer(isRightAnswer, System.currentTimeMillis() - lastActivated, a);
            return isRightAnswer;
        }
    }

    public void checkFinish() throws ServerException {
        // if (isAllAnswer()) {
        // mLog.debug("finish:"+isTableStop());
        // mLog.debug(""+allOutJustOne());
        if (isTableStop() || allOutJustOne()) {
            isPlaying = false;
        }
        // System.out.println("Table :" + isPlaying);
        if (!isPlaying || point == 15) {
            isPlaying = false;
            endMatch();
        } else {

        }
        // }

    }

    private boolean allOutJustOne() {
        int t = playings.size();
        for (int i = 0; i < playings.size(); i++) {
            t = (playings.get(i).isOut ? t - 1 : t);
        }
        return (!isSingleMode && (t == 0));
    }

    /*
     * @SuppressWarnings("unchecked") private void sendResult() { stat =
     * GameStat.SEND_RESULT; lastActivated = System.currentTimeMillis(); if
     * (point > 0) { // not first round TrieuPhuAnswerResponse q =
     * (TrieuPhuAnswerResponse) owner.currentSession
     * .getMessageFactory().getResponseMessage( MessagesID.TRIEUPHU_ANSWER);
     * q.setSuccess((ArrayList<TrieuPhuPlayer>) playings.clone());
     * broadcastMsg(q, playings, waitings, owner, true); } lastActivated =
     * System.currentTimeMillis(); setCurrentTimeOut(3000); }
     */
    public void sendResultForViewer(ISession sess) {
        try {
            SendQuestionResponse quest = (SendQuestionResponse) owner.currentSession.getMessageFactory().getResponseMessage(MessagesID.SENDQUESTION);
            Question q = question.get(point - 1);
            quest.setSuccess(q.detail, q.answers, q.best, point, playings, ZoneID.AILATRIEUPHU);
            sess.write(quest);
        } catch (Throwable e) {
            mLog.error(e.getMessage());
        }
    }

    private void sendNewRound() {
        // stat = GameStat.SEND_QUESTION;
        lastActivated = System.currentTimeMillis();
        isBonusTimeout = false;
        point++;
        for (int i = 0; i < playings.size(); i++) {
            playings.get(i).getNewRound();
        }
        // send next question for client
        ISession notNullSession = getNotNullSession();

        SendQuestionResponse quest = (SendQuestionResponse) notNullSession.getMessageFactory().getResponseMessage(MessagesID.SENDQUESTION);

        Question q = question.get(point - 1);
        quest.setSuccess(q.detail, q.answers, q.best, point, playings, ZoneID.AILATRIEUPHU);
        broadcastMsg(quest, playings, waitings, owner, true);

        if (!excepts.contains(q.id)) {
            excepts.add(q.id);
        }

        isSentAnswer = false;
        /*
         * if (point > 5) timeOut = 30000; else timeOut = 20000;
         */
        sendSuper(q);

        setCurrentTimeOut(timeOut);
        lastActivated = System.currentTimeMillis();
    }

    private void sendSuper(Question q) {
        MessageFactory msgFactory = getNotNullSession().getMessageFactory();
        SendAdvResponse advRes = (SendAdvResponse) msgFactory.getResponseMessage(MessagesID.SEND_ADV);

        //advRes.session = aSession;
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(1)).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(q.best);

        advRes.setSuccess(sb.toString());
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            TrieuPhuPlayer p = playings.get(i);
            if (p.isSuper && p.currentSession != null) {
                try {
                    advRes.session = p.currentSession;
                    p.currentSession.write(advRes);
                } catch (Throwable e) {
                }
            }
        }
    }

    private void sentRoundResult() {
        // stat = GameStat.SEND_QUESTION;
        lastActivated = System.currentTimeMillis();

        int playingSize = this.playings.size();
        StringBuilder sb = new StringBuilder();
        Question q = question.get(point - 1);
        sb.append(Integer.toString(q.best)).append(AIOConstants.SEPERATOR_BYTE_3);

        for (int i = 0; i < playingSize; i++) {
            TrieuPhuPlayer player = this.playings.get(i);
            sb.append(Long.toString(player.id)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(player.currentAnswerPos)).append(AIOConstants.SEPERATOR_BYTE_2);
        }

        sb.deleteCharAt(sb.length() - 1);
        // send next question for client
        ISession notNullSession = getNotNullSession();

        TrieuPhuAnswerResponse quest = (TrieuPhuAnswerResponse) notNullSession.getMessageFactory().getResponseMessage(MessagesID.TRIEU_PHU_ANSWER);
        quest.mCode = ResponseCode.SUCCESS;
        quest.value = sb.toString();
        broadcastMsg(quest, playings, waitings, owner, true);

        if (!excepts.contains(q.id)) {
            excepts.add(q.id);
        }

        /*
         * if (point > 5) timeOut = 30000; else timeOut = 20000;
         */
        setCurrentTimeOut(SLEEP_BEF_NEW_QUES);
        lastActivated = System.currentTimeMillis();
    }

    private boolean isTableStop() {
        int j = playings.size();
        for (int i = 0; i < playings.size(); i++) {
            if (playings.get(i).isStop) {
                j--;
            }
        }
        // mLog.debug(isSingleMode +":"+j);
        // mLog.debug(""+isAllAnswer());
        // mLog.debug(""+((!isSingleMode && (j < 2)) || (j==0))+"");
        return (isAllAnswer() && ((!isSingleMode && (j < 1)) || (j == 0))); //Todo:for event
//                return (isAllAnswer() && ((!isSingleMode && (j < 2)) || (j == 0))); //not for event
    }

    public void changeMode(long uid) throws BusinessException, ServerException {
        if (uid == owner.id) {
            TrieuPhuPlayer p = (TrieuPhuPlayer) owner;
            if (!p.isAnswer) {
                p.autoPlay();
            }
            endMatch();
            this.playings.addAll(waitings);
            this.waitings.clear();
        } else {
            throw new BusinessException("Bạn không phải chủ phòng rồi!");
        }
    }

    public void help(long uid, int type) throws BusinessException {
        TrieuPhuPlayer p = findPlayer(uid);
        Question q = question.get(point - 1);
        int result = p.help(type, q.best);
        if (result > 0) {
            int bonusTimeout = 0;
            if (!isBonusTimeout) {
                isBonusTimeout = true;
                long notActivated = System.currentTimeMillis() - lastActivated;
                int existsTimeout = timeOut - (int) notActivated;
                if (existsTimeout < 0) {
                    existsTimeout = 0;
                }
                bonusTimeout = BONUS_TIME_OUT / 1000;

                lastActivated = System.currentTimeMillis();
                setCurrentTimeOut(existsTimeout + BONUS_TIME_OUT);
            }

            StringBuilder sbBroadCast = new StringBuilder();

            sbBroadCast.append(Long.toString(uid)).append(AIOConstants.SEPERATOR_BYTE_1);
            sbBroadCast.append(Integer.toString(type)).append(AIOConstants.SEPERATOR_BYTE_1);
            sbBroadCast.append(Integer.toString(bonusTimeout));

            StringBuilder sbCurrent = new StringBuilder(sbBroadCast.toString());
            sbCurrent.append(AIOConstants.SEPERATOR_BYTE_3);
            if (type == 1)//help 50/50
            {
                int variantSize = p.getLstVariants().size();
                for (int i = 0; i < variantSize; i++) {
                    sbCurrent.append(Integer.toString(p.getLstVariants().get(i))).append(AIOConstants.SEPERATOR_BYTE_1);
                }

                if (variantSize > 0) {
                    sbCurrent.deleteCharAt(sbCurrent.length() - 1);
                }
            } else {
                sbCurrent.append(Integer.toString(result));
            }

            // send next question for client
            ISession notNullSession = getNotNullSession();

            TrieuPhuHelpResponse broadcastRes = (TrieuPhuHelpResponse) notNullSession
                    .getMessageFactory()
                    .getResponseMessage(MessagesID.TRIEU_PHU_HELP);

            broadcastRes.mCode = ResponseCode.SUCCESS;
            broadcastRes.value = sbBroadCast.toString();

            broadcastMsg(broadcastRes, playings, waitings, p, false);

            TrieuPhuHelpResponse currRes = (TrieuPhuHelpResponse) notNullSession
                    .getMessageFactory()
                    .getResponseMessage(MessagesID.TRIEU_PHU_HELP);

            currRes.mCode = ResponseCode.SUCCESS;
            currRes.value = sbCurrent.toString();

            if (p.currentSession != null) {
                try {
                    p.currentSession.write(currRes);
                } catch (ServerException ex) {
                    mLog.error(ex.getMessage(), ex);
                    outCodeSB.append(ex.getMessage()).append(NEW_LINE);
                }
            }

        } else {
            throw new BusinessException("Không tìm thấy phương án trợ giúp");
        }

    }

    @Override
    public ISession getNotNullSession() {
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            TrieuPhuPlayer player = this.playings.get(i);
            if (!player.isOut && player.currentSession != null
                    && player.currentSession.getMessageFactory() != null) {
                return player.currentSession;
            }
        }

        return null;
    }

    public void kickout(long userKickoutid, KickOutRequest rqKickOut)
            throws BusinessException {
        if (userKickoutid != owner.id) {
            throw new BusinessException(Messages.NOT_OWNER_PERSON);
        }

        TrieuPhuPlayer player = findPlayer(rqKickOut.uid);
        if (player == null) {
            throw new BusinessException(Messages.PLAYER_OUT);
        }

        if (this.isPlaying) {
            throw new BusinessException(Messages.PLAYING_TABLE);
        }

        player.currentSession.setLastFP(System.currentTimeMillis() - 20000); //for fast play

        MessageFactory msgFactory = getNotNullSession().getMessageFactory();
        OutResponse broadcastMsg = (OutResponse) msgFactory
                .getResponseMessage(MessagesID.OUT);
        broadcastMsg
                .setSuccess(ResponseCode.SUCCESS, rqKickOut.uid,
                        player.username + " bị chủ bàn đá ra ngoài",
                        player.username, 0);

        broadcastMsg(broadcastMsg, playings, waitings, player, true);
        if (player.isMonitor) {
            removeWaiting(player.id);
        } else {
            removePlaying(player.id);
        }
        Room room = player.currentSession.leftRoom(matchID);

        if (room != null) {
            room.left(player.currentSession);
        }

        player.currentSession.setRoom(null);

    }

    @Override
    public int getTableSize() {
        return playings.size() + waitings.size();
    }

    public int onlinePlayers() {
        int size = 0;
        int playingSize = this.playings.size();

        for (int i = 0; i < playingSize; i++) {
            if (!playings.get(i).isOut) {
                size++;
            }
        }

        return size;
    }

    public int realPlaying() {
        int size = this.playings.size();
        int realPlaying = 0;
        for (int i = 0; i < size; i++) {
            TrieuPhuPlayer player = this.playings.get(i);
            if (!player.isOut && !player.isStop) {
                realPlaying++;
            }
        }

        return realPlaying;
    }

    private void endMatch() throws ServerException {
        outCodeSB.append("end match").append(NEW_LINE);
        mLog.debug("end match");

        this.isPlaying = false;
        ISession notNullsession = getNotNullSession();
        MessageFactory msgFactory = notNullsession.getMessageFactory();
        if (!isSingleMode) {
            winner = playings.get(0);
            if (winner.point > 0) {
                isNoOneWin = false;
            }

            int playingSize = this.playings.size();

            for (int i = 1; i < playingSize; i++) {
                TrieuPhuPlayer player = this.playings.get(i);
                int result = winner.isNewWin(player);

                if (result < 0) {
                    winner = player;
                    isNoOneWin = false;
                }
            }

            updateCash();
            long newOwnerId = 0;
            // send end message to client

            if (owner.isOut || owner.notEnoughMoney()) {
                TrieuPhuPlayer newOwner = ownerQuit();
                if (newOwner != null) {
                    newOwnerId = newOwner.id;
                }
            }
            try {

                EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory.getResponseMessage(MessagesID.MATCH_END);
                // set the result
                endMatchRes.setZoneID(ZoneID.AILATRIEUPHU);
                endMatchRes.mCode = ResponseCode.SUCCESS;
                endMatchRes.value = getEndValue(newOwnerId);

                // owner.currentSession.write(endMatchRes);
                broadcastMsg(endMatchRes, playings, waitings, owner, true);
            } catch (Throwable e) {
            }
            removePlayerOut();
//			this.removeNotEnoughMoney(this.getRoom());
            resetPlayers();
        } else {
            try {
//				JSONObject endJson = getEndValue(0);
                EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory.getResponseMessage(MessagesID.MATCH_END);
                // set the result
                endMatchRes.setZoneID(ZoneID.AILATRIEUPHU);
                endMatchRes.mCode = ResponseCode.SUCCESS;
                endMatchRes.value = getEndValue(0);
                endMatchRes.session = owner.currentSession;
                owner.currentSession.write(endMatchRes);
                broadcastMsg(endMatchRes, new ArrayList<TrieuPhuPlayer>(), waitings, owner, false);
                resetPlayers();
            } catch (Throwable e) {
            }
        }
    }

    private String getEndValue(long newOwnerId) {
        Question q = question.get(point - 1);
        StringBuilder sb = new StringBuilder();
        sb.append(Long.toString(newOwnerId)).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(Integer.toString(q.best)).append(AIOConstants.SEPERATOR_BYTE_3);
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            TrieuPhuPlayer player = this.playings.get(i);
            sb.append(Long.toString(player.id)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(player.getWonMoney())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(player.cash)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(player.cauhoi)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(player.point)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(player.currentAnswerPos)).append(AIOConstants.SEPERATOR_BYTE_1);
//            sb.append((player.currentSession.isExpiredNew() || player.isOut) ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.isOut ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(player.notEnoughMoney() ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_2);
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    @Override
    protected List<SimplePlayer> removeNotEnoughMoney() {
        List<SimplePlayer> removedPlayers = new ArrayList<SimplePlayer>();
        boolean isChangeOwner = false;
        for (TrieuPhuPlayer p : playings) {
            p.moneyForBet = firstCashBet;
            if (p != null && p.notEnoughMoney()) {
                if (p.id == owner.id) {
                    isChangeOwner = true;
                }
                removedPlayers.add(p);
            }
        }

        if (isChangeOwner) {
            TrieuPhuPlayer player = ownerQuit();
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

    public TrieuPhuPlayer ownerQuit() {
        if (this.playings.isEmpty()) {
            return null;
        } else {
            for (int i = 0; i < this.playings.size(); i++) {
                TrieuPhuPlayer player = this.playings.get(i);
                if (!player.notEnoughMoney() && !player.isOut) {
                    owner = player;
                    ownerSession = owner.currentSession;
                    return (TrieuPhuPlayer) owner;
                }
            }
            if (this.waitings.isEmpty()) {
                return null;
            } else {
                for (int i = 0; i < this.waitings.size(); i++) {
                    TrieuPhuPlayer player = this.waitings.get(i);
                    if (!player.notEnoughMoney() && !player.isOut) {
                        owner = player;
                        ownerSession = owner.currentSession;
                        return (TrieuPhuPlayer) owner;
                    }
                }
                return null;
            }
        }
    }

    private void resetPlayers() {
        playings.addAll(waitings);
        waitings.clear();
        for (int i = 0; i < playings.size(); i++) {
            playings.get(i).reset();
        }

        isSentAnswer = true;
        point = 0;
        isNoOneWin = true;
    }

    private void removePlayerOut() {
        List<TrieuPhuPlayer> removedPlayer = new ArrayList<TrieuPhuPlayer>();
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            TrieuPhuPlayer p = playings.get(i);
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

    }

    public void updateCash() throws ServerException {
        int playingSize = this.playings.size();
        if (isNoOneWin) {
            for (int i = 0; i < playingSize; i++) {
                TrieuPhuPlayer player = this.playings.get(i);
                player.setWonMoney(0);
            }

            return;
        }

        Connection con = DBPoolConnection.getConnection();
        try {
            String desc = "ALTP :" + matchID;
            UserDB userDb = new UserDB(con);
            boolean havingMinusBalance = false;
            long moneyWin = 0;
            List<TrieuPhuPlayer> lstWinners = new ArrayList<TrieuPhuPlayer>();
            List<TrieuPhuPlayer> lstLosers = new ArrayList<TrieuPhuPlayer>();
            lstWinners.add(winner);
//                        for (int i = 0; i < playingSize; i++) {
//				TrieuPhuPlayer player = this.playings.get(i);
//				if (player.id != winner.id ) {
//                                    if(!player.isOut && player.point == winner.point)
//                                    {
//                                        lstWinners.add(player);
//                                    }
//                                    else
//                                    {
//					player.isWin = false;
//                                        long lostMoney = (winner.point - player.point);
//                                        if(lostMoney < firstCashBet)
//                                        {
//                                            lostMoney = firstCashBet;
//                                        }
//                                        
//                                        if(player.cash < lostMoney)
//                                        {
//                                            lostMoney = player.cash;
//                                            
//                                        }
//                                        
//					moneyWin += lostMoney;
//					
//                                        player.setWonMoney(-lostMoney);
//                                        lstLosers.add(player);
//                                    }
//                                    
//				}
//			}
//                        
//                        int winSize = lstWinners.size();
//                        int loserSize = lstLosers.size();
//                        long averageWonMoney = moneyWin/winSize;
//                        long notEnoughWonMOney = 0;
//                        for(int i = 0; i< winSize; i++ )
//                        {
//                            TrieuPhuPlayer player = lstWinners.get(i);
//                            if(player.cash< averageWonMoney)
//                            {
//                                notEnoughWonMOney += averageWonMoney - player.cash;
//                            }
//                        }
            for (int i = 0; i < playingSize; i++) {
                TrieuPhuPlayer player = this.playings.get(i);
                if (player.id != winner.id) {
                    if (!player.isOut && player.point == winner.point) {
                        lstWinners.add(player);
                    } else {
                        lstLosers.add(player);
                        player.isWin = false;
                        long lostMoney = (winner.point - player.point);
                        if (lostMoney < firstCashBet) {
                            lostMoney = firstCashBet;
                        }

                        if (player.cash < lostMoney) {
                            lostMoney = player.cash;
                        }

                        moneyWin += lostMoney;
//                                        player.cash = userDb.updateUserMoneyForTP(lostMoney,
//							player.isWin, player.id, desc,
//							player.getExperience(), 10003, player.getAchivementQuestion());
//                                        
//					if (player.cash < 0) {
//						moneyWin += player.cash;
//                                                lostMoney += player.cash;
//                                                havingMinusBalance = true;
//						player.cash = 0;
//					}
                        player.setWonMoney(-lostMoney);
                    }
                }
            }

            int winSize = lstWinners.size();
            long partWonMoney = moneyWin / winSize;
            long revertLoseMoney = 0;
            for (int i = 0; i < winSize; i++) {
                TrieuPhuPlayer player = lstWinners.get(i);
                player.isWin = true;
                player.setWonMoney((int) (partWonMoney * REAL_GOT_MONEY));

                if (player.getWonMoney() > player.cash) {
                    player.setWonMoney(player.cash);
                    long revert = partWonMoney - player.cash;
                    revertLoseMoney += revert;
                    moneyWin -= revert;
                    player.setWonMoney(player.cash);
                }
//                            player.cash = userDb.updateUserMoneyForTP(player.getWonMoney(), true,
//					player.id, desc, player.getExperience(), 10003, player.getAchivementQuestion());

                // Check for in-game event for winner
                player.checkEvent(true);
            }

            //luu nguoi thua
            int lostSize = lstLosers.size();
            long partRevert = 0;

            if (lostSize > 0 && revertLoseMoney > 0) {
                partRevert = revertLoseMoney / lostSize;
            }
            for (int i = 0; i < lostSize; i++) {
                TrieuPhuPlayer player = lstLosers.get(i);
                if (partRevert > 0) {
                    long lostMoney = (winner.point - player.point);
                    if (lostMoney < (partRevert - player.getWonMoney()))//hoan tien cho nguoi nay neu nhu no ko thua het sach tien
                    {
                        lostMoney -= partRevert;
                        if (lostMoney > 0) {
                            //hoan tien ok
                            player.setWonMoney(-lostMoney);
                        } else {
                            //sau khi hoan tien gia tri tien mat >0 nen cho no thang = 0
                            player.setWonMoney(0);
                        }
                    }
                }

                player.cash = userDb.updateUserMoneyForTP(-player.getWonMoney(),
                        player.isWin, player.id, desc,
                        player.getExperience(), 10003, player.getAchivementQuestion());

                if (player.cash < 0) {
                    moneyWin += player.cash;
                    havingMinusBalance = true;
                    player.cash = 0;
                }

                // Check for in-game event for loser
                player.checkEvent(false);
            }

            //Save database winner
            partWonMoney = moneyWin / winSize;

            for (int i = 0; i < winSize; i++) {
                TrieuPhuPlayer player = lstWinners.get(i);
                player.isWin = true;
                player.setWonMoney((int) (partWonMoney * REAL_GOT_MONEY));

                if (player.getWonMoney() > player.cash) {
//                                player.setWonMoney(player.cash);
                    player.setWonMoney(player.cash);
                }

                player.cash = userDb.updateUserMoneyForTP(player.getWonMoney(), true,
                        player.id, desc, player.getExperience(), 10003, player.getAchivementQuestion());
            }

            // Save for winner
//			winner.isWin = true;
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

    public boolean isAllAnswer() {
        for (int i = 0; i < playings.size(); i++) {
            TrieuPhuPlayer player = this.playings.get(i);

            if (!player.isStop && !player.isOut && !player.isAnswer) {
                return false;
            }
        }
        return true;
    }

    public TrieuPhuTable(TrieuPhuPlayer owner, long money, long matchId, Room r) {
        // stat = GameStat.NOT_START;
        playings = new ArrayList<TrieuPhuPlayer>();
        waitings = new ArrayList<TrieuPhuPlayer>();
        this.setRoom(r);
        matchID = matchId;
        this.owner = owner;
        playings.add(owner);
        firstCashBet = money;
        owner.isGiveUp = false; // owner doesnt bet
        this.winID = owner.id;
        excepts = new ArrayList<Integer>();
    }

    public TrieuPhuTable(TrieuPhuPlayer owner, long money) {
        // stat = GameStat.NOT_START;
        playings = new ArrayList<TrieuPhuPlayer>();
        waitings = new ArrayList<TrieuPhuPlayer>();
        this.winID = owner.id;
        excepts = new ArrayList<Integer>();
        this.owner = owner;
        this.playings.add(owner);
        this.firstCashBet = money;
        owner.isGiveUp = false; // owner doesnt bet

        logdir = "trieuPhu";
    }

    public void start() throws BusinessException {
        // stat = GameStat.START;
        setCurrentTimeOut(timeOut);
        lastActivated = System.currentTimeMillis();
        isPlaying = true;
        int playingSize = playings.size();
        for (int i = 0; i < playingSize; i++) {
            TrieuPhuPlayer p = playings.get(i);
            if (com.tv.xeeng.game.data.Utils.isSuperUser(p.id)) {
                p.isSuper = true;
            } else {
                p.isSuper = false;
            }
        }
        /*
         * ArrayList<Integer> temp = makeQuestionRandom(0, 1000);
         * question.clear(); QuestionManager qmng =
         * owner.currentSession.getQuestMng(); for (int t : temp) {
         * question.add(qmng.getQuestion(t)); }
         */
        // fixQuestion();
        if (playings.size() == 1) {
            isSingleMode = true;
        } else {
            isSingleMode = false;
        }
        makeQuestions();

        // System.out.println("owner =" +owner.isStop);
        sendNewRound();
    }

    private void makeQuestions() throws BusinessException {
//		question.clear();
        QuestionManager qm = new QuestionManager();
        question = qm.makeRandomList(isSingleMode);
    }

    /*
     * 1--> 383 : level 1 384--> 632 : level 10 633 --> 838 level 11 839 -->
     * 1021 level 12 1022 --> 1190 level 13 1191 --> 1345 level 14 1346 --> 1401
     * level 15 1402 --> 1687 level 2 1688 --> 1917 level 3 1918 --> 2215 level
     * 4 2216 --> 2526 level 5 2527 --> 2918 level 6 2919 --> 3312 level 7 3313
     * --> 3708 level 8 3709 --> 4089 level 9
     */
//	private static Couple<Integer, Integer> levelToCouple(int l) {
//		switch (l) {
//		case 1:
//			return new Couple<Integer, Integer>(1, 383);
//		case 10:
//			return new Couple<Integer, Integer>(384, 632);
//		case 11:
//			return new Couple<Integer, Integer>(633, 838);
//		case 12:
//			return new Couple<Integer, Integer>(839, 1021);
//		case 13:
//			return new Couple<Integer, Integer>(1022, 1190);
//		case 14:
//			return new Couple<Integer, Integer>(1191, 1345);
//		case 15:
//			return new Couple<Integer, Integer>(1346, 1401);
//		case 2:
//			return new Couple<Integer, Integer>(1402, 1687);
//		case 3:
//			return new Couple<Integer, Integer>(1688, 1917);
//		case 4:
//			return new Couple<Integer, Integer>(1918, 2215);
//		case 5:
//			return new Couple<Integer, Integer>(2216, 2526);
//		case 6:
//			return new Couple<Integer, Integer>(2527, 2918);
//		case 7:
//			return new Couple<Integer, Integer>(2919, 3312);
//		case 8:
//			return new Couple<Integer, Integer>(3313, 3708);
//		case 9:
//			return new Couple<Integer, Integer>(3709, 4089);
//
//		default:
//			return new Couple<Integer, Integer>(1, 383);
//		}
//	}
//	public static void main(String[] args) {
//		QuestionManager.initHash();
//		QuestionManager.init();
//		ArrayList<Question> qs = new ArrayList<Question>();
//		ArrayList<Integer> data = makeQuestionRandom();
//		for (int l = 1; l < 16; l++) {
//			int i = data.get(l-1);
//			try {
//				Question q = QuestionManager.getQuestion(i,l);
//				qs.add(q);
//			} catch (Throwable e) {
//			}
//		}
//		for(Question qt : qs){
//			System.out.println(qt.level+ qt.detail);
//		}
//	}
//	private static ArrayList<Integer> makeQuestionRandom() {
//		ArrayList<Integer> res = new ArrayList<Integer>();
//		int i = 0;
//		while (i < 15) {
//			Couple<Integer, Integer> c = levelToCouple(i + 1);
//			Random rand = new Random(System.currentTimeMillis());
//			int t = (int) (Math.abs(rand.nextLong() % (c.e2 - c.e1))) + c.e1;
//			i++;
//			res.add(t);
//		}
//		return res;
//	}
//	@SuppressWarnings({ "unchecked", "unused" })
//	private ArrayList<Integer> makeQuestionRandom(int first, int last) {
//		ArrayList<Integer> res = new ArrayList<Integer>();
//		ArrayList<Integer> temp = (ArrayList<Integer>) excepts.clone();
//		int i = 0;
//		while (i < 15) {
//			int t = temp.get(0);
//			while (temp.contains(res)) {
//				Random rand = new Random(System.currentTimeMillis());
//				t = (int) (Math.abs(rand.nextLong() % (last - first))) + first;
//			}
//			if (t == temp.get(0)) {
//				continue;
//			} else {
//				i++;
//				res.add(t);
//			}
//		}
//		return res;
//	}
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
    public void doTimeout() {
        try {
            if (isPlaying) {
                lastActivated = System.currentTimeMillis();
                outCodeSB.append("do timeout").append(NEW_LINE);
                mLog.debug("doTimeout");
                for (int i = 0; i < playings.size(); i++) {
                    playings.get(i).autoPlay();
                }
                checkFinish();
                // mLog.debug("Table: "+ isPlaying);
                if (isPlaying) {
                    if (isAllAnswer()) {
                        if (isSentAnswer) {
                            sendNewRound();
                        } else {
                            isSentAnswer = true;
                            sentRoundResult();
                        }
                    }
                    /*
                     * else if ((stat == GameStat.SEND_QUESTION)) sendResult();
                     */
                }
            }
            // sendNewRound();
        } catch (Exception ex) {
            // this.isPlaying = false;
            mLog.error(ex.getMessage(), ex);
        } catch (ServerException ex) {
            mLog.error(ex.getMessage(), ex);
        }
    }
}

enum GameStat {

    NOT_START, START, SEND_RESULT, SEND_QUESTION,
}
