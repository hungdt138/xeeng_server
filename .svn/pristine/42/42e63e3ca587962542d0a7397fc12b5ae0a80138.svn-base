/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;



import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.ForgotPasswordRequest;
import com.tv.xeeng.base.protocol.messages.ForgotPasswordResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class ForgotPasswordBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ForgotPasswordBusiness.class);
@Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
//        mLog.debug("[BET] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory = aSession.getMessageFactory();
		ForgotPasswordResponse resTransferCash = (ForgotPasswordResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
        
        try {
                ForgotPasswordRequest rqAddComment = (ForgotPasswordRequest) aReqMsg;
                if(aSession.getByteProtocol()< 1)
                    aSession.setByteProtocol(AIOConstants.PROTOCOL_REFACTOR_BIENG);
                
                StringBuilder sb = new StringBuilder();
                sb.append(AIOConstants.KEYWORD_FORGOT_PSW).append(" ").append(rqAddComment.userName).append(" ").append(Integer.toString(rqAddComment.partnerId)).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(AIOConstants.DAUSO_FORGOT_PSW);
                resTransferCash.setSuccess(sb.toString());
                aResPkg.addMessage(resTransferCash);
        }
        
        catch (Throwable t) {
                

        } 
        
        return 1;
    }
}
