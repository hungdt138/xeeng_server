package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.XESendPrivateMessageRequest;
import com.tv.xeeng.base.protocol.messages.XESendPrivateMessageResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.XEPrivateMessageEntity;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class XESendPrivateMessageBusiness extends AbstractBusiness {

    private static final Logger mLog
            = LoggerContext.getLoggerFactory().getLogger(XESendPrivateMessageBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Send Private Message] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        XESendPrivateMessageResponse res
                = (XESendPrivateMessageResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        XESendPrivateMessageRequest rq = (XESendPrivateMessageRequest) aReqMsg;

        XEPrivateMessageEntity pm = new XEPrivateMessageEntity(
                aSession.getUID(), rq.getToUserId(), rq.getTitle(), rq.getContent());
        boolean success = XEDataUtils.insertPrivateMessage(pm);

        if (success) {
            res.mCode = ResponseCode.SUCCESS;
            res.setMessage("Gửi thành công.");
        } else {
            res.mCode = ResponseCode.FAILURE;
            res.setMessage("Gửi không thành công.");
        }

        aResPkg.addMessage(res);

        return 1;
    }
}
