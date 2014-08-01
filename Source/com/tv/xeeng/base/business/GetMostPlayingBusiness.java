package com.tv.xeeng.base.business;

import java.util.Vector;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetMostPlayingResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class GetMostPlayingBusiness extends AbstractBusiness
{

    private static final Logger mLog = 
    	LoggerContext.getLoggerFactory().getLogger(GetMostPlayingBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg)
    {
        mLog.debug("[GET MOST PLAYING]: Catch");
//        aSession.getCollectInfo().append("->GetMostPlaying: ");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetMostPlayingResponse resGetMostPlayingList = (GetMostPlayingResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try
        {
            resGetMostPlayingList.session = aSession;
            long uid = aSession.getUID();
            mLog.debug("[GET MOST PLAYING]: for" + uid);
            Vector<UserEntity> richests = DatabaseDriver.getMostPlaying();
            resGetMostPlayingList.setSuccess(ResponseCode.SUCCESS, richests);
        } catch (Throwable t) {
            resGetMostPlayingList.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu ");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resGetMostPlayingList != null)){
                aResPkg.addMessage(resGetMostPlayingList);
            }
        }

        return 1;
    }
}
