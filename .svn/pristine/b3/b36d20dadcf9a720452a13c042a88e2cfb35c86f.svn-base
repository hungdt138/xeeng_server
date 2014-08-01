package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetEventResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class GetEventBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GetEventBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetEventResponse resBoc = (GetEventResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            resBoc.mCode = ResponseCode.SUCCESS;
            resBoc.value = "Event 21->23/9 VUA BÀI Tiến lên miền nam (Số trận thắng nhiều nhất)";

        } finally {
            aResPkg.addMessage(resBoc);
        }
        return 1;
    }
}
