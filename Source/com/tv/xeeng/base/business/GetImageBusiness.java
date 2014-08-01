package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetImageRequest;
import com.tv.xeeng.base.protocol.messages.GetImageResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class GetImageBusiness  extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(GetImageBusiness.class);

	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {
		int rtn = PROCESS_FAILURE;
		MessageFactory msgFactory = aSession.getMessageFactory();
		GetImageResponse resGetI = (GetImageResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
                
                resGetI.session = aSession;
		mLog.debug("[GetImage]: Catch");
		try {
			// request message and its values
			GetImageRequest rqGetI = (GetImageRequest) aReqMsg;
			String name = rqGetI.name;
			String image = DatabaseDriver.getImage(name);
			resGetI.setSuccess(ResponseCode.SUCCESS, image, name);
			aSession.write(resGetI);
			rtn = PROCESS_OK;
		} catch (Throwable t) {
			// response failure
			resGetI.setFailure(ResponseCode.FAILURE, "Không tìm thấy ảnh!");
			//aSession.setLoggedIn(false);
			rtn = PROCESS_OK;
			mLog.error("Process message " + aReqMsg.getID() + " error.", t);
			aResPkg.addMessage(resGetI);
		}

		return rtn;
	}

}
