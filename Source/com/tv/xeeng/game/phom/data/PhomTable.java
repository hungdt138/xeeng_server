package com.tv.xeeng.game.phom.data;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.base.common.BlahBlahUtil;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.JoinResponse;
import com.tv.xeeng.base.protocol.messages.StartRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DBCache;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.*;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class PhomTable extends SimpleTable {

    public boolean testing = false;
    public int testCode = 0;
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(PhomTable.class);
    public ArrayList<PhomPlayer> playings = new ArrayList<>();
    private ArrayList<PhomPlayer> waitings = new ArrayList<>();

    //public ArrayList<PhomPlayer> forScores = new ArrayList<PhomPlayer>();
    //private final Object lockPlaying = new Object();
    //private final Object lockWaiting = new Object();
    //maximal number of players
    //public int maximumPlayer = 4;
    //list off rest cards
    public Vector<Poker> restCards = new Vector<>();
    public PhomPlayer currentPlayer;
    private PhomPlayer prePlayer;
    private PhomPlayer winner;
    private int currentIndexOfPlayer;
    private int firstRoundIndex;

    public boolean isUKhan = false;
    public boolean anCayMatTien = true; // default
    public boolean taiGuiUDen = false;
    private Poker currPoker = null;
    private Poker currPlayPoker = null;
    private boolean chot = false;
    private boolean haBai = false;
    public int turn = 0;
    private static final int AUTO_BOC_BAI = 28000;
    private static final int AUTO_DANH = 3000;
    private boolean isCheckCard = false;
    public boolean isPreparingfinish = false;
    public ArrayList<PhomPlayer> superUsers = new ArrayList<>();

    public boolean getChot() {
        return this.chot;
    }

    public boolean isFull() {
        return (playings.size() + waitings.size() >= getMaximumPlayer());
    }

    public PhomPlayer firstChot = null;
    private PhomPlayer preChot = null;
    public int haTurn = 0;
    //public int matchNum = 0;

    public boolean isAnyReady() {
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            PhomPlayer t = this.playings.get(i);
//            mLog.error("---THANGTD START DEBUG---" + t.username + " is ready: " + t.isReady);
            if (t.id != this.owner.id && !t.isReady) {
                return false;
            }
        }

        return true;
    }

    public long getJoinMoney() {
        return firstCashBet;
    }

    public int numRealPlaying() {
        int sum = 0;
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            PhomPlayer p = this.playings.get(i);
            if (!p.isAutoPlay) {
                sum++;
            }
        }
        return sum;
    }

    @Override
    public void destroy() {

        try {
            // timerAuto.destroy();
        } catch (Exception e) {
            //e.printStackTrace();
        }

        super.destroy();

        // timer.destroy();
    }

    public PhomPlayer getWinner() {
        return winner;
    }

    public PhomPlayer getPrePlayer() {
        return prePlayer;
    }

    public PhomPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    public int getCurrentIndexOfPlayer() {
        return currentIndexOfPlayer;
    }

    public Poker getCurrPoker() {
        return currPoker;
    }

    public Poker getCurrPlayPoker() {
        return currPlayPoker;
    }

    public void resetAllReady() {
        int playingSize = this.playings.size();

        for (int i = 0; i < playingSize; i++) {
            PhomPlayer p = this.playings.get(i);
            if (p.id != this.owner.id) {
                p.isReady = false;
            }
        }
    }

    public void setNewStarter(PhomPlayer player) {
        int index;
        try {
            index = indexOfPlayer(player);

            if (index >= 0) {
                setNewStarter(index);
            } else {
                setNewStarter(0);
            }
        } catch (PhomException ex) {
            mLog.error(ex.getMessage());
        }
    }

    private void setNewStarter(int index) {
        this.currentIndexOfPlayer = index;
        this.currentPlayer = playings.get(index);
        this.firstRoundIndex = index;
        this.prePlayer = this.currentPlayer;
    }

    public ArrayList<PhomPlayer> getPlayings() {
        return playings;
    }

    public ArrayList<PhomPlayer> clonePlaying() {
        ArrayList<PhomPlayer> players = new ArrayList<>();
        int playingSize = playings.size();
        for (int i = 0; i < playingSize; i++) {
            PhomPlayer player = playings.get(i);
            players.add(player);
        }

        return players;
    }

    public ArrayList<PhomPlayer> getWaitings() {
        return waitings;
    }

    public long getMoneyBet() {
        return firstCashBet;
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
            throw new BusinessException(Messages.FULL_PLAYER_MSG);
        }

