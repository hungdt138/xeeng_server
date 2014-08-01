/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.ReceiveDutyResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
/**
 *
 * @author tuanda
 */
public class ReceiveDutyBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(ReceiveDutyBusiness.class);
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        MessageFactory msgFactory = aSession.getMessageFactory();
        ReceiveDutyResponse resWapGame = (ReceiveDutyResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resWapGame.setSuccess();
        aResPkg.addMessage(resWapGame);
        return 1;
    }
}
