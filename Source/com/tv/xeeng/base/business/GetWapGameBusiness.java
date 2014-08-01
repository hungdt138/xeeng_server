/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetWapGameResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
/**
 *
 * @author tuanda
 */
public class GetWapGameBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetWapGameBusiness.class);
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetWapGameResponse resWapGame = (GetWapGameResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resWapGame.setFailure("Tính năng này sẽ sớm ra mắt");
        aResPkg.addMessage(resWapGame);
        return 1;
    }
}
