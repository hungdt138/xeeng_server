package com.tv.xeeng.game.data;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XENotifyEventItemResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DBCache;
import com.tv.xeeng.databaseDriven.EventItemsDB;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.protocol.MessageFactory;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;

public class SimplePlayer {

    private static Logger mLog = LoggerContext.getLoggerFactory().getLogger(SimplePlayer.class);

    public int level;
    public int avatarID;
    public String username;
    public boolean isStop;
    public long id;
    public long moneyForBet;   //bet money. There 2 types of money: default money and bet money
    public boolean isWin;
    public boolean isGiveUp;
    public long cash;
    public long currentMatchID;
    public ISession currentOwner;
    public ISession currentSession;
    public boolean isReady = false;
    public int index;
    public int pos;
    public boolean isOut = false;
    private int experience;
    protected long wonMoney;
    private long lastActivated;

    public boolean isMonitor = false; // like isObserve of phom
    private static final int TIMES = 4;
    private long betOther; //for bet together game 3 cay
    private long multiBetMoney;

    private boolean chan; //for game xocDia;

    private boolean confirmBetOther = false;

    protected boolean showHand = false;

    private long betChan;
    private long betLe;

    public boolean notEnoughMoney() {
        //System.out.println("cash " + cash + " moneyForBet: " + moneyForBet);
        if (cash < TIMES * moneyForBet) {
            return true;
        }

        return false;
    }

    public void write(Object obj) {
        try {
            this.currentSession.write(obj);
        } catch (ServerException e) {
            // TODO: handle exception
        }
    }

//    public void setCurrentOwner(ISession currentOwner) {
//        this.currentOwner = currentOwner;
//    }
    // event purpose - Added by ThangTD
    public void checkEvent(boolean isWin) throws ServerException {
        if (!DBCache.isLoadMonthlyEvent) {
            return;
        }

        List<EventItemEntity> itemsList;
        if (isWin) {
            itemsList = EventItemsDB.getWonListItems();
        } else {
            itemsList = EventItemsDB.getLostListItems();
        }

        if (itemsList != null) {
            // Get random item with fixed rate
            Random ran = new Random();
            float sum = 0;
            float value = ran.nextFloat() * 100;
            int idx = -1;

            for (int i = 0; i < itemsList.size(); i++) {
                EventItemEntity item = (EventItemEntity) itemsList.get(i);
                if ((value >= sum) && (value < (sum + item.getRate())) && (item.getQuantity() != 0)) {
                    idx = i;
                    break;
                }

                sum += item.getRate();
            }

            // Item found
            if (idx != -1) {
                EventItemEntity entity = (EventItemEntity) itemsList.get(idx);
                if (null != entity) {
                    if (XEDataUtils.insertInventoryOfUser(this.id, entity.getCode())) {
                        mLog.debug("--- THANGTD EVENT ITEM --- " + this.username + " got item " + entity.getName());

                        MessageFactory msgFactory = this.currentSession.getMessageFactory();
                        XENotifyEventItemResponse notifyRespone = (XENotifyEventItemResponse) msgFactory.getResponseMessage(MessagesID.NOTIFY_EVENT_ITEM);
                        notifyRespone.setResult(ResponseCode.SUCCESS, "Chúc mừng bạn đã nhận được " + entity.getName().toUpperCase() + " từ sự kiện Ghép chữ. Vui lòng vào mục sự kiện để kiểm tra");
                        
                        this.currentSession.write(notifyRespone);
                    }
                }
            }
        }
    }
    // End added

    /**
     * @return the experience
     */
    public int getExperience() {
        return experience;
    }

    /**
     * @param experience the experience to set
     */
    public void setExperience(int experience) {
        this.experience = experience;
    }

    public long moneyLost(long money_) {
        if (this.cash <= 0) {
            return 0;
        } else if (this.cash < money_) {
            return this.cash;
        } else {
            return money_;
        }
    }

    /**
     * @return the wonMoney
     */
    public long getWonMoney() {
        return wonMoney;
    }

    /**
     * @param wonMoney the wonMoney to set
     */
    public void setWonMoney(long wonMoney) {
        this.wonMoney = wonMoney;
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

    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public SimplePlayer clone() {
        SimplePlayer player = new SimplePlayer();
        player.id = this.id;
        player.currentSession = this.currentSession;
        player.isOut = this.isOut;
        return player;
    }

    /**
     * @return the betOther
     */
    public long getBetOther() {
        return betOther;
    }

    /**
     * @param betOther the betOther to set
     */
    public void setBetOther(long betOther) {
        this.betOther = betOther;
    }

    /**
     * @return the multiBetMoney
     */
    public long getMultiBetMoney() {
        return multiBetMoney;
    }

    /**
     * @param multiBetMoney the multiBetMoney to set
     */
    public void setMultiBetMoney(long multiBetMoney) {
        this.multiBetMoney = multiBetMoney;
    }

    /**
     * @return the showHand
     */
    public boolean isShowHand() {
        return showHand;
    }

    /**
     * @param showHand the showHand to set
     */
    public void setShowHand(boolean showHand) {
        this.showHand = showHand;
    }

    /**
     * @return the confirmBetOther
     */
    public boolean isConfirmBetOther() {
        return confirmBetOther;
    }

    /**
     * @param confirmBetOther the confirmBetOther to set
     */
    public void setConfirmBetOther(boolean confirmBetOther) {
        this.confirmBetOther = confirmBetOther;
    }

    /**
     * @return the chan
     */
    public boolean isChan() {
        return chan;
    }

    /**
     * @param chan the chan to set
     */
    public void setChan(boolean chan) {
        this.chan = chan;
    }

    /**
     * @return the betChan
     */
    public long getBetChan() {
        return betChan;
    }

    /**
     * @param betChan the betChan to set
     */
    public void setBetChan(long betChan) {
        this.betChan = betChan;
    }

    /**
     * @return the betLe
     */
    public long getBetLe() {
        return betLe;
    }

    /**
     * @param betLe the betLe to set
     */
    public void setBetLe(long betLe) {
        this.betLe = betLe;
    }

}
