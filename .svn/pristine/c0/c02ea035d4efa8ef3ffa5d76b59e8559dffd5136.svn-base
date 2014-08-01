package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.XEGetRemainingFreeGoldResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.GiftDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class XEGetRemainingFreeGoldBusiness extends AbstractBusiness {

    private static final Logger logger
            = LoggerContext.getLoggerFactory().getLogger(XEGetRemainingFreeGoldBusiness.class);
    private static final int TIMES_PER_DAY = 3;

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        logger.debug("[Get Num Of Received Free Gold]");
        MessageFactory msgFactory = aSession.getMessageFactory();
        XEGetRemainingFreeGoldResponse res
                = (XEGetRemainingFreeGoldResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        int receivedTimes = GiftDB.getNumOfReceivedFreeGold(aSession.getUID());
        if (receivedTimes >= 0) {
            res.mCode = ResponseCode.SUCCESS;
            res.setNumOfReceived(receivedTimes);
        } else {
            res.mCode = ResponseCode.FAILURE;
            res.setMessage("Không lấy được thông tin.");
        }

        aResPkg.addMessage(res);

        return 1;
    }
}
