package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.KeepConnectionResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class KeepConnectionBusiness extends AbstractBusiness {
	private static final Logger mLog =
        LoggerContext.getLoggerFactory().getLogger(KeepConnectionBusiness.class);
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) throws ServerException {
    	
    	//mLog.debug("Keep Connection for: "+ aSession.getUID());
//    	aSession.getCollectInfo().append("->KeepConnection: ");
    	MessageFactory msgFactory = aSession.getMessageFactory();
		KeepConnectionResponse resReconn = (KeepConnectionResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		//resReconn.setSuccess(ResponseCode.SUCCESS);
		try {
                    resReconn.session = aSession;
			aSession.write(resReconn);
		} catch (Throwable e) {
			mLog.error(e.getMessage());
		}
    	return 1;
    }

}
