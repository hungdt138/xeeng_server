package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.BuyLevelRequest;
import com.tv.xeeng.base.protocol.messages.BuyLevelResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.databaseDriven.InfoDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class BuyLevelBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(BuyLevelBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[BUY-LEVEL]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        BuyLevelResponse resBuyLevel =
                (BuyLevelResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            BuyLevelRequest rqBuyLevel = (BuyLevelRequest) aReqMsg;
            long uid = rqBuyLevel.uid;
            int currLevel = DatabaseDriver.getUserLevel(uid);
            if (currLevel == 15) {
                resBuyLevel.setFailure(ResponseCode.FAILURE, "Bạn đã ở trên đỉnh, không thể lên hơn được nữa");
            } else {
                InfoDB infoDb = new InfoDB();
                long money = infoDb.getMoneyForUpdateLevel(currLevel + 1);
                long cashU = DatabaseDriver.getUserMoney(uid);
                if (cashU >= money) {
                    DatabaseDriver.updateLevel(uid);
                    long newMoney = infoDb.getMoneyForUpdateLevel(currLevel + 1);
                    resBuyLevel.setSuccess(ResponseCode.SUCCESS, cashU - money, currLevel + 2, newMoney);
                } else {
                    resBuyLevel.setFailure(ResponseCode.FAILURE, "Bạn không đủ tiền để nâng cấp.");
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            resBuyLevel.setFailure(ResponseCode.FAILURE, "Có lỗi xảy ra khi bạn nâng cấp");
        } finally {
            if ((resBuyLevel != null)) {
                aResPkg.addMessage(resBuyLevel);
            }
        }
        return 1;
    }
}
