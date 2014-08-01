package com.tv.xeeng.base.business;


import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetEventResponse;
import com.tv.xeeng.base.protocol.messages.RejectInviteResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;





public class RejectInviteBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(RejectInviteBusiness.class);
        
	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {

		
		MessageFactory msgFactory = aSession.getMessageFactory();
		RejectInviteResponse resBoc = (RejectInviteResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		try {
                    aSession.setRejectInvite(true);
		    resBoc.mCode = ResponseCode.SUCCESS;
                        
                        
		} finally {
//			aResPkg.addMessage(resBoc);
		}
		return 1;
	}

	
}
