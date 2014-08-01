package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetTourInfoRequest;
import com.tv.xeeng.base.protocol.messages.GetTourInfoResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class GetTourInfoBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(GetTourInfoBusiness.class);

	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {

		mLog.debug("[Get Tour Info]: Catch");
		MessageFactory msgFactory = aSession.getMessageFactory();
		GetTourInfoResponse resBoc = (GetTourInfoResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		try {
			GetTourInfoRequest rqBoc = (GetTourInfoRequest) aReqMsg;
			int tID = rqBoc.tourID;
			String res = aSession.getTourMgr().getTourDetail(tID);
			resBoc.setSuccess(res);
		} catch (Throwable t) {
			resBoc.setFailure(ResponseCode.FAILURE,
					"Có lỗi xảy ra." + t.getMessage());
			t.printStackTrace();
		} finally {
			aResPkg.addMessage(resBoc);
		}
		return 1;
	}

}
