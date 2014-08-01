package com.tv.xeeng.base.business;


import java.util.ArrayList;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetTournementListResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.tournement.TournementEntity;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class GetTournementListBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(GetTournementListBusiness.class);

	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {
		MessageFactory msgFactory = aSession.getMessageFactory();
		GetTournementListResponse resTourInfo = (GetTournementListResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		try {
			ArrayList<TournementEntity> res = aSession.getTourMgr().getToursInfo(aSession.getUID());
			if(res.size() > 0) {
				resTourInfo.setSuccess(res);
			} else {
				resTourInfo.setFailure("Hiện tại chưa có giải đấu nào");
			}
			aSession.write(resTourInfo);
		} catch (Throwable t) {
			mLog.error("Process message " + aReqMsg.getID() + " error.", t.getMessage());
			try {
				aSession.write(resTourInfo);
			} catch (ServerException ex) {
			}
		}
		return 1;
	}
}
