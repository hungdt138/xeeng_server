package com.tv.xeeng.game.tienlen.data;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.base.common.BlahBlahUtil;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.StartRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.*;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

public class TienLenTable extends SimpleTable {
    public final static int CARDS_NULL = 0, CARDS_SINGLE = 1, CARDS_COUPLE = 2,
            CARDS_XAMCO = 3, CARDS_TUQUY = 4, CARDS_SERIAL = 5,
            CARDS_SERIAL_COUPLE = 6, CARDS_HAI = 12;
    public final static int PERFECT_TUQUY = 7, PERFECT_3SERIAL_COUPLE = 8,
            PERFECT_5SERIAL_COUPLE = 9, PERFECT_6COUPLE = 10,
            PERFECT_SANHRONG = 11;
    private final static int EXPERIENCE_BET = 1;
    private final static int TIENLEN_LOG_TYPE = 10005;
    public ArrayList<TienLenPlayer> superUsers = new ArrayList<>();
    private static final Logger logT = LoggerContext.getLoggerFactory().getLogger(TienLenTable.class);
    private ArrayList<TienLenPlayer> playings = new ArrayList<>();
    private ArrayList<TienLenPlayer> waitings = new ArrayList<>();
//	private final Object lockPlaying = new Object();
//	private final Object lockWaiting = new Object();
    private int currentIndexOfPlayer;
    private int preIndexOfPlayer;
    private int preTypeOfCards = -1;
    private boolean hidePoker = false;
    private boolean isChangeSetting = false;
    private Poker[] currCards;
    private Duty duty;
    private TienLenPlayer currPlayer;
    public TienLenPlayer winner;
    // private boolean isPerfect = false;
    // private Timer timerAuto = new Timer(ZoneID.TIENLEN, 10000);
    public boolean fightOccur = false; // Mỗi lần xảy ra chặt chém mất tiền thì set là true

    public int cheat = 0;
    public long idCheat = 0; //525389; // testing purpose
    private boolean isProcessEnd = false;
    private final int tax = 5;

    public boolean isFull() {
        return (playings.size() + waitings.size() >= getMaximumPlayer());
    }

    @Override
    public boolean isFullTable() {
        return playings.size() + waitings.size() >= getMaximumPlayer();
    }

    public void changeMoney(long m) {
        isChangeSetting = true;
        firstCashBet = m;
        int len = this.playings.size();
        for (int i = 0; i < len; i++) {
            TienLenPlayer p = this.playings.get(i);
            p.moneyForBet = m;
        }
        len = waitings.size();
        for (int i = 0; i < len; i++) {
            TienLenPlayer p = this.waitings.get(i);
            p.moneyForBet = m;
        }
    }

    @Override
    public TienLenPlayer getCurrPlayer() {
        return currPlayer;
    }

    public void resetAllReady() {
        int len = this.playings.size();
        for (int i = 0; i < len; i++) {
            TienLenPlayer p = this.playings.get(i);
            p.isReady = false;
        }
        len = waitings.size();
        for (int i = 0; i < len; i++) {
            TienLenPlayer p = this.waitings.get(i);
            p.isReady = false;
        }
    }

    public int getCurrentIndexOfPlayer() {
        return currentIndexOfPlayer;
    }

    public ArrayList<TienLenPlayer> getPlayings() {
        return playings;
    }

