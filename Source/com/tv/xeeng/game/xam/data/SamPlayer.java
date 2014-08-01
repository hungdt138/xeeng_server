/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.xam.data;

import java.util.ArrayList;
import org.slf4j.Logger;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.TurnRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

/**
 *
 * @author tuanda
 */
public class SamPlayer extends SimplePlayer {

    public static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(SamPlayer.class);

    public Poker caiCard;
    private ArrayList<Poker> playingCards = new ArrayList<>();
    public ArrayList<Long> cashLost = new ArrayList<>();
    public ArrayList<Long> cashWin = new ArrayList<>();
    private SamTable table;
    public boolean huyBaoSam = false;
    public boolean thoi = false;
    public ArrayList<String> comment = new ArrayList<>();
    public boolean de2CuoiDung = false;
    public boolean isNotPlayGreatestCard = false;
    public boolean isSuper = false;
    public boolean isObserve;
    public boolean isCai;

    public void huyBaoSam() throws BusinessException {
        if (huyBaoSam) {
            throw new BusinessException("Ban da huy bao sam roi!");
        }
        huyBaoSam = true;
    }

    public int playingCardSize() {
        return playingCards.size();
    }

    public String cardsToString() {
        StringBuilder sb = new StringBuilder();
        for (Poker p : playingCards) {
            sb.append(p.toInt()).append("#");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public void setTable(SamTable t) {
        table = t;
    }

    public ArrayList<Poker> getPlayingCards() {
        return playingCards;
    }

    public void reset() {
        playingCards.clear();
        cashLost.clear();
        cashWin.clear();
        comment.clear();
        thoi = false;
        huyBaoSam = false;
        isNotPlayGreatestCard = false;
        de2CuoiDung = false;
        isSuper = false;
        isObserve = false;
        isCai = false;
        caiCard = null;
    }

    public void checkNotPlayGreatestCard(ArrayList<Poker> card, boolean nextHas1Card) {
        if (!nextHas1Card) {
            return;
        }
        sort();
        if (card.size() == 1) {
            if (card.get(0).getNum() != playingCards.get(playingCards.size() - 1).getNum()) {
                //isNotPlayGreatestCard = true;
                table.uidNotPlayGreatestCard = id;
            }
        }
    }

    /**
     * if res > 0 --> lost else --> won
     *
     * @return
     */
    public long cashLost(long moneyBet) {
        if (!table.hasBaoSam() && table.uidNotPlayGreatestCard == 0 && table.perfectType == 0) {
            cashLost.add(cashLostEndMatch(moneyBet));
        }
        
        long res = 0;
        for (long l : cashLost) {
            res += l;
        }
        for (long l : cashWin) {
            res -= l;
        }

        mLog.debug("---THANGTD DEBUG SAM---" + username + " cash lost: " + res);

        return res;
    }

    public String comment() {
        StringBuilder res = new StringBuilder();
        int len = comment.size();
        for (int i = len - 1; i >= 0; i--) {
            String s = comment.get(i);
            res.append(s).append(",");
        }
        if (res.length() > 0) {
            res.deleteCharAt(res.length() - 1);
        }

        return res.toString();
    }

    public long cashLostEndMatch(long moneyBet) {
        sort();
        
        long res = 0;
        
        if (playingCards.size() == 10) {
            comment.add("Bài treo!");
            res += 20 * moneyBet;
        } else if (isNotPlayGreatestCard) {
            comment.add("Phạt: không đánh cây to nhất!");
            res += 20 * moneyBet;
        } else {
            int soCay = 0;
            for (Poker p : playingCards) {
                soCay++;
                res += moneyBet;
            }
            comment.add("Còn " + soCay + " cây");
        }

        int heo = 0;
        for (Poker p : playingCards) {
            if (p.getNum() == 15 || p.getNum() == 2) {
                heo++;
            }
        }
        if (heo > 0) {
            res += 10 * heo * moneyBet;
            comment.add("Thối " + heo + " cây heo");
        }
        
        int tuquy = 0;
        for (int i = 0; i < playingCards.size() - 3; i++) {
            if (playingCards.get(i).getNum() == playingCards.get(i + 1).getNum() && playingCards.get(i + 1).getNum() == playingCards.get(i + 2).getNum()
                    && playingCards.get(i + 2).getNum() == playingCards.get(i + 3).getNum()) {
                tuquy++;
            }
        }
        if (tuquy > 0) {
            res += 20 * tuquy * moneyBet;
            comment.add("Thối " + tuquy + " tứ quý");
        }
        
        return res;
    }

    public SamPlayer(long uid) {
        this.id = uid;
        this.isGiveUp = false;
        //table = t;
    }

    @Override
    public void setReady(boolean isReady) {
        this.isReady = isReady;
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

    public void setCards(ArrayList<Poker> cards) {
        playingCards = cards;
        sort();
    }

    public void setCaiCard(Poker card) {
        caiCard = card;
    }

    public boolean cardIsAll2() {
        if (playingCards.isEmpty()) {
            return false;
        }
        for (Poker p : playingCards) {
            if (p.getNum() != 2 && p.getNum() != 15) {
                return false;
            }
        }
        return true;
    }

    public void play(ArrayList<Poker> playCard, ArrayList<Poker> currCard, long lastUID, TurnRequest rq, boolean has1Card)
            throws BusinessException, Exception {
        if (!hasCards(playCard)) {
            throw new BusinessException("Play card not found!");
        }

        try {
            if (isPlayValid(playCard, currCard, lastUID, has1Card)) {
                removePlayCards(playCard);
                if (playingCards.size() == 1) {
                    table.has1Card = true;
                }
                sort();
            } else {
                throw new BusinessException("Play not valid!");
            }
        } catch (Throwable e) { // luot truoc khong danh cay cao nhat
            comment.add("Phạt do không đánh cây cao nhất!");
            throw new Exception();
        }
    }

    private boolean hasCards(ArrayList<Poker> playCard) {
        for (Poker p : playCard) {
            if (!isInCards(p)) {
                return false;
            }
        }
        return true;
    }

    private void removePlayCards(ArrayList<Poker> playCard) {
        for (Poker p : playCard) {
            removeCard(p);
        }
    }

    private void removeCard(Poker playCard) {
        for (int i = 0; i < playingCards.size(); i++) {
            Poker p = playingCards.get(i);
            if (p.equals(playCard)) {
                playingCards.remove(i);
                return;
            }
        }
    }

    public boolean has1Card() {
        return playingCards.size() == 1;
    }

    private boolean isPlayValid(ArrayList<Poker> playCard, ArrayList<Poker> currCard, long lastUID, boolean has1Card) throws BusinessException {
        if (isNotPlayGreatestCard) {
            throw new BusinessException("Bạn bị treo do lượt trước không đánh cây to nhất");
        }
        GroupCard gP = new GroupCard(playCard);

        if (currCard == null || currCard.isEmpty()) {
            return true;
        }

        GroupCard gC;
        try {
            gC = new GroupCard(currCard);
        } catch (NullPointerException e1) {
            return true;
        } catch (Throwable e) {
            return false;
        }

        SamPlayer lastPlayer = table.findPlayer(lastUID);
        checkNotPlayGreatestCard(playCard, has1Card);

        if (gP.isValidPlay(gC)) {
            int tk = gP.isTK();
            int tkC = gC.isTK();
            if (gC.isHeo() && (tk > 0)) {
                table.fightOccur = true;

                int time;
                if (tk == 14 || tk == 1) { // tk At chat 2
                    //comment.add("Tứ quý Át chặt heo");
                    //lastPlayer.comment.add("Bị tứ quý Át chặt heo");
                    time = 40;
                } else {
                    //comment.add("Tứ quý " + tk + " chặt heo");
                    //lastPlayer.comment.add("Bị tứ quý " + tk + " chặt heo");
                    time = 20;
                }
                long fightMoney = time * table.firstCashBet;
                //lastPlayer.cashLost.add(fightMoney);
                long data[] = new long[4];
                data[0] = lastUID;
                data[1] = id;
                data[2] = fightMoney;
                data[3] = tk;//tk chat heo = tk
                table.fightInfo.add(data);
            } else if (tkC > 0 && tk > tkC && !table.fightInfo.isEmpty()) {//tk chat chong tk
                long data[] = table.fightInfo.get(table.fightInfo.size() - 1);
                data[0] = data[1];
                data[1] = id;
                data[2] = data[2] * 2;
                data[3] = 0; // tk chat Heo = 0;
                table.fightInfo.clear();
                table.fightInfo.add(data);
            } else {
                table.fightInfo.clear();
                table.fightOccur = false;
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean isInCards(Poker c) {
        for (Poker p : playingCards) {
            if (p.equals(c)) {
                return true;
            }
        }
        return false;
    }

    public int samDacBiet() {
        if (isSanhRong()) {
            comment.add("Sâm đặc biệt: sảnh rồng");
            return 5;
        }

        if (isTK2()) {
            comment.add("Sâm đặc biệt: tứ quý heo");
            return 4;
        }

        if (isDongHoa()) {
            comment.add("Sâm đặc biệt: đồng hoa");
            return 3;
        }

        if (is3Sam()) {
            comment.add("Sâm đặc biệt: ba sám");
            return 2;
        }

        if (is5Doi()) {
            comment.add("Sâm đặc biệt: năm đôi");
            return 1;
        }

        sort();

        return 0;
    }

    private boolean isSanhRong() {
        boolean res = false;
        for (int i = 0; i < 9; i++) {
            if (playingCards.get(i + 1).getNum() != playingCards.get(i).nextCard()) {
                res = false;
            }
        }
        if (!res) {
            sort1();
            for (int i = 0; i < 9; i++) {
                if (playingCards.get(i + 1).getNum() != playingCards.get(i).nextCard()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isTK2() {
        sort();
        int number = 0;
        for (int i = 6; i < 10; i++) {
            if (playingCards.get(i).getNum() == 15) {
                number++;
            }
        }
        return number == 4;
    }

    private boolean isDongHoa() {
        boolean isDen = playingCards.get(0).isDen();
        for (int i = 1; i < 10; i++) {
            if (playingCards.get(i).isDen() != isDen) {
                return false;
            }
        }
        return true;
    }

    private boolean is3Sam() {
        int numSam = 0;
        int i = 0;
        while (i < 8) {
            if (playingCards.get(i).isDoi(playingCards.get(i + 1)) && playingCards.get(i).isDoi(playingCards.get(i + 2))) {
                numSam++;
                i += 3;
            } else {
                i++;
            }
        }
        return numSam == 3;
    }

    private boolean is5Doi() {
        int i = 0;
        while (i < 9) {
            if (playingCards.get(i).isDoi(playingCards.get(i + 1))) {
                i += 2;
            } else {
                return false;
            }
        }
        return true;
    }

    private void sort1() {
        int len = playingCards.size();
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                if (playingCards.get(i).isGreater1(playingCards.get(j))) {
                    Poker temp = playingCards.get(i);
                    playingCards.set(i, playingCards.get(j));
                    playingCards.set(j, temp);
                }
            }
        }
    }

    private void sort() {
        int len = playingCards.size();
        for (int i = 0; i < len - 1; i++) {
            for (int j = i + 1; j < len; j++) {
                if (playingCards.get(i).isGreater(playingCards.get(j))) {
                    Poker temp = playingCards.get(i);
                    playingCards.set(i, playingCards.get(j));
                    playingCards.set(j, temp);
                }
            }
        }
    }

    public void autoPlay(SamTable table) {
        ISession session;
        if (this.currentSession != null) {
            session = this.currentSession;
        } else {
            session = this.currentOwner;
        }

        if (session.getMessageFactory() == null) {
            session = table.getNotNullSession();
        }

        IResponsePackage responsePkg = session.getDirectMessages();
        MessageFactory msgFactory = session.getMessageFactory();

        TurnRequest reqMatchTurn = (TurnRequest) msgFactory.getRequestMessage(MessagesID.MATCH_TURN);
        reqMatchTurn.isGiveup = true;
        reqMatchTurn.uid = this.id;
        reqMatchTurn.mMatchId = table.matchID;
        if (table.isNewRound) {
            reqMatchTurn.isGiveup = false;
            reqMatchTurn.isTimeoutTL = true;
            reqMatchTurn.tienlenCards = playingCards.get(0).toInt() + "";
        }

        IBusiness business;
        try {
            business = msgFactory.getBusiness(MessagesID.MATCH_TURN);
            business.handleMessage(session, reqMatchTurn, responsePkg);
        } catch (ServerException se) {
        }
    }
}
