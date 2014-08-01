package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.XEGetPrivateMessageRequest;
import com.tv.xeeng.base.protocol.messages.XEGetPrivateMessageResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.XEPrivateMessageEntity;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class XEGetPrivateMessageBusiness extends AbstractBusiness {

    private static final Logger mLog
            = LoggerContext.getLoggerFactory().getLogger(XEGetPrivateMessageBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Get All Private Messages] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        XEGetPrivateMessageResponse res
                = (XEGetPrivateMessageResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        XEGetPrivateMessageRequest rq = (XEGetPrivateMessageRequest) aReqMsg;

        XEPrivateMessageEntity pm = XEDataUtils.getPrivateMessage(rq.getPmId());
        if (pm == null || pm.getToUserId() != aSession.getUID()) {
            /* nếu không có tin nhắn hoặc tin nhắn thuộc sở hữu của user khác */
            res.mCode = ResponseCode.FAILURE;
            res.setMessage("Không tìm thấy tin nhắn.");
        } else {
            pm.setDetail(true);

            res.mCode = ResponseCode.SUCCESS;
            res.setMessage(pm.toString());
        }

        aResPkg.addMessage(res);

        return 1;
    }
}
