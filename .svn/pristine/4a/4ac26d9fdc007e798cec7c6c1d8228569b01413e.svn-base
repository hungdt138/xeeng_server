package com.tv.xeeng.game.phom.data;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BocPhomRequest;
import com.tv.xeeng.base.protocol.messages.HaPhomRequest;
import com.tv.xeeng.base.protocol.messages.TurnRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.Couple;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

import java.util.*;
import java.util.logging.Level;

import static com.tv.xeeng.server.Server.REAL_GOT_MONEY;

public class PhomPlayer extends SimplePlayer {

    public PhomTable table;
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(PhomPlayer.class);

    private static final int AUTO_DANH = 3000;
    private static final int TIMES = 4;
    public boolean isOwner;
    public boolean isObserve;

    // public ISession currentSession;
    public boolean haPhom = false;
    public boolean momStatus = false;
    public boolean uStatus = false;
    public boolean doneBocBai = false;
    public boolean doneHaBai = false;
    public boolean outOfTime = false;

    // An cay chot hoac cho an cay mat tien
    public ArrayList<Couple<Long, Long>> cashLost = new ArrayList<Couple<Long, Long>>();
    public ArrayList<Couple<Long, Long>> cashWin = new ArrayList<Couple<Long, Long>>();
    public long money = 0;
    public int numberCardPlay;

    // list of all current cards in hand
    public ArrayList<Poker> allCurrentCards = new ArrayList<Poker>();
    // list of playing cards
    public Vector<Poker> playingCards = new Vector<Poker>();
    // list of getting cards
    public Vector<Poker> gettingCards = new Vector<Poker>();
    // list of eating cards
    public Vector<Poker> eatingCards = new Vector<Poker>();
    // list of offering cards - danh sach cac quan bai gui sang phom nguoi ha	
    // bai truoc
    public Vector<Poker> offeringCards = new Vector<Poker>();

    // list of phom
    public ArrayList<Phom> phoms = new ArrayList<Phom>();
    private int phomSize = 0;

    // Status of game
    public int START = 0;
    public int BOC = 1;
    public int AN = 2;
    public int DANH = 3;
    public int HA = 4;

    public int gameStatus = -1;

    // Tien thua cuoi van - ThangTD
    public long resultMoney = 0;

    public long moneyLost() {
        wonMoney = -(moneyLost(-moneyCompute()));
        return wonMoney;
    }

    public long moneyCompute() {
        long res = 0;

        for (Couple<Long, Long> win : this.cashWin) {
            //mLog.debug("win:"+win.e2);
            res += (long) ((double) win.e2 * REAL_GOT_MONEY);
//            mLog.debug("---THANGTD TAX DEBUG---" + this.username + " CASH WIN: " + (long)((double)win.e2 * REAL_GOT_MONEY));
        }
        for (Couple<Long, Long> lost : this.cashLost) {
            //mLog.debug("lost:"+lost.e2);
            res -= lost.e2;
//            mLog.debug("---THANGTD TAX DEBUG---" + this.username + " CASH LOST: " + lost.e2);
        }

        //mLog.debug("total:"+res);
//        if (res > 0) { 
//            res = (long)((double)res * REAL_GOT_MONEY);
//        }

        return res;
    }

    // Added by ThangTD
    public long moneyLostNew() {
        return -(moneyLost(-moneyComputeNew()));
    }

    public long moneyComputeNew() {
        long res = 0;

        for (Couple<Long, Long> win : this.cashWin) {
            //mLog.debug("win:"+win.e2);
            res -= (long) ((double) win.e2);
            mLog.debug("---THANGTD TAX DEBUG---" + this.username + " WINNER LOST: " + win.e2);
        }
        for (Couple<Long, Long> lost : this.cashLost) {
            //mLog.debug("lost:"+lost.e2);
            res += (long) ((double) lost.e2 * REAL_GOT_MONEY);
            mLog.debug("---THANGTD TAX DEBUG---" + this.username + " WINNER WIN: " + (long) ((double) lost.e2 * REAL_GOT_MONEY));
        }

        //mLog.debug("total:"+res);
//        if (res > 0) { 
//            res = (long)((double)res * REAL_GOT_MONEY);
//        }

        return res;
    }
    // End added by ThangTD

    public String cardToString(ArrayList<Phom> cards) {
        String res = "";
        if (cards.size() > 0) {
            res += cards.get(0).toString();
            for (int i = 1; i < cards.size(); i++) {
                res += ";";
                res += cards.get(i).toString();
            }
        }
        return res;
    }

    public String cardToString(Vector<Poker> cards) {
        String res = "";
        if (cards.size() > 0) {
            res += cards.get(0).toInt();
            for (int i = 1; i < cards.size(); i++) {
                res += "#";
                res += cards.get(i).toInt();
            }
        }
        return res;
    }

    public String getUCards() {
        if (phoms.isEmpty()) {
            return allPokersToString();
        }

        return "";
    }

    public String allPokersToString() {
        String res = "";
        if (this.allCurrentCards.size() > 0) {
            res += this.allCurrentCards.get(0).toInt();
            for (int i = 1; i < this.allCurrentCards.size(); i++) {
                res += "#";
                res += this.allCurrentCards.get(i).toInt();
            }
        }

        if (uStatus) {
            for (int i = 0; i < phoms.size(); i++) {
                if (res.length() > 0) {
                    res = res + "#" + phoms.get(i).toString();
                } else {
                    res = phoms.get(i).toString();
                }
            }
        }
        return res;
    }

    // final point
    public int point;

    // stopping order - thu tu ha bai
    public int stoppingOrder;
    public boolean isLastMove = false;
    private boolean vuaAnPhaiKhong = false;

    /**
     * 0: Waiting 1: Vua boc 2: Vua an 3: Ha
     */
    public int status;

    /**
     * 0: Khong U 1: U 3 phom bt 11: U den 12: Tai gui u den 2: U khan 3: U 0 diem - gui het bai
     */
    public int uType = 0;

    public boolean isAutoPlay = false; // Khi user out, may se tu choi va bat flag nay len

    public PhomPlayer() {
    }

    public boolean getVuaAnPhaiKhong() {
        return vuaAnPhaiKhong;
    }

    public void setCurrentOwner(ISession currentOwner) {
        this.currentOwner = currentOwner;
    }

