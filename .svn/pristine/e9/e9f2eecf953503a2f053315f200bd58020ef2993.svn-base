package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.XEReceiveFreeGoldRequest;
import com.tv.xeeng.base.protocol.messages.XEReceiveFreeGoldResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.GiftDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class XEReceiveFreeGoldBusiness extends AbstractBusiness {

    private static final Logger logger
            = LoggerContext.getLoggerFactory().getLogger(XEReceiveFreeGoldBusiness.class);
    public static final int TIMES_PER_DAY = 3;
    private static final int NUM_OF_GOLD = 6000;
    private static final int MIN_GOLD_ALLOWED = 4000;

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        logger.debug("[Receive Free Gold]");
        MessageFactory msgFactory = aSession.getMessageFactory();
        XEReceiveFreeGoldResponse res
                = (XEReceiveFreeGoldResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        XEReceiveFreeGoldRequest rq = (XEReceiveFreeGoldRequest) aReqMsg;

        int receivedTimes = GiftDB.getNumOfReceivedFreeGold(aSession.getUID());
        if (receivedTimes >= TIMES_PER_DAY) {
            res.mCode = ResponseCode.FAILURE;
            res.setMessage("Bạn đã hết lượt nhận Gold miễn phí, vui lòng quay lại vào ngày mai.");
        } else {
            boolean success = GiftDB.receiveFreeGoldByDay(aSession.getUID(), NUM_OF_GOLD, MIN_GOLD_ALLOWED);
            if (success) {
                receivedTimes += 1;

                res.mCode = ResponseCode.SUCCESS;
                if (receivedTimes < TIMES_PER_DAY) {
                    res.setMessage(String.format("Nhận Gold thành công, bạn còn %d lượt nhận Gold miễn phí trong ngày hôm nay.", TIMES_PER_DAY - receivedTimes));
                } else {
                    res.setMessage("Nhận Gold thành công, đây là lượt nhận Gold cuối cùng của bạn trong ngày hôm nay.");
                }

                // refresh cache
                CacheUserInfo.deleteCacheUserById(aSession.getUID());
            } else {
                res.mCode = ResponseCode.FAILURE;
                res.setMessage("Bạn không đủ điều kiện để nhận Gold miễn phí.");
            }
        }

        aResPkg.addMessage(res);

        return 1;
    }
}
