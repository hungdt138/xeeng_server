/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetGiftTypeResponse;
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
public class GetGiftTypeBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory()
            .getLogger(GetGiftTypeBusiness.class);
@Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) {


        MessageFactory msgFactory = aSession.getMessageFactory();
        GetGiftTypeResponse resBoc = (GetGiftTypeResponse) msgFactory
                .getResponseMessage(aReqMsg.getID());
        try {
            resBoc.setSuccess(InfoDB.getGiftType());
        }catch (Throwable e) {
            resBoc.setFailure(e.getMessage());
        } finally {
            aResPkg.addMessage(resBoc);
        }
        return 1;
    }
}
