/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.protocol.*;
import com.tv.xeeng.server.Server;
import org.slf4j.Logger;

import java.sql.SQLException;

public class LogoutBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(LogoutBusiness.class);
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        try {
            mLog.debug("logout session");
            aSession.setLoggedIn(false);
            
            UserDB userDb = new UserDB();
                
            userDb.logout(aSession.getUID(), aSession.getCollectInfo().toString());
            
//            CacheUserInfo cacheUser = new CacheUserInfo();
//            UserEntity entity = cacheUser.getUserInfo(aSession.getUID());
//            entity.isLogin = false;
//            entity.isOnline = false;
//            cacheUser.updateCacheUserInfo(entity);            
//            aSession.setUserName("");

            // thống kê người chơi online
            Server.userOnlineList.remove(aSession.getUID());
            
            if(aSession.getRoom()!= null && aSession.getRoom().getAttactmentData() != null && 
                    aSession.getRoom().getAttactmentData().getNotNullSession() != null)
            {
                IResponsePackage responsePkg = aSession.getDirectMessages();
				MessageFactory msgFactory = aSession.getMessageFactory();		
		        long matchID = aSession.getRoom().getRoomId();
				if(msgFactory == null) return 1;
		                
				// Case
				IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_CANCEL);
				CancelRequest rqMatchCancel = (CancelRequest) msgFactory
						.getRequestMessage(MessagesID.MATCH_CANCEL);
				rqMatchCancel.uid = aSession.getUID();
				rqMatchCancel.mMatchId = matchID;
				rqMatchCancel.isLogout = true;
	
				try {
					business.handleMessage(aSession, rqMatchCancel, responsePkg);
				} catch (ServerException se) {
					mLog.error("[Netty Socket] Exception Catch Error!", se.getCause());
				}
                
            }
            try
            {
                aSession.getManager().removePrvChatSession(aSession.getUID());
            }
            catch(Exception ex)
            {
                mLog.error(ex.getMessage(), ex);
            }
        } catch (SQLException ex) {
             mLog.debug("logout session", ex);
        }
        return 1;
        
    }
}