    public boolean containPlayer(long id) {
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            if (playings.get(i).id == id) {
                return true;
            }
        }
        len = waitings.size();
        for (int i = 0; i < len; i++) {
            if (waitings.get(i).id == id) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<TienLenPlayer> getWaitings() {
        return waitings;
    }

    // Create table: owner, money, matchID, numberPlayer
    public TienLenTable(TienLenPlayer ow, long money, long match, int numPlayer) {
        logdir = "tienlen";
        this.owner = ow;
        this.firstCashBet = money;
        this.matchID = match;
        this.maximumPlayer = numPlayer;
        this.playings.add(ow);
        hidePoker = false;
        /*
         * timerAuto.setRuning(false); timerAuto.start();
         */
    }

    public void ready(long uid, boolean r) throws BusinessException {
        TienLenPlayer player = findPlayer(uid);
        if (player == null || player.isOut || player.isOutGame) {
            throw new BusinessException("ban khong ton tai o ban choi nay");
        }

        player.setReady(r);
        long now = System.currentTimeMillis();
        player.setLastActivated(now);
        owner.setLastActivated(now);
    }

    public boolean isAnyReady() {
//        if (!isChangeSetting) {
//            return true;
//        }
        int len = this.playings.size();
        for (int i = 0; i < len; i++) {
            TienLenPlayer t = this.playings.get(i);
//            mLog.error("---THANGTD START DEBUG---" + t.username + " is ready: " + t.isReady);
            if (t.id != this.owner.id && !t.isReady) {
                return false;
            }
        }

        return true;
    }

    // Player join
    public void join(TienLenPlayer player) throws TienLenException {
//        lastActivated = System.currentTimeMillis();
//        player.setLastActivated(lastActivated);
    	if (this.isPlaying) {
            if (!isFullTable()) {
                player.isObserve = true;
                this.waitings.add(player);
            } else {
                throw new TienLenException(Messages.FULL_PLAYER_MSG);
            }
        } else {
            if (!isFullTable()) {
                this.playings.add(player);
                logMini.append(BlahBlahUtil.getLogString(String.format("[%s] vào bàn", player.username)));
            } else {
                throw new TienLenException("Phong da thua nguoi roi ban");
            }
        }
        
        mLog.debug("---THANGTD JOIN DEBUG TLMN---" + player.username + " is ready: " + player.isReady);
    }

    // Player removed
    public void remove(TienLenPlayer player) throws TienLenException {
        try {
            // out_code.println("Remove player : " + player.id);
            outCodeSB.append("Remove player ").append(player.id).append(NEW_LINE);
            if (player != null) {
                int playingSize = this.playings.size();
                for (int i = 0; i < playingSize; i++) {
                    TienLenPlayer p = this.playings.get(i);
                    if (p.id == player.id) {
                        playings.remove(player);
                        return;
                    }
                }

                int waitingSize = this.waitings.size();

                for (int i = 0; i < waitingSize; i++) {
                    TienLenPlayer p = this.waitings.get(i);
                    if (p.id == player.id) {
                        waitings.remove(p);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            outCodeSB.append("Remove player !!!error ").append(player.id).append(NEW_LINE);
            StringBuilder error = new StringBuilder();
            error.append(e.getMessage()).append(" remove player: ").append(player.username);
            logT.error(error.toString(), e.getStackTrace());
        }
    }

    @Override
    protected List<SimplePlayer> removeNotEnoughMoney() {
        List<SimplePlayer> removedPlayers = new ArrayList<>();
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            if (playings.get(i).notEnoughMoney()) {
                removedPlayers.add(playings.get(i));
            }
        }
        boolean isChangeOwner = false;
        len = removedPlayers.size();
        for (int i = 0; i < len; i++) {
            if (removedPlayers.get(i).id == owner.id) {
                isChangeOwner = true;
            }
            //remove((TienLenPlayer)removedPlayers.get(i));
            playings.remove((TienLenPlayer) removedPlayers.get(i));
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

    // Người chơi nào có quân bài nhỏ nhất được đánh trước
    private void numStart() {
        byte num = this.playings.get(0).minCard();
        currentTurn = 0;
        int len = this.playings.size();
        for (int i = 1; i < len; i++) {
            TienLenPlayer p = this.playings.get(i);
            if (Utils.isBigger(num, p.minCard())) {
                num = p.minCard();
                currentTurn = i;
            }
        }
        this.currPlayer = this.playings.get(currentTurn);
        // System.out.println("Min card: " + currPlayer.myHand[0]);
    }

    public int getValue(int b) {
        return (b - 1) % 13;
    }

    public String cardToString(int card) {
        String[] s = {"tep", "bich", "ro", "co"};
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf((getValue(card) + 1))).append(String.valueOf(s[(card - 1) / 13]));
        return sb.toString();
        //return "" + (getValue(card) + 1) + "" + s[(card - 1) / 13];
    }
  
    public String cardToString(byte[] card) {
        StringBuilder sb = new StringBuilder();
        //String s = "";
        int len = card.length;
        for (int i = 0; i < len; i++) {
            sb.append(" ").append(cardToString(card[i]));
            //s = s + " " + cardToString(card[i]);
        }
        //return s;
        return sb.toString();
    }

    private void chiaCheat(int type) {
        matchNum++;
        outCodeSB.append("---Start game-----Chia bai MatchId: ").append(matchID).append(" - ").append("Chủ bàn: ").append(owner.username)
                .append(" - ").append("Tiền cược: ").append(firstCashBet).append(NEW_LINE);
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        logMini.append(BlahBlahUtil.getLogString("*** Bắt đầu ***"));

        String playerList = "Danh sách người chơi: ";
        for (int i = 0; i < playings.size(); i++) {
            playerList += String.format("[%s (%d) - %d] ", playings.get(i).username, playings.get(i).id, playings.get(i).cash);
        }
        logMini.append(BlahBlahUtil.getLogString(playerList));

        Couple<ArrayList<Integer>, ArrayList<Integer>> data = new Couple<>(new ArrayList<Integer>(), new ArrayList<Integer>());
        ChiaBai chiaBai = new ChiaBai();
        if (type == 0) {
            chia();
            return;
        } else if (type == 1) { // toi trang
            int index = (int) Math.round(Math.random() * 3);
            data = chiaBai.toiTrang(index);
        } else if (type == 2) { // 3 doi thong lon
            data = chiaBai.baDoiThongLon();
        } else if (type == 3) { // 4 doi thong lon
            data = chiaBai.bonDoiThongLon();
        }
        int len = playings.size();
        if ((len <= 4) && (len > 1)) {
            int index = 0;
            for (int i = 0; i < len; i++) {
                TienLenPlayer p = this.playings.get(i);
                byte[] cards = new byte[13];
                if (p.id == idCheat) {
                    for (int t = 0; t < data.e1.size(); t++) {
                        cards[t] = data.e1.get(t).byteValue();
                    }
                } else {
                    for (int j = 13 * index; j < 13 * (index + 1); j++) {
                        cards[j - (13 * index)] = data.e2.get(j).byteValue();
                    }
                    index++;
                }

                p.setMyCards(cards);
                outCodeSB.append(p.username).append(":");
                for (int k = 0; k < cards.length; k++) {
                    outCodeSB.append(" ").append(cards[k]);
                }

                outCodeSB.append("; (").append(cardToString(cards)).append(" )");
                outCodeSB.append(NEW_LINE);
            }
        } else {
            logT.debug("Sai ne!");
            // throw new BusinessException("Em sai roi!");
        }
    }

    private void chia() {
        // matchNum++;
        // out.println();
        // out.println("*******************" + matchID + "-" + matchNum + " : "
        // + owner.username + "***************************");
        // out_code.println();
        // out_code.println("*******************" + matchID + "-" + matchNum
        // + " : " + owner.username + "***************************");

        // outCodeSB.append("---Start game-----Chia bai MatchId: ").append(matchID).append("-").append("Owner name:")
        // .append(owner.username).append(NEW_LINE);

        ArrayList<Integer> currList = getRandomList();
        int len = playings.size();
        if ((len <= 4) && (len > 1)) {
            for (int i = 0; i < len; i++) {
                TienLenPlayer p = this.playings.get(i);
                byte[] cards = new byte[13];
                for (int j = 13 * i; j < 13 * (i + 1); j++) {
                    cards[j - (13 * i)] = currList.get(j).byteValue();
                }
                // if(i== 1)
                // {
                // cards[0] = 1;
                // cards[1] = 14;
                // cards[2] = 27;
                // cards[3] = 40;
                // }
                // //hard code
                // if(i == 1)
                // {
                // cards[0] = 9;
                // cards[1] = 22;
                // cards[2] = 10;
                // cards[3] = 23;
                // cards[4] = 11;
                // cards[5] = 24;
                //
                // }

                p.setMyCards(cards);
                outCodeSB.append(p.username).append(":");
                int carLen = cards.length;
                for (int k = 0; k < carLen; k++) {
                    outCodeSB.append(" ").append(cards[k]);
                }

                outCodeSB.append(";(").append(cardToString(cards)).append(" )");
                outCodeSB.append(NEW_LINE);
            }
            // out.flush();
            // playings.get(3).setMyCards(new byte[]{6, 19, 7, 20, 8, 21, 9, 22,
            // 43, 12, 51, 23, 34});
        } else {
            logT.debug("Sai ne!");
        }
    }

    private ArrayList<Integer> getRandomList() {
        ArrayList<Integer> res = new ArrayList<>();
        ArrayList<Integer> currList = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            currList.add(i, i + 1);
        }
        // tư qui Ace
        // res.add(1);
        // res.add(14);
        // res.add(27);
        // res.add(40);
        //
        // currList.remove(39);
        // currList.remove(26);
        // currList.remove(13);
        // currList.remove(0);

        // 3 doi thong va tư qui + heo
        // res.add(8);
        // res.add(21);
        // res.add(34);
        // res.add(47);
        // res.add(2);
        //
        //
        // currList.remove(46);
        // currList.remove(33);
        // currList.remove(20);
        // currList.remove(7);
        // currList.remove(1);
        //
        // currList.remove(8);
        // currList.remove(21);
        //
        // currList.remove(9);
        // currList.remove(22);
        //
        // currList.remove(10);
        // currList.remove(23);
        int len = currList.size();

        for (int i = 0; i < len; i++) {
            int index = getRandomNumber(currList, res);
            currList.remove(index);
        }
        return res;
    }

    private int getRandomNumber(ArrayList<Integer> input, ArrayList<Integer> result) {
        int lengh = input.size();
        long maxSize = System.currentTimeMillis() * lengh;
        Random rand = new Random(maxSize);
//		int index = (int) Math.round(Math.random() * lengh);
        int index = (int) (Math.abs(rand.nextLong()) % lengh);

        result.add(input.get(index));
        return index;
    }

    // Start match
    public long[] startMatch() throws BusinessException {
        duty = new Duty();
        superUsers.clear();

        int playingSize = playings.size();
        for (int i = 0; i < playingSize; i++) {
            TienLenPlayer p = playings.get(i);
            if (com.tv.xeeng.game.data.Utils.isSuperUser(p.id)) {
                superUsers.add(p);
            }
        }
        
        //TEst
        //cheat = 2;
//        if(superUsers.isEmpty()) {
//            superUsers.add((TienLenPlayer)owner);
//        }
        //
        currentTimeOut = 30000;
        lastActivated = System.currentTimeMillis();
        resetTable();

        long[] L = new long[2];
        L[0] = -1;
        L[1] = -1;

        if (playingSize > 1) {
            this.isPlaying = true;
            isChangeSetting = false;
            // chia();
            chiaCheat(cheat);// Add New
            cheat = 0;

            for (int i = 0; i < playingSize; i++) {
                TienLenPlayer p = playings.get(i);
                int perfectType = checkPerfect(p.myHand);
                if (perfectType > 0) {
                    // Xác xuất 10% thắng trắng - ThangTD
                    int tl = (int) Math.round(Math.random() * 9);
                    if (tl == 4) {
                        outCodeSB.append(p.username).append(": Tới trắng - perfect type = ").append(perfectType).append(NEW_LINE);
                        L[0] = p.id;
                        L[1] = perfectType;
                        isNewMatch = true;
                        this.winner = p;
                        this.currPlayer = p;

                        // L[1] = PERFECT_3SERIAL_COUPLE;
                        return L;
                    } else {
                        outCodeSB.append(p.username).append(": Tới trắng - perfect type = ").append(perfectType).append(" nhưng cần chia lại!").append(NEW_LINE);
                        return startMatch();
                    }
                }
            }

            if (currPlayer != null && currPlayer.isOutGame) {
                isNewMatch = true;
            }

            if (isNewMatch) {
                numStart();
            } else {
                // Trường hợp người được đánh trước đã thoát thì chuyển lượt cho người khác
                if (currPlayer.isOutGame) {
                    currPlayer = playings.get(findNext(getUserIndex(getCurrentTurnID())));
                }
            }
            // this.timerAuto.setOwnerSession(ownerSession);
            // startTime();
                        /*
             * try { this.timerAuto.start(); } catch (Exception e) {
             * this.timerAuto.reset(); }
             */
        } else {
            logT.debug("Chua co nguoi choi nao!");
            this.isPlaying = false;
            throw new BusinessException("Ban khong the bat dau khi co 1 minh ban");
        }
        
        return L;
    }

    /*
     * 0: Mot cay binh thuong 1: Đôi thông (cặp, đôi): 2 lá bài có cùng giá trị.
     * 2: Tam (Xám Cô): 3 lá bài có cùng giá trị. 3: Sảnh (lốc, dây): Một dây
     * các quân bài có giá trị nối tiếp nhau (không bắt buộc phải đồng chất)
     * Quân 2 không bao giờ được nằm trong một Sảnh. Quân 2 có thể chặt bất kỳ
     * quân lẻ nào, đôi 2 có thể chặt bất kỳ các đôi, tam 2 (3 quân 2) có thể
     * chặt bất kỳ tam khác. 4: Sảnh Rồng: dây các quân bài từ 2 đến A (3-át) 5:
     * Tứ Quý: 4 quân bài có giá trị giống nhau. Tứ Quý Át, Tứ Quý K 6: Ba đôi
     * thông: 3 đôi kề nhau 7: Bốn đôi thông: 4 đôi kề nhau. 8: Năm đôi thông
     * với 5 đôi kề nhau. 9: Bốn đôi thông có 3 bích. 10: Ba đôi thông có 3
     * bích. 11: Sau doi
     */
    @Override
    public TienLenPlayer findPlayer(long uid) {
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            TienLenPlayer p = this.playings.get(i);
            if (p.id == uid) {
                return p;
            }
        }
        len = waitings.size();
        for (int i = 0; i < len; i++) {
            TienLenPlayer p = this.waitings.get(i);
            if (p.id == uid) {
                return p;
            }
        }
        return null;
    }

    // Give up - player
    // public void giveUp(long uid) {
    // try {
    // TienLenPlayer p = findPlayer(uid);
    // if (p != null) {
    // p.isAcive = false;
    // nextPlayer();
    // } else {
    // mLog.debug("Khong tim thay player" + uid);
    // }
    // } catch (SimpleException e) {
    // }
    // }
    public int numRealPlaying() {
        int sum = 0;
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            TienLenPlayer p = this.playings.get(i);
            if (!p.isOutGame) {
                sum++;
            }
        }
        return sum;
    }

    public TienLenPlayer ownerQuit() {
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            TienLenPlayer p = playings.get(i);
            if (!p.notEnoughMoney() && !p.isOutGame) {
                ISession pS = p.currentSession;
                for (int j = 0; j < len; j++) {

                    playings.get(j).currentOwner = pS;
                }
                ownerSession = pS;
                return p;
            }
        }
        return null;
    }