    /* Lay danh sach phom theo cac quan bai da an
     * 
     */
    public String getPhom() {

        String cards = "";

        if (eatingCards.isEmpty()) {
            return "";
        }

        for (Poker po : eatingCards) {
            try {
                Vector<Phom> ps = getPhom(po, allCurrentCards);

                if (ps.size() > 0) {
                    Phom pp = ps.get(ps.size() - 1);
                    if (cards.length() == 0) {
                        cards = pp.toString();
                    } else {
                        cards = cards + ";" + pp.toString();
                    }
                }

            } catch (PhomException ex) {
                java.util.logging.Logger.getLogger(PhomPlayer.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }

        cards = cards.replace(" ", "");
        //System.out.println("Cards : " + cards);

        return cards;
    }

    /*
     * Tu dong lay danh sach phom, gom da an cay va con lai tren bai
     */
    public String getPhomAuto() {
        String cards = "";
        ArrayList<Poker> remainCards = (ArrayList<Poker>) this.allCurrentCards;
        // Cac phom da an
        for (Poker po : eatingCards) {
            try {
                Vector<Phom> ps = getPhom(po, allCurrentCards);
                if (ps.size() > 0) {
                    Phom pp = ps.get(ps.size() - 1);
                    if (cards.length() == 0) {
                        cards = pp.toString();
                    } else {
                        cards = cards + ";" + pp.toString();
                    }
                    removeVectorPoker(remainCards, pp.cards);
                }

            } catch (PhomException ex) {
                java.util.logging.Logger.getLogger(PhomPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        int size = remainCards.size();

        // cac phom co san tren bai
        if (size > 0) {
            ArrayList<Poker> sortedRemainCards = this.sortCard(remainCards);
            Poker p = null;
            try {
                for (int i = 0; i < size; i++) {
                    p = sortedRemainCards.get(i);
                    if (isInListPoker(p, remainCards)) {
                        Vector<Phom> ps = getPhom(p, remainCards);
                        if (ps.size() > 0) {
                            Phom pp = ps.get(ps.size() - 1);
                            if (cards.length() == 0) {
                                cards = pp.toString();
                            } else {
                                cards = cards + ";" + pp.toString();
                            }
                            removeVectorPoker(remainCards, pp.cards);
                        }
                    }
                }
            } catch (Exception ex) {
                // no event
            }
        }

        cards = cards.replace(" ", "");
        return cards;
    }

    public String getPhomAutoThanhnvt() {
        String cards = "";
        ArrayList<Poker> remainCards = (ArrayList<Poker>) this.allCurrentCards;

        Vector<Phom> phoms = getPhomThanhnvt();
        if (phoms.size() > 0) {
            for (Phom p : phoms) {
                if (cards.length() == 0) {
                    cards = p.toString();
                } else {
                    cards = cards + ";" + p.toString();
                }
            }
        }

        return cards;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Vector getPhomVector() {

        Vector<Poker> cards = new Vector();
        if (eatingCards.isEmpty()) {
            return cards;
        }

        for (Poker po : eatingCards) {
            try {
                Vector<Phom> ps = getPhom(po, allCurrentCards);
                for (Phom p : ps) {
                    for (Poker p1 : p.cards) {
                        if (!isInListPoker(p1, cards)) {
                            cards.add(p1);
                        }
                    }
                }
                // if (ps.size() > 0) {
                // Phom pp = ps.get(ps.size() - 1);
                // for (Poker p2 : pp.cards) {
                // cards.add(p2);
                // }
                // }
            } catch (PhomException ex) {
                mLog.debug("Error: " + ex.getMessage());
                java.util.logging.Logger.getLogger(PhomPlayer.class.getName())
                        .log(Level.SEVERE, null, ex);
                continue;
            }
        }

        return cards;
    }

    public ArrayList<ArrayList<Integer>> getCards(String input) {
        ArrayList<ArrayList<Integer>> res = new ArrayList<ArrayList<Integer>>();
        String[] i1 = input.split(";");
        for (String i : i1) {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            String[] i2 = i.split("#");
            for (String j : i2) {
                if (!j.isEmpty()) {
                    try {
                        temp.add(Integer.parseInt(j));
                    } catch (NumberFormatException ex) {
                        mLog.error(ex.getMessage(), "j = '" + j + "'");
                    }
                }
            }
            res.add(temp);
        }
        return res;
    }

    /**
     * @param phom
     */
    @SuppressWarnings("unchecked")
    public void autoPlay(PhomTable phom) {
        try {
            /*
             * System.out.println("autoPlay! Came here : " + currentMatchID + "
             * : " + id);
             */
            ISession session;

            if (currentSession != null) {
                session = currentSession;
            } else {
                session = currentOwner;
            }

            if (!isAutoPlay) {
                outOfTime = true;
            }

            ISession notNullSession = phom.getNotNullSession();

            MessageFactory msgFactory = notNullSession.getMessageFactory();
            IResponsePackage responsePkg = notNullSession.getDirectMessages();
            IBusiness business;

            //  fix error null session
            if (session.getMessageFactory() == null) {
                session = notNullSession;
            }

            // MessagesID.BOC_PHOM
            if (allCurrentCards.size() < 10 && !doneBocBai) {
                BocPhomRequest rqBoc = (BocPhomRequest) msgFactory.getRequestMessage(MessagesID.BOC_PHOM);
                rqBoc.matchID = currentMatchID;
                rqBoc.uid = id;
                rqBoc.zoneId = ZoneID.PHOM;// only for auto

                business = msgFactory.getBusiness(MessagesID.BOC_PHOM);
                business.handleMessage(session, rqBoc, responsePkg);

                // waitMe(t,10000);
                phom.setCurrentTimeOut(AUTO_DANH);
                return;
            }
            // MessagesID.HA_PHOM
            if (phom.isHaBaiTurn() && phom.getCurrentPlayer().id == id && !doneHaBai) {
                HaPhomRequest rqTurn = (HaPhomRequest) msgFactory.getRequestMessage(MessagesID.HA_PHOM);
                rqTurn.matchID = currentMatchID;
//                rqTurn.cards1 = getPhomAuto();
                rqTurn.cards1 = getPhomAutoThanhnvt(); // thanhnvt
                rqTurn.u = 0;

                if (!haPhom && rqTurn.cards1.length() == 0) {
                    momStatus = true;
                    doneHaBai = true;
                    mLog.debug("---AUTO HẠ PHỎM MÓM---[id " + phom.getCurrentPlayer().username + "][" + phom.getCurrentPlayer().id + "]");

                    phom.setCurrentTimeOut(AUTO_DANH);
                    return;
                } else if (!haPhom || rqTurn.cards1.length() > 0) {
                    rqTurn.cards = getCards(rqTurn.cards1);

                    // check u
                    int size = 0;
                    for (ArrayList<Integer> temp : rqTurn.cards) {
                        size += temp.size();
                    }

                    if (size >= 9) {
                        rqTurn.u = 1;
                    }

                    rqTurn.uid = id;
                    rqTurn.zoneId = ZoneID.PHOM;// only for auto
                    business = msgFactory.getBusiness(MessagesID.HA_PHOM);
                    business.handleMessage(session, rqTurn, responsePkg);

                    // waitMe(t,5000);
                    phom.setCurrentTimeOut(AUTO_DANH);
                    return;
                }
            }
            // MessagesID.MATCH_TURN
            if (phom.getCurrentPlayer().id == id) {
                TurnRequest rqTurn = (TurnRequest) msgFactory.getRequestMessage(MessagesID.MATCH_TURN);
                rqTurn.mMatchId = currentMatchID;
                rqTurn.uid = id;
                rqTurn.isTimeout = true;
                rqTurn.zoneId = ZoneID.PHOM;// only for auto
                business = msgFactory.getBusiness(MessagesID.MATCH_TURN);

                Vector<Poker> pv = getPhomVector();
                // boolean flag = true;
                try {
                    ArrayList<Poker> lstLePoker = make(allCurrentCards);
                    int lePokerSize = lstLePoker.size();
                    int retryCount = 0;
                    long currentTime = System.currentTimeMillis();
                    while (lePokerSize > 0) {
                        retryCount++;
                        Random rand = new Random(currentTime * retryCount);
                        int index = (int) (Math.abs(rand.nextLong()) % lePokerSize);
                        Poker p = lstLePoker.get(index);
                        if (!isInListPoker(p, pv) && (!isInListPoker(p, eatingCards))) {
                            // System.out.println("Phom:");
                            // printVectorPoker(pv);
                            int card = p.toInt();
                            rqTurn.phomCard = card;
                            business.handleMessage(session, rqTurn, responsePkg);
                            return;
                        }

                        lstLePoker.remove(p);
                        lePokerSize--;
                    }

                    for (int i = allCurrentCards.size() - 1; i >= 0; i--) {
                        Poker p = allCurrentCards.get(i);

                        if (!isInListPoker(p, pv) && (!isInListPoker(p, eatingCards))) {
                            // System.out.println("Phom:");
                            // printVectorPoker(pv);
                            int card = p.toInt();
                            rqTurn.phomCard = card;
                            business.handleMessage(session, rqTurn, responsePkg);
                            return;
                        }
                    }
                } catch (Exception ex) {
                    mLog.error(ex.getMessage(), ex);
                }

                for (int i = allCurrentCards.size() - 1; i >= 0; i--) {
                    Poker p = allCurrentCards.get(i);
                    int card = p.toInt();
                    if (!isInListPoker(p, pv)
                            && (!isInListPoker(p, eatingCards))) {
                        // System.out.println("Phom:");
                        // printVectorPoker(pv);
                        rqTurn.phomCard = card;
                        business.handleMessage(session, rqTurn, responsePkg);
                        return;
                    }
                    // for (Poker p2 : pv) {
                    // if (allCurrentCards.get(i).toInt() == p2.toInt()) {
                    // flag = false;
                    // }
                    // }
                    // for (Poker p3 : this.eatingCards) {
                    // if (card == p3.toInt()) {
                    // flag = false;
                    // }
                    // }
                    // if (flag) {
                    // business.handleMessage(session, rqTurn,
                    // responsePkg);
                    // return;
                    // }
                }

                rqTurn.phomCard = eatingCards.get(eatingCards.size() - 1).toInt();
                business.handleMessage(session, rqTurn, responsePkg);
            }

        } catch (ServerException ex) {
            // ex.printStackTrace();
            /*
             * java.util.logging.Logger.getLogger(PhomPlayer.class.getName()).log
             * ( Level.SEVERE, null, ex);
             */
        } catch (Exception ex) {
            // e.printStackTrace();
            mLog.error(ex.getMessage(), ex);
        }
    }

    private boolean isInListPoker(Poker p, Vector<Poker> input) {
        for (Poker p1 : input) {
            if (p.isEqual(p1)) {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        outOfTime = false;
        point = 0;
        isStop = false;
        momStatus = false;
        uStatus = false;
        allCurrentCards.clear();
        playingCards.clear();
        gettingCards.clear();
        offeringCards.clear();
        eatingCards.clear();
        phoms.clear();
        cashLost.clear();
        cashWin.clear();
        isWin = false;
        money = 0;
        uType = 0;

        setPhomSize(0);

        stoppingOrder = 0;// chua ha bai
        vuaAnPhaiKhong = false;
        isObserve = false;
        haPhom = false;

        doneHaBai = false;
        doneBocBai = false;

        isOut = false;
        gameStatus = -1;
    }

    @Override
    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public void setPhoms(ArrayList<Phom> input) {
        gameStatus = HA;
        this.phoms = input;
    }

    private boolean isIn(Phom phom, Poker card) {
        for (Poker p : phom.cards) {
            if (p.toInt() == card.toInt()) {
                return true;
            }
        }
        return false;
    }

    private boolean isIn(Poker card) {
        for (Phom p : this.phoms) {
            if (isIn(p, card)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkEatCardInPhoms() {
        for (Poker p : this.eatingCards) {
            if (!isIn(p)) {
                return false;
            }
        }
        return true;
    }

    public boolean isUkhan() {
        for (int i = 0; i < this.allCurrentCards.size() - 1; i++) {
            Poker pi = this.allCurrentCards.get(i);
            for (int j = i + 1; j < this.allCurrentCards.size(); j++) {
                Poker pj = this.allCurrentCards.get(j);
                if (pi.isCa(pj)) {
                    return false;
                }
            }
        }
        this.uType = 2;
        return true;
    }

    public void removePoker(Poker p) {
        for (int i = 0; i < allCurrentCards.size(); i++) {
            if (allCurrentCards.get(i).isEqual(p)) {
                allCurrentCards.remove(i);
                break;
            }
        }
    }

    public void gui(Vector<Poker> cards) throws PhomException {
        // isLastMove = true;
        this.offeringCards.addAll(cards);
        computeFinalPoint();
    }

    public void guiED(int phomID, Vector<Poker> cards) throws PhomException {
        try {
            Phom p = this.phoms.get(phomID);
            p.gui(cards);
        } catch (Exception e) {
            try {
                int phomSize = this.phoms.size();
                boolean guiOk = false;
                for (int i = 0; i < phomSize; i++) {
                    if (i != phomID)//anotherIndex
                    {
                        Phom p = this.phoms.get(i);
                        if (p.isAllowGui(cards)) {
                            p.gui(cards);
                            guiOk = true;
                            break;
                        }
                    }
                }

                if (!guiOk) {
                    throw new PhomException("Gui sai phom");
                }
            } catch (Exception ex) {
                throw new PhomException("Gui sai phom");
            }
        }
    }

    public boolean checkU(int card, boolean isTaiGuiUDen, boolean isHa,
                          ArrayList<ArrayList<Integer>> cards)
            throws PhomException {

        int lenght = 0;
        for (Phom temp : phoms) {
            lenght += temp.cards.size();
        }

        boolean flagU = false;

        for (ArrayList<Integer> temp : cards) {
            lenght += temp.size();
            Vector<Poker> phom = new Vector<Poker>();
            for (int i : temp) {
                int phomSize = phoms.size();
                for (int j = 0; j < phomSize; j++) {
                    Phom p = phoms.get(j);
                    int pokerSize = p.cards.size();

                    for (int k = 0; k < pokerSize; k++) {
                        Poker phomElement = p.cards.get(k);

                        if (phomElement.toInt() == i) {
                            return false;
                        }
                    }
                }
                phom.add(Utils.numToPoker(i));
            }
//			phoms.add(new Phom(phom));
        }
        if (lenght >= 9 || this.eatingCards.size() == 3) {
            flagU = true;
        }

        if (!flagU) {
            return false;
        }

        gameStatus = HA;

        if (card > 0) {
            Poker p = Utils.numToPoker(card);
            play(p);
        }

        for (ArrayList<Integer> temp : cards) {
            lenght += temp.size();
            Vector<Poker> phom = new Vector<Poker>();

            for (int i : temp) {
                phom.add(Utils.numToPoker(i));
            }

            phoms.add(new Phom(phom));
        }

        if (this.eatingCards.size() == 3) {
            this.uType = 11;
        } else if (lenght >= 9) {

            if (isHa && isTaiGuiUDen) {
                this.uType = 12;
            } else {
                this.uType = 1;
            }

        } else {
            return false;
        }
        allCurrentCards.clear();

        return true;

    }

    @SuppressWarnings("unused")
    private boolean checkDupPokerPhom(Vector<Poker> input) {
        // int index = 0;
        for (Poker p : input) {
            if (this.eatingCards.contains(p)) {
                index++;
            }
        }
        return (index < 2);
    }

    public boolean hasPoker(Poker p) {
        for (Poker po : this.allCurrentCards) {
            if (po.toInt() == p.toInt()) {
                return true;
            }
        }
        return false;
    }

    public void takeTenthPoker(Poker p) {
        // mLog.info(username + " Take tenth Cards : " + p + " : " +
        // allCurrentCards.size() + "  ; " + showCards(allCurrentCards));
        this.allCurrentCards.add(p);
        // mLog.info(username + " Take tenth Cards : " + p + " : " +
        // allCurrentCards.size() + "  ; " + showCards(allCurrentCards));
    }

    public void take(Poker p) {
        gameStatus = BOC;
//		mLog.debug("Player " + id + ": Take : " + p.toString());
        vuaAnPhaiKhong = false;
        if (this.numberCardPlay >= 3) {
            status = 3;
        } else {
            status = 1;
        }
        this.allCurrentCards.add(p);
        this.gettingCards.add(p);
        doneBocBai = true;

//		showCards();
    }

    public void eated(PhomPlayer player, long money/*boolean isChot, int index, boolean isAn*/) {
        Couple<Long, Long> couple = new Couple<Long, Long>(this.id, money);
        player.cashLost.add(couple);
        /*if (isChot) {
         Couple<Long, Long> chot = new Couple<Long, Long>(this.id,
         this.moneyForBet * 4);
         player.cashLost.add(chot);
         } else if (isAn) {
         switch (index) {
         case 1:
         player.cashLost.add(new Couple<Long, Long>(this.id,
         this.moneyForBet * 1));
         break;
         case 2:
         player.cashLost.add(new Couple<Long, Long>(this.id,
         this.moneyForBet * 2));
         break;
         case 3:
         player.cashLost.add(new Couple<Long, Long>(this.id,
         this.moneyForBet * 3));
         break;
         default:
         break;
         }
         }*/

        /*
         * if(index == 3){ Couple<Long, Long> uDen = new Couple<Long,
         * Long>(this.id, this.moneyForBet*5); player.cashLost.add(uDen); }
         */
    }

    public PhomPlayer prePlayer() throws PhomException {
        return this.table.getPrePlayer(this);
    }

    public PhomPlayer nextPlayer() throws PhomException {
        return this.table.getNextPlayer(this);
    }

    private int isValidEat(Poker p) {
        ArrayList<Poker> temp = new ArrayList<Poker>();
        temp.addAll(allCurrentCards);
        temp.add(p);
        try {
            Vector<Phom> phom = getPhom(p, temp);
            boolean hasPhom = !phom.isEmpty();
            if (hasPhom) {
                if (temp.size() == phom.get(0).cards.size()) {
                    return 1;//U roi khong con cay danh
                }
                return 0;//co phom
            }
            return -1;// khong co phom
        } catch (Throwable e) {
            return -1;//khong co phom
        }

    }

    public long eat(Poker p, PhomPlayer player, boolean isChot, boolean isAn, int round) throws PhomException, BusinessException {
        int eatValid = isValidEat(p);
        if (eatValid == -1) {
            throw new PhomException("Bạn ăn không đúng rồi!");
        }

        gameStatus = AN;
        long res = 0;
        vuaAnPhaiKhong = true;
        // status = 2;
        this.allCurrentCards.add(p);
        this.eatingCards.add(p);
        doneBocBai = true;

        if (isChot) {
            Couple<Long, Long> chot = new Couple<Long, Long>(player.id, this.moneyForBet * 4);
//            Couple<Long, Long> chotAfterTax = new Couple<Long, Long>(player.id, (long)((double)this.moneyForBet * 4 * REAL_GOT_MONEY));
            this.cashWin.add(chot);
            res += chot.e2;
        } else if (isAn) {
            Couple<Long, Long> an = new Couple<Long, Long>(player.id, this.moneyForBet * round);
//            Couple<Long, Long> anAfterTax = new Couple<Long, Long>(player.id, (long)((double)this.moneyForBet * round * REAL_GOT_MONEY));
            /*switch (this.eatingCards.size()) {
             case 1:
             an = new Couple<Long, Long>(player.id, this.moneyForBet * 1);
             break;
             case 2:
             an = new Couple<Long, Long>(player.id, this.moneyForBet * 2);
             break;
             case 3:
             an = new Couple<Long, Long>(player.id, this.moneyForBet * 3);

             break;
             default:
             throw new PhomException("Khong the nhu the duoc!");
             }*/
            this.cashWin.add(an);

            res += an.e2;
            momStatus = false;
        }
        
        /*
         * //U den if(this.eatingCards.size() == 3){ Couple<Long, Long> uDen =
         * new Couple<Long, Long>(player.id, this.moneyForBet*5);
         * this.cashWin.add(uDen); }
         */
        eated(player, res);
//		mLog.debug("Vong:"+round+" An dc: "+res);
        changePlayedCard();
        if (eatValid == 1) {
            throw new BusinessException("U!");
        }

        return res;
    }

    // Chuyen cay bai an.
    private void changePlayedCard() throws PhomException {
        PhomPlayer pre = prePlayer();
        PhomPlayer next = nextPlayer();
        if (pre.id == next.id) { // co 2 nguoi
            if (pre.playingCards.size() == playingCards.size()) { // chuyen
                pre.playingCards.remove(pre.playingCards.size() - 1);
                int size = playingCards.size();
                Poker po = playingCards.remove(size - 1);
                pre.playingCards.add(po);
            } else { // remove
                pre.playingCards.remove(pre.playingCards.size() - 1);
            }
        } else { // >2 nguoi
            if (pre.playingCards.size() == playingCards.size()) { // pre danh cuoi
                pre.playingCards.remove(pre.playingCards.size() - 1);
                int size = playingCards.size();
                Poker po = playingCards.remove(size - 1);
                pre.playingCards.add(po);
            } else {
                while (next.playingCards.size() == playingCards.size()) {
                    next = next.nextPlayer();
                }
                if (next.id == pre.id) {// pre danh dau
                    pre.playingCards.remove(pre.playingCards.size() - 1);
                } else {// pre danh giua chung
                    pre.playingCards.remove(pre.playingCards.size() - 1);
                    int size = next.playingCards.size();
                    Poker po = next.playingCards.remove(size - 1);
                    pre.playingCards.add(po);
                }
            }
        }
    }

    public String showPureCards(ArrayList<Poker> allCurrentCards) {
        String s = "";
        for (int i = 0; i < allCurrentCards.size(); i++) {
            if (i == allCurrentCards.size() - 1) {
                s = s + (allCurrentCards.get(i).toInt());
            } else {
                s = s + (allCurrentCards.get(i).toInt() + " ");
            }
        }
        return s;
    }

    public String showCards(ArrayList<Poker> allCurrentCards) {
        String s = "";
        for (int i = 0; i < allCurrentCards.size(); i++) {
            s = s + (allCurrentCards.get(i).toString() + "|");
        }

//        s = s + ("(");
//        for (int i = 0; i < allCurrentCards.size(); i++) {
//            if (i == allCurrentCards.size() - 1) {
//                s = s + (allCurrentCards.get(i).toInt());
//            } else {
//                s = s + (allCurrentCards.get(i).toInt() + " ");
//            }
//        }
//        s = s + (")");
        return s;
    }

    public String showCards(Vector<Poker> allCurrentCards) {
        String s = "";
        for (int i = 0; i < allCurrentCards.size(); i++) {
            s = s + (allCurrentCards.get(i).toString() + "|");
        }

        s = s + ("(");
        for (int i = 0; i < allCurrentCards.size(); i++) {
            s = s + (allCurrentCards.get(i).toInt() + " ");
        }
        s = s + (")");

        return s;
    }

    //	public void showCards() {
//		mLog.info("[" + this.currentMatchID + "]" + "Player [" + id + "]["
//				+ username + "](" + allCurrentCards.size() + "):"
//				+ showCards(allCurrentCards));
//	}
    @Override
    public boolean notEnoughMoney() {
        if (cash < TIMES * moneyForBet) {
            return true;
        }
        return false;
    }

    public void play(Poker p) throws PhomException {
        if (hasPoker(p)) {
            doneBocBai = false;
            gameStatus = DANH;
            this.numberCardPlay++;
            for (int i = 0; i < allCurrentCards.size(); i++) {
                if (allCurrentCards.get(i).isEqual(p)) {
                    allCurrentCards.remove(i);
                    break;
                }
            }
            this.playingCards.add(p);

            status = 0;

        } else {
            mLog.error("Quan bai ko ton tai : " + p + " ; " + currentSession.userInfo());
            mLog.error("[" + this.currentMatchID + "]" + "Player [" + id + "][" + username + "](" + allCurrentCards.size() + "):"
                    + showCards(allCurrentCards) + "  ;  " + "Player " + id + ": play : " + p.toString());
            throw new PhomException("Khong tim thay cay bai!");
        }
    }

    public void setPoker(String s) {
        String[] cards = s.split("#");
        for (int i = 0; i < cards.length; i++) {
            Poker p = Utils.numToPoker(Integer.parseInt(cards[i]));
            allCurrentCards.add(p);
        }
    }

    public void setPoker(Vector<Poker> restCards, String s) {

        // System.out.println("restCards : " + restCards.size());
        // System.out.println("allCurrentCards : " + allCurrentCards.size());
        String[] cards = s.split(" ");

        for (int i = 0; i < cards.length; i++) {
            Poker p = Utils.numToPoker(Integer.parseInt(cards[i]));
            allCurrentCards.add(p);
            // removePoker(Utils.numToPoker(Integer.parseInt(cards[i])));

            for (int j = 0; j < restCards.size(); j++) {
                if (restCards.get(j).isCa(p)) {
                    restCards.remove(j);
                    break;
                }
            }
        }

        Random r = new Random();
        for (int i = 0; i < 9 - cards.length; i++) {
            int k = r.nextInt(restCards.size());
            allCurrentCards.add(restCards.get(k));
            restCards.remove(k);
        }

    }

    public void setPokers(ArrayList<Poker> inputPoker) {
        this.allCurrentCards = inputPoker;

    }

    public void setAvatarID(int avatarID) {
        this.avatarID = avatarID;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCash(long c) {
        this.cash = c;
    }

    public PhomPlayer(long id) {
        this.id = id;
        isStop = false;
        this.isGiveUp = false;
    }

    public void setMoney(long money) {
        moneyForBet = money;
    }

    public PhomPlayer(ArrayList<Poker> inputPoker, long id, long minBet) {
        this.allCurrentCards = inputPoker;
        this.id = id;
        this.moneyForBet = minBet;
        this.isGiveUp = false;
        isStop = false;
    }

    // calculate final point
    public void computeFinalPoint() {
        // add all rest cards of player
        // remove all offering cards
        for (Poker p : this.offeringCards) {
            removePoker(p);
        }
        // remove all cards in phom
        int size = phoms.size();
        for (int i = 0; i < size; i++) {
            for (Poker p : this.phoms.get(i).cards) {
                // this.allCurrentCards.remove(p);
                removePoker(p);
            }
        }
        compute();
    }

    private void compute() {
        point = 0;

        if (momStatus) {
            this.point = 1000;
            return;
        }

        for (Poker p : this.allCurrentCards) {
            this.point += p.getNum();
        }

    }

    public void setStoppingOrder(int stoppingOrder) {
        this.stoppingOrder = stoppingOrder;
    }

    public boolean isWin(PhomPlayer other) {
        if (this.point == other.point) {
            return (this.stoppingOrder < other.stoppingOrder);
        } else {
            return (this.point < other.point);
        }
    }

    //	/**
//	 * An xong chua oanh. true: u false: bt
//	 */
//	public boolean playAfterEat() throws PhomException {
//		switch (this.eatingCards.size()) {
//		case 1:
//		case 2: {
//			if (checkValid()) {
//				return false;
//			} else {
//				throw new PhomException("An tam bay roi em!");
//			}
//		}
//		case 3: {
//			if (checkValid()) {
//				return true;
//			} else {
//				throw new PhomException("An tam bay roi em!");
//			}
//		}
//		default:
//			throw new PhomException("Khong the nhu the dc!");
//		}
//	}
//	public boolean checkValid() throws PhomException {
//		ArrayList<Poker> input = this.allCurrentCards;
//		Vector<Poker> eat = this.eatingCards;
//
//		/*
//		 * for(int i = 0; i < phoms.size(); i++){ Phom ph = phoms.get(i); if
//		 * (dq(getSubPhom(phoms, i),removePokerFromList(eat),
//		 * removePhomFromCards(input, ph.cards))) return true; }
//		 */
//		return dq(eat, input);
//	}
    public Vector<Phom> listPhom = new Vector<Phom>();

//	private boolean dq(Vector<Poker> eat, ArrayList<Poker> input)
//			throws PhomException {
//		if (eat.isEmpty()) {
//			return true;
//		} else if (eat.size() == 1) {
//			Vector<Phom> temp = getPhom(eat.get(0), input);
//			if (temp.size() > 0) {
//				// System.out.println("====================");
//				for (Phom p : temp) {
//					// System.out.println(p.toString());
//					listPhom.add(p);
//				}
//				return true;
//			} else {
//				return false;
//			}
//		} else {
//			Vector<Phom> phoms = getPhom(eat.get(0), input);
//			for (int i = 0; i < phoms.size(); i++) {
//				Phom ph = phoms.get(i);
//				listPhom.add(ph);
//				if (dq(removePokerFromList(eat),
//						removePhomFromCards(input, ph.cards))) {
//					return true;
//				} else {
//					listPhom.remove(ph);
//				}
//			}
//		}
//		return false;
//	}
//	private Vector<Poker> removePokerFromList(Vector<Poker> input) {
//		Vector<Poker> res = new Vector<Poker>();
//		for (int i = 1; i < input.size(); i++) {
//			res.add(input.get(i));
//		}
//		return res;
//	}
//	private ArrayList<Poker> removePhomFromCards(ArrayList<Poker> input,
//			Vector<Poker> cards) {
//		ArrayList<Poker> res = new ArrayList<Poker>();
//		for (Poker p : input) {
//			if (!cards.contains(p)) {
//				res.add(p);
//			}
//		}
//		return res;
//	}

    /*
     * private Vector<Phom> getSubPhom(Vector<Phom> phoms, int index){
     * Vector<Phom> res = new Vector<Phom>(); for(int i = 0; i < phoms.size();
     * i++){ if(i != index) res.add(phoms.get(i)); } return res; }
     */
    private Vector<Phom> getPhom(Poker card, ArrayList<Poker> input) throws PhomException {
        Vector<Phom> res = new Vector<Phom>();
        if (input.contains(card)) {
            res.addAll(getPhomDoc(card, input));
            res.addAll(getPhomNgang(card, input));
        }
        return res;
    }


    private Vector<Poker> getSubListDoc(Vector<Poker> input, int index, int length) throws PhomException {
        Vector<Poker> res = new Vector<Poker>();
        for (int i = index; i < index + length; i++) {
            res.add(input.get(i));
        }
        return res;
    }

    private Vector<Phom> getSubPhoms(Vector<Poker> input, int length, Poker card) throws PhomException {
        Vector<Phom> res = new Vector<Phom>();
        if (length <= input.size()) {
            for (int i = 0; i <= input.size() - length; i++) {
                Vector<Poker> temp = getSubListDoc(input, i, length);
                if (temp.contains(card)) {
                    res.add(new Phom(temp));
                }
            }
        }
        return res;
    }

    // thanhnvt {

    /**
     * Lấy danh sách phỏm.
     * @return
     * @throws PhomException
     */
    private Vector<Phom> getPhomThanhnvt() {
        Vector<Phom> phomsNgangDoc = getPhomNgangThenDoc(new Vector<Poker>(allCurrentCards));
        Vector<Phom> phomsDocNgang = getPhomDocThenNgang(new Vector<Poker>(allCurrentCards));

        phomsNgangDoc = splitLongPhom(phomsNgangDoc);
        phomsDocNgang = splitLongPhom(phomsDocNgang);

        boolean validPhomsNgangDoc = validPhomList(phomsNgangDoc, eatingCards);
        boolean validPhomsDocNgang = validPhomList(phomsDocNgang, eatingCards);

        Vector<Phom> result = null;
        if (validPhomsNgangDoc && validPhomsDocNgang) {
            // lấy theo cách có nhiều phỏm hơn
            if (phomsNgangDoc.size() > phomsDocNgang.size()) {
                result = phomsNgangDoc;
            } else if (phomsDocNgang.size() < phomsNgangDoc.size()) {
                result = phomsDocNgang;
            } else {
                // nếu cùng số lượng phỏm, lấy theo cách có nhiều lá bài hơn
                int ngangDocSize = 0, docNgangSize = 0;
                for (Phom phom : phomsNgangDoc) {
                    ngangDocSize += phom.cards.size();
                }
                for (Phom phom : phomsDocNgang) {
                    docNgangSize += phom.cards.size();
                }
                if (ngangDocSize >= docNgangSize) {
                    result = phomsNgangDoc;
                } else {
                    result = phomsDocNgang;
                }
            }
        } else if (validPhomsNgangDoc) {
            result = phomsNgangDoc;
        } else if (validPhomsDocNgang) {
            result = phomsDocNgang;
        } else {
            result = null;
        }

        return result;
    }

    /**
     * Lấy hết danh sách phỏm ngang, sau đó lấy phỏm dọc.
     * @param pokers
     * @return
     */
    private Vector<Phom> getPhomNgangThenDoc(Vector<Poker> pokers) {

        Vector<Phom> phoms = findPhomByValue(pokers);

        Vector<Poker> phomPokers = new Vector<Poker>();

        for (Phom phom : phoms) {
            phomPokers.addAll(phom.cards);
        }

        Vector<Poker> remainCardList = subtractPoker(pokers, phomPokers);
        Vector<Phom> phomDoc = findPhomByType(remainCardList);
        if (phomDoc != null) {

            phoms.addAll(phomDoc);
        }

        return phoms;
    }

    /**
     * Lấy hết danh sách phỏm dọc, sau đó lấy phỏm ngang.
     * @param pokers
     * @return
     */
    private Vector<Phom> getPhomDocThenNgang(Vector<Poker> pokers) {

        Vector<Phom> phoms = findPhomByType(pokers);

        Vector<Poker> phomPokers = new Vector<Poker>();

        for (Phom phom : phoms) {
            phomPokers.addAll(phom.cards);
        }

        Vector<Poker> remainCardList = subtractPoker(pokers, phomPokers);
        Vector<Phom> phomNgang = findPhomByValue(remainCardList);
        if (phomNgang != null) {

            phoms.addAll(phomNgang);
        }

        return phoms;
    }

    /**
     * Lấy danh sách phỏm ngang.
     *
     * @param pCardList
     * @return
     */
    private Vector<Phom> findPhomByValue(Vector<Poker> pCardList) {
        Vector<Phom> result = new Vector<Phom>();
        if (pCardList != null && pCardList.size() > 0) {

            Vector<Poker> tempCardList = new Vector<Poker>(pCardList);
            Collections.sort(tempCardList, compareByIncreaseValue);
            Vector<Poker> aPhomList = new Vector<Poker>();
            for (Iterator<Poker> iter = tempCardList.iterator(); iter
                    .hasNext(); ) {

                Poker card = iter.next();
                Poker last = null;
                try {
                    last = aPhomList.lastElement();
                } catch (NoSuchElementException nsee) {
                }
                if (last != null) {

                    if (last.getNum() == card.getNum()) {

                        aPhomList.add(card);
                    } else {

                        if (aPhomList.size() > 2) {

                            result.add(new Phom(aPhomList));
                        }

                        aPhomList = new Vector<Poker>();
                        aPhomList.add(card);
                    }
                } else {

                    aPhomList.add(card);
                }
                iter.remove();
            }

            if (aPhomList != null && aPhomList.size() > 2) {
                result.add(new Phom(aPhomList));
            }
        }

        return result;
    }

    /**
     * Lấy danh sách phỏm dọc.
     *
     * @param pCardList
     * @return
     */
    private Vector<Phom> findPhomByType(Vector<Poker> pCardList) {

        Vector<Phom> result = new Vector<Phom>();
        if (pCardList != null && pCardList.size() > 0) {

            Vector<Poker> tempCardList = new Vector<Poker>(pCardList);
            Collections.sort(tempCardList, compareByType);
            Vector<Poker> aPhomList = new Vector<Poker>();
            for (Iterator<Poker> iter = tempCardList.iterator(); iter
                    .hasNext(); ) {

                Poker card = iter.next();
                Poker last = null;
                try {

                    last = aPhomList.lastElement();
                } catch (NoSuchElementException nsee) {
                }
                if (last != null) {

                    if (last.getNum() + 1 == card.getNum() && last.type == card.type) {

                        aPhomList.add(card);
                    } else {

                        if (aPhomList.size() > 2) {

                            result.add(new Phom(aPhomList));
                        }

                        aPhomList = new Vector<Poker>();
                        aPhomList.add(card);
                    }
                } else {

                    aPhomList.add(card);
                }
                iter.remove();
            }
            if (aPhomList != null && aPhomList.size() > 2) {

                result.add(new Phom(aPhomList));
            }
        }

        return result;
    }

    private boolean validPhomList(Vector<Phom> pPhomList, Vector<Poker> pTakenCardList) {

        if (pTakenCardList == null) {
            return true;
        }

        // nếu một phỏm có chứa nhiều hơn 1 cây ăn thì không hợp lệ {
        for (Phom phom : pPhomList) {

            int numOfTakenCardInPhom = 0;
            for (Poker card : phom.cards) {

                if (pTakenCardList.contains(card)) {

                    numOfTakenCardInPhom++;
                }
            }
            if (numOfTakenCardInPhom > 1) {

                return false;
            }
        }
        // } nếu một phỏm có chứa nhiều hơn 1 cây ăn thì không hợp lệ

        // nếu số cây ăn nằm trong phỏm ít hơn toàn bộ số cây ăn thì không hợp lệ {
        int num = 0;
        for (Poker taken : pTakenCardList) {

            for (Phom phom : pPhomList) {

                if (phom.cards.contains(taken)) {

                    num++;
                    break;
                }
            }
        }
        if (num < pTakenCardList.size()) {

            return false;
        }
        // } nếu số cây ăn nằm trong phỏm ít hơn toàn bộ số cây ăn thì không hợp lệ

        return true;
    }

    /**
     * Tách những phỏm có 6 cây trở lên thành 2 phỏm (nếu được).
     * @param pPhomList
     * @return
     */
    private Vector<Phom> splitLongPhom(Vector<Phom> pPhomList) {
        Vector<Phom> result = new Vector<Phom>();;
        if (pPhomList != null && pPhomList.size() > 0) {
            for (Phom phom : pPhomList) {

                int size = phom.cards.size();
                if (size >= 6) {
                    // tìm phỏm 1 hợp lệ
                    // vd: trường hợp dây phỏm là 1, 2, 3, 4, 5, 6, 7. Người chơi ăn cây 4 và cây 7.
                    // nếu lấy phỏm đầu là 1, 2 ,3 thì không hợp lệ.
                    for (int i = 3; i < size; i++) {
                        Vector<Poker> firstPhom = new Vector<Poker>(phom.cards.subList(0, i));
                        boolean validPhom = false;
                        for (Poker p : eatingCards) {
                            if (firstPhom.contains(p)) {
                                validPhom = true;
                                break;
                            }
                        }

                        if (validPhom) {
                            result.add(new Phom(new Vector<Poker>(firstPhom)));

                            Vector<Poker> remainPhom = subtractPoker(phom.cards, firstPhom);
                            validPhom = false;
                            for (Poker p : eatingCards) {
                                if (remainPhom.contains(p)) {
                                    validPhom = true;
                                    break;
                                }
                            }
                            if (validPhom) {
                                result.add(new Phom(new Vector<Poker>(remainPhom)));
                            }

                            break;
                        }
                    }
                    result.add(new Phom(new Vector<Poker>(phom
                            .cards.subList(0, 3))));
                    result.add(new Phom(new Vector<Poker>(phom
                            .cards.subList(3, size))));
                } else {
                    result.add(phom);
                }
            }
        }
        return result;
    }


    private Comparator<Poker> compareByIncreaseValue = new Comparator<Poker>() {

        @Override
        public int compare(Poker lhs, Poker rhs) {

            if (lhs.getNum() < rhs.getNum()) {
                return -1;
            } else if (lhs.getNum() > rhs.getNum()) {
                return 1;
            } else {
                if (lhs.type.toInt() < rhs.type.toInt()) {
                    return -1;
                } else if (lhs.type.toInt() > rhs.type.toInt()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    };

    private Comparator<Poker> compareByType = new Comparator<Poker>() {

        @Override
        public int compare(Poker o1, Poker o2) {

            if (o1.type.toInt() < o2.type.toInt()) {

                return -1;
            } else if (o1.type.toInt() > o2.type.toInt()) {

                return 1;
            } else {

                if (o1.getNum() < o2.getNum()) {

                    return -1;
                } else {

                    return 1;
                }
            }
        }
    };

    // } thanhnvt

    private Vector<Poker> subtractPoker(Vector<Poker> a, Vector<Poker> b) {
        Vector<Poker> dest = new Vector<Poker>();
        dest.addAll(a);
        dest.removeAll(b);

        return dest;
    }

    private Vector<Phom> getPhomDocThanhnvt(Poker card, ArrayList<Poker> input) throws PhomException {
        Vector<Phom> res = new Vector<Phom>();
        Vector<Poker> temp = new Vector<Poker>();

        int in = card.num;
        while (true) {
            int tmpValue = in % 13;
            if (tmpValue == 1) {
                mLog.debug("---THANGTD PHOM ERROR--- Tự động kiểm tra phỏm dọc player " + this.username + " [" + card.toString() + "]");
                break;
            }

            Poker p = new Poker(in - 1, card.type);
            if (input.contains(p)) {
                temp.add(p);
                in--;
            } else {
                break;
            }
        }

        in = card.num;
        temp.add(card);
        while (true) {
            int tmpValue = in % 13;
            if (tmpValue == 0) {
                mLog.debug("---THANGTD PHOM ERROR--- Tự động kiểm tra phỏm dọc player " + this.username + " [" + card.toString() + "]");
                break;
            }

            Poker p = new Poker(in + 1, card.type);
            if (input.contains(p)) {
                temp.add(p);
                in++;
            } else {
                break;
            }
        }

        if (temp.size() >= 3) {
            for (int i = 3; i <= temp.size(); i++) {
                res.addAll(getSubPhoms(temp, i, card));
            }
        }

        return res;
    }

    private Vector<Phom> getPhomDoc(Poker card, ArrayList<Poker> input) throws PhomException {
        Vector<Phom> res = new Vector<Phom>();
        Vector<Poker> temp = new Vector<Poker>();

        int in = card.num;
        while (true) {
            int tmpValue = in % 13;
            if (tmpValue == 1) {
                mLog.debug("---THANGTD PHOM ERROR--- Tự động kiểm tra phỏm dọc player " + this.username + " [" + card.toString() + "]");
                break;
            }

            Poker p = new Poker(in - 1, card.type);
            if (hasPoker(p)) {
                temp.add(p);
                in--;
            } else {
                break;
            }
        }

        in = card.num;
        temp.add(card);
        while (true) {
            int tmpValue = in % 13;
            if (tmpValue == 0) {
                mLog.debug("---THANGTD PHOM ERROR--- Tự động kiểm tra phỏm dọc player " + this.username + " [" + card.toString() + "]");
                break;
            }

            Poker p = new Poker(in + 1, card.type);
            if (hasPoker(p)) {
                temp.add(p);
                in++;
            } else {
                break;
            }
        }

        if (temp.size() >= 3) {
            for (int i = 3; i <= temp.size(); i++) {
                res.addAll(getSubPhoms(temp, i, card));
            }
        }
        return res;
    }

    private Vector<Poker> getSubListNgang(Vector<Poker> input, int index) {
        Vector<Poker> res = new Vector<Poker>();
        for (int i = 0; i < 4; i++) {
            if (i != index) {
                res.add(input.get(i));
            }
        }
        return res;
    }

    private Vector<Phom> getPhomNgang(Poker card, ArrayList<Poker> input)
            throws PhomException {
        Vector<Phom> res = new Vector<Phom>();
        Vector<Poker> temp = new Vector<Poker>();
        for (Poker p : input) {
            if (p.num == card.num) {
                temp.add(p);
            }
        }
        if (temp.size() == 3) {
            res.add(new Phom(temp));
        } else if (temp.size() == 4) {
            for (int i = 0; i < 4; i++) {
                if (temp.get(i).type != card.type) {
                    res.add(new Phom(getSubListNgang(temp, i)));
                }
            }
            res.add(new Phom(temp));
        }
        return res;
    }

    // Ha phom - khong gui tu dong - oanh luon

    /**
     * @return the phomSize
     */
    public int getPhomSize() {
        return phomSize;
    }

    /**
     * @param phomSize the phomSize to set
     */
    public void setPhomSize(int phomSize) {
        this.phomSize = phomSize;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Poker> make(ArrayList<Poker> input) {
        ArrayList<Poker> cards = sortCard(input);

        /*System.out.println("===  Bo Bai ===");
         for (Poker p : cards) {
         System.out.print(p.toString() + " - ");
         }*/
        ArrayList<Poker> temp = new ArrayList<Poker>();
        ArrayList<ArrayList<Poker>> res = new ArrayList<ArrayList<Poker>>();
        ArrayList<Poker> cayLe = new ArrayList<Poker>();

        while (true) {
            if (cards.isEmpty()) {
                break;
            }

            temp.add(cards.get(0));
            for (int i = 1; i < cards.size(); i++) {
                if (timCa(temp, cards.get(i))) {
                    temp.add(cards.get(i));
                }
            }
            /*
             * if (temp.size() == 1) cayLe.addAll(temp); else
             */
            res.add((ArrayList<Poker>) temp.clone());
            for (Poker p : temp) {
                removePoker(cards, p);
            }
            temp.clear();
        }

        int index = 0;
        while (index < res.size() - 1) {
            ArrayList<Poker> arr = res.get(index);
            boolean isChange = false;
            for (int i = index + 1; i < res.size(); i++) {
                ArrayList<Poker> arr1 = res.get(i);
                if (isRelationship(arr, arr1)) {
                    arr.addAll(arr1);
                    res.remove(i);
                    isChange = true;
                    break;
                }
            }
            if (!isChange) {
                index++;
            }
        }
        //System.out.println("\n===  Ca ===");
        ArrayList<Poker> ca = new ArrayList<Poker>();
        for (ArrayList<Poker> arr : res) {
            if (arr.size() == 1) {
                cayLe.addAll(arr);
            } else {
                ArrayList<Poker> leCay = mappingList(timPhomDoc(arr),
                        timPhomNgang(arr));
                if (leCay != null) {
                    cayLe.addAll(leCay);
                } else {
                    ca.addAll(arr);
                }
                /*for (Poker p : arr) {
                 System.out.print(p.toString() + " - ");
                 }
                 System.out.println("\n======");*/
            }
        }
        //System.out.println("===  Cay Le ===");
        if (cayLe.isEmpty()) {
            cayLe.addAll(ca);
        }
        /*for (Poker p : cayLe) {
         System.out.print(p.toString() + " - ");
         }*/
        // System.out.println("======");
        return cayLe;
    }

    private boolean isRelationship(ArrayList<Poker> l1, ArrayList<Poker> l2) {
        for (Poker p : l1) {
            if (timCa(l2, p)) {
                return true;
            }
        }
        return false;
    }

    private void removePoker(ArrayList<Poker> cards, Poker p) {
        for (int i = 0; i < cards.size(); i++) {
            Poker p1 = cards.get(i);
            if (p.isEqual(p1)) {
                cards.remove(i);
                return;
            }
        }
    }

    private void removeVectorPoker(ArrayList<Poker> cards, Vector<Poker> vdata) {
        int size = vdata.size();
        Poker p = null;
        for (int i = 0; i < size; i++) {
            p = vdata.get(i);
            removePoker(cards, p);
        }
    }

    private boolean timCa(ArrayList<Poker> cards, Poker p) {
        for (Poker p1 : cards) {
            if (p.isCa(p1)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Poker> timPhomNgang(ArrayList<Poker> shortedCards) {//tim cay le cua phom ngang 444 va 4 & 5
        ArrayList<Poker> res = new ArrayList<Poker>();
        Hashtable<Integer, ArrayList<Poker>> hash = new Hashtable<Integer, ArrayList<Poker>>();
        for (Poker p : shortedCards) {
            ArrayList<Poker> temp = hash.get(p.num);
            if (temp == null) {
                temp = new ArrayList<Poker>();
            }
            temp.add(p);
            hash.put(p.num, temp);
        }
        boolean hasPhom = false;
        Enumeration<Integer> keys = hash.keys();
        while (keys.hasMoreElements()) {
            int k = keys.nextElement();
            if (hash.get(k).size() == 1) {
                res.addAll(hash.get(k));
            }
            if (hash.get(k).size() >= 3) {
                hasPhom = true;
            }
        }
        if (hasPhom) {
            return res;
        }
        return null;
    }

    private ArrayList<Poker> timPhomDoc(ArrayList<Poker> shortedCards) { //tim cay le cua phom doc 3, 4, 5 & 4
        ArrayList<Poker> res = new ArrayList<Poker>();
        Hashtable<PokerType, ArrayList<Poker>> hash = new Hashtable<PokerType, ArrayList<Poker>>();

        for (Poker p : shortedCards) {
            ArrayList<Poker> temp = hash.get(p.type);
            if (temp == null) {
                temp = new ArrayList<Poker>();
            }
            temp.add(p);
            hash.put(p.type, temp);
        }

        boolean hasPhom = false;
        Enumeration<PokerType> keys = hash.keys();

        while (keys.hasMoreElements()) {
            PokerType k = keys.nextElement();
            ArrayList<Poker> temp = hash.get(k);
            if (temp.size() < 3) {
                res.addAll(temp);
            }
            if (temp.size() >= 3) {
                for (int i = 1; i < temp.size(); i++) {
                    if (temp.get(i - 1).num + 1 != temp.get(i).num) {
                        hasPhom = false;
                        break;
                    }
                    hasPhom = true;
                }
            }
        }
        if (hasPhom) {
            return res;
        }

        return null;
    }

    private ArrayList<Poker> mappingList(ArrayList<Poker> l1, ArrayList<Poker> l2) {
        ArrayList<Poker> res = new ArrayList<Poker>();
        if (l1 == null) {
            return l2;
        }
        if (l1.isEmpty() && l2 != null) {
            return l2;
        }
        if (l2 == null || l2.size() == 0) {
            return l1;
        }
        for (Poker p : l1) {
            if (isInListPoker(p, l2)) {
                res.add(p);
            }
        }
        return res;
    }

    private boolean isInListPoker(Poker p, ArrayList<Poker> input) {
        for (Poker p1 : input) {
            if (p.isEqual(p1)) {
                return true;
            }
        }
        return false;
    }

    // sap xep mang porker tang dan
    private ArrayList<Poker> sortCard(ArrayList<Poker> cardArray) {
        // convert At to 14
        int cardSize = cardArray.size();
        ArrayList<Poker> sortedCard = new ArrayList<Poker>();
        for (int i = 0; i < cardSize; i++) {
            Poker pk1 = cardArray.get(i);
            sortedCard.add(pk1);
        }
        // sort card
        for (int i = 0; i < cardSize; i++) {
            for (int j = i + 1; j < cardSize; j++) {
                Poker pokerI = sortedCard.get(i);
                Poker pokerJ = sortedCard.get(j);
                if (pokerI.isGreater(pokerJ)) {
                    sortedCard.set(i, pokerJ);
                    sortedCard.set(j, pokerI);
                }
            }
        }

        return sortedCard;
    }

    public static void main(String[] args) {
        ArrayList<Poker> res = new ArrayList<Poker>();
        PhomPlayer player = new PhomPlayer();
        res.add(new Poker(9, PokerType.Co));
        res.add(new Poker(8, PokerType.Ro));
        res.add(new Poker(7, PokerType.Tep));
        res.add(new Poker(5, PokerType.Ro));
        res.add(new Poker(10, PokerType.Pic));
        res.add(new Poker(2, PokerType.Co));
        res.add(new Poker(1, PokerType.Tep));
        res.add(new Poker(4, PokerType.Pic));
        res.add(new Poker(13, PokerType.Tep));
        player.allCurrentCards = res;
        System.out.println(player.isUkhan());
        //player.make(res);
    }

    private boolean notInList(int i, ArrayList<Integer> input) {
        for (int j : input) {
            if (i == j) {
                return false;
            }
        }
        return true;
    }

    /*
     * Kiem tra phom ha 
     */
    public boolean isAllowHa(ArrayList<ArrayList<Integer>> cards) {
        ArrayList<Integer> check = new ArrayList<Integer>();
        if (this.haPhom) {
            if (cards != null && cards.size() > 0) {
                for (ArrayList<Integer> a : cards) {
                    if (!isPhom(a)) {
                        return false;
                    }

                    for (int i : a) {
                        if (!notInList(i, check)) {
                            return false;
                        }
                        check.add(i);
                        Poker p = Utils.numToPoker(i);
                        if (!hasPoker(p)) {
                            return false;
                        }
                        if (phomHasPoker(p)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } else {
            return true;
        }
    }

    /*
     * Kiem tra mang so co phai la phom khong
     */
    private boolean isPhom(ArrayList<Integer> cards) {
        if (cards == null || cards.isEmpty()) {
            return false;
        }

        ArrayList<Poker> pokers = new ArrayList<Poker>();
        for (int i : cards) {
            Poker p = Utils.numToPoker(i);
            pokers.add(p);
        }

        int num = pokers.get(0).num;
        for (Poker p : pokers) {
            if (p.num != num) {
                ArrayList<Poker> po = sortCard(pokers);
                for (int i = 0; i < po.size() - 1; i++) {
                    Poker pI = po.get(i);
                    Poker pI1 = po.get(i + 1);
                    if (pI.num != pI1.num - 1 || pI.type != pI1.type) {
                        return false;
                    }
                }
                return true;
            }
        }
        return true;

    }

    /*
     * Kiem tra trong danh sach phom co chua quan bai
     */
    private boolean phomHasPoker(Poker pi) {
        for (Phom ph : phoms) {
            for (Poker p : ph.cards) {
                if (p.num == pi.num && p.type == pi.type) {
                    return true;
                }
            }
        }
        return false;
    }

}
