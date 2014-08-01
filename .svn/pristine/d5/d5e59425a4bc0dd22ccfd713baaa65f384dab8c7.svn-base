package com.tv.xeeng.game.tienlen.data;

import java.util.ArrayList;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.TurnRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


public class TienLenPlayer extends SimplePlayer {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(TienLenPlayer.class);
    public ArrayList<Poker> playingCards = new ArrayList<>();
    public int point;
    // public int timeReq;
    // public boolean isReady = false;
    public boolean isOwner;
    public boolean isObserve;
    public boolean isAcive = true; // For each round while playing
    // public ISession currentSession;
    // Thomc
    public boolean isOutGame = false; // set true nếu thoát game
    // public ArrayList<Couple<Long, Long>> cashLost = new
    // ArrayList<Couple<Long, Long>>();
    // public ArrayList<Couple<Long, Long>> cashWin = new ArrayList<Couple<Long,
    // Long>>();
    public long money = 0;

    @Override
    public long moneyLost(long money_) {
        if (this.cash <= 0) {
            return 0;
        } else if (this.cash < money_) {
            return this.cash;
        } else {
            return money_;
        }
    }

    public void setCurrentOwner(ISession currentOwner) {
        this.currentOwner = currentOwner;
    }
    /**
     * For Time Out
     */
    public boolean isGetData = false;

    public void setState(boolean is) {
        isGetData = is;
    }

    public void setCurrentMatchID(long currentMatchID) {
        this.currentMatchID = currentMatchID;
    }

    public TienLenPlayer() {
        this.isGiveUp = false;
        // this.timeReq = 0;
    }

    public void reset(long money_) {
        point = 0;
        isStop = false;
        playingCards = new ArrayList<>();
        moneyForBet = money_;
        // timeReq = 0;
        isGiveUp = false;
        isGetData = false;
        money = 0;
        setReady(false);
        isObserve = false;
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

    public TienLenPlayer(long id) {
        this.id = id;
        this.isGiveUp = false;
        // this.timeReq = 0;
    }

    public TienLenPlayer(ArrayList<Poker> inputPoker, long id, long minBet) {
        this.playingCards = inputPoker;
        this.id = id;
        this.moneyForBet = minBet;
        this.isGiveUp = false;
        // this.timeReq = 0;

    }

    public void setPokers(Poker[] inputPoker) {
        int len = inputPoker.length;
        for (int i =0; i< len; i++) {
            Poker p = inputPoker[i];
            this.playingCards.add(p);
            //System.out.print("; " + p.getNum() + " " + p.getType());
        }
    }

//	public int minPoker() {
//		int res = 100;
//		for (Poker p : this.playingCards) {
//			if (res > p.toInt()) {
//				res = p.toInt();
//			}
//		}
//		return res;
//	}
    public void removeCards(Poker[] cards) {
        int len = cards.length;
        for (int i =0; i< len; i++) {
            Poker p = cards[i];
            this.playingCards.remove(p);
        }
    }
    // Thomc
    public byte[] myHand = new byte[13]; // các lá bài trên tay người chơi
    public int numHand = 13;// Số bài trên tay hiện tại;

    // Nhận quân bài lúc chia bài
    public void setMyCards(byte[] cards) {
//		System.out.println("Card của: " + this.username
//				+ Utils.bytesToString(cards));
        numHand = cards.length;
//		System.out.println(" cards.length lúc nhận bài" + cards.length);
//		System.out.println("cards.length: " + cards.length);
        this.myHand = Utils.sortCards(cards);
//		System.out.println("numHand sau khi xếp: " + numHand);
        // this.myHand = cards;
    }

    // Kiểm tra các quân bài gửi lên có đúng bài của người chơi hay không trước
    // khi remove
    public boolean isContainsCards(byte[] revCards) {
        int len = revCards.length;
        for (int i = 0; i < len; i++) {
            boolean isContainsCard = false;
            for (int j = 0; j < myHand.length; j++) {
                if (revCards[i] == myHand[j]) {
                    isContainsCard = true;
                    break;
                }
            }
            if (!isContainsCard) {
                return false;
            }
        }
        return true;
    }

    public String byteToString(byte[] d) {
        StringBuilder s = new StringBuilder();
        int len = d.length;
        for (int i = 0; i < len; i++) {
            s.append(" ").append(String.valueOf(d[i]));
        }
        s.append(" ; len : ").append(String.valueOf(d.length));
        return s.toString();
    }

    // remove (những) quân bài vừa đánh
    public void removeCards(byte[] revCards) {
//		System.out.println("revCards: " + revCards[0]);
//		System.out.println("numHand: " + numHand);
//		System.out.println("revCards.length: " + revCards.length);
        this.numHand = numHand - revCards.length;
        if (numHand < 0) {
            return;
        }

        byte[] newHand = new byte[numHand];

        try {
            if (numHand > 0) {
                int newIndex = 0;
                int len = myHand.length;
                for (int j = 0; j < len; j++) {
                    byte myHandj = myHand[j];
                    boolean needRemove = false;
                    for (int i = 0; i < revCards.length; i++) {
//						System.out.println("myHand[j]: " + myHand[j]);
                        if (myHandj == revCards[i]) {
                            needRemove = true;
                            break;
                        }
                    }
                    if (!needRemove) {
//						System.out.println("newHand.length" + newHand.length);
//						System.out.println("newIndex" + newIndex);
                        newHand[newIndex] = myHandj;
                        newIndex++;
                    }
                }
                myHand = newHand;
            }
        } catch (Throwable t) {
            mLog.error(concatString("numHand : ", String.valueOf(numHand) , " ;; matchID : "
                    , String.valueOf(this.currentMatchID)));
            mLog.error(concatString("revCards : ", byteToString(revCards)));
            mLog.error(concatString("myHand : " , byteToString(myHand)));
        }
    }

    // Trả về quân bài nhỏ nhất của người chơi
    public byte minCard() {
        byte min = myHand[0];
        for (int i = 1; i < numHand; i++) {
            if (Utils.isBigger(min, myHand[i])) {
                min = myHand[i];
            }
        }
        return min;
    }

    public void autoPlay(TienLenTable table) {
        /*
         * System.out.println("ownerSession" + ownerSession);
         * System.out.println("tienlenPlayer.id" + tienlenPlayer.id);
         */
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
        reqMatchTurn.mMatchId = this.currentMatchID;
        if (table.isNewRound) {
            reqMatchTurn.isGiveup = false;
            reqMatchTurn.isTimeoutTL = true;
            reqMatchTurn.tienlenCards = this.myHand[0] + "";
        }
        
        IBusiness business;
        // Check if timeout
        // if (reqMatchTurn.uid != -1) {
        try {
            business = msgFactory.getBusiness(MessagesID.MATCH_TURN);
            business.handleMessage(session, reqMatchTurn, responsePkg);
        } catch (ServerException se) {
        }
        // }
    }
    
    private String concatString(String... input) {
        StringBuilder sb = new StringBuilder();
        int len = input.length;
        for(int i = 0; i< len; i++) {
            String s = input[i];
            sb.append(s);
        }
        return sb.toString();
    }
}
