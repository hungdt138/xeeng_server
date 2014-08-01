/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetTopicNewPikachuRequest;
import com.tv.xeeng.base.protocol.messages.GetTopicNewPikachuResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.newpika.data.TopicManager;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class GetTopicNewPikachuBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory()
            .getLogger(GetTopicNewPikachuBusiness.class);
    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) {


        MessageFactory msgFactory = aSession.getMessageFactory();
        GetTopicNewPikachuResponse resBoc = (GetTopicNewPikachuResponse) msgFactory
                .getResponseMessage(aReqMsg.getID());
        try {
            aSession.setCurrentZone(ZoneID.NEW_PIKA);
            GetTopicNewPikachuRequest rqAlb = (GetTopicNewPikachuRequest) aReqMsg;
            resBoc.setSuccess(TopicManager.getNewTopic(rqAlb.id));
        } catch (Throwable e) {
            resBoc.setFailure(e.getMessage());
        } finally {
            aResPkg.addMessage(resBoc);
        }
        return 1;
    }
}
