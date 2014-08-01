package com.tv.xeeng.game.data;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.*;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.IResponseMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import com.tv.xeeng.server.Server;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class SimpleTable {

    private Room room;
    public int maximumPlayer;
    public boolean isPlaying = false;
    public int level;
    public long matchID;
    public long matchNum = 0;
    public long matchIDAuto;

    public ISession ownerSession;
    public long firstCashBet;    //default setup money of table

    public SimplePlayer owner;
    public String name;
    protected int currentTimeOut;
    public static final String NEW_LINE = System.getProperty("line.separator");
    protected StringBuilder outCodeSB = new StringBuilder();
    public StringBuilder logMini = new StringBuilder(); // log này dùng để ghi lịch sử của mỗi bàn chơi, chỉ bao gồm các thông tin: Thời gian bắt đầu / kết thúc từng ván, người vào / ra.

    public static int AUTO_KICKOUT_TIMEOUT = 20000;
    public static int AUTO_KICKOUT_OWNER_TIMEOUT = 30000;
    protected static int SLEEP_BEETWEEN_MATCH_TIMEOUT = 10000;

    protected int currentIndexPlayer;
    protected final double REAL_GOT_MONEY = Server.REAL_GOT_MONEY;

    protected final String NONE_EXISTS_PLAYER = "Không tồn tại người chơi này";
    protected final String NOT_ALLOW_OWNER_MONEY_BET = "Số tiền vượt quá số tiền có thể thua của chủ bàn";
    protected final String NOT_ALLOW_USER_MONEY_BET = "Số tiền vượt quá số tiền bạn đang có";
    protected final String NOT_ALLOW_OWNER_BET = "Chủ bàn không được phép đặt cược";
    protected final String NOT_ALLOW_OWNER_BET_OTHER = "Chủ bàn không được phép chơi cận biên";
    protected final String REQUIRED_MONEY = "Tiền đặt cược ít hơn tiền bàn";
    protected final String NOT_PLAYING_TABLE = "Ván chơi đã kết thúc";
    protected final String DONE_BET = "Bạn đã đặt cược rồi";

    protected final int LOG_TYPE_GAME_START = 10000;
    protected final int TIMES = 4;

    public boolean dontWantAnyUser = false;
    public boolean isKickoutBot = false;
    protected boolean isTryKickOut = true;

    public static final Logger mLog = LoggerContext.getLoggerFactory()
            .getLogger(SimpleTable.class);

//    public com.tv.xeeng.base.logger.Logger newLog;
    private long ownerId;
    protected SimplePlayer botPlayer;
    protected static final int MAX_RETRY_BOT = 10;

    public int tableIndex;
    public int phantram = 10;

    public int getTableIndex() {
        return tableIndex;
    }

    public void setTableIndex(int tableIndex) {
        this.tableIndex = tableIndex;
    }

    public long phongID;

    public void setPhongID(long phongID) {
        this.phongID = phongID;
    }

    public long getPhongID() {
        return phongID;
    }

    protected long lastActivated; //for auto process(timeout)

    protected void checkBotUser() {
        try {
            List<? extends SimplePlayer> lstPlaying = getNewPlayings();
            int playingSize = lstPlaying.size();
            for (int i = 0; i < playingSize; i++) {
                SimplePlayer player = lstPlaying.get(i);
                if (!player.isOut && player.currentSession != null && player.currentSession.getBotType() > 0) {
                    botPlayer = player;
                    player.currentSession.setBotType(0);
                    break;
                }
            }
        } catch (Exception ex) {
        }

    }

    public boolean hasBotUser() {
        try {
            List<? extends SimplePlayer> lstPlaying = getNewPlayings();
            int playingSize = lstPlaying.size();
            UserDB db = new UserDB();
            for (int i = 0; i < playingSize; i++) {
                SimplePlayer player = lstPlaying.get(i);
                if (db.checkBotUser(player.id)) {
                    return true;
                }
            }
        } catch (Exception ex) {
        }

        return false;

    }

    protected void resetBotCheck() {
        botPlayer = null;
    }

    public SimplePlayer findPlayer(long uid) throws SimpleException {

        List<? extends SimplePlayer> playings = getNewPlayings();
        List<? extends SimplePlayer> waitings = getNewWaitings();

        if (playings == null) {
            return null;
        }

        int playingSize = playings.size();
        for (int i = 0; i < playingSize; i++) {
            SimplePlayer player = playings.get(i);
            if (player.id == uid) {
                return player;
            }
        }

        if (waitings != null) {
            int waitingSize = waitings.size();
            for (int i = 0; i < waitingSize; i++) {
                SimplePlayer player = waitings.get(i);
                if (player.id == uid) {
                    return player;
                }
            }

        }

        return null;
    }

    public void removePlayer(SimplePlayer player) {
    }

    //override get current player for another game later
    public SimplePlayer getCurrPlayer() {
        return null;
    }

    public FileWriter outFile_code;// = new FileWriter(args[0]);
    public PrintWriter out_code = null;// = new PrintWriter(outFile);
    public FileWriter outFile;// = new FileWriter(args[0]);
    public PrintWriter out;// = new PrintWriter(outFile);

    public long getMatchID() {
        return matchID;
    }

    public void setMatchID(long matchID) {
        this.matchID = matchID;
    }

    public boolean getIsPlaying() {
        return this.isPlaying;
    }

    public void setIsPlaying(boolean b) {
        this.isPlaying = b;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMaximumPlayer() {
        return maximumPlayer;
    }

    public void setMaximumPlayer(int max) {
        maximumPlayer = max;
    }

    public long getMinBet() {
        return firstCashBet;
    }

    public long getMatchIDAuto() {
        return matchIDAuto;
    }

    public void destroy() {
        saveLogToFile();
        try {
            if (out_code != null) {
                out_code.println("Room destroy!");
                out.println("Room destroy!");

                out.close();
                outFile.close();
                out_code.close();
                outFile_code.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isFullTable() {
        return true;
    }

    public void setMatchIDAuto(long matchIDAuto) {
        this.matchIDAuto = matchIDAuto;
    }

    public void setOwnerSession(ISession ownerSession) {
        this.ownerSession = ownerSession;
    }

    public ISession getOwnerSession() {
        return ownerSession;
    }

    public String logdir = "none_log";

    protected void saveLogToFile() {

        try {
            new File("logs/" + logdir).mkdirs();

        } catch (Exception e) {
            //e.printStackTrace();
        }

        if (matchID == 0) {
            return;
        }
        try {
            System.out.println("save log to file came here");
            String path = "logs/" + logdir + "/match_" + matchID + ".txt";
            File f = new File(path);
            boolean append = false;
            if (f.exists()) {
                append = true;
            }

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, append), Charset.forName("UTF-8").newEncoder()));

//            FileWriter outF = new FileWriter("logs/" + logdir + "/match_" + matchID + ".txt", append);
//            PrintWriter writer = new PrintWriter(outF);
            writer.write(getOutCodeSB().toString());
            writer.flush();
            writer.close();

            if (logMini.toString().length() != 0) {
                String filePathMini = "logs/" + logdir + "/match_" + matchID + "_mini.txt";
                File fileMini = new File(path);
                boolean appendMini = false;
                if (fileMini.exists()) {
                    append = true;
                }
                BufferedWriter writerMini = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePathMini, append), Charset.forName("UTF-8").newEncoder()));
                writerMini.write(logMini.toString());
                writerMini.flush();
                writerMini.close();

                logMini.setLength(0);
            }

            outCodeSB.setLength(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initLogFile() {
        try {
            File dir1 = new File(".");

            boolean success = (new File("logs/" + logdir)).mkdirs();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (matchID == 0) {
            return;
        }
        try {

            String str = "logs/" + logdir + "/match_" + matchID + ".txt";
            File f = new File(str);
            boolean append = false;
            if (f.exists()) {
                append = true;
            }

            outFile = new FileWriter("logs/" + logdir + "/match_" + matchID + ".txt", append);
            out = new PrintWriter(outFile);

            outFile_code = new FileWriter("logs/" + logdir + "/match_" + matchID + "_code.txt", append);
            out_code = new PrintWriter(outFile_code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the lastActivated
     */
    public long getLastActivated() {
        return lastActivated;
    }

    /**
     * @param lastActivated the lastActivated to set
     */
    public void setLastActivated(long lastActivated) {
        this.lastActivated = lastActivated;
    }

    public void doTimeout() //should be abstract method
    {
        //System.out.println("Vao day de");
    }

    /**
     * @return the currentTimeOut
     */
    public int getCurrentTimeOut() {
        return currentTimeOut;
    }

    /**
     * @param currentTimeOut the currentTimeOut to set
     */
    public void setCurrentTimeOut(int currentTimeOut) {
        this.currentTimeOut = currentTimeOut;
    }

    /**
     * @return the ownerId
     */
    public long getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId the ownerId to set
     */
    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    protected List<SimplePlayer> removeNotEnoughMoney() {
        return null;
    }

    public void cancel(List<? extends SimplePlayer> players) {
        try {
            MessageFactory msgFactory = owner.currentSession.getMessageFactory();
            for (int i = 0; i < players.size(); i++) {
                SimplePlayer player = players.get(i);
                if (player.id != owner.id) {
                    try {
                        CancelResponse removeRes = (CancelResponse) msgFactory.getResponseMessage(MessagesID.MATCH_CANCEL);
                        removeRes.setSuccess(ResponseCode.SUCCESS, players.get(i).id);
                    } catch (Exception ex) {
                        mLog.error("cancel session error", ex);
                    }
                }
            }

            CancelResponse removeRes = (CancelResponse) msgFactory.getResponseMessage(MessagesID.MATCH_CANCEL);
            removeRes.setSuccess(ResponseCode.SUCCESS, owner.id);

        } catch (Exception ex) {
            mLog.error("cancel before delete invalid room", ex);

            outCodeSB.append("cancel before delete invalid room").append(NEW_LINE);
        }
    }

    private void sendMsg(SimplePlayer player, IResponseMessage response) {
//        try {
        //check make sure don't send to session if it 's in another room
        Room playerRoom = player.currentSession.getRoom();
        if (playerRoom == null || (playerRoom != null && playerRoom.getAttactmentData().matchID == this.matchID)) {
            if (response instanceof TurnResponse || response instanceof AnPhomResponse
                    || response instanceof GuiPhomResponse || response instanceof GetPokerResponse
                    || response instanceof ReadyResponse || response instanceof EndMatchResponse
                    || response instanceof CancelResponse || response instanceof BocPhomResponse
                    || response instanceof JoinedResponse || response instanceof HaPhomResponse
                    || response instanceof StartResponse || response instanceof LatBaiResponse
                    || response instanceof ChangeSettingResponse || response instanceof OutResponse) {
//                if (this instanceof PhomTable || this instanceof  TienLenTable)
                response = response.clone(player.currentSession);
//                else
//                    response.setSession(player.currentSession);
            } else {
                response.setSession(player.currentSession);
            }

            try {
                if (player.currentSession != null) {
                    player.currentSession.write(response);
                }
            } catch (ServerException ex) {
                mLog.error("Ok, null exception from here...");
                mLog.error(player.currentSession.toString());
                mLog.error("and response is...");
                mLog.error(response.toString());

                mLog.error(ex.getMessage(), ex);
                outCodeSB.append(ex.getMessage()).append(NEW_LINE);
            }
        }
//        } catch (ServerException ex) {
//            if (ex == null) {
//                mLog.warn("Null khi send broadcast");
//                outCodeSB.append("Null khi send broadcast").append(NEW_LINE);
//            } else {
//                mLog.error(ex.getMessage(), ex);
//                outCodeSB.append(ex.getMessage()).append(NEW_LINE);
//            }
//        }

    }

    public void broadcastMsg(IResponseMessage msg, List<? extends SimplePlayer> playings,
            List<? extends SimplePlayer> waitings, SimplePlayer sender, boolean isSendMe) {
        try {
            boolean isSentMe = false;
            if (isSendMe && sender != null && sender.currentSession != null && !sender.isOut) {
                isSentMe = true;
                sendMsg(sender, msg);//send owner
            }

            for (int i = 0; i < playings.size(); i++) {
                SimplePlayer player = playings.get(i);
                if (player != null && !player.isOut && ((sender == null) || (sender != null && (player.id != sender.id || (isSendMe && !isSentMe)))))//have you sent it?
                {
                    sendMsg(player, msg);
                }
            }

            for (int i = 0; i < waitings.size(); i++) {
                SimplePlayer player = waitings.get(i);
                if (!player.isOut && ((sender == null) || (sender != null && (player.id != sender.id || (isSendMe && !isSentMe))))) {
                    sendMsg(player, msg);
                }
            }

//            outCodeSB.append(String.format("(Gửi broadcast thành công: messageId = %d)", msg.getID())).append(NEW_LINE);
//            outCodeSB.append(NEW_LINE);
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
        }

    }

    public void removeNotEnoughMoney(Room room) {
        long newOwnerId = ownerId;
//        System.out.println("old ownerId "+ ownerId);
        List<SimplePlayer> removedPlayers = this.removeNotEnoughMoney();

        if (newOwnerId == this.getOwnerId()) {
            newOwnerId = 0;
        } else {
            newOwnerId = this.getOwnerId();
        }

        //System.out.println("new ownerId "+ newOwnerId);
        for (int i = 0; i < removedPlayers.size(); i++) {
            SimplePlayer removedPlayer = removedPlayers.get(i);
            //kick out this user
            MessageFactory msgFactory = removedPlayer.currentSession.getMessageFactory();
            OutResponse outRes = (OutResponse) msgFactory.getResponseMessage(MessagesID.OUT);
            outRes.session = removedPlayer.currentSession;
            outRes.setSuccess(ResponseCode.SUCCESS, removedPlayer.id, "Ban không đủ tiền chơi game này", "", (int) this.getMatchID());
            try {
                removedPlayer.currentSession.write(outRes);
            } catch (ServerException ex) {
                //mLog.error("Out tienlen player error", ex);
            }
            //send another people to inform this not enough money event
            CancelResponse removeRes = (CancelResponse) msgFactory.getResponseMessage(MessagesID.MATCH_CANCEL);
            removeRes.setSuccess(ResponseCode.SUCCESS, removedPlayer.id);
            removeRes.newOwner = newOwnerId;

            if (getNewPlayings() != null) {
                broadcastMsg(removeRes, getNewPlayings(), getNewWaitings(), removedPlayer, false);
            } else //room.broadcastMessage(removeRes, removedPlayer.currentSession, false);
            {
                room.left(removedPlayer.currentSession);
            }

            if (removedPlayer.currentSession != null) {
                removedPlayer.currentSession.setRoom(null);
            }

        }

    }

    public void kickTimeout(Room room) {
    }

    public void kickTimeout(Room room, SimplePlayer removedPlayer, long newOwnerId) {
        MessageFactory msgFactory = removedPlayer.currentSession.getMessageFactory();
        if (msgFactory == null) {
            if (owner == null) {
                mLog.error("auto delete room");
                outCodeSB.append("auto delete room");
                this.destroy();
                room.allLeft();

            }
            msgFactory = owner.currentSession.getMessageFactory();
        }

        OutResponse outRes = (OutResponse) msgFactory.getResponseMessage(MessagesID.OUT);
        String msg = "Bạn chưa bấm sẵn sàng";
        if (newOwnerId != 0) {
            msg = "Bạn chưa bấm bắt đầu";
        }
        outRes.setSuccess(ResponseCode.SUCCESS, removedPlayer.id, msg, "", (int) this.getMatchID());
        outRes.type = 1;
        if (removedPlayer == null || removedPlayer.currentSession == null) {
            String errorDescription = "kick timeout error null player or session";
            mLog.error(errorDescription);
            outCodeSB.append(errorDescription).append(NEW_LINE);
            return;
        }
        try {
            removedPlayer.currentSession.write(outRes);
        } catch (ServerException ex) {
            //mLog.error("Out tienlen player error", ex);
        }

        CancelResponse removeRes = (CancelResponse) msgFactory.getResponseMessage(MessagesID.MATCH_CANCEL);
        removeRes.setSuccess(ResponseCode.SUCCESS, removedPlayer.id);
        removeRes.newOwner = newOwnerId;
        try {
            removedPlayer.currentSession.leftRoom(matchID);
        } catch (Exception ex) {
        }
        room.left(removedPlayer.currentSession);
        //room.broadcastMessage(removeRes, removedPlayer.currentSession, true);

    }

    public void autoStartGame(SimplePlayer player) {
        /*ISession session ;
	
         session = player.currentSession;
         IResponsePackage responsePkg = session.getDirectMessages();
                
         MessageFactory msgFactory = player.currentSession.getMessageFactory();
         RestartRequest restartRequest = (RestartRequest) msgFactory
         .getRequestMessage(MessagesID.MATCH_RESTART);
        
         restartRequest.mMatchId = matchID;
         restartRequest.money = firstCashBet;
         restartRequest.uid = player.id;
         try {
         IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_RESTART);
         business.handleMessage(player.currentSession, restartRequest, responsePkg);
         } catch (ServerException se) {
         }*/
    }

    // override set ready for every game  -- we should rename this method later
    public void playerReady(long uid, boolean r) {
        try {
            SimplePlayer player = findPlayer(uid);

            if (player != null) {
                player.setReady(r);
                long now = System.currentTimeMillis();
                player.setLastActivated(now);
                owner.setLastActivated(now);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

//    protected void sendReadyMessage(SimplePlayer player, IResponseMessage msg)
//    {
//        
//    }
    // override set ready for every game  -- we should rename this method later
    public void playerReadyWithBroadcast(long uid, boolean r) {
        try {
            SimplePlayer player = findPlayer(uid);
            if (player != null) {
                player.setReady(r);
                long now = System.currentTimeMillis();
                player.setLastActivated(now);
                owner.setLastActivated(now);

                MessageFactory msgFactory = player.currentSession.getMessageFactory();
                if (msgFactory == null) {
//                    if(owner == null)
//                    {
//                        mLog.error("auto delete room");
//                        outCodeSB.append("auto delete room").append(NEW_LINE);
////                        this.destroy();
//                        room.allLeft();
//
//                    }
//                    msgFactory = owner.currentSession.getMessageFactory();
                    ISession session = getNotNullSession();
                    msgFactory = session.getMessageFactory();
                }

                //send ready json to room
                ReadyResponse readyMsg = (ReadyResponse) msgFactory.getResponseMessage(MessagesID.MATCH_READY);
                readyMsg.setSuccess(ResponseCode.SUCCESS, uid, r);

                //sendReadyMessage(player, readyMsg);                    
                broadcastMsg(readyMsg, this.getNewPlayings(), this.getNewWaitings(), player, true);

                lastaActive = System.currentTimeMillis();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    /**
     *
     * Do kick-out
     */
    public long kickOutTime = 35000;
    public long lastaActive;

    private boolean isAllReady() {
        int len = getNewPlayings().size();
        if (len < 2) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            SimplePlayer p = getNewPlayings().get(i);
            if (!p.isReady && p.id != owner.id) {
                return false;
            }
        }
        return true;
    }

    public void doAutoKickOut() {
        //if (!isPlaying) {
        //mLog.debug("Auto Kick out owner!123");
        if (isAllReady()) {
            mLog.debug("Auto Kick out owner!");
            autoKickOwner();
        }
        lastaActive = System.currentTimeMillis();
        // }
    }

    private void autoKickOwner() {
        ISession session;

        session = owner.currentSession;
        IResponsePackage responsePkg = session.getDirectMessages();

        MessageFactory msgFactory = owner.currentSession.getMessageFactory();
        CancelRequest restartRequest = (CancelRequest) msgFactory
                .getRequestMessage(MessagesID.MATCH_CANCEL);

        restartRequest.mMatchId = matchID;

        try {
            IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_CANCEL);
            business.handleMessage(owner.currentSession, restartRequest, responsePkg);
        } catch (ServerException se) {
            mLog.error(se.getMessage());
        }
    }

    /**
     * @return the outCodeSB
     */
    public StringBuilder getOutCodeSB() {
        return outCodeSB;
    }

    /**
     * @param outCodeSB the outCodeSB to set
     */
    public void setOutCodeSB(StringBuilder outCodeSB) {
        this.outCodeSB = outCodeSB;
    }

    protected boolean updateUserCash(UserDB userDb, SimplePlayer winner,
            SimplePlayer loser, double betMoney, String desc) throws DBException, SQLException {

        boolean havingMinusBalance = false;
        int logtypeId = LOG_TYPE_GAME_START;
        try {
            logtypeId += ownerSession.getCurrentZone();
        } catch (Exception ex) {
            mLog.error("updateUserCash", ex);
        }
        if (loser.cash == 0) {
            loser.cash -= betMoney;//avoid to save to db if lose doesn't have cash
        } else {
            loser.cash = userDb.updateUserMoney((long) betMoney,
                    false, loser.id, desc + winner.id,
                    0, logtypeId);
        }

        if (loser.cash < 0) {
            betMoney += loser.cash;
            havingMinusBalance = true;
            loser.cash = 0;
        }

        long wonMoney = (long) (betMoney * REAL_GOT_MONEY);
        loser.setWonMoney((long) -betMoney + loser.getWonMoney());
        winner.setWonMoney(wonMoney + winner.getWonMoney());

        //save winner
        winner.cash = userDb.updateUserMoney(wonMoney,
                true, winner.id, desc + loser.id,
                1, logtypeId);

        outCodeSB.append("user: ").append(loser.username).append("lost money: ").append(loser.getWonMoney()).append(NEW_LINE);
        outCodeSB.append("user: ").append(loser.username).append("won money: ").append(winner.getWonMoney()).append(NEW_LINE);

        return havingMinusBalance;
    }

    protected boolean updateUserCash(UserDB userDb, SimplePlayer player, boolean isWin,
            double betMoney, String desc) throws DBException, SQLException {

        boolean havingMinusBalance = false;
        int logtypeId = LOG_TYPE_GAME_START;
        try {
            logtypeId += ownerSession.getCurrentZone();
        } catch (Exception ex) {
            mLog.error("updateUserCash", ex);
        }
        long money = 0;
        if (isWin) {
            money = (long) (betMoney * REAL_GOT_MONEY);
        } else {
            money = (long) betMoney;
        }
        player.setWonMoney(money);
        player.cash = userDb.updateUserMoney(money,
                isWin, player.id, desc,
                0, logtypeId);

        if (player.cash < 0) {
            havingMinusBalance = true;
            player.cash = 0;
        }
        return havingMinusBalance;
    }

    public int getTableSize() {
        return 0;
    }

    /**
     * @return the room
     */
    public Room getRoom() {
        return room;
    }

    /**
     * @param room the room to set
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    //we should overried this method for every game
    public boolean newContainPlayer(long uid) {
        return false;
    }

    protected void join(SimplePlayer player) throws BusinessException {
        //override to input in playing and waiting player
    }

    public List<? extends SimplePlayer> getNewPlayings() {
        return null;
    }

    public List<? extends SimplePlayer> getNewWaitings() {
        return null;
    }

    protected void joinResponse(JoinResponse joinResponse) {
        //override if there 's any game which has addition info in Join response
    }

    public ISession getNotNullSession() {
        return null;
    }

    public boolean checkEnoughMoney(long money) {
        return money < TIMES * this.firstCashBet;
    }

    public void xeNewTable(UserEntity user, XENewRequest req, ISession aSession, int phongID, int tableIndex) {
        Zone zone = aSession.findZone(aSession.getCurrentZone());
        Room newRoom = zone.createRoom(req.tableName, aSession.getUID(), phongID);
        newRoom.setIndex(tableIndex);
        newRoom.join(aSession);
        newRoom.setPassword(null);
        newRoom.setName(req.tableName);
        newRoom.setPlayerSize(req.maxPlayers);
        newRoom.setOwnerName(user.mUsername);
        newRoom.setZoneID(aSession.getCurrentZone());

        owner.cash = user.money;
        owner.avatarID = user.avatarID;
        owner.level = user.level;
        owner.username = user.mUsername;
        owner.currentSession = aSession;
        owner.moneyForBet = req.moneyBet;
        owner.currentMatchID = newRoom.getRoomId();
        owner.isReady = true;
        owner.currentOwner = owner.currentSession;

        this.name = req.tableName;
        this.setOwnerSession(aSession);
        this.setTableIndex(tableIndex);
        this.setRoom(newRoom);
        this.setMaximumPlayer(req.maxPlayers);
        aSession.setRoom(newRoom);

        // index = rqMatchNew.tableIndex;
        this.setPhongID(phongID);
        newRoom.setAttachmentData(this);
        this.matchID = newRoom.getRoomId();

        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), aSession.getCurrentZone(), newRoom, phongID);
        CacheMatch.add(entity);
    }

    public void newTable(UserEntity user, NewRequest rqMatchNew, ISession aSession) throws BusinessException {
        if (checkEnoughMoney(user.money)) {
            throw new BusinessException(Messages.MSG_NOT_ENOUGH_MONEY_TO_CREATE);
        }
        Zone zone = aSession.findZone(aSession.getCurrentZone());

        Room newRoom = zone.createRoom(rqMatchNew.roomName,
                rqMatchNew.uid, rqMatchNew.phongID);

        newRoom.setIndex(rqMatchNew.tableIndex);

        newRoom.join(aSession);

        newRoom.setPassword(rqMatchNew.password);
        newRoom.setName(rqMatchNew.roomName);
        newRoom.setPlayerSize(rqMatchNew.size);
        newRoom.setOwnerName(user.mUsername);
        newRoom.setZoneID(aSession.getCurrentZone());
        System.out.println("Zone: " + newRoom.getZoneID());

        owner.cash = user.money;
        owner.avatarID = user.avatarID;
        owner.level = user.level;
        owner.username = user.mUsername;
        owner.currentSession = aSession;
        owner.moneyForBet = rqMatchNew.moneyBet;
        owner.currentMatchID = newRoom.getRoomId();
        owner.isReady = true;
        owner.currentOwner = owner.currentSession;

        this.name = rqMatchNew.roomName;
        this.setOwnerSession(aSession);
        this.setTableIndex(rqMatchNew.tableIndex);
        this.setRoom(newRoom);
        aSession.setRoom(newRoom);

        // index = rqMatchNew.tableIndex;
        this.setPhongID(rqMatchNew.phongID);
        newRoom.setAttachmentData(this);
        this.matchID = newRoom.getRoomId();

        MatchEntity entity = new MatchEntity(newRoom.getRoomId(), aSession.getCurrentZone(), newRoom, rqMatchNew.phongID);
        CacheMatch.add(entity);

    }

    public void join(ISession aSession, JoinRequest request, UserEntity newUser, SimplePlayer newPlayer) throws BusinessException, ServerException {
        MessageFactory msgFactory = aSession.getMessageFactory();
        JoinResponse resMatchJoin = (JoinResponse) msgFactory.getResponseMessage(MessagesID.MATCH_JOIN);
        resMatchJoin.setSession(aSession);
        long moneyOfPlayer = newUser.money;
        long uid = newUser.mUid;

        if (checkEnoughMoney(moneyOfPlayer)) {
            throw new BusinessException(Messages.MSG_NOT_ENOUGH_MONEY);
        }

        if (this.isFullTable()) {
            throw new BusinessException(Messages.FULL_PLAYER_MSG);
        }

        if (this.newContainPlayer(newUser.mUid)) {
            //this user in room so we dont' send another people about this event 
            throw new BusinessException(Messages.STILL_IN_TABLE);
        }

        newPlayer.avatarID = newUser.avatarID;
        newPlayer.level = newUser.level;
        newPlayer.cash = newUser.money;
        newPlayer.username = newUser.mUsername;
        newPlayer.moneyForBet = this.firstCashBet;
        newPlayer.currentMatchID = request.mMatchId;
        newPlayer.currentSession = aSession;

//        newPlayer.currentOwner = owner.currentSession; //review it
//        
        join(newPlayer);
        room.join(aSession);
        aSession.setRoom(room);

//        if(aSession.getCurrentZone() == ZoneID.CHAN)
//        	aSession.setTable(this);
//        else 
//        	aSession.setTable(null);
        JoinedResponse broadcastMsg = (JoinedResponse) msgFactory.getResponseMessage(MessagesID.MATCH_JOINED);

        broadcastMsg.setSession(aSession);
        broadcastMsg.setSuccess(ResponseCode.SUCCESS, uid, newUser.mUsername, newUser.level, newUser.avatarID, newUser.money, aSession.getCurrentZone());

        // join's values
        resMatchJoin.setSuccess(ResponseCode.SUCCESS, room.getName(), this.firstCashBet, aSession.getCurrentZone());
        resMatchJoin.isPlaying = this.isPlaying;
        resMatchJoin.setRoomID(request.mMatchId);
        resMatchJoin.setCurrentPlayers(this.getNewPlayings(), this.getNewWaitings(), this.owner);

        joinResponse(resMatchJoin); //override this response if some game wants to have special info

        resMatchJoin.phongID = request.phongId;

        aSession.write(resMatchJoin);

        // hien thi broadcast mesage
//        this.mLog.debug("Joint sent message " + );
        broadcastMsg(broadcastMsg, this.getNewPlayings(), this.getNewWaitings(), newPlayer, false);
    }

    public void changeSetting(long money) {
        List<? extends SimplePlayer> playings = getNewPlayings();
        List<? extends SimplePlayer> waitings = getNewWaitings();
        if (playings != null) {
            int playingSize = playings.size();
            for (int i = 0; i < playingSize; i++) {
                SimplePlayer player = playings.get(i);
                if (player != null) {
                    //this is an old version we should send cancel message to him
                    player.moneyForBet = money;
                }

            }
        } else {
            return;
        }

        if (waitings != null) {
            int waitingSize = waitings.size();
            for (int i = 0; i < waitingSize; i++) {
                SimplePlayer player = waitings.get(i);
                if (player != null) {
                    //this is an old version we should send cancel message to him
                    player.moneyForBet = money;
                }
            }
        }

    }

    public void supRemOldVer(long newOwner, int protocolSupport) {
        List<? extends SimplePlayer> playings = getNewPlayings();
        List<? extends SimplePlayer> waitings = getNewWaitings();
        List<SimplePlayer> lstOldVersion = new ArrayList<SimplePlayer>();
        List<SimplePlayer> removePlayers = new ArrayList<SimplePlayer>();

        if (playings != null) {
            int playingSize = playings.size();
            for (int i = 0; i < playingSize; i++) {
                SimplePlayer player = playings.get(i);
//                if(player.currentSession != null && player.currentSession.getByteProtocol()< AIOConstants.PROTOCOL_MODIFY_MID)

                if (player.currentSession != null && player.currentSession.getByteProtocol() < protocolSupport) {
                    //this is an old version we should send cancel message to him
                    lstOldVersion.add(player);
                }

                if (player.isOut || player.notEnoughMoney()) {
                    removePlayers.add(player);
                }

            }
        } else {
            return;
        }

        if (waitings != null) {
            int waitingSize = waitings.size();
            for (int i = 0; i < waitingSize; i++) {
                SimplePlayer player = waitings.get(i);
                if (player.currentSession != null && player.currentSession.getByteProtocol() < AIOConstants.PROTOCOL_BETA
                        && !player.isOut) {
                    //this is an old version we should send cancel message to him
                    lstOldVersion.add(player);
                }
            }
        }

        int oldSize = lstOldVersion.size();
        int removeSize = removePlayers.size();

        if (removeSize == 0 || oldSize == 0) {
            return;
        }

        ISession notNullSession = getNotNullSession();
        if (notNullSession == null) {
            notNullSession = owner.currentSession;
        }

        MessageFactory msgFactory = notNullSession.getMessageFactory();

        for (int i = 0; i < removeSize; i++) {
            SimplePlayer removedPlayer = removePlayers.get(i);
            CancelResponse removeRes = (CancelResponse) msgFactory.getResponseMessage(MessagesID.MATCH_CANCEL);
            removeRes.setSuccess(ResponseCode.SUCCESS, removedPlayer.id);
            removeRes.newOwner = newOwner;
            for (int j = 0; j < oldSize; j++) {
                SimplePlayer oldVerPlayer = lstOldVersion.get(j);
                sendMsg(oldVerPlayer, removeRes);
            }
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getTableIndex()).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(this.getTableSize()).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(this.firstCashBet).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(this.matchID).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(this.maximumPlayer).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(this.isPlaying ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(this.name).append(AIOConstants.SEPERATOR_BYTE_1);
        try {
            int zoneId = this.room.getZoneID();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        sb.append(this.room.getZoneID());
        return sb.toString();
    }
}
