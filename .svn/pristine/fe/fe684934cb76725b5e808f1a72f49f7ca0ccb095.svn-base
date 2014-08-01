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
import com.tv.xeeng.base.protocol.messages.GetAchievementDetailRequest;
import com.tv.xeeng.base.protocol.messages.GetAchievementDetailResponse;
import com.tv.xeeng.base.protocol.messages.GetEventDetailRequest;
import com.tv.xeeng.base.protocol.messages.GetEventDetailResponse;
import com.tv.xeeng.base.protocol.messages.GetListEventResponse;
import com.tv.xeeng.base.protocol.messages.SendImageResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.EventDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.EventEntity;
import com.tv.xeeng.game.data.EventPlayerEntity;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.QueueEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.CacheEventInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



import java.util.List;

public class GetAchievementDetailBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(GetAchievementDetailBusiness.class);
        
       

	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {

		
		MessageFactory msgFactory = aSession.getMessageFactory();
		GetAchievementDetailResponse resBoc = (GetAchievementDetailResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		try {
                    GetAchievementDetailRequest rqEvent = (GetAchievementDetailRequest) aReqMsg;
                    StringBuilder sb = new StringBuilder();
                    if(rqEvent.achievementId >0)
                    {
                        CacheEventInfo cache = new CacheEventInfo();
                        List<EventPlayerEntity> lstPlayers = cache.getEventPlayer(rqEvent.achievementId);
                        int playingSize = lstPlayers.size();
                        for(int i = 0; i< playingSize; i++)
                        {
                            EventPlayerEntity playerEntity = lstPlayers.get(i);
                            sb.append(playerEntity.getUsrEntity().mUid).append(AIOConstants.SEPERATOR_BYTE_1);
                            sb.append(playerEntity.getUsrEntity().mUsername).append(" - ").
                                    append(playerEntity.point).append(" lần").append(AIOConstants.SEPERATOR_BYTE_1);
                            sb.append(playerEntity.getDescription()).append(AIOConstants.SEPERATOR_BYTE_2);
//                            sb.append("1").append(AIOConstants.SEPERATOR_BYTE_1);
//                            sb.append("kenshin1").append(AIOConstants.SEPERATOR_BYTE_1);
//                            sb.append("0").append(AIOConstants.SEPERATOR_BYTE_2);
//                            sb.append("2").append(AIOConstants.SEPERATOR_BYTE_1);
//                            sb.append("kenshin2").append(AIOConstants.SEPERATOR_BYTE_1);
//                            sb.append("0");
                        }
                        
                        if(playingSize>0)
                        {
                            sb.deleteCharAt(sb.length()-1);
                        }
                    }
                            
//                    List<EventEntity> lstEvents = EventDB.getEvents();
//                    int size = lstEvents.size();
//                    
//                    StringBuilder sb = new StringBuilder();
//                    for(int i = 0; i< size; i++)
//                    {
//                        EventEntity entity = lstEvents.get(i);
//                        
//                        if(entity.getEventId() == rqEvent.eventId)
//                        {
//                            sb.append(entity.getEventId()).append(AIOConstants.SEPERATOR_BYTE_1);
//                            sb.append(entity.getContent());
//                            
//                            resBoc.mCode = ResponseCode.SUCCESS;
//                            resBoc.value = sb.toString();
//                        
//                            if(entity.isDetailImage())
//                            {
//                                StringBuilder thumb = new StringBuilder();
//                                thumb.append(entity.getEventId()).append(AIOConstants.SEPERATOR_BYTE_1);
//                                thumb.append("0").append(AIOConstants.SEPERATOR_BYTE_1);
//                                thumb.append(entity.getPicDetail());
//                                insertQueue(msgFactory, thumb.toString(), aSession);
//                            }
//                            return 1;
//                        }
//                    }
                    
                    resBoc.mCode = ResponseCode.SUCCESS;
                    resBoc.value = sb.toString();
                        
		} 
                catch(Exception ex)
                {
                    mLog.error(ex.getMessage(), ex);
                    resBoc.setFailure("co loi ket noi voi co so du lieu");
                }
                finally {
			aResPkg.addMessage(resBoc);
		}
		return 1;
	}

	
}