//      player.setLastActivated(System.currentTimeMillis());
        if (isPlaying) {

            waitings.add((PhomPlayer) player);
            player.isMonitor = true;
//                    ((PhomPlayer)player).isObserve = true;
        } else {
            playings.add((PhomPlayer) player);
            player.isMonitor = false;
        }

        outCodeSB.append("player: ").append(player.username).append(" join").append(NEW_LINE);
        logMini.append(BlahBlahUtil.getLogString(String.format("[%s] vào bàn", player.username)));
    }

    @Override
    protected void joinResponse(JoinResponse joinResponse) {
        //override if there 's any game which has addition info in Join response
        if (this.isPlaying) {
            joinResponse.isObserve = true;
        }

        joinResponse.setCurrentPlayersPhom(this.getPlayings(), this.getWaitings(), this.owner);
        boolean isResume = false;
        String cards = "";
        joinResponse.setPhomInfo(this.anCayMatTien, this.taiGuiUDen, this.isPlaying, isResume, this.currentPlayer.id, cards, this.restCards.size());
        joinResponse.setCapacity(this.getMaximumPlayer());
    }

    @Override
    public boolean isFullTable() {
        return playings.size() + waitings.size() >= getMaximumPlayer();
    }

    // Create table
    public PhomTable(PhomPlayer ow, String na, long money, Room room) {
//        String fileName = "logNew/" + room.getZone().getZoneName()
//                + "/Phong_" + room.phongID + "/" + room.index + ".log";
        this.setRoom(room);
//        newLog = Zone.getLogger(fileName);
//        newLog.debug("Create: " + ow.username);
//        newLog.debug("MatchID: " + this.matchID);
        ow.table = this;
        ow.isReady = true;
        this.owner = ow;
        this.name = na;
        this.firstCashBet = money;
        this.playings.add((PhomPlayer) this.owner);
        this.currentIndexOfPlayer = 0;
        this.firstRoundIndex = 0;
        this.currentPlayer = (PhomPlayer) this.owner;
        currentPlayer.setCurrentOwner(this.ownerSession);
        this.prePlayer = this.currentPlayer;

        // timerAuto.setRuning(false);
        // timerAuto.start();
        logdir = "phom_log";

        //initLogFile();
    }
    /*
     @SuppressWarnings("unused")
     private void resetFirstRoundIndex() {
     if (this.firstRoundIndex == this.playings.size()) {
     this.firstRoundIndex = 0;
     } else {
     this.firstRoundIndex++;
     }
     }
     */

    public void setUKhan(boolean isUK) {
        this.isUKhan = isUK;
    }

    public boolean getUKhan() {
        return this.isUKhan;
    }

    public void setAnCayMatTien(boolean isAN) {
        this.anCayMatTien = isAN;
    }

    public void setTai(boolean isTai) {
        this.taiGuiUDen = isTai;
    }

    public void setNumber(int numberPalyer) {
        if ((numberPalyer < 4) && (numberPalyer > 1)) {
            this.maximumPlayer = numberPalyer;
        }
    }

    /**
     * *************************************************
     */
    // Player joined
    public void join(PhomPlayer player) throws PhomException {
        try {
            player.table = this;
            player.setLastActivated(System.currentTimeMillis());

            player.setCurrentOwner(this.ownerSession);
            player.currentMatchID = this.matchID;

            // System.out.println("last Real Acess : "+ownerSession.);
            if (isPlaying) {
                player.isObserve = true;
                waitings.add(player);
//                    newLog.debug("Join waiting: " + player.username);
            } else if (playings.size() < 4) {
                player.isObserve = false;
                this.playings.add(player);
//                    newLog.debug("Join playing: " + player.username);
            }

            mLog.debug("---THANGTD JOIN DEBUG PHOM---" + player.username + " is ready: " + player.isReady);
//            getOutCodeSB().append("Join player: ").append(player.username).append(NEW_LINE);
            getOutCodeSB().append(String.format("(%s vào bàn)", player.username)).append(NEW_LINE).append(NEW_LINE);
            logMini.append(BlahBlahUtil.getLogString(String.format("[%s] vào bàn", player.username)));

//            if (!isPlaying) {
//                for (PhomPlayer p : playings) {
//                    if (p.currentSession == null) {
//                        remove(p);
//                        break;
//                    }
//                }
//            }
        } catch (Exception e) {
            throw new PhomException(e.getMessage());
        }
    }

    public void removeObserver(PhomPlayer player) {
        try {
            int waitingSize = this.waitings.size();
            for (int i = 0; i < waitingSize; i++) {
                if (this.waitings.get(i).id == player.id) {
                    this.waitings.remove(player);
                }
            }
        } catch (Exception e) {
            if (player != null) {
                mLog.error(concatString(turnInfo(), " : ", player.username));
            } else {
                mLog.error(concatString(turnInfo(), " : remove Null"));
            }
        }

    }

    /**
     * *************************************************
     */
    // Player removed
    public void remove(PhomPlayer player) throws PhomException {
        try {
            getOutCodeSB().append(String.format("(kick người chơi %s)", player.username)).append(NEW_LINE).append(NEW_LINE);
            logMini.append(BlahBlahUtil.getLogString(String.format("Kick [%s]", player.username)));

            int playingSize = this.playings.size();

            for (int i = 0; i < playingSize; i++) {
                PhomPlayer p = this.playings.get(i);
                if (p.id == player.id) {
                    playings.remove(p);
                    return;
                }
            }

            int waitingSize = this.waitings.size();

            for (int i = 0; i < waitingSize; i++) {
                PhomPlayer p = this.waitings.get(i);
                if (p.id == player.id) {
                    waitings.remove(p);
                    return;
                }
            }

        } catch (Exception e) {
            if (player != null) {
                mLog.error(concatString(turnInfo(), " : ", player.username));
            } else {
                mLog.error(concatString(turnInfo(), " : remove Null"));
            }

            throw new PhomException(e.getMessage());
        }
    }

    public void chageMoney(long m) {
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            PhomPlayer p = this.playings.get(i);
            p.moneyForBet = m;
        }

        int waitingSize = this.waitings.size();
        for (int i = 0; i < waitingSize; i++) {
            PhomPlayer p = this.waitings.get(i);
            p.moneyForBet = m;
        }
    }

    /**
     * *************************************************
     */
    /*
     * Start game
     */
    public Duty duty;

    public void start() throws PhomException {

//        this.newLog.debug("Start:");
        this.restCards = new Vector<>();
        superUsers.clear();
        int playingSize = this.playings.size();

        for (int i = 0; i < playingSize; i++) {
            PhomPlayer p = this.playings.get(i);
            if (com.tv.xeeng.game.data.Utils.isSuperUser(p.id)) {
                superUsers.add(p);
            }
        }

        lastActivated = System.currentTimeMillis();
        this.isPlaying = false;
        haTurn = 0;
        if (turn > 0) {
            reset();
        }
        turn = 0;
        matchNum++;
        isCheckCard = false;
        if (waitings.size() > 0) {
            resetPlayers();
        }

        playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            PhomPlayer player = this.playings.get(i);
            player.momStatus = true;
        }

        if (playingSize > 1) {
            isPreparingfinish = false;
            this.isPlaying = true;
            chiabai();
            duty = new Duty();
//            getOutCodeSB().append("--------START Game--------").append(NEW_LINE);
            getOutCodeSB().append("[+] Thông tin người chơi").append(NEW_LINE);

            String playerList = "Danh sách người chơi: ";
            for (int i = 0; i < playingSize; i++) {
//                getOutCodeSB().append("player ").append(i).append(" id: ").append(playings.get(i).id).append(" userName: ").append(playings.get(i).username).append(" money: ").append(playings.get(i).money).append(NEW_LINE);
                getOutCodeSB().append(String.format("    - %d: %s (%d) - %d Gold", i, playings.get(i).username, playings.get(i).id, playings.get(i).cash)).append(NEW_LINE);

                playerList += String.format("[%s (%d)] ", playings.get(i).username, playings.get(i).id);
            }
            getOutCodeSB().append(NEW_LINE);

            logMini.append(BlahBlahUtil.getLogString(playerList));
        } else {
            throw new PhomException("Chua co nguoi choi cung!");
        }
    }

    public PhomPlayer checkUKhan() {
        if (this.isUKhan) {
            int temp = this.currentIndexOfPlayer;
            int terminalID;
            terminalID = this.playings.size();
//            if (temp == 0) {
//                terminalID = this.playings.size() - 1;
//            } else {
//                terminalID = temp - 1;
//            }
            while (temp != terminalID) {
                PhomPlayer p = this.playings.get(temp);
                if (p.isUkhan()) {
                    uType = 2;
                    this.winner = p;
                    this.winner.uType = 2;
//                    resetAutoKickOut();
//                    this.isPlaying = false;
                    this.isPreparingfinish = true;
                    currentPlayer.uStatus = true;
                    this.winner.isWin = true;
                    int playingSize = this.playings.size();
                    for (int i = 0; i < playingSize; i++) {
                        PhomPlayer player = this.playings.get(i);
                        if (player.id != p.id) {
                            player.cashLost.add(new Couple<>(this.currentPlayer.id, this.firstCashBet * 5));
                            p.cashWin.add(new Couple<>(p.id, this.firstCashBet * 5));
                            player.isWin = false;

                            player.resultMoney = -(this.firstCashBet * 5);
                            p.resultMoney += this.firstCashBet * 5;
                        }
                    }
                    //this.winner = p;
                    updateCash(1);

                    getOutCodeSB().append(String.format("========================[ Ù khan (người thắng: %s) ]========================", winner.username)).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE);

                    logMini.append(BlahBlahUtil.getLogString("Ùùùuuu khan"));
                    logMini.append(BlahBlahUtil.getLogString("*** Kết thúc ***"));
                    logMini.append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE);
                    saveLogToFile();

                    return p;
                }
//                else {
//                    if (temp == this.playings.size() - 1) {
//                        temp = 0;
//                    } else {
//                        temp++;
//                    }
//                }
                temp++;
            }
            return null;
        } else {
            return null;
        }
    }

    public boolean isHaBaiTurn() {
        return restCards.size() < this.playings.size();
//        int leftCard = restCards.size();
//        if (leftCard < this.playings.size()) {
//            return true;
//        }
//
//        return false;
    }

    public boolean lastTurn() {
        // int number = this.playings.size();
        int leftCard = restCards.size();

        return (leftCard <= 0);
    }

    public void createLogFile() {
        try {
//            getOutCodeSB().append("-----start game -----MatchId: ").append(matchID).append("-").append("Owner name:").append(owner.username).append(NEW_LINE);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            getOutCodeSB().append(String.format("========================[ Ván %d (chủ bàn: %s, %s) ]========================", matchID, owner.username, format.format(new Date()))).append(NEW_LINE);
            logMini.append(BlahBlahUtil.getLogString("*** Bắt đầu ***"));
            getOutCodeSB().append("[+] Tiền cược: ").append(firstCashBet).append(NEW_LINE);
            getOutCodeSB().append("[+] Thông tin chia bài").append(NEW_LINE);
            //show card
            int playingSize = playings.size();
            for (int i = 0; i < playingSize; i++) {
                PhomPlayer player = playings.get(i);
//                getOutCodeSB().append("Player index: ").append(i).append(" User name: ").append(player.username).append("Cards: ").append(player.showCards(player.allCurrentCards)).append(" pure card").append(player.showPureCards(player.allCurrentCards));

                getOutCodeSB().append(String.format("    - %s: %s", player.username, player.showCards(player.allCurrentCards))).append(NEW_LINE);
            }

            getOutCodeSB().append(NEW_LINE);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void chiabai() {
        int number = this.playings.size();
        if (currentIndexOfPlayer >= number) {
            setNewStarter((PhomPlayer) owner);
        }
        currentPlayer = playings.get(currentIndexOfPlayer);

        if (number > 4) {
            //return;
        } else {
            for (int j = 0; j < number; j++) {
                playings.get(j).setMoney(this.firstCashBet);
            }

            ArrayList<ArrayList<Poker>> res = chia(number);
            for (int j = 0; j < number; j++) {
                this.playings.get(j).setPokers(res.get(j));
            }

            this.currentPlayer.takeTenthPoker(this.restCards.lastElement());
            this.restCards.remove(this.restCards.size() - 1);
//            for (int j = 0; j < number; j++) {
//                this.playings.get(j).showCards();
//            }
            currentTimeOut = AUTO_BOC_BAI + AUTO_DANH;

            createLogFile();
        }
    }
    //tuanda-begin

    private boolean isInListPoker(int x, ArrayList<Poker> ps) {
        for (Poker p : ps) {
            if (p.toInt() == x) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Integer> chiaFixNguoi(ArrayList<Integer> currList, ArrayList<Poker> p) {
        ArrayList<Integer> res = new ArrayList<>();
        for (int x : currList) {
            if (!isInListPoker(x, p)) {
                res.add(x);
            }
        }
        return res;
    }

    @SuppressWarnings("unused")
    private void chiaFix() {

        //System.out.println("chia fix");
        for (int j = 0; j < 2; j++) {
            playings.get(j).setMoney(this.firstCashBet);
        }
        //ArrayList<ArrayList<Poker>> res = new ArrayList<ArrayList<Poker>>();
        ArrayList<Integer> currList = new ArrayList<>();
        for (int i = 1; i < 53; i++) {
            currList.add(i);
        }
        //Chu ban
        ArrayList<Poker> p2 = new ArrayList<>();
        p2.add(new Poker(8, PokerType.Co));
        p2.add(new Poker(9, PokerType.Co));
        p2.add(new Poker(10, PokerType.Co));
        p2.add(new Poker(12, PokerType.Co));
        p2.add(new Poker(4, PokerType.Ro));
        p2.add(new Poker(5, PokerType.Ro));
        p2.add(new Poker(6, PokerType.Ro));
        p2.add(new Poker(7, PokerType.Ro));
        p2.add(new Poker(4, PokerType.Tep));
        p2.add(new Poker(3, PokerType.Tep));
//        p2.add(new Poker(3, PokerType.Tep));
//        p2.add(new Poker(13, PokerType.Pic));
//        p2.add(new Poker(2, PokerType.Ro));
//        p2.add(new Poker(4, PokerType.Tep));
//        p2.add(new Poker(6, PokerType.Ro));
        currList = chiaFixNguoi(currList, p2);
        this.playings.get(1).setPokers(p2);
        ArrayList<Poker> p1 = new ArrayList<>();
        p1.add(new Poker(11, PokerType.Co));
        p1.add(new Poker(13, PokerType.Co));

        p1.add(new Poker(1, PokerType.Ro));
        p1.add(new Poker(1, PokerType.Tep));
        p1.add(new Poker(1, PokerType.Co));
        p1.add(new Poker(1, PokerType.Pic));
        p1.add(new Poker(4, PokerType.Pic));
        p1.add(new Poker(7, PokerType.Co));
        p1.add(new Poker(9, PokerType.Tep));
        p1.add(new Poker(10, PokerType.Pic));

//        p2.add(new Poker(8, PokerType.Co));
//        p2.add(new Poker(9, PokerType.Ro));
//        p2.add(new Poker(10, PokerType.Tep));
//        p2.add(new Poker(12, PokerType.Pic));
//        
//        p2.add(new Poker(2, PokerType.Co));
//        p2.add(new Poker(3, PokerType.Ro));
//        p2.add(new Poker(4, PokerType.Tep));
//        p2.add(new Poker(5, PokerType.Pic));
//        p2.add(new Poker(6, PokerType.Ro));
//            currList = chiaFixNguoi(currList, p2);
//        this.playings.get(1).setPokers(p2);
//        ArrayList<Poker> p1 = new ArrayList<Poker>();
//        p1.add(new Poker(11, PokerType.Co));
//        p1.add(new Poker(13, PokerType.Co));
//        
//        p1.add(new Poker(1, PokerType.Ro));
//        p1.add(new Poker(1, PokerType.Tep));
//        p1.add(new Poker(1, PokerType.Co));
//        p1.add(new Poker(1, PokerType.Pic));
//        p1.add(new Poker(4, PokerType.Pic));
//        p1.add(new Poker(7, PokerType.Co));
//        p1.add(new Poker(9, PokerType.Tep));
//        p1.add(new Poker(10, PokerType.Pic));
//        currList = chiaFixNguoi(currList, p1);
//        this.playings.get(0).setPokers(p1);
//        ArrayList<Poker> p3 = new ArrayList<Poker>();
//        currList = chiaFixNguoi(currList, p3);
//        ArrayList<Poker> p4 = new ArrayList<Poker>();
//        currList = chiaFixNguoi(currList, p4);
        for (int x : currList) {
            Poker p = Utils.numToPoker(x);
            this.restCards.add(p);
        }

        currentTimeOut = AUTO_BOC_BAI + AUTO_DANH;
        //return res;

        //return res;
    }

    private ArrayList<ArrayList<Poker>> chia(int number) {
        ArrayList<ArrayList<Poker>> res = new ArrayList<>();
        ArrayList<Integer> currList = Utils.getRandomList();

        for (int i = 0; i < number; i++) {
            ArrayList<Poker> p = new ArrayList<>();
            for (int j = 0; j < 9; j++) {
                Poker temp = Utils.numToPoker(currList.get(9 * i + j));
                p.add(temp);
            }
            res.add(p);
        }
        /**
         * all rest cards 2 players --> 8 cards 3 players --> 12 cards 4 players
         * --> 16 cards
         */
        for (int j = 0; j < 4 * number; j++) {
            Poker p = Utils.numToPoker(currList.get(9 * number + j));
            this.restCards.add(p);
        }
        return res;
    }

    public int uType = 0;

    public void processU(int u) {
        try {
            uType = u;
            this.winner = this.currentPlayer;
            this.winner.uType = u;
//            resetAutoKickOut();
//            this.isPlaying = false;
            this.isPreparingfinish = true;
            currentPlayer.uStatus = true;
            int bonusTime = 1;

//            int temp = duty.checkDuty(winner);
//            if (temp > 0) {
//                isTakeDuty = true;
//                bonusTime = temp;
//            }
//            mLog.debug("Duty0:"+isTakeDuty + "--"+winner.id);
            // U den
            if (firstChot != null && currentPlayer.id == firstChot.id) {
                firstChot = preChot;
            }

            // 0: Khong U | 1: U 3 phom bt | 2: U khan | 3: U 0 diem - gui het bai | 11: U den | 12: Tai gui u den
            // 11: U den | 12: Tai gui u den
            if ((this.currentPlayer.uType == 11) || (this.currentPlayer.uType == 12 || currentPlayer.eatingCards.size() == 3)) {
                PhomPlayer preP = getPrePlayer(this.currentPlayer);
                preP.cashLost.add(new Couple<>(this.currentPlayer.id, this.firstCashBet * 5 * (this.playings.size() - 1)));
                this.currentPlayer.cashWin.add(new Couple<>(preP.id, this.firstCashBet * 5 * (this.playings.size() - 1)));
                this.currentPlayer.isWin = true;

                preP.resultMoney = -(this.firstCashBet * 5 * (this.playings.size() - 1));
                this.currentPlayer.resultMoney = this.firstCashBet * 5 * (this.playings.size() - 1);
            } else if (this.currentPlayer.uType == 1 || uType == 3) { // 1: U 3 phom bt | 3: U 0 diem - gui het bai
                this.currentPlayer.isWin = true;

                for (PhomPlayer p : this.playings) {
                    if (p.id != this.currentPlayer.id) {
                        p.cashLost.add(new Couple<>(this.currentPlayer.id, this.firstCashBet * 5));
                        this.currentPlayer.cashWin.add(new Couple<>(p.id, this.firstCashBet * 5));

                        p.resultMoney = -(this.firstCashBet * 5);
                        this.currentPlayer.resultMoney += this.firstCashBet * 5;
                    }
                }
            } else if (firstChot != null && currentPlayer.id != firstChot.id) {
                firstChot.cashLost.add(new Couple<>(this.currentPlayer.id, this.firstCashBet * 5 * (this.playings.size() - 1)));
                this.currentPlayer.cashWin.add(new Couple<>(firstChot.id, this.firstCashBet * 5 * (this.playings.size() - 1)));
                currentPlayer.isWin = true;

                firstChot.resultMoney = -(this.firstCashBet * 5 * (this.playings.size() - 1));
                this.currentPlayer.resultMoney = this.firstCashBet * 5 * (this.playings.size() - 1);
            }

            updateCash(bonusTime);
            this.isPlaying = false;
//            forScores = new ArrayList<PhomPlayer>();
//            for (PhomPlayer p : playings) {
//                forScores.add(p);
//            }
//            getOutCodeSB().append("----End game --- Winner -- ").append(winner.username).
//                    append("duty").append(isTakeDuty ? "1" : "0").append(NEW_LINE);
//            getOutCodeSB().append("----------------------------------").append(NEW_LINE);

            getOutCodeSB().append(String.format("========================[ Ù (người thắng: %s) ]========================", winner.username)).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE);

            logMini.append(BlahBlahUtil.getLogString("Ùùùuuu"));
            logMini.append(BlahBlahUtil.getLogString("*** Kết thúc ***"));
            logMini.append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE);

            saveLogToFile();

        } catch (Exception e) {
            //e.printStackTrace();
            mLog.error(concatString("process U error ", e.getMessage()), e.getStackTrace());
        }
    }

    /**
     * *************************************************
     */
    // Play
    public void play(long uid, Poker card, boolean isAuto) throws PhomException {
//        if (!isCheckCard) {
//            isCheckCard = true;
//            if (turn == 0)//make sure it 's first time
//            {
//                int trueCards = this.playings.size() * 4 - 1;
//                int cardsInBox = restCards.size();
//                if (trueCards != cardsInBox) {
//                    try {
//                        mLog.warn("error number cards in PHom matchId " + matchID);
////                        int count = 0;
//                        for (int i = trueCards; i < cardsInBox; i++) {
//                            restCards.remove(0);
//                        }
//                    } catch (Exception ex) {
//                        mLog.error(ex.getMessage(), ex);
//                    }
//                }
//            }
//        }

        int playerCardSize = this.currentPlayer.allCurrentCards.size() + this.currentPlayer.getPhomSize();
        if (uid == this.currentPlayer.id && (playerCardSize == 10 || isAuto)) {
            try {
//                newLog.debug(this.currentPlayer.username + " play, uid = " + uid + ", card = " + card.toString());
                if (playerCardSize != 10) {
                    mLog.warn(concatString("error phom matchId ", String.valueOf(matchID), " numberCard ", String.valueOf(playerCardSize)));
//                    outCodeSB.append("error number card ").append(playerCardSize).append(" all current card:").
//                            append(this.currentPlayer.allCurrentCards.size()).append(NEW_LINE);
                    outCodeSB.append(String.format("[+] /!\\ Lỗi: Số lá bài của người chơi là %d", playerCardSize)).append(NEW_LINE);
                }

                currPlayPoker = card;
                this.currPoker = card;
//                System.out.println("Play curent poker: " + this.currPoker.toString());

                if (card == null) {
                    mLog.error(concatString(turnInfo(), ": play : card null ! ; uid=", String.valueOf(uid)));
//                    getOutCodeSB().append(turnInfo()).append(": play : card null ! ; uid=").append(uid);

                    getOutCodeSB().append(turnInfo()).append(String.format("[+] /!\\ Lỗi: %s đánh [null]", currentPlayer.username)).append(NEW_LINE).append(NEW_LINE);
                } else {
//                    getOutCodeSB().append(turnInfo()).append(": play : ").append(card.toString()).append(" cardint: ").append(card.toInt()).append(" ; left : ").append(currentPlayer.allCurrentCards.size()).append(NEW_LINE);
//                         getOutCodeSB().append(String.format("    - Còn %d lá trong bộ bài và %d lá trong phỏm", currentPlayer.allCurrentCards.size(), currentPlayer.getPhomSize())).append(NEW_LINE).append(NEW_LINE);
                    getOutCodeSB().append(turnInfo()).append(String.format("[+] %s đánh [%s]", currentPlayer.username, card.toString())).append(NEW_LINE).append(NEW_LINE);
                }

                this.currentPlayer.play(card);

                if (currentPlayer.allCurrentCards.isEmpty()) {
                    if (currentPlayer.eatingCards.size() == 3) {
                        currentPlayer.uType = 11;
                    } else {
                        currentPlayer.uType = 1;
                    }
//                    newLog.debug("Ù r?i");
                    processU(currentPlayer.uType);
//                    winner = currentPlayer;
                    return;
                } else {
                    setChot();
                    next();
                }

                if (this.isPlaying == false || this.isPreparingfinish) {
//                    newLog.debug("Kết thúc ván");
                    if (DBCache.isUsePhom) {
                        //Thu tu ha de xac dinh mom truoc co loi
                        int i = indexOfPlayer(this.currentPlayer);
                        int j;
                        int indexStop = 0;
                        this.currentPlayer.setStoppingOrder(indexStop);
                        if (i == this.playings.size() - 1) {
                            j = 0;
                        } else {
                            j = i + 1;
                        }

                        int count = 0;
                        while (j != i && count < 4) {
                            indexStop++;
                            count++;
                            this.playings.get(j).setStoppingOrder(indexStop);
                            if (j == this.playings.size() - 1) {
                                j = 0;
                            } else {
                                j++;
                            }
                        }
                    }
                    postProcess();
                }

            } catch (PhomException ex) {
//                getOutCodeSB().append("!!!!!!!!!!!error  play").append("uid: ").append(uid).append("current userId: ").append(this.currentPlayer.id).append(" MatchID: ").append(matchID).append(turnInfo()).append(" number cards: ").append(this.currentPlayer.allCurrentCards.size()).append(NEW_LINE);

                getOutCodeSB().append(turnInfo()).append(String.format("[+] /!\\ Lỗi: Không đánh được bài - bộ bài có %d lá", this.currentPlayer.allCurrentCards.size())).append(NEW_LINE).append(NEW_LINE);

                mLog.error(concatString("!!!!!!!!!!!error ", turnInfo(), ": play : ", card.toString(), " ; uid=", String.valueOf(uid), " ; Current:" + " matchId:", String.valueOf(matchID)), ex);

                throw ex;
            }
        } else {
//            getOutCodeSB().append("!!!!!!!!!!!error  play").append("uid: ").append(uid).append("current userId: ").append(this.currentPlayer.id).append(" MatchID: ").append(matchID).append(turnInfo()).append(" number cards: ").append(this.currentPlayer.allCurrentCards.size()).append(NEW_LINE);
            getOutCodeSB().append(turnInfo()).append(String.format("[+] /!\\ Lỗi: Không đánh được bài - bộ bài có %d lá", this.currentPlayer.allCurrentCards.size())).append(NEW_LINE).append(NEW_LINE);

            mLog.error(concatString("!!!!!!!!!!!error ", turnInfo(), ": play : ", card.toString(), " ; uid=", String.valueOf(uid), " ; Current:" + " matchId:", String.valueOf(matchID)));

            throw new PhomException(concatString(String.valueOf(uid), " dang oanh bua ne!"));
        }
    }

    public String turnInfo() {
//            String infoTurn = getNewInforTurn();
        StringBuilder sb = new StringBuilder();
//        sb.append("---------turn info--------------------------").append(NEW_LINE);

        try {
//            sb.append("turn : ").append(turn / playings.size() + 1).append(" id : ").append(currentPlayer.id).append(" userName: ").append(currentPlayer.username).append(" current index: ").append(indexOfPlayer(currentPlayer)).append(" rescard size: ").append(restCards.size()).append(NEW_LINE);
            sb.append(String.format("[+] Lượt %d", turn / playings.size() + 1)).append(NEW_LINE);
            sb.append(String.format("    - Người chơi: %s (%d)", currentPlayer.username, currentPlayer.id)).append(NEW_LINE);
            sb.append(String.format("    - Vị trí trong bàn: %d", indexOfPlayer(currentPlayer))).append(NEW_LINE);
            sb.append(String.format("    - Số lá bài trong nọc: %d", restCards.size())).append(NEW_LINE);
            sb.append(NEW_LINE);
            sb.append(NEW_LINE);
        } catch (PhomException ex) {
            mLog.error(ex.getMessage(), ex.getStackTrace());
        }

//        sb.append("--------------------------------------").append(NEW_LINE);
        return sb.toString();
    }

    /**
     * *************************************************
     */
    // Boc
    public Poker getCard(long uid) throws PhomException {
//		out_code.println("[getCard]: uid : " + uid + " ; current : "
//				+ currentPlayer.id + " ; rescard : " + restCards.size());
        if (uid == this.currentPlayer.id && !currentPlayer.doneBocBai) {
            // if (true){
            // System.out.println("1: "+this.restCards.size());
            int rescardSize = restCards.size();
//            getOutCodeSB().append("[getCard]: uid : ").append(uid).append("; rescard : ").append(rescardSize).append(NEW_LINE);

            int lastCard = rescardSize - 1;
            Poker res = this.restCards.get(lastCard);
//            newLog.debug(this.currentPlayer.username + ", uid = " + uid + " b?c:" + res.toString());
//            newLog.debug("Chot:" + chot);
            // System.out.println("2: "+this.restCards.size());
            this.restCards.remove(lastCard);
            // System.out.println("3:"+this.restCards.size());
            this.currentPlayer.take(res);
            this.currPlayPoker = null;

//            getOutCodeSB().append("Boc card: ").append(res.toInt()).append(":").append(res.toString()).append(NEW_LINE);
            getOutCodeSB().append(String.format("[+] %s bốc lên [%s]", currentPlayer.username, res.toString())).append(NEW_LINE).append(NEW_LINE);
            return res;
        } else {
//                    out_code.println("!!!!!!!!!!!error getCard ");
//            getOutCodeSB().append("!!!!!!!!!!!error  getCard").append("uid: ").append(uid).append(" current userId ").append(this.currentPlayer.id).append(" MatchID: ").append(matchID).append(turnInfo()).append(NEW_LINE);

            getOutCodeSB().append(String.format("[+] /!\\ Lỗi: %s không bốc được bài", currentPlayer.username)).append(NEW_LINE).append(NEW_LINE);

            mLog.debug(concatString("!!!!!!!!!!!error ", turnInfo(), ": play : ", " ; uid=", String.valueOf(uid), " ; Current:", String.valueOf(this.currentPlayer.id), " matchId:", String.valueOf(matchID)));
            throw new PhomException(concatString(String.valueOf(uid), " dang oanh bua"));
        }
    }

    /**
     * *************************************************
     */
    // An
    public long eat(long uid) throws PhomException, BusinessException {
        long res = 0;
//                out_code.println("eat with uid:" + uid);

        if (uid == this.currentPlayer.id && !this.currentPlayer.doneBocBai) {
//            getOutCodeSB().append("Eat card with uid: ").append(uid).append(NEW_LINE);

            getOutCodeSB().append(String.format("[+] %s ăn bài", this.currentPlayer.username)).append(NEW_LINE).append(NEW_LINE);

            PhomPlayer prePlayerTemp = getPrePlayer(this.currentPlayer);
//            newLog.debug(this.currentPlayer.username + " an" + this.currPoker.toString());
//            newLog.debug("Chot:" + chot);

            preChot = firstChot;
            if (prePlayerTemp.haPhom /*&& firstChot == null*/) {
                firstChot = currentPlayer;
            }

//            if (prePlayer.haPhom && firstChot == null) {
//                firstChot = currentPlayer;
//            }
            if (restCards.size() <= playings.size()) {
                chot = true;
            } else {
                chot = false;
            }

            this.currPlayPoker = null;

            res = this.currentPlayer.eat(this.currPoker, prePlayerTemp, this.chot, this.anCayMatTien, round());
        } else {
//            getOutCodeSB().append("!!!!!!!!!!!error  eat").append("uid: ").append(uid).append(" current userId ").append(this.currentPlayer.id).append(" MatchID: ").append(matchID).append(turnInfo()).append(NEW_LINE);

            getOutCodeSB().append(String.format("[+] /!\\ Lỗi: Không ăn được bài")).append(NEW_LINE).append(NEW_LINE);

//                        out_code.println("!!!!!!!!!!!error eat");
//            mLog.debug("!!!!!!!!!!!error " + turnInfo() + ": play : " + " ; uid="
//                    + uid + " ; Current:" + this.currentPlayer.id + " matchId:" + matchID);
            if (this.currentPlayer.doneBocBai) {
                throw new PhomException(concatString(String.valueOf(uid), " an sau khi boc"));
            } else {
                throw new PhomException(concatString(String.valueOf(uid), " dang oanh bua"));
            }
        }
        return res;
    }

    private int round() {
        return 4 - (restCards.size() / playings.size());
    }

    public boolean containPlayer(long id) {
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            if (playings.get(i).id == id) {
                return true;
            }
        }

        return false;
    }

    /**
     * *************************************************
     */
    /*
     * Ha phom 0: Khong U 1: U 3 phom 2: UKhan
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ArrayList<ArrayList<Integer>> haPhom(long uid, ArrayList<ArrayList<Integer>> cards, int u, int card) throws PhomException, SimpleException {
        if (!this.isPlaying) {
            throw new PhomException("Ván chơi đã kết thúc!");
        }
        if (cards == null) {
            cards = new ArrayList();
        }

        PhomPlayer player = findPlayer(uid);

//        getOutCodeSB().append("Ha phom: ").append(" userName ").append(player.username).append(" Ha:").append(cards).append(NEW_LINE);
        StringBuilder sbPhom = new StringBuilder();
        StringBuilder rawPhom = new StringBuilder();
        for (ArrayList<Integer> phom : cards) {
            sbPhom.append("(");
            rawPhom.append("(");
            for (Integer b : phom) {
                sbPhom.append("[").append(Utils.numToPoker(b)).append("], ");
                rawPhom.append("[").append(b).append("], ");
            }
            sbPhom.delete(sbPhom.length() - 2, sbPhom.length());
            rawPhom.delete(rawPhom.length() - 2, rawPhom.length());
            sbPhom.append("), ");
            rawPhom.append("), ");
        }
        sbPhom.delete(sbPhom.length() - 2, sbPhom.length());
        rawPhom.delete(rawPhom.length() - 2, rawPhom.length());

        getOutCodeSB().append(String.format("[+] Hạ phỏm")).append(NEW_LINE);
        getOutCodeSB().append(String.format("    - Người chơi: %s", player.username)).append(NEW_LINE);
        getOutCodeSB().append(String.format("    - Phỏm: %s", sbPhom)).append(NEW_LINE).append(NEW_LINE);
        getOutCodeSB().append(String.format("    - Phỏm Raw: %s", rawPhom)).append(NEW_LINE).append(NEW_LINE);

        if (player.isAllowHa(cards)) {
            ArrayList<ArrayList<Integer>> res = new ArrayList<>();
            switch (u) {
                case 0: {// Khong U

                    if (this.restCards.size() > this.playings.size() + 1) {
                        throw new PhomException("Bạn không được phép hạ bài");
                    }

                    if (!player.isAutoPlay && player.outOfTime) {
                        lastActivated = System.currentTimeMillis();
                        currentTimeOut = 10000;
                    }

                    player.doneHaBai = true;
                    try {
                        player.haPhom = true;
                        if (cards.isEmpty()) {
                            if (!player.momStatus) {
                                try {
                                    String autoPhom = player.getPhom();
                                    cards = player.getCards(autoPhom);
                                    mLog.warn("error ha phom matchId " + matchID + " auto phom: " + autoPhom);
                                } catch (Exception ex) {
                                    mLog.error(ex.getMessage(), ex);
                                }
                            }
                        } else {
                            player.momStatus = false;
                        }
                        if (cards.size() > 0) {
                            for (ArrayList<Integer> test : cards) {
                                if (test.size() < 3) {
                                    throw new BusinessException("Bạn hạ sai rồi!");
                                }
                            }
                        }
                        haTurn++;
                        if (player.stoppingOrder == 0) {
                            player.stoppingOrder = haTurn;
                        }
                        ArrayList<Phom> phoms = player.phoms;
                        if (cards != null & cards.size() > 0) {
                            int cardsLen = cards.size();
                            for (int cL = 0; cL < cardsLen; cL++) {
                                ArrayList<Integer> a = cards.get(cL);
                                Vector<Poker> temp = new Vector<>();
                                boolean flagNewPhom = true;
                                for (int i : a) {
                                    Poker p = Utils.numToPoker(i);
                                    if (!player.hasPoker(p)) {
                                        flagNewPhom = false;
                                    }

                                    player.removePoker(p);
                                    temp.add(p);

                                }

                                phoms.add(new Phom(temp));

                                if (flagNewPhom) {
                                    player.setPhomSize(player.getPhomSize() + temp.size());
                                }

                            }
                        }
                        player.setPhoms(phoms);
                    } catch (Exception e) {
                        throw new PhomException("Phom ko dung");
                    }
                    res = cards;
                    break;
                }
                case 3:
                case 11:
                case 1: {// U

                    if (player.checkU(card, this.taiGuiUDen, this.haBai, cards)) {
                        player.doneHaBai = true;
                        player.uStatus = true;

                        if (!player.isAutoPlay && player.outOfTime) {
                            lastActivated = System.currentTimeMillis();
                            currentTimeOut = 10000;
                        }
                        int phomLen = player.phoms.size();

                        for (int pL = 0; pL < phomLen; pL++) {
                            Phom phom = player.phoms.get(pL);
                            ArrayList<Integer> temp = new ArrayList<>();
                            int phomCardLen = phom.cards.size();
                            for (int pCL = 0; pCL < phomCardLen; pCL++) {
                                Poker p = phom.cards.get(pCL);
                                temp.add(p.toInt());
                            }
                            res.add(temp);
                        }
                        if (player.eatingCards.size() == 3) {
                            player.uType = 11;
                        } else {
                            player.uType = u;
                        }
                        currentPlayer = player; //fix u of old player
                        processU(player.uType);
                    } else {
                        throw new PhomException("Khong phai U ban oi");
                    }
                    break;
                }
//                case 2: {// UKhan
//                    if (this.isUKhan) {
//                        this.uType = 2; //  fix u tron(send error utype= 2 :(
//                    } else {
//                        throw new PhomException("Khong choi U khan!");
//                    }
//                    break;
//                }
                default:
                    throw new PhomException("Bạn không được phép hạ bài");
            }
            return res;
        } else {
//            getOutCodeSB().append("!!!!!!!!!!!error  eat").append("uid: ").append(uid).append(" current userId ").append(player.id).append(" MatchID: ").append(matchID).append(turnInfo()).append(NEW_LINE);

            getOutCodeSB().append("[+] /!\\ Lỗi: Không được phép hạ bài").append(NEW_LINE).append(NEW_LINE);

//            mLog.debug("!!!!!!!!!!!error " + turnInfo() + ": play : " + " ; uid="
//                    + uid + " ; Current:" + player.id + " matchId:" + matchID);
            throw new PhomException("Bạn không được phép hạ bài");
        }
    }

    /**
     * *************************************************
     */
    // Gui
    public boolean gui(long sUid, ArrayList<Integer> cards, long dUid, int phomID) throws PhomException {
        Vector<Poker> temp = new Vector<>();
        boolean flagNewPhom = true;
        int cardsLen = cards.size();

        if (cardsLen > 0) {
            getOutCodeSB().append("[+] Gửi bài: ");
        }

        for (int cL = 0; cL < cardsLen; cL++) {
            int i = cards.get(cL);
            Poker p = Utils.numToPoker(i);
            temp.add(p);
            if (!currentPlayer.hasPoker(p)) {
                flagNewPhom = false;
            }
            getOutCodeSB().append(String.format("[%s] ", p));
        }

        if (cardsLen > 0) {
            getOutCodeSB().append(NEW_LINE).append(NEW_LINE);
        }

        try {

            PhomPlayer dPlayer = findPlayer(dUid);
            PhomPlayer sPlayer = findPlayer(sUid);
            if (flagNewPhom) {
                currentPlayer.setPhomSize(currentPlayer.getPhomSize() + cards.size());
            }
//            newLog.debug(sPlayer.username + " g?i d?n " + dPlayer.username);
//            newLog.debug("Card: " + temp.toString());
            //newLog.debug("Ph?m: " + dPlayer.getPhomVector().get(phomID).toString());
            dPlayer.guiED(phomID, temp);
            sPlayer.gui(temp);
            sPlayer.isStop = true;

            if (sPlayer.point == 0 || sPlayer.allCurrentCards.size() <= 1) { // U
                sPlayer.uType = 3;
                processU(sPlayer.uType);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            //e.printStackTrace();
            throw new PhomException("Khong tim dc nguoi de gui");
        }
    }

    /**
     * *************************************************
     */
    // Het van - tinh tien
    public void postProcess() throws PhomException {
        // this.currentPlayer.setStoppingOrder(this.playings.size());

        int len = playings.size();

        getOutCodeSB().append(String.format("[+] Tính điểm")).append(NEW_LINE);

        for (int i = 0; i < len; i++) {
            PhomPlayer player = this.playings.get(i);
            player.computeFinalPoint();

            getOutCodeSB().append(String.format("    - %s: %d điểm", player.username, player.point)).append(NEW_LINE);
        }

        getOutCodeSB().append(NEW_LINE);

        PhomPlayer[] players = new PhomPlayer[len];
        for (int i = 0; i < len; i++) {
            players[i] = playings.get(i);
        }

        // Utils.quicksortPhomPlayers(0, this.playings.size() - 1, players);
        // sort
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len - i - 1; j++) {
                // if (players[j].point > players[j + 1].point)
                if (!players[j].isWin(players[j + 1])) {
                    PhomPlayer tmp = players[j];
                    players[j] = players[j + 1];
                    players[j + 1] = tmp;
                }
            }
        }

        winner = players[0];
        int bonusTime = 1;
        //mLog.debug("PhomDuty0 UID:" + winner.id);

        int temp = duty.checkDuty(winner);
        if (temp > 0) {
            bonusTime = temp;
            isTakeDuty = true;
        }
        //mLog.debug("Duty0:" + isTakeDuty);

//		if (winner == null) {
//			System.out.println("Winner is still null.");
//		}
        winner.isWin = true;

        //int playingSize = playings.size();
        for (int i = 0; i < len; i++) {
//			System.out.println(i + ": " + players[i].username + " : "
//					+ players[i].point + " : " + players[i].moneyCompute()
//					+ " : " + players[i].stoppingOrder);
            players[i].setExperience(len - i); // add experience after match
        }

        for (int i = players.length - 1; i > 0; i--) {
            PhomPlayer p = players[i];
            int heso = i + 4 - players.length;
            if (!p.momStatus) {
                p.cashLost.add(new Couple<>(this.winner.id, this.firstCashBet * heso));
                this.winner.cashWin.add(new Couple<>(p.id, this.firstCashBet * heso));

                p.resultMoney = -(this.firstCashBet * heso);
                this.winner.resultMoney += this.firstCashBet * heso;
//                mLog.debug("---RESULT MONEY---" + p.username + " " + p.resultMoney);
//                mLog.debug("---RESULT MONEY WINNER---" + this.winner.username + " " + this.winner.resultMoney);
            } else {
                p.cashLost.add(new Couple<>(this.winner.id, this.firstCashBet * 4));
                this.winner.cashWin.add(new Couple<>(p.id, this.firstCashBet * 4));

                p.resultMoney = -(this.firstCashBet * 4);
                this.winner.resultMoney += this.firstCashBet * 4;
//                mLog.debug("---RESULT MONEY---" + p.username + " " + p.resultMoney);
//                mLog.debug("---RESULT MONEY WINNER---" + this.winner.username + " " + this.winner.resultMoney);
            }
        }

        updateCash(bonusTime);
//        forScores = new ArrayList<PhomPlayer>();
//        for (PhomPlayer p : playings) {
//            forScores.add(p);
//        }

//		out.println();
//		out.println("//EndGame :  winner: " + winner.username);
//		out.println("//------------------------------------");
//		out.flush();
//        getOutCodeSB().append("----End game --- Winner -- ").append(winner.username).append(NEW_LINE);
//        getOutCodeSB().append("----------------------------------").append(NEW_LINE).append(NEW_LINE);
        getOutCodeSB().append(String.format("========================[ Kết thúc (người thắng: %s) ]========================", winner.username)).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE);

        logMini.append(BlahBlahUtil.getLogString("*** Kết thúc ***"));
        logMini.append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE);
        saveLogToFile();
    }

    public ArrayList<PhomPlayer> allOutJustOne(long uid) {
        logMini.append(BlahBlahUtil.getLogString("Tất cả rời phòng (còn lại chủ phòng)"));

        ArrayList<PhomPlayer> res = new ArrayList<>();
        try {
            int len = playings.size();
            for (int i = 0; i < len; i++) {
                if (this.playings.get(i).id == uid) {
                    //winner is me
                    this.winner = this.playings.get(i);
                    this.winner.cashWin.add(new Couple<>(this.winner.id, this.firstCashBet * 4));

                    this.winner.resultMoney += this.firstCashBet * 4;
                } else {
                    this.playings.get(i).cashLost.add(new Couple<>(this.owner.id, this.firstCashBet * 4));

                    this.playings.get(i).resultMoney = -(this.firstCashBet * 4);
                }

                res.add(this.playings.get(i));
            }

            winner.isWin = true;
            updateCash(1);

            getOutCodeSB().append(String.format("========================[ Kết thúc (người thắng: %s) ]========================", winner.username)).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE);

            logMini.append(BlahBlahUtil.getLogString("*** Kết thúc ***"));
            logMini.append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append(NEW_LINE);
            saveLogToFile();

            saveLogToFile();
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return res;
    }

    @Override
    protected List<SimplePlayer> removeNotEnoughMoney() {
        List<SimplePlayer> removedPlayers = new ArrayList<>();
        boolean isChangeOwner = false;
        int playingSize = this.playings.size();

        for (int i = 0; i < playingSize; i++) {
            if (playings.get(i).notEnoughMoney()) {
                removedPlayers.add(playings.get(i));
            }
        }
        int rmSize = removedPlayers.size();
        for (int i = 0; i < rmSize; i++) {
            if (removedPlayers.get(i).id == owner.id) {
                isChangeOwner = true;

            }
            playings.remove(removedPlayers.get(i));
        }

        if (isChangeOwner) {
            if (playings.size() > 0) {
                owner = playings.get(0);
//                resetAutoKickOut();
            }
        }
        this.setOwnerId(owner.id);

        return removedPlayers;
    }

    public boolean isTakeDuty = false;

    private void updateCash(int bonusTime) {
        String moneyChanges = "";

//        if (bonusTime >= 2) {
//            isCheckCard = true;
//            isTakeDuty = true;
//        }
        try {
            int totalMoneyWin = 0;
            int totalMoneyWinNew = 0;
            UserDB userDb = new UserDB();
            String desc = concatString("Phom: ", String.valueOf(matchID));
            int logtypeId = LOG_TYPE_GAME_START + ZoneID.PHOM;
            boolean havingMinusBalance = false;
            int playingSize = playings.size();

            moneyChanges += String.format("Thay đổi Gold (%d người chơi): ", playingSize);

            for (int i = 0; i < playingSize; i++) {
                PhomPlayer player = playings.get(i);

                moneyChanges += String.format("[%s - Win=%s]", player.username, player.isWin ? "T" : "F");
                if (!player.isWin) {
//                    long plus = player.moneyLost() * bonusTime;
                    long plus = player.moneyLost();
                    long plusNew = player.moneyLostNew();

                    // : fix(error game -> hack money)
                    if (winner.cash < -plus) {
                        plus = -winner.cash;
                    }

                    if (winner.cash < plusNew) {
                        plusNew = winner.cash;
                    }

                    if (player.cash < -plus) {
                        plus = -player.cash;
                    }

                    if (player.cash < plusNew) {
                        plusNew = player.cash;
                    }

//                    boolean isWin = playings.get(i).id == winner.id;
//                    if (isWin && plus > 0) {
//                        plus = (long) (plus - plus * REAL_GOT_MONEY);
//                    }
//                    mLog.debug("---THANGTD TAX DEBUG---" + player.username + " PLUS AFTER CONVERTING: " + plus);
//                    mLog.debug("---THANGTD TAX DEBUG---" + player.username + " PLUS NEW AFTER CONVERTING: " + plusNew);
                    totalMoneyWin += plus;
                    totalMoneyWinNew += plusNew;
//                    mLog.debug("---THANGTD TAX DEBUG---TOTAL MONEY WIN BEFORE TAX: " + totalMoneyWin);
//                    mLog.debug("---THANGTD TAX DEBUG---TOTAL MONEY WIN NEW BEFORE TAX: " + totalMoneyWinNew);

                    player.money = plus;
//                    player.money = plusNew;

                    long oldCash = player.cash;
//                    player.cash = userDb.updateUserMoney(plus, true, playings.get(i).id, desc, playings.get(i).getExperience(), logtypeId);
                    player.cash = userDb.updateUserMoney(plus, true, player.id, desc, player.getExperience(), logtypeId);

                    // moneyChanges += String.format("[%s] %s%d ; ", player.username, plus >= 0 ? "+" : "", plus);
                    moneyChanges += String.format(" %s%d ; ", plus >= 0 ? "+" : "", plus);

                    if (player.cash < 0) {
//                        logMini.append(BlahBlahUtil.getLogString(String.format("/!\\ Âm tiền: [%] còn %d Gold", player.username, player.cash)));

                        havingMinusBalance = true;
                        player.cash = 0;
                        player.money = -oldCash;
//                        totalMoneyWin += oldCash + plus; //correct sum(it should be smaller)
                        totalMoneyWinNew += oldCash - plusNew;
                        totalMoneyWinNew = (int) ((double) totalMoneyWinNew * REAL_GOT_MONEY);
                    }
                    
                    // Check for in-game event for loser
                    player.checkEvent(false);
                }
            }

            // Check for in-game event for winner
            winner.checkEvent(true);
            
            //totalMoneyWin = -totalMoneyWin * 90 / 100;
//            if (totalMoneyWin < 0) {
//                totalMoneyWin = (int) (-(double) totalMoneyWin * REAL_GOT_MONEY);
//            } else {
//                totalMoneyWin = (int) (-(double) totalMoneyWin / REAL_GOT_MONEY);
//            }
            mLog.debug("---THANGTD TAX DEBUG---TOTAL MONEY WIN NEW AFTER TAX: " + totalMoneyWinNew);

//            winner.setWonMoney(totalMoneyWin);
//            winner.money = totalMoneyWin;
            winner.setWonMoney(totalMoneyWinNew);
            winner.money = totalMoneyWinNew;

            int dutyGot = 0;
            if (isTakeDuty) {
                dutyGot = 1;
            }

//            winner.cash = userDb.updateUserMoneyForTP(totalMoneyWin, true, winner.id, desc, winner.getExperience(), logtypeId, dutyGot);
            winner.cash = userDb.updateUserMoneyForTP(totalMoneyWinNew, true, winner.id, desc, winner.getExperience(), logtypeId, dutyGot);

            moneyChanges += NEW_LINE + String.format("Người thắng: [%s] %s%d ; ", winner.username, totalMoneyWinNew >= 0 ? "+" : "", totalMoneyWinNew);
            logMini.append(BlahBlahUtil.getLogString(moneyChanges));
            logMini.append(BlahBlahUtil.getLogString(String.format("Người thắng: [%s]", winner.username)));

            outCodeSB.append("[+] " + moneyChanges).append(NEW_LINE).append(NEW_LINE);
            /*userDb.updateUserMoney(totalMoneyWin, true, winner.id, desc, winner.getExperience(), logtypeId);
             winner.cash = winner.cash + totalMoneyWin;*/

            //Event Phom
            try {
                //mLog.debug("Duty:" + isTakeDuty + ":" + winner.id);
                if (isTakeDuty) {
                    userDb.updateGameEvent(winner.id, ZoneID.PHOM);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (havingMinusBalance) {
                userDb.notMinus();
            }
            resetAutoKickOut();
        } catch (Throwable ex) {
            //mLog.error("Error message" + ex.getMessage());
            //ex.printStackTrace();
        }
    }

    //Reset auto kickout user
    public void resetAutoKickOut() {
//        long timeActivated = System.currentTimeMillis() + SLEEP_BEETWEEN_MATCH_TIMEOUT;
//        for (int i = 0; i < this.playings.size(); i++) {
//            this.playings.get(i).setLastActivated(timeActivated);
//
//        }
//        for (int i = 0; i < this.waitings.size(); i++) {
//            this.waitings.get(i).setLastActivated(timeActivated);
//        }
//        owner.setLastActivated(timeActivated);
    }

    /**
     * *************************************************
     */
    public void resetPlayers() {
//		System.out.println("Reset players now!");
        List<PhomPlayer> removedPlayer = new ArrayList<>();
        int playingSize = this.playings.size();

        for (int i = 0; i < playingSize; i++) {
            PhomPlayer p = this.playings.get(i);
//            if (p.isOut || p.notEnoughMoney() || p.currentSession.isExpiredNew()) {
            if (p.isOut || p.notEnoughMoney()) {
                removedPlayer.add(p);
            }
        }

        int removeSize = removedPlayer.size();
        for (int i = 0;
                i < removeSize;
                i++) {
            playings.remove(removedPlayer.get(i));
        }

        this.playings.addAll(this.waitings);

//        resetAutoKickOut();
        this.waitings.clear();

        this.isPlaying = false;
        preChot = null;

        for (PhomPlayer p : playings) {
            p.isReady = false;
        }

        owner.isReady = true;
    }

    // reset game
    public void reset() throws PhomException {
//		System.out.println("Reset Phom data!");
//		out_code.println("Reset Phom data!");
//        getOutCodeSB().append("Reset Phom data!").append(NEW_LINE);
        resetPlayers();
        turn = 0;
        isTakeDuty = false;
        if (winner != null) {
            // mLog.info("Last Match winner : " + winner.username);
        } else {
            winner = (PhomPlayer) owner;
            mLog.error("Last match winner is null!");
            return;
        }
        int index = indexOfPlayer(this.winner);

        // this.owner = this.winner;
        currentPlayer = winner;
        firstChot = null;
        this.chot = false;
        this.haBai = false;
        this.currPoker = null;
        this.currPlayPoker = null;
        this.restCards = new Vector<>();

        if (index >= 0) {
            setNewStarter(index);
        } else {
            setNewStarter(0);
        }
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            PhomPlayer player = this.playings.get(i);
            player.reset();
        }

    }

    /**
     * *************************************************
     */
    // find player
    @Override
    public PhomPlayer findPlayer(long uid) throws SimpleException {
        int playingSize = this.playings.size();

        for (int i = 0; i < playingSize; i++) {
            PhomPlayer p = this.playings.get(i);

            if (p.id == uid) {
                return p;
            }
        }

        int waitingSize = this.waitings.size();

        for (int i = 0; i < waitingSize; i++) {
            PhomPlayer p = this.waitings.get(i);
            if (p.id == uid) {
                return p;
            }
        }
        mLog.error(concatString(turnInfo(), " : findPlayer : ", String.valueOf(uid), " matchId: ", String.valueOf(matchID)));
        return null;
    }

    public int indexOfPlayer(PhomPlayer p) throws PhomException {
        if (p == null) {
            return -1;
        }

        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            PhomPlayer player = this.playings.get(i);
            if (player.id == p.id) {
                return i;
            }
        }
        return -1;
    }

    private void setChot() {
        try {

            if (this.currentPlayer.playingCards.size() == 3) { //
                PhomPlayer next = getNextPlayer(this.currentPlayer);
                if (next.playingCards.size() == 2) { //
                    PhomPlayer n = getNextPlayer(next);
                    if (n.playingCards.size() == 3) { //
                        this.chot = true;
                    }
                } else if (next.playingCards.size() == 3) {
                    // this.haBai = true;
                }

            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public long getPrePlayerID(long uid) throws PhomException {
        try {
            PhomPlayer player = findPlayer(uid);
            int index = indexOfPlayer(player);
            if (index == 0) {
                return (this.playings.size() - 1);
            } else {
                return (index - 1);
            }
        } catch (Exception e) {
            throw new PhomException("Khong tim thay nguoi choi!");
        }
    }

    public PhomPlayer getPrePlayer(PhomPlayer p) throws PhomException {
        int index = indexOfPlayer(p);
        if (index == 0) {
            return this.playings.get(this.playings.size() - 1);
        } else {
            return this.playings.get(index - 1);
        }
    }

    public PhomPlayer getNextPlayer(PhomPlayer p) throws PhomException {
        int index = indexOfPlayer(p);
        if (index == (this.playings.size() - 1)) {
            return this.playings.get(0);
        } else {
            return this.playings.get(index + 1);
        }
    }

    public PhomPlayer ownerQuit() {
//		System.out.println("owner quit!");
        PhomPlayer newOwner = null;
        //synchronized (playings) {
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            if (!playings.get(i).isAutoPlay && !playings.get(i).notEnoughMoney()) {
                newOwner = playings.get(i);
                break;
            }
        }
        //}
        if (newOwner == null) {
            return newOwner;
        }
        for (int j = 0; j < len; j++) {
            playings.get(j).currentOwner = newOwner.currentSession;
        }
        ownerSession = newOwner.currentSession;
        //this.owner = newOwner;
        return newOwner;
    }

    public void processAuto() {
        if (currentPlayer.currentOwner == null) {
            currentPlayer.currentOwner = ownerSession;
        }
        if (currentPlayer.isAutoPlay) {
            currentTimeOut = 5000;
        } else {
            currentTimeOut = AUTO_BOC_BAI;
            if (turn == 0) {
                //it 's the first time
                currentTimeOut += AUTO_DANH;
            }
        }
        lastActivated = System.currentTimeMillis();
    }

    // Next player
    public void gameStop() {
        resetAutoKickOut();
        this.isPlaying = false;
    }

    public void next() {
        this.prePlayer = this.playings.get(this.currentIndexOfPlayer);

        if (this.currentIndexOfPlayer == this.playings.size() - 1) {
            if (!this.playings.get(0).isStop) {
                this.currentIndexOfPlayer = 0;
                this.currentPlayer = this.playings.get(0);
            } else {
                resetAutoKickOut();
                this.isPlaying = false;
            }
        } else {
            if (!this.playings.get(this.currentIndexOfPlayer + 1).isStop) {
                this.currentIndexOfPlayer++;
                this.currentPlayer = this.playings.get(this.currentIndexOfPlayer);
            } else {
                resetAutoKickOut();
                this.isPlaying = false;
            }
        }

        if (currentPlayer.haPhom && !taiGuiUDen) {
            resetAutoKickOut();
            isPlaying = false;
            return;
        }

        currentPlayer.doneBocBai = false;
        currentPlayer.doneHaBai = false;
        currentPlayer.outOfTime = false;
        if (isPlaying) {
            processAuto();
        }

        turn++;

        if (currentTimeOut > AUTO_BOC_BAI) {
            /*
             * only first time you have 30seconds time out
             */
            currentTimeOut = AUTO_BOC_BAI;
        }

        if (lastTurn()) {
            gameStop();
        }
    }

    @Override
    public void autoStartGame(SimplePlayer player) {
        //ISession session = player.currentSession;
        //IResponsePackage responsePkg = session.getDirectMessages();

        MessageFactory msgFactory = player.currentSession.getMessageFactory();
        StartRequest restartRequest = (StartRequest) msgFactory.getRequestMessage(MessagesID.MATCH_START);

        restartRequest.mMatchId = matchID;

        try {
            IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_START);
            business.handleMessage(player.currentSession, restartRequest, null);

        } catch (ServerException se) {
        }
    }

    @Override
    public void kickTimeout(Room room) {
        try {
            if (!this.isPlaying) {
                int len = playings.size();

                if (len > 1) {
                    long now = System.currentTimeMillis();
                    //check user which does nothing when he comes to table
                    boolean isAllJoinReady = true;
                    for (int i = 0; i < len; i++) {

                        PhomPlayer phomPlayer = this.playings.get(i);

                        if (!phomPlayer.isReady && phomPlayer.id != owner.id) {
                            isAllJoinReady = false;
                            //does this user over time out
                            if (now - phomPlayer.getLastActivated() > AUTO_KICKOUT_TIMEOUT) {
                                // kich him
                                kickTimeout(room, phomPlayer, 0);
                                this.remove(phomPlayer);
                                outCodeSB.append("Auto kick out ").append(phomPlayer.username).append(NEW_LINE);

                                logMini.append(BlahBlahUtil.getLogString(String.format("Tự động kick [%s]", phomPlayer.username)));
                            }
                        }
                    }

                    if (isAllJoinReady) {
                        //start game
                        if (now - owner.getLastActivated() > AUTO_KICKOUT_OWNER_TIMEOUT) {
                            SimplePlayer oldOwner = owner.clone();

                            PhomPlayer currOwner = findPlayer(owner.id);
                            currOwner.isAutoPlay = true;
                            currOwner.isOut = true;
                            this.ownerQuit();
                            kickTimeout(room, oldOwner, owner.id);
                            //autoStartGame(owner);
                            this.resetPlayers();
                        }

                    }

                }
            }
        } catch (Exception ex) {
            mLog.error("Phom kick time out", ex);
        }
    }

    //    public boolean isHabai()
//    {
//        return restCards.size()< this.playings.size();
//    }
    @Override
    public ISession getNotNullSession() {
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            PhomPlayer player = this.playings.get(i);
            if (!player.isAutoPlay && player.currentSession != null && player.currentSession.getMessageFactory() != null) {
                return player.currentSession;
            }
        }

        return null;
    }

    // Override method of SimpleTable. Auto process if player doesn't play by
    // his self
    @Override
    public void doTimeout() {
        if (this.isPlaying && !this.isPreparingfinish) {
            lastActivated = System.currentTimeMillis();
            if (currentPlayer.currentOwner != null) {
                if (currentPlayer != null) {
                    try {
                        outCodeSB.append("****AutoPlay*** ").append(currentPlayer.username).append(NEW_LINE);
                        currentPlayer.autoPlay(this);
                    } catch (Exception e) {
                        mLog.error("Phom do timeout", e);
                    }
                }
            } else {
//                System.out.println("OMG currentOwner is null!");
            }
        }

    }

    @Override
    public int getTableSize() {
        return this.playings.size() + this.waitings.size();
    }

    public long getStartId() {
        if (winner == null || winner.isOut) {
            return owner.id;
        }

        return winner.id;
    }

    private String concatString(String... input) {
        StringBuilder sb = new StringBuilder();
        int len = input.length;
        for (int i = 0; i < len; i++) {
            String s = input[i];
            sb.append(s);
        }
        return sb.toString();
    }
}
