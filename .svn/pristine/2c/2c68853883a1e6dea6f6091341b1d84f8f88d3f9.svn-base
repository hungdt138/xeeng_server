package com.tv.xeeng.base.business;

import java.util.logging.Level;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BotFastPlayRequest;
import com.tv.xeeng.base.protocol.messages.BotFastPlayResponse;
import com.tv.xeeng.base.protocol.messages.FastPlayRequest;
import com.tv.xeeng.base.protocol.messages.FastPlayResponse;
import com.tv.xeeng.base.protocol.messages.JoinRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.Couple;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class BotFastPlayBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(BotFastPlayBusiness.class);
        
        private static final String ALL_TABLES_BUSY = "Hiện tại không có bàn rỗi nào phù hợp với số tiền của bạn! Xin bạn thử lại sau ít phút";
        private static final String MAX_TWO_REQUEST = "Bạn thực hiện quá nhanh 2 yêu cầu tìm bàn. 2 yêu cầu tìm bàn cách nhau 10s!";
        
        private static final int MAX_BETWEEN_TWO_REQUEST = 10000;  
        
                
	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {
		int rtn = PROCESS_FAILURE;
		MessageFactory msgFactory = aSession.getMessageFactory();
		BotFastPlayResponse resfP = (BotFastPlayResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		mLog.debug("[FAST PLAY]: Catch");
		try {
			// request message and its values
			BotFastPlayRequest rqfP = (BotFastPlayRequest) aReqMsg;
			// broadcast
			

                        
                        int zoneID = aSession.getCurrentZone();
//                        if(aSession.getByteProtocol() > AIOConstants.PROTOCOL_ACTIVE_LOGIN)
//                        {
//                            zoneID = rqfP.zoneId;
                            aSession.setCurrentZone(zoneID);
//                        }
                        
                        
                        
			Zone zone = aSession.findZone(zoneID);
//			Couple<Integer, Long> res = new Couple<Integer, Long>(0, 0l);
                        long matchId = 0;
			try {
                            CacheUserInfo cacheUser = new CacheUserInfo();
                            UserEntity usrEntity = cacheUser.getUserInfo(aSession.getUID());
                            
				matchId = zone.botfastPlay(usrEntity.money/4);
//				resfP.setSuccess(ResponseCode.SUCCESS, res.e2, res.e1);
//				
//				aSession.write(resfP);
//                                aSession.getCollectInfo().append("->FastPlay: ");
			} catch (Exception e) {
                            throw new BusinessException(ALL_TABLES_BUSY);
				
			}
                        
                        if(matchId == 0)
                        {
                            throw new BusinessException(ALL_TABLES_BUSY);
                        }
			// try {
                        
                       
//                        aSession.setLastFastMatch(res.e2);
			IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_JOIN);
			JoinRequest rqJoin = (JoinRequest) msgFactory
					.getRequestMessage(MessagesID.MATCH_JOIN);
			rqJoin.mMatchId = matchId;
			rqJoin.zone_id = zoneID;
			//rqJoin.uid = aSession.getUID();
			//rqJoin.roomID = rqfP.roomID;
			business.handleMessage(aSession, rqJoin, aResPkg);
			/*
			 * } catch (ServerException se) {
			 * 
			 * }
			 */
			
			rtn = PROCESS_OK;
		} 
                catch(BusinessException be)
                {
                    resfP.setFailure(ResponseCode.FAILURE,
						be.getMessage());
                    try {
                        //System.out.println("Hello:" + resfP.mCode);
                            aSession.write(resfP);
                    } catch (ServerException ex) {
                        mLog.error("fast playing ", ex);
                    }
                    
                    return PROCESS_OK;
                }
                catch (Throwable t) {
			// response failure
			resfP.setFailure(ResponseCode.FAILURE, "Lỗi !");
			// aSession.setLoggedIn(false);
			rtn = PROCESS_OK;
			mLog.error("Process message " + aReqMsg.getID() + " error.", t);
			aResPkg.addMessage(resfP);
		}

		return rtn;
	}
}
