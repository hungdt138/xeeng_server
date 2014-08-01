package com.tv.xeeng.base.business;



import java.util.Vector;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetFrientListResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.base.session.SessionManager;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.databaseDriven.FriendDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


public class GetFrientListBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetFrientListBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[GET FRIENDLIST]: Catch");
        aSession.getCollectInfo().append("->GetFrientList: ");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetFrientListResponse resGetFriendList = (GetFrientListResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resGetFriendList.session = aSession;
        try {
            long uid = aSession.getUID();
            mLog.debug("[GET FRIENDLIST]: for" + uid);
            FriendDB friendDB = new FriendDB();
             Vector<UserEntity> frientlist = friendDB.getFrientList(uid, true);
             
//            Vector<UserEntity> frientlist = DatabaseDriver.getFrientList(uid);
//            SessionManager manager = aSession.getManager();
//            Vector<UserEntity> res = new Vector<UserEntity>();
//            for (UserEntity user : frientlist) {
//                ISession buddy = (ISession) manager.findSession(user.mUid);
//                if (buddy != null) {
//                	user.isLogin = true;
//                }else {
//                	user.isLogin = false;
//                }
//                res.add(user);
//                
//            }
            
             resGetFriendList.setSuccess(ResponseCode.SUCCESS, frientlist);
        } catch (Throwable t) {
            resGetFriendList.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu ");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resGetFriendList != null)) {
                aResPkg.addMessage(resGetFriendList);
            }
        }
        return 1;
    }
}
