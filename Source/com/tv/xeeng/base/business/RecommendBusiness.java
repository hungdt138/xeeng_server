/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;


import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetDutyResponse;
import com.tv.xeeng.base.protocol.messages.RecommendResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DutyDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

/**
 *
 * @author tuanda
 */
public class RecommendBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog = LoggerContext.getLoggerFactory().getLogger(RecommendBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {

        MessageFactory msgFactory = aSession.getMessageFactory();
        RecommendResponse resWapGame = (RecommendResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        
        resWapGame.setSuccess(ResponseCode.SUCCESS);
        aResPkg.addMessage(resWapGame);
        return 1;
    }
}
