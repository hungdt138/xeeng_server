package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.PostNewRequest;
import com.tv.xeeng.base.protocol.messages.PostNewResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class PostNewBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(SuggestBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[ POST ]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        PostNewResponse resSuggest = (PostNewResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            PostNewRequest rqSuggest = (PostNewRequest) aReqMsg;
            String name = rqSuggest.name;
            String note = rqSuggest.note;
            mLog.debug("[ Post ]: of" + name);
            DatabaseDriver.insertPost(name, note);
            resSuggest.setSuccess(ResponseCode.SUCCESS);
        } catch (Throwable t) {
            resSuggest.setFailure(ResponseCode.FAILURE, "Xử lý bị lỗi!!!");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if (resSuggest != null) {
                aResPkg.addMessage(resSuggest);
            }
        }
        return 1;
    }
}
