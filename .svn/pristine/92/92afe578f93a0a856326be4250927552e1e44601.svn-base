/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GiftForUserRequest;
import com.tv.xeeng.base.protocol.messages.GiftForUserResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.InfoDB;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class GiftForUserBusiness extends AbstractBusiness {    
    private static final Logger mLog = LoggerContext.getLoggerFactory()
            .getLogger(GiftForUserBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) {


        MessageFactory msgFactory = aSession.getMessageFactory();
        GiftForUserResponse resBoc = (GiftForUserResponse) msgFactory
                .getResponseMessage(aReqMsg.getID());
        try {
            GiftForUserRequest rq = (GiftForUserRequest) aReqMsg;            
            resBoc.setSuccess(String.valueOf(InfoDB.giftForUser(aSession.getUID(), rq.objectID, rq.type)));
        } catch (Throwable e) {
            resBoc.setFailure(e.getMessage());
        } finally {
            aResPkg.addMessage(resBoc);
        }
        return 1;
    }
}
