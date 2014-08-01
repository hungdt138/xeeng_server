package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.XEGetAllPrivateMessagesRequest;
import com.tv.xeeng.base.protocol.messages.XEGetAllPrivateMessagesResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.XEPrivateMessageEntity;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import java.util.List;
import org.slf4j.Logger;

public class XEGetAllPrivateMessagesBusiness extends AbstractBusiness {

    private static final int NUM_OF_DAY = 7;

    private static final Logger mLog
            = LoggerContext.getLoggerFactory().getLogger(XEGetAllPrivateMessagesBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Get All Private Messages] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        XEGetAllPrivateMessagesResponse res
                = (XEGetAllPrivateMessagesResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        XEGetAllPrivateMessagesRequest rq = (XEGetAllPrivateMessagesRequest) aReqMsg;

//        if (rq.getUserId() != aSession.getUID()) {
//            res.mCode = ResponseCode.FAILURE;
//            res.setMessage("Bạn không được phép truy cập tin nhắn của người chơi khác. Toàn bộ thông tin liên quan đến hành động của bạn đã được gửi tới đội ngũ kỹ thuật.");
//        } else {
        List<XEPrivateMessageEntity> lstPM = XEDataUtils.getAllPrivateMessages(aSession.getUID(), NUM_OF_DAY);

        if (lstPM != null) {
            res.mCode = ResponseCode.SUCCESS;
            res.setMessage(XEDataUtils.serializeList(lstPM));
        } else {
            res.mCode = ResponseCode.FAILURE;
            res.setMessage("Có lỗi xảy ra.");
        }
//        }

        aResPkg.addMessage(res);

        return 1;
    }
}
