package com.tv.xeeng.base.business;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.BookTourRequest;
import com.tv.xeeng.base.protocol.messages.BookTourResponse;
import com.tv.xeeng.base.protocol.messages.GetEventResponse;
import com.tv.xeeng.base.protocol.messages.GetMSTTResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.tournement.TourManager;
import com.tv.xeeng.game.tournement.TournementEntity;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class GetMSTTBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(GetMSTTBusiness.class);
        
	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {

		
		MessageFactory msgFactory = aSession.getMessageFactory();
		GetMSTTResponse resBoc = (GetMSTTResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		try {
			resBoc.mCode = ResponseCode.SUCCESS;
                        StringBuilder sb = new StringBuilder();
//                        sb.append("12345").append(AIOConstants.SEPERATOR_NEW_ELEMENT);
//                        sb.append("03-10-2012").append(AIOConstants.SEPERATOR_NEW_ARRAY);
//                        sb.append("12346").append(AIOConstants.SEPERATOR_NEW_ELEMENT);
//                        sb.append("03-10-2012");
                        resBoc.value = sb.toString();
                        
		} finally {
			aResPkg.addMessage(resBoc);
		}
		return 1;
	}

	
}
