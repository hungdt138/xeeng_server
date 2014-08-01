package com.tv.xeeng.base.business;

import java.util.Vector;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetFreeFriendListRequest;
import com.tv.xeeng.base.protocol.messages.GetFreeFriendListResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.base.session.SessionManager;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class GetFreeFriendListBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GetFreeFriendListBusiness.class);
    
    private static final int TIMES = 4;

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
//        mLog.debug("[GET FREE FRIENDLIST]: Catch");
//        aSession.getCollectInfo().append("->GetFreeFriendList: ");
//        
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetFreeFriendListResponse resGetFreeFriendList = (GetFreeFriendListResponse) msgFactory.getResponseMessage(aReqMsg.getID());
       
        try {
            long uid = aSession.getUID();
            GetFreeFriendListRequest rqGet = (GetFreeFriendListRequest) msgFactory.getRequestMessage(aReqMsg.getID());
            int level = rqGet.level;
            
//            mLog.debug("[GET FREE FRIENDLIST]: for" + uid);
            SessionManager manager = aSession.getManager();
            Vector<UserEntity> res = new Vector<UserEntity>();
            
            //TODO:  get free user in all zone
//            res = manager.dumpFreeUsers(20);
            Room room = aSession.getRoom();
            long minimumMoneyJoin = 0;
            if(room != null)
            {
                minimumMoneyJoin = room.getAttactmentData().firstCashBet * TIMES;
            }
            
            res = manager.dumpFreeFriend(30, level, aSession.getCurrentZone(), minimumMoneyJoin);
            
            mLog.warn(("friendlist size " + res.size()));
            
            resGetFreeFriendList.setSuccess(ResponseCode.SUCCESS, res);
            resGetFreeFriendList.setSession(aSession);
        } catch (Throwable t) {
            resGetFreeFriendList.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu ");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resGetFreeFriendList != null)) {
                aResPkg.addMessage(resGetFreeFriendList);
            }
        }
        return 1;
    }
}