    // private void nextPlayer() {
    // this.preIndexOfPlayer = this.currentIndexOfPlayer;
    // int temp = this.currentIndexOfPlayer;
    // if (temp == this.playings.size() - 1) {
    // temp = 0;
    // } else {
    // temp++;
    // }
    // if (this.playings.get(temp).isAcive) {
    // if (temp == this.currentIndexOfPlayer) {
    // startNewRound();
    // this.preIndexOfPlayer = temp;
    // }
    // this.currentIndexOfPlayer = temp;
    // } else {
    // nextPlayer();
    // }
    // }
    public void startNewRound() {
        // this.preTypeOfCards = -1;
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            TienLenPlayer p = playings.get(i);
            p.isAcive = true;
        }
    }

    // Reset auto kickout user
    public void resetAutoKickOut() {
//		long timeActivated = System.currentTimeMillis()
//				+ SLEEP_BEETWEEN_MATCH_TIMEOUT;
//		for (int i = 0; i < this.playings.size(); i++) {
//			this.playings.get(i).setLastActivated(timeActivated);
//
//		}
//
//		for (int i = 0; i < this.waitings.size(); i++) {
//			this.waitings.get(i).setLastActivated(timeActivated);
//		}
//		owner.setLastActivated(timeActivated);
//		owner.isReady = true;
    }

    // Reset game
    public void resetTable() {
        // System.out.println("chạy vào reset table!");
        newRound();
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            TienLenPlayer player = playings.get(i);
            player.reset(player.moneyForBet);
        }
        lastTurnID = 0;
        predID = 0;
        lastCards = new byte[0];
        isNewRound = true;

        resetAutoKickOut();
        // if (isNewMatch) {
        // System.out.println("OMG isperfect!");
        // isNewMatch = true;
        // }
        // isNewMatch = false;
    }
    // Thomc
    private int currentTurn = 0; // người hiện tại được đi,
    public long lastTurnID = 0; // người đánh cuối, cập nhật mỗi lần đánh vòng
    // mới hoặc chặt
    public long predID = 0; // người đi trước đấy (hoặc vừa bỏ lượt)
    public byte[] lastCards = new byte[0];// (các) quân bài vừa đánh
    public boolean isNewRound = true;
    public boolean isNewMatch = true;
    public final static int SUCCESS = 0, INVALID_PLAY = 1, INVALID_FIGTH = 2,
            INVALID_TURN = 3, INVALID_GIVEUP = 6, END_MATCH = 4,
            END_MATCH_PERFECT = 5, CARDS_NOT_FOUND = 7;
    public ArrayList<long[]> fightInfo = new ArrayList(); // lưu thông tin khi chặt bài; để xử lý chặt chồng;

    public int play(long uid, String cards, boolean isGiveup, boolean isTimeOut) throws TienLenException {
        getDuty().setYourDuty(false);
        
        int cardType = CARDS_NULL;

        if (cards != null) {
            cardType = checkCardsType(Utils.stringToByte(cards));
        }
        
        if (uid == getCurrentTurnID() || (!isGiveup && !isNewRound 
                && ((cardType == CARDS_SERIAL_COUPLE && Utils.stringToByte(cards).length == 8) || cardType == CARDS_TUQUY))) {
            lastActivated = System.currentTimeMillis();
            // 4 đôi thông được chặt không cần hỏi
            boolean noNeedTurn = (cardType == CARDS_SERIAL_COUPLE && Utils.stringToByte(cards).length == 8) || cardType == CARDS_TUQUY;

            if (isGiveup) {
                outCodeSB.append(currPlayer.username).append(" : giveUp").append(NEW_LINE);
            } else {
                outCodeSB.append(currPlayer.username).append(" : play : ").append(cards).append(" [").append(cardToString(Utils.stringToByte(cards)))
                        .append(" ]").append(NEW_LINE);
            }
            // out.flush();

            if (!isGiveup) {
                byte[] fightCard = Utils.stringToByte(cards);

                if (!noNeedTurn && !currPlayer.isContainsCards(fightCard)) {
                    return CARDS_NOT_FOUND;
                }
                
                if (isNewRound) {
                    startNewRound();// TODO:  fix error turn
                    if (isValidTurn(fightCard)) {
                        getDuty().checkDuty(currPlayer, cardType, cards);

                        fightOccur = false;
                        // Người được đi lúc đầu phải đánh (các) quân bài chứa
                        // quân bài nhỏ nhất
                        // if (isNewMatch) {
                        // if (cards[0] != users[0].myHand[0]) {
                        // pushDebug("Quân bài đánh ra lần đầu phải chứa quân nhỏ nhất!");
                        // return;
                        // } else {
                        // isNewMatch = false;
                        // }
                        // }
                        // System.out.println("currPlayer: " +
                        // currPlayer.username);
                        if (currPlayer.numHand > 0) {
                            currPlayer.removeCards(fightCard);
                        }
                        // System.out.println("Quân bài còn lại: " +
                        // currPlayer.numHand);

                        if (currPlayer.numHand <= 0) {
                            // System.out.println("Kết thúc ván (có người chơi hết bài)!");
                            resetAutoKickOut();
                            isPlaying = false;
                            this.winner = currPlayer;
                            return END_MATCH;
                        }
                        
                        isNewRound = false;
                        lastCards = fightCard;
                        lastTurnID = uid;
                    } else {
                        // System.out.println("Đánh không hợp lệ!");
                        outCodeSB.append("!!!!!!!Đánh không hợp lệ userId: ").append(uid).append(" current index: ").append(currentIndexOfPlayer).append(" card ")
                                .append(cards).append(NEW_LINE);
                        
                        byte[] sortedCards = Utils.sortCards(fightCard);
                        outCodeSB.append("!!!!!!!Sorted cards: ").append(cardToString(sortedCards)).append(NEW_LINE);
                        
                        logT.error(concatString("!!!!! Error Tien len play (invalid turn) ", " matchId ", String.valueOf(matchID)));

                        return INVALID_PLAY;
                    }
                } else {
                    if (isValidFight(fightCard, lastCards)) {
                        getDuty().checkDuty(currPlayer, cardType, cards);

                        findPlayer(uid).removeCards(fightCard);
                        int lastCardsType = checkCardsType(lastCards);
                        // int fightCardsType = checkCardsType(fightCard);
                        if (((Utils.getValue(lastCards[lastCards.length - 1]) == 12 || lastCardsType == CARDS_TUQUY 
                                || (lastCardsType == CARDS_SERIAL_COUPLE)) && (Utils.getValue(fightCard[fightCard.length - 1]) != 12))) {
                            fightOccur = true;
                            // System.out.println("Chặt heo/hàng !");
                            fightProcess(lastTurnID, uid, lastCards);
                            findPlayer(uid).isAcive = true;// lúc thằng có 4 đôi
                            // thông bỏ lượt rùi
                            // sau đó nó chặt
                            // không cần tới
                            // lượt nên phải
                            // active lại cho nó
                        } else {
                            fightOccur = false;
                        }
                        if (currPlayer.numHand == 0) { // Kết thúc ván (có người chơi hết bài)
                            // System.out.println("Kết thúc ván (có người chơi hết bài)!");
                            this.winner = currPlayer;

                            saveLogToFile();

                            return END_MATCH;
                        }
                        lastCards = fightCard;
                        lastTurnID = uid;
                    } else {
                        // System.out.println("Chặt không hợp lệ!");
                        return INVALID_FIGTH;
                    }
                }
            } else {
                if ((!isTimeOut) && (isNewRound)) {
                    return INVALID_GIVEUP;
                }
                fightOccur = false;
                findPlayer(uid).isAcive = false;
            }
            
            predID = uid;
            nextUser(getUserIndex(uid));
            return SUCCESS;
        } else {
            // System.out.println("Không đúng lượt đi!");
            outCodeSB.append("!!!!!!!Không đúng lượt đi userId: ").append(uid).append(" current index: ").append(currentIndexOfPlayer).append(" card ")
                                .append(cards).append(NEW_LINE);
            outCodeSB.append("!!!!!!!Lượt đi hiện tại là: ").append(getCurrentTurnID()).append(NEW_LINE);
            return INVALID_TURN;
        }
    }

    // xử lý chặt/chặt chồng;
    public void fightProcess(long preID, long fightID, byte[] cards) {
        // trường hợp chặt đè
        if (fightInfo.size() == 1) {
            long data[] = new long[5];
            long preData[] = fightInfo.get(0);
            
            // người bị chặt đè
            long overFightID = preData[1];
            TienLenPlayer overFightPlayer = findPlayer(overFightID);
            data[0] = overFightID;
            
            // người chặt hiện tại
            data[1] = fightID;
            TienLenPlayer fightPlayer = findPlayer(fightID);
            
            // người bị chặt lúc đầu
            long firstID = preData[0];
            TienLenPlayer firstPlayer = findPlayer(firstID);
            data[3] = firstID;
            
            // trả lại tiền cho người bị chặt lúc đầu
            // tiền trả lại bị tính phế - ThangTD
            long returnMoney = preData[2];
            long returnMoneyAfterTax = (long)((double)returnMoney * REAL_GOT_MONEY);
//            firstPlayer.money += returnMoney;
            firstPlayer.money += returnMoneyAfterTax;
//            data[4] = returnMoney;
            data[4] = returnMoneyAfterTax;            
            
            // người bị chặt đè phải trả lại số tiền ăn (có phế) trước đó - ThangTD
            overFightPlayer.money -= returnMoneyAfterTax;

            // và chịu toàn bộ tiền chặt
            long newMoney = caculateMoneyFight(cards); // tiền chặt mới
            
            long totalMoney = overFightPlayer.moneyLost(returnMoney + newMoney);
            long totalMoneyAfterTax = (long)((double)totalMoney * REAL_GOT_MONEY);
            overFightPlayer.money -= totalMoney;
//            fightPlayer.money += newMoney;
            fightPlayer.money += totalMoneyAfterTax;
            data[2] = totalMoney;
            fightInfo = new ArrayList<>();
            fightInfo.add(data);
        } // chặt 1 lần
        else {
            TienLenPlayer prePlayer = findPlayer(preID);
            TienLenPlayer fightPlayer = findPlayer(fightID);
            long fightMoney = prePlayer.moneyLost(caculateMoneyFight(cards));
            prePlayer.money -= fightMoney;
//            fightPlayer.money += fightMoney;
            fightPlayer.money += (long)((double)fightMoney * REAL_GOT_MONEY); // tính phế tiền ăn - ThangTD
            long data[] = new long[3];
            data[0] = preID;
            data[1] = fightID;
            data[2] = fightMoney;
            fightInfo.add(data);
        }
    }

    // Tính tiền bị chặt
    public long caculateMoneyFight(byte[] cards) {
        long newMoney = 0;
        int cardsType = checkCardsType(cards);
        switch (cardsType) {
            case CARDS_TUQUY:
                newMoney = 7 * firstCashBet;
                break;
            case CARDS_SERIAL_COUPLE:
                if (cards.length == 6) {
                    // 3 đôi thông
                    newMoney = 5 * firstCashBet; // 3 đôi thông
                } else {
                    // 4 đôi thông
                    newMoney = 13 * firstCashBet; // 4 đôi thông
                }
                break;
            default:
                for (int i = 0; i < cards.length; i++) {
                    // heo đen
                    if (Utils.getType(cards[i]) == 1 || Utils.getType(cards[i]) == 2) {
                        newMoney += 2 * firstCashBet; // heo đen
                    } else {// heo đỏ
                        newMoney += 5 * firstCashBet; // heo đỏ
                    }
                }
                break;
        }
        
        return newMoney;
    }

    public long getCurrentTurnID() {
        // return playings.get(currentTurn).id;
        if (findPlayer(currPlayer.id) == null) {
            currPlayer = (TienLenPlayer) owner;
            return owner.id;
        }

        return currPlayer.id;
    }

    public void newRound() {
        isNewRound = true;
        lastCards = new byte[0];
        int len = this.playings.size();
        for (int i = 0; i < len; i++) {
            TienLenPlayer p = playings.get(i);
            p.isAcive = true;
        }
        fightInfo = new ArrayList<>();
    }

    public void nextUser(int preIndex) {
        // System.out.println("number user:  " + playings.size());
        // System.out.println("getUserIndex(lastTurnID)" +
        // getUserIndex(lastTurnID));
        currentTurn = preIndex;

        int count = 0;
        int playingSize = playings.size();
        while (count < playingSize) {
            currentTurn++;

            currentTurn = currentTurn % playingSize;

            if ((!playings.get(currentTurn).isAcive || playings.get(currentTurn).isOutGame) && playings.get(currentTurn).id != lastTurnID) {
                count++; // this user is out
            } else {
                break;
            }
        }

        // currentTurn = currentTurn % playings.size();
        if (count < playingSize) {
            currPlayer = playings.get(currentTurn);
            if (playings.get(currentTurn).id == lastTurnID) {
                newRound();
            } else {
                isNewRound = false;
            }
        } else {
            outCodeSB.append("Error next user").append(NEW_LINE);
            logT.error(concatString("Error next user matchId: ", String.valueOf(matchID)));
        }
        // startTime();
        // System.out.println("number user:  " + playings.size());
    }

    public int findNext(int preIndex) {
        int point = preIndex;

        int count = 0;
        int playingSize = playings.size();
        while (count < playingSize) {
            count++;
            point++;
            point = point % playings.size();
            if (!playings.get(point).isOutGame) {
                return point;
            }
        }
        return -1;
    }

    public int getUserIndex(long id) {
        int len = playings.size();
        for (int i = 0; i < len; i++) {
            if (playings.get(i).id == id) {
                return i;
            }
        }
        return -1;
    }

    // Kiểm tra quân bài đánh ra trong lượt mới có hợp lệ không
    public boolean isValidTurn(byte[] cards) {
        if (checkCardsType(cards) > 0) {
            return true;
        } else {
            return false;
        }
    }

    // kiểm tra chặt quân có hợp lệ không
    public boolean isValidFight(byte[] fightCards, byte[] lastCards) {
        if (fightCards == null || lastCards == null || fightCards.length == 0 || lastCards.length == 0) {
            return false;
        }
        fightCards = Utils.sortCards(fightCards);
        lastCards = Utils.sortCards(lastCards);
        
        // chặt cùng cấp (số quân bằng nhau)
        if (fightCards.length == lastCards.length) {
            if (checkCardsType(fightCards) != checkCardsType(lastCards)) {
                return false;
            } else {
                return Utils.isBigger(fightCards[fightCards.length - 1], lastCards[lastCards.length - 1]);
            }
        } // chặt đặc biệt
        else {
            // chặt băng tứ quý
            if (checkCardsType(fightCards) == CARDS_TUQUY) {
                // Tứ quý chặt 1 quân 2, hoặc 1 đôi 2, hoặc 3 đôi thông bất kỳ
                if ((checkCardsType(lastCards) <= 2 && Utils.getValue(lastCards[lastCards.length - 1]) == 12)
                        || (checkCardsType(lastCards) == CARDS_SERIAL_COUPLE && lastCards.length == 6)) {
                    // System.out.println("Chặt bằng Tứ Quý!");
                    return true;
                } else {
                    return false;
                }
            } else if (checkCardsType(fightCards) == CARDS_SERIAL_COUPLE) {
                switch (fightCards.length) {
                    // chặt = 3 đôi thông (chặt được 1 heo :p)
                    case 6:
                        if (lastCards.length == 1 && Utils.getValue(lastCards[0]) == 12) {
                            // System.out.println("Chặt 2 bằng 3 đôi thông !");
                            return true;
                        } else {
                            return false;
                        } // 4 đôi thông chặt 1 quân 2, hoặc 1 đôi 2, hoặc 3 đôi thông bất kỳ và tứ quý
                    case 8:
                        if ((checkCardsType(lastCards) <= 2 && Utils.getValue(lastCards[lastCards.length - 1]) == 12)
                                || (checkCardsType(lastCards) == CARDS_SERIAL_COUPLE && lastCards.length == 6)
                                || checkCardsType(lastCards) == CARDS_TUQUY) {
                            // System.out.println("Chặt bằng 4 đôi thông!");
                            return true;
                        } else {
                            return false;
                        }
                    default:
                        return false;
                }
            } else {
                return false;
            }
        }
        // return true;
    }

    // trả về loại quân bài(s) đánh ra
    public int checkCardsType(byte cards[]) {
        int carLength = cards.length;
        if (cards == null || carLength == 0) {
            return CARDS_NULL;
        }

        if (carLength == 1) {
            return CARDS_SINGLE;
        }

        for (int i = 0; i < carLength - 1; i++) {
            if (Utils.getValue(cards[i]) != Utils.getValue(cards[i + 1])) {
                if (carLength > 2) {
                    if (isSerialCards(cards)) {
                        return CARDS_SERIAL;
                    } else if (isSerialCouple(cards)) {
                        return CARDS_SERIAL_COUPLE;
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
            }
        }
        
        switch (carLength) {
            case 2:
                return CARDS_COUPLE;

            case 3:
                return CARDS_XAMCO;

            case 4:
                return CARDS_TUQUY;

            default:
                return -1;
        }
    }

    // kiểm tra các quân bài có phải là bộ dây (Sảnh) hay không
    public boolean isSerialCards(byte[] cards) {
        int carLength = cards.length;
        if (cards == null || carLength == 0) {
            return false;
        }
        // Trong sảnh không bao giờ chứa quân 2 trừ sảnh rồng
        // A có giá trị là 11, 2 có giá trị là 12
        for (int i = 0; i < carLength; i++) {
            if (Utils.getValue(cards[i]) == 12 && carLength != 13) {
                return false;
            }
        }
        
        cards = Utils.sortCards(cards);

        for (int i = 0; i < carLength - 1; i++) {
            if (Utils.getValue(cards[i] + 1) != Utils.getValue(cards[i + 1])) {
                return false;
            }
        }
        
        return true;
    }

    // kiểm tra các quân bài là các cặp đôi liên tiếp nhau (n đôi thông)
    public boolean isSerialCouple(byte[] cards) {
        int carLength = cards.length;
        if (cards == null || carLength < 6 || carLength % 2 != 0) {
            return false;
        }
        
        // Trong sảnh không bao giờ chứa quân 2
        for (int i = 0; i < carLength; i++) {
            if (Utils.getValue(cards[i]) == 12) {
                return false;
            }
        }
        
        Utils.sortCards(cards);
        // System.out.println("length: " + cards.length);

        for (int i = 0; i < carLength - 3; i += 2) {
            if (!(Utils.getValue(cards[i]) == Utils.getValue(cards[i + 1]) && (Utils.getValue(cards[i + 1]) + 1 == Utils.getValue(cards[i + 2])))) {
                return false;
            }
        }
        
        if (Utils.getValue(cards[carLength - 1]) == Utils.getValue(cards[carLength - 2])) {
            return true;
        } else {
            return false;
        }
    }

    private static Poker intToPoker(int i) {
        int n = (i - 1) % 13 + 1;
        int t = (i - 1) / 13 + 1;
        return new Poker(n, intToPokerType(t));
    }

    private static PokerType intToPokerType(int t) {
        if (t == 1) {
            return PokerType.Pic;
        } else if (t == 2) {
            return PokerType.Tep;
        } else if (t == 3) {
            return PokerType.Ro;
        } else if (t == 4) {
            return PokerType.Co;
        }
//        if (t == 4) {
//            return PokerType.Co;
//        } else if (t == 3) {
//            return PokerType.Ro;
//        } else if (t == 1) {
//            return PokerType.Pic;
//        } else if (t == 2) {
//            return PokerType.Tep;
//        }
        return null;
    }

    public static void main(String[] args) {
        ChiaBai ch = new ChiaBai();
//        byte[] data = new byte[13];
//        ArrayList<Integer> d = ch.toiTrang(2).e1;
//        for (int i = 0; i < d.size(); i++) {
//            data[i] = d.get(i).byteValue();
//        }
//        byte[] data = new byte[]{3,29,31,44,32,45,21,8,47,35,48,24,11};
        byte[] data = new byte[]{42,19,3,45,20,17,4,33,48,22,23,49,2};
        data = Utils.sortCards(data);
        
//        for (byte b : data) {
//            System.out.println(b + " - " + intToPoker(b).toString());
//        }
        System.out.println(is6Couple(data));
    }
    
    // kiểm tra trường hợp bài vừa chia xong có 6 đôi --> tới trắng
    public static boolean is6Couple(byte[] myHand) {
        int numCouple = 0;
        int i = 0;
        int length = myHand.length;
        while (i < length - 1) {
            if (intToPoker(myHand[i]).isDoi(intToPoker(myHand[i + 1]))) {
                numCouple++;
                i += 2;
            } else {
                i++;
            }
        }
        return (numCouple == 6);
        /*int notInCouple = 0;

         for (int i = 1; i < myHand.length - 1; i++) {
         if (!(Utils.getValue(myHand[i]) == Utils.getValue(myHand[i + 1]) || Utils
         .getValue(myHand[i]) == Utils.getValue(myHand[i - 1]))) {
         notInCouple++;

         }
         }
         if (Utils.getValue(myHand[0]) != Utils.getValue(myHand[1])) {
         notInCouple++;

         }
         if (Utils.getValue(myHand[myHand.length - 1]) != Utils
         .getValue(myHand[myHand.length - 2])) {
         notInCouple++;

         } // pushDebug("notInCouple=" + notInCouple);
         if (notInCouple < 2) {
         int couple3 = 0;

         byte preCard = -1;

         for (int i = 0; i < myHand.length - 1; i++) {
         int num = 0;

         for (int j = 0; j <= myHand.length - 1; j++) {
         if (Utils.getValue(myHand[i]) == Utils.getValue(myHand[j])) {
         num++;

         }
         }
         if (num == 3 && myHand[i] != preCard) {
         couple3++;
         preCard = myHand[i];

         }
         }
         // pushDebug("couple3=" + couple3);
         if ((couple3 <= 1 && notInCouple == 0)
         || (couple3 == 0 && notInCouple == 1)) {
         // System.out.println("Tới trắng 6 đôi!!!");

         return true;

         }
         }
         return false;*/
    }

    // Kiểm tra trường hợp bài vừa chia có 5 đôi thông --> tới trắng
    public boolean is5SerialCouple(byte[] myHand) {
        Vector tempVector = new Vector();
        int len = myHand.length;
        for (int i = 0; i < len - 1; i++) {
            if (Utils.getValue(myHand[i]) == Utils.getValue(myHand[i + 1]) && Utils.getValue(myHand[i]) != 12) {
                tempVector.addElement(Utils.getValue(myHand[i]) + "");
            }
        }
        
        int coupleNum = 0;
        len = tempVector.size();
        for (int j = 0; j < len - 1; j++) {
            if (Byte.parseByte(tempVector.elementAt(j).toString()) + 1 == Byte.parseByte(tempVector.elementAt(j + 1).toString())) {
                coupleNum++;
            }
        }

        // Note: có trường hợp không phải 5 đôi thông nhưng thuộc vào 6 đôi nên
        // phải gọi is6Couple trước trong checkPerfect :D
        if (coupleNum >= 4) {
            // System.out.println("Tới trắng 5 đôi thông!!!");
            return true;
        }
        
        return false;

    }

    // Kiểm tra trường hợp bài vừa chia có phải là sảnh rồng hay không --> tới trắng
    public boolean isSanhRong(byte[] myHand) {
        if (checkCardsType(myHand) == CARDS_SERIAL) {
            return true;
        } else {
            Vector tempVector = new Vector();
            int len = myHand.length;
            for (int i = 0; i < len; i++) {
                boolean isAdded = false;

                for (int j = 0; j < tempVector.size(); j++) {
                    if (Utils.getValue(myHand[i]) == Utils.getValue(Byte.parseByte(tempVector.elementAt(j).toString()))) {
                        isAdded = true;

                        break;
                    }
                }
                if (!isAdded) {
                    tempVector.addElement(myHand[i] + "");
                }
            }
            if (tempVector.size() == 12) {
                byte[] tempCards = new byte[12];

                for (int k = 0; k < tempVector.size(); k++) {
                    tempCards[k] = Byte.parseByte(tempVector.elementAt(k).toString());
                }
                if (checkCardsType(tempCards) == CARDS_SERIAL) {
                    return true;
                }
            }
        }
        return false;

    }

    // kiểm tra trường hợp thắng ngay khi chia bài xong(tới trắng)
    public int checkPerfect(byte[] myHand) {
        int len = myHand.length;
        if (myHand == null || len != 13) {
            outCodeSB.append("Bài vừa chia không đúng!!!").append(NEW_LINE);
            return -1;
        } 
        
        // bài có tứ quý 2
        byte[] firstCards = new byte[4];
        byte[] lastCards = new byte[4];
        int lastIndex = 0;

        for (int i = 0; i < len; i++) {
            if (i >= 0 && i <= 3) {
                firstCards[i] = myHand[i];
            } else if (i >= len - 4 && i <= len - 1) {
                lastCards[lastIndex] = myHand[i];
                lastIndex++;
            }
        }
//		if ((checkCardsType(firstCards) == CARDS_TUQUY && Utils
//				.getValue(firstCards[0]) == 0)
//				|| (checkCardsType(lastCards) == CARDS_TUQUY && Utils
//						.getValue(lastCards[0]) == 12)) {
//			// System.out.println("Tới trắng Tứ Quý!!!");
//			return PERFECT_TUQUY;
//		}
//                // 3 đôi thông có 3 bích
//		if (Utils.getValue(myHand[0]) == Utils.getValue(myHand[1])
//				&& Utils.getValue(myHand[0]) == 0
//				&& Utils.getType(myHand[0]) == 1) {
//			int count4 = 0, count5 = 0;
//			for (int i = 2; i < myHand.length; i++) {
//				if (Utils.getValue(myHand[i]) == 1) {
//					count4++;
//				} else if (Utils.getValue(myHand[i]) == 2) {
//					count5++;
//				}
//			}
//			if (count5 >= 2 && count4 >= 2) {
//				// System.out.println("Tới trắng 3 đôi thông 3 bích!!!");
//				return PERFECT_3SERIAL_COUPLE;
//			}
//		}		
        if (checkCardsType(lastCards) == CARDS_TUQUY && Utils.getValue(lastCards[0]) == 12) {
            // System.out.println("Tới trắng Tứ Quý!!!");
            return PERFECT_TUQUY;
        }
        // 6 đôi
        if (is6Couple(myHand)) {
            // System.out.println("Tới trắng 6 đôi!!!");
            return PERFECT_6COUPLE;
        } // 5 đôi thông
        if (is5SerialCouple(myHand)) {
            // System.out.println("Tới trắng 5 đôi thông!!!");
            return PERFECT_5SERIAL_COUPLE;
        } // Sảnh rồng
        if (isSanhRong(myHand)) {
//            System.out.println("Tới trắng Sảnh rồng!!!");
            return PERFECT_SANHRONG;
        }
        
        return -1;
    }

    // gửi về thông tin và bài của người chơi trong trường hợp tới trắng!
    public ArrayList<String[]> GetEndGamePerfect(long idWin) {
        outCodeSB.append("----GetEndGamePerfect -- ").append(NEW_LINE);
        ArrayList arr = new ArrayList();
        long winMoney = 0;

        int playingSize = playings.size();
        int indexWiner = 0;

        for (int i = 0; i < playingSize; i++) {
            TienLenPlayer p = playings.get(i);
            if (p.id != idWin) {
                String[] o = new String[5];
                o[0] = Long.toString(p.id);
                o[1] = Utils.bytesToString(p.myHand);
                long lostMoney = p.moneyLost(26 * firstCashBet);// thua trắng
                // =// 26 lá

                // TODO:  fix error if user has only 5000 but they can win
                // more
                if (winner.cash < lostMoney) {
                    lostMoney = winner.cash;
                }

                if (lostMoney > p.cash) {
                    lostMoney = p.cash;
                }

                winMoney += lostMoney;
                p.money -= lostMoney;
                o[2] = Long.toString(-lostMoney);
                o[3] = concatString(p.username, ": -", String.valueOf(lostMoney), "$ thua trắng!");
                o[4] = Integer.toString(i);
                arr.add(o);

                p.setExperience(EXPERIENCE_BET);
            } else {
                indexWiner = i;
                p.setExperience(playings.size());
            }
        }
        
        winner.money += (long)((double)winMoney * REAL_GOT_MONEY);
        updateCash();
//        if (winMoney > 0) {
//            winMoney = winMoney - (winMoney * this.tax / 100);
//            winMoney = (long)((double)winMoney * REAL_GOT_MONEY);
//        }
        String[] winnerO = new String[5];
        winnerO[0] = Long.toString(idWin);
        winnerO[1] = Utils.bytesToString(winner.myHand);
        winnerO[2] = Long.toString(winner.money);
        winnerO[3] = concatString(winner.username, ": +", String.valueOf(winMoney), "$ tới trắng!");
        winnerO[4] = Integer.toString(indexWiner);
        arr.add(winnerO);
        outCodeSB.append(winnerO[3]).append(NEW_LINE);
        saveLogToFile();
        //resetPlayers();
        // timerAuto.setRuning(false);
        return arr;
    }

    // trả về kết quả lúc hết ván
    public ArrayList<String[]> GetEndGame(long idWin) {
        ArrayList arr = new ArrayList();
        long winMoney = 0;

        outCodeSB.append("----GetEndGame -- ").append(NEW_LINE);
        int bonusTimes = 1;

        if (duty.getCurrDutyPlayerId() == winner.id) {
            // this user did duty
//            bonusTimes = duty.getBonusTimes(); // No duty babe! - ThangTD
            outCodeSB.append("----duty times -- ").append(bonusTimes).append(NEW_LINE);
        }

        int playingSize = playings.size();
        int indexWinner = 0;
        for (int i = 0; i < playingSize; i++) {
            TienLenPlayer p = playings.get(i);
            if (p.id != idWin) {
                String[] Oj = countCard(p, bonusTimes);
                if (Oj != null) {
                    long lostMoney = Long.parseLong(Oj[1]);
                    long totalLostMoney = lostMoney - p.money;
                    long realLostMoney = lostMoney - p.money;

                    // TODO: fix error if user has only 5000 but they can win more
//                    if (winner.cash < lostMoney) {
//                        lostMoney = winner.cash;
//                        p.money = -lostMoney;
//                    }
//
//                    if (p.cash < lostMoney) {
//                        lostMoney = p.cash;
//                        p.money = -lostMoney;
//                    }
                    
                    winMoney += lostMoney;
                    long diff = 0;
                    
                    if (winner.cash < realLostMoney) {
                        diff = (totalLostMoney - winner.cash);
                        realLostMoney = winner.cash;
//                        p.money = -realLostMoney;
                    }

                    if (p.cash < realLostMoney) {
                        diff = (totalLostMoney - p.cash);
                        realLostMoney = p.cash;
//                        p.money = -realLostMoney;
                    }
                    
                    p.money = -realLostMoney;

//                    winMoney += lostMoney;
                    winMoney -= diff;

                    //if(p.money < 0) {
                    //    Oj[1] = Long.toString((long)(p.money*REAL_GOT_MONEY));
                    //p.money *= REAL_GOT_MONEY;
                    //}else {
                    Oj[1] = Long.toString(p.money);
                    //}
                    Oj[4] = Integer.toString(i);
                    arr.add(Oj);
                    p.setExperience(EXPERIENCE_BET);
                }
            } else {
                indexWinner = i;
                p.setExperience(playings.size());
            }
        }
        
        winner.money += (long)((double)winMoney * REAL_GOT_MONEY);
        
        updateCash();
//        if (winMoney > 0) {
//            winMoney = winMoney - (winMoney * this.tax / 100);
//            winMoney = (long)((double)winMoney * REAL_GOT_MONEY);
//        }
        // winner.cashWin.add(new Couple<Long, Long>(winner.id, winMoney));
        String[] winnerO = new String[5];
        winnerO[0] = Long.toString(idWin);
        winnerO[1] = Long.toString(winner.money);
        winnerO[2] = concatString(winner.username, ": ", String.valueOf(winner.money), "$, Về nhất!");
//        if (duty.getCurrDutyPlayerId() == winner.id) {
//            winnerO[2] = concatString(winnerO[2], duty.toString());
//        }
        // if (winner.money > 0) {
        // /*winnerO[2] = findPlayer(idWin).username + ": +" + winner.money
        // + "$, Về nhất!";*/
        // winnerO[2] = winner.username + ": +" + winner.money
        // + "$, Về nhất!" + duty.toString();
        // } else {
        // /*winnerO[2] = findPlayer(idWin).username + ": " + winner.money
        // + "$, Về nhất!";*/
        // winnerO[2] = winner.username + ": " + winner.money
        // + "$, Về nhất!" + duty.toString();
        // }
        winnerO[4] = Integer.toString(indexWinner);
        arr.add(winnerO);
        //resetPlayers();
        outCodeSB.append(winnerO[2]).append(NEW_LINE);
        // timerAuto.setRuning(false);
        outCodeSB.append("----End game --- Winner -- ").append(winner.username).append(NEW_LINE);
        outCodeSB.append("----------------------------------").append(NEW_LINE);
        outCodeSB.append(NEW_LINE);

        logMini.append(BlahBlahUtil.getLogString("*** Kết thúc ***"));

        saveLogToFile();

        return arr;
    }

    public void resetPlayers() {
        // remove người chơi thoát giữa chừng để chuẩn bị ván mới
        ArrayList<TienLenPlayer> needRemovePlayer = new ArrayList<>();
//		synchronized (lockPlaying) {
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            TienLenPlayer p = this.playings.get(i);
            if (p.isOut || p.notEnoughMoney()) {
                needRemovePlayer.add(p);
            }
        }
        
        int removeSize = needRemovePlayer.size();
        if (removeSize > 0) {
            for (int i = 0; i < needRemovePlayer.size(); i++) {
                playings.remove(needRemovePlayer.get(i));
            }
        }
        playings.addAll(waitings);
        waitings.clear();
        isProcessEnd = false;

        playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            TienLenPlayer p = this.playings.get(i);
            p.numHand = 0;
        }
        isChangeSetting = false;
//		}
        resetAutoKickOut();
    }

    // đếm heo/hàng trong bài còn lại khi kết thúc ván
    public String[] countCard(TienLenPlayer p, int bonusTimes) {
        int count_2red = 0;// số quân heo đỏ
        int count_2black = 0;// số quân heo đen
        int count_tuquy = 0;// số tứ quý trong bài
        int count_3couple = 0;// số 3 đôi thông trong bài
        long lostMoney = 0;
        StringBuilder note = new StringBuilder();
        int numHand = p.numHand;
        byte[] myHand = p.myHand;
        if (myHand == null || numHand == 0) {
            return null;
        } else {
            if (numHand == 13) {
                note.append("phạt cóng! ");
                lostMoney += 26 * firstCashBet; // phạt cóng = 26 lá
            } else {
                note.append("còn ").append(numHand).append(" lá, ");
                lostMoney += numHand * firstCashBet;
            }
            
            for (int i = 0; i < numHand; i++) {
                if (Utils.getValue(myHand[i]) == 12) {
                    if (Utils.getType(myHand[i]) == 1 || Utils.getType(myHand[i]) == 2) {
                        count_2black++;
                        lostMoney += 2 * firstCashBet; // heo đen = 2 lá
                    } else {
                        count_2red++;
                        lostMoney += 5 * firstCashBet; // heo đỏ = 5 lá
                    }
                }
            }
            
            if (count_2black > 0 || count_2red > 0) {
                note.append("thối ");
                if (count_2black > 0) {
                    note.append(count_2black).append(" heo đen, ");
                }
                if (count_2red > 0) {
                    note.append(count_2red).append(" heo đỏ, ");
                }
            }
            
            if (numHand >= 4) {
                int[] appearRate = new int[12];// tần suất xuất hiện các quân
                // bài
                for (int i = 0; i < 12; i++) {
                    appearRate[i] = 0;
                }

                for (int j = 0; j < numHand; j++) {
                    if (Utils.getValue(myHand[j]) != 12) {
                        appearRate[Utils.getValue(myHand[j])]++;
                    }
                }

                // kiểm tra có 4 đôi thông không
                if (numHand >= 4) {
                    for (int i = 0; i < appearRate.length - 3; i++) {
//                    for (int i = 0; i < appearRate.length - 4; i++) {
                        if (appearRate[i] >= 2 && appearRate[i + 1] >= 2 && appearRate[i + 2] >= 2 && appearRate[i + 3] >= 2) {
                            for (int j = 0; j < 4; j++) {
                                appearRate[i + j] = appearRate[i + j] - 2;
                            }
                            lostMoney += 13 * firstCashBet; // 4 đôi thông = 13
                            // lá
                            note.append("bốn đôi thông, ");
                            // money=money+13;
                        }
                    }
                    // kiểm tra tứ quý
                    for (int i = 0; i < appearRate.length; i++) {
                        if (appearRate[i] == 4) {
                            appearRate[i] = 0;
                            count_tuquy++;
                            lostMoney += 7 * firstCashBet; // tứ quý = 7 lá
                            // money=money+7;
                        }
                    }
                    if (count_tuquy > 0) {
                        note.append(count_tuquy).append("tứ quý, ");
                    }
                    // kiểm tra có 3 đôi thông không
                    for (int i = 0; i < appearRate.length - 2; i++) {
//                    for (int i = 0; i < appearRate.length - 3; i++) {
                        if (appearRate[i] >= 2 && appearRate[i + 1] >= 2 && appearRate[i + 2] >= 2) {
                            count_3couple++;
                            lostMoney += 5 * firstCashBet; // 3 đôi thông = 5 lá
                        }
                    }
                    if (count_3couple > 0) {
                        note.append(count_3couple).append(" bộ 3 đôi thông");
                    }
                }
            }
        }
        // System.out.println(result);
//        lostMoney *= bonusTimes;
//        lostMoney = p.moneyLost(lostMoney);
        // System.out.println("lostMoney: " + lostMoney);
//        p.money -= lostMoney;
        
        String[] o = new String[5];
        o[0] = Long.toString(p.id);
        o[1] = Long.toString(lostMoney);
//        o[1] = Long.toString(p.money);
        // System.out.println(" p.money: " + p.money);
        String resString;
        if (p.money > 0) {
            resString = concatString(p.username, ": +", String.valueOf(p.money), "$, ", note.toString());
        } else {
            //note = p.username + ": " + p.money + "$, " + note;
            resString = concatString(p.username, ": ", String.valueOf(p.money), "$, ", note.toString());
        }

        //o[2] = note.toString();
        o[2] = resString;
        o[3] = Utils.bytesToString(p.myHand);

        return o;
    }

    // Added by ThangTD
    public boolean is4DoiThong(TienLenPlayer p)
    {
        int numHand = p.numHand;
        byte[] myHand = p.myHand;
        if (myHand == null || numHand == 0) {
            return false;
        } else if (numHand >= 4) {
            int[] appearRate = new int[12]; // tần suất xuất hiện các quân
            for (int i = 0; i < 12; i++) {
                appearRate[i] = 0;
            }

            for (int j = 0; j < numHand; j++) {
                if (Utils.getValue(myHand[j]) != 12) {
                    appearRate[Utils.getValue(myHand[j])]++;
                }
            }
            
            for (int i = 0; i < appearRate.length - 4; i++) {
                if (appearRate[i] >= 2 && appearRate[i + 1] >= 2 && appearRate[i + 2] >= 2 && appearRate[i + 3] >= 2) {
                    return true;        
                }
            }
        }
        
        return false;
    }
    // End Added by ThangTD
    
    /*
     * public void startTime() { timerAuto.setTimer(20000);
     * timerAuto.setTienLenPlayer(currPlayer); timerAuto.setTienLenTable(this);
     * timerAuto.setRuning(true); timerAuto.reset();
     * 
     * }
     */
    private void updateCash() {
        String moneyChanges = "Thay đổi Gold: ";

        try {
            if (!isProcessEnd) {
                isProcessEnd = true;
            } else {
                logT.warn(concatString("tien len bi end match 2 lan ", String.valueOf(matchID)));
                return;
            }
            
            int playingSize = this.playings.size();
            if (playingSize < 2) {
                logT.warn(concatString("hack game tlmn  ", String.valueOf(matchID)));
                return;
            }

            UserDB userDb = new UserDB();
            StringBuilder desc = new StringBuilder();
            desc.append("TienLen: ").append(String.valueOf(matchID));
            boolean havingMinusBalance = false;

            for (int i = 0; i < playingSize; i++) {
                TienLenPlayer p = playings.get(i);
                if (p != winner) {
                    if (p.money < 0) {
                        //p.money *=REAL_GOT_MONEY;
                    }
                    
                    long plus = p.money;
                    long oldCash = p.cash;
                    p.cash = userDb.updateUserMoney(plus, true, p.id, desc.toString(), p.getExperience(), TIENLEN_LOG_TYPE);

                    moneyChanges += String.format("[%s] %s%d ; ", p.username, plus >= 0 ? "+" : "-", plus);
                    // playings.get(i).cash = playings.get(i).cash + plus;
                    if (p.cash < 0) {
                        logMini.append(BlahBlahUtil.getLogString(String.format("/!\\ Âm tiền: [%] còn %d Gold", p.username, p.cash)));

                        winner.money += (long)((double)p.cash * REAL_GOT_MONEY);
                        
                        p.cash = 0;
                        p.money = -oldCash;
                        
                        outCodeSB.append("Error calculate money game ").append(NEW_LINE);
                        logT.debug("Error calculate money game ");
                        // userDb.notMinus();
                        havingMinusBalance = true;
                    }
                    
                    // Check for in-game event for loser
                    p.checkEvent(false);
                }
            }
            
            // Check for in-game event for winner
            winner.checkEvent(true);

            // Save for winner
//            if (winner.money > 0) {
//                winner.money = (long)((double)winner.money * REAL_GOT_MONEY);
//            }
//                        winner.cash = userDb.updateUserMoney(winner.money, true, winner.id, desc, winner.getExperience(), TIENLEN_LOG_TYPE);
//                        
            int dutyGot = 0;
//            if (duty.getCurrDutyPlayerId() == winner.id) {
//                dutyGot = 1;
//            }

            winner.cash = userDb.updateUserMoneyForTP(winner.money, true, winner.id, desc.toString(), winner.getExperience(), TIENLEN_LOG_TYPE, dutyGot);

            moneyChanges += String.format("[%s] %s%d ; ", winner.username, winner.money >= 0 ? "+" : "", winner.money);
            logMini.append(BlahBlahUtil.getLogString(moneyChanges));
            logMini.append(BlahBlahUtil.getLogString(String.format("Người thắng: [%s]", winner.username)));


            //Event Tien Len
            try {
                mLog.debug("Duty Condition check" + winner.id);
                boolean altCondition = (dutyGot == 1) && (firstCashBet >= 500) && (playings.size() > 2);                
                if(altCondition) {
                    userDb.updateGameEvent(winner.id, ZoneID.TIENLEN);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            // winner.cash += winner.money;

            if (havingMinusBalance) {
                userDb.notMinus();
            }

            resetAutoKickOut();
        } catch (Throwable ex) {
            outCodeSB.append("Error update cash ").append(ex.getStackTrace()).append(NEW_LINE);
        }
    }
    /*
     * public void destroy() { try { timerAuto.destroy();
     * 
     * } catch (Exception e) { e.printStackTrace(); } super.destroy(); //
     * System.out.println("Destroy : " + this.name);
     * 
     * 
     * //timer.destroy(); }
     */
    @Override
    public void autoStartGame(SimplePlayer player) {
        ISession session;

        session = player.currentSession;

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
                int len = this.playings.size();
                if (len > 1) {
                    long now = System.currentTimeMillis();
                    // check user which does nothing when he comes to table
                    boolean isAllJoinReady = true;

                    // Room room = bacayZone.findRoom(matchId);

                    for (int i = 0; i < len; i++) {
                        TienLenPlayer tlPlayer = this.playings.get(i);

                        if (!tlPlayer.isReady && tlPlayer.id != owner.id) {
                            isAllJoinReady = false;
                            // does this user over time out
                            if (now - tlPlayer.getLastActivated() > AUTO_KICKOUT_TIMEOUT) {
                                // kich him
                                kickTimeout(room, tlPlayer, 0);
                                this.remove(tlPlayer);
                                tlPlayer.isOutGame = true;
                                String kickOutMessage = concatString("Auto kick out ", tlPlayer.username);
                                logT.debug(kickOutMessage);
                                outCodeSB.append(kickOutMessage).append(NEW_LINE);
                            }
                        }
                    }

                    if (isAllJoinReady) {
                        // start game
                        if (now - owner.getLastActivated() > AUTO_KICKOUT_OWNER_TIMEOUT) {
                            SimplePlayer oldOwner = owner.clone();

                            TienLenPlayer currOwner = findPlayer(owner.id);
                            currOwner.isOutGame = true;
                            owner = this.ownerQuit();
                            kickTimeout(room, oldOwner, owner.id);
                            // autoStartGame(owner);
                            if (currPlayer != null && currOwner.id == currPlayer.id) {
                                currPlayer.isOutGame = true;
                            }

                            this.resetPlayers();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            try {
                logT.error(concatString("tienlen Kick time out error matchId ", String.valueOf(matchID)), ex);
                outCodeSB.append("Kick out error").append(NEW_LINE);
                cancel(playings);
                room.allLeft();
                this.destroy();
            } catch (Exception exx) {
            }
        }
    }

    @Override
    public ISession getNotNullSession() {
        int playingSize = this.playings.size();
        for (int i = 0; i < playingSize; i++) {
            TienLenPlayer player = this.playings.get(i);
            if (!player.isOutGame && player.currentSession != null
                    && player.currentSession.getMessageFactory() != null) {
                return player.currentSession;
            }
        }

        return null;
    }

    @Override
    public void doTimeout() {
        lastActivated = System.currentTimeMillis();
        /*
         * if(this.isPlaying) {
         */
        if (currPlayer == null) {
            logT.error(concatString("tienlen do time out currPlay = null matchId", String.valueOf(matchIDAuto)));
            outCodeSB.append("-[Auto player is null] + players size")
                    .append(this.playings.size()).append(" current index: ")
                    .append(currentIndexOfPlayer).append(" matchId:")
                    .append(this.matchID).append(NEW_LINE);

            String str = concatString("[Auto player is null] , players size ", String.valueOf(this.playings.size()), " current Index: ", String.valueOf(currentIndexOfPlayer), " matchId: ",
                    String.valueOf(this.matchID));

            this.isPlaying = false;
            // out_code.println(str);
            logT.warn(str);
            //return;
        } else {
            if (currPlayer.currentSession != null) {
                outCodeSB.append("Player ").append(currPlayer.username).append(" auto play").append(NEW_LINE);

                currPlayer.autoPlay(this);
            } else {
                outCodeSB.append("Auto OMG currentSession is null!").append(NEW_LINE);
            }
        }
        // return;
		/*
         * }
         */

    }

    @Override
    public int getTableSize() {
        return this.playings.size() + this.waitings.size();
    }

    /**
     * @return the duty
     */
    public Duty getDuty() {
        return duty;
    }

    /**
     * @return the hidePoker
     */
    public boolean isHidePoker() {
        return hidePoker;
    }

    /**
     * @param hidePoker the hidePoker to set
     */
    public void setHidePoker(boolean hidePoker) {
        this.hidePoker = hidePoker;
    }

    @Override
    public List<? extends SimplePlayer> getNewPlayings() {
        return playings;
    }

    @Override
    public List<? extends SimplePlayer> getNewWaitings() {
        return waitings;
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