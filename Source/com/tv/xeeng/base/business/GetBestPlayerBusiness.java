package com.tv.xeeng.base.business;

import java.util.Vector;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetBestPlayerResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class GetBestPlayerBusiness extends AbstractBusiness
{

    private static final Logger mLog = 
    	LoggerContext.getLoggerFactory().getLogger(GetBestPlayerBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg)
    {
        // process's status
        mLog.debug("[GET BEST PLAYER]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetBestPlayerResponse resGetBestPlayer = (GetBestPlayerResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        
        resGetBestPlayer.session = aSession;
        try
        {
            long uid = aSession.getUID();
            mLog.debug("[GET BEST PLAYER]: for" + uid);
            CacheUserInfo cacheUser = new CacheUserInfo();
            UserEntity currEntity = cacheUser.getUserInfo(uid);
            Vector<UserEntity> richests =null;
            
            if(currEntity.partnerId == AIOConstants.M4V_PARTNER)
            {
                UserDB db = new UserDB();
                richests = (Vector<UserEntity>) db.getBestPlayer(currEntity.partnerId);
            }
            else
            {
            
                richests = DatabaseDriver.getBestPlayer();
            }
            
            resGetBestPlayer.setSuccess(ResponseCode.SUCCESS, richests);
        } catch (Throwable t) {
            resGetBestPlayer.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu ");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resGetBestPlayer != null)) {
                aResPkg.addMessage(resGetBestPlayer);
            }
        }
        return 1;
    }
}
