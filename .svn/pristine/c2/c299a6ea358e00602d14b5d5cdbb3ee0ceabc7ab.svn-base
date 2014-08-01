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
import com.tv.xeeng.base.data.CommonQueue;
import com.tv.xeeng.base.protocol.messages.GetListAchievementResponse;
import com.tv.xeeng.base.protocol.messages.GetListEventResponse;
import com.tv.xeeng.base.protocol.messages.SendImageResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.EventDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.AchievementEntity;
import com.tv.xeeng.game.data.EventEntity;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.QueueEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



import java.util.List;

public class GetListAchievementBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(GetListAchievementBusiness.class);
        
        

	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {

		
		MessageFactory msgFactory = aSession.getMessageFactory();
		GetListAchievementResponse resBoc = (GetListAchievementResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		try {
                    
                    resBoc.mCode = ResponseCode.SUCCESS;
                    resBoc.value = EventDB.getAchivements(aSession.getUserEntity().partnerId);
                        
		} finally {
			aResPkg.addMessage(resBoc);
		}
		return 1;
	}

	
}
