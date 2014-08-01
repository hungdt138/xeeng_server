package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetUserInfoRequest;
import com.tv.xeeng.base.protocol.messages.GetUserInfoResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.FriendDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import java.util.logging.Level;
import org.slf4j.Logger;

public class GetUserInfoBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GetUserInfoBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[GET USER INFOS]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetUserInfoResponse resGetUserInfo = (GetUserInfoResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            GetUserInfoRequest rqGetUserInfo = (GetUserInfoRequest) aReqMsg;

            resGetUserInfo.session = aSession;
            long uid = rqGetUserInfo.mUid;
            long source_uid = aSession.getUID();
            mLog.debug("[GET USER INFOS]:" + uid);
//            UserDB userDb = new UserDB();
//            
//            UserEntity user = userDb.getUserInfo(uid);
            CacheUserInfo cacheUser = new CacheUserInfo();
            UserEntity user = cacheUser.getUserInfo(uid);
            if (user != null) {
                boolean isFriend = false;
                if (uid == source_uid) {
                    isFriend = true;
                } else {
                    FriendDB friendDb = new FriendDB();
                    isFriend = friendDb.isFriend(source_uid, uid);
                }
                resGetUserInfo.partnerId = rqGetUserInfo.partnerId;
                resGetUserInfo.mobileVersion = rqGetUserInfo.mobileVersion;
                mLog.debug("---THANGTD USERINFO REQUEST DEBUG---partnerId: " + rqGetUserInfo.partnerId + " version: " + rqGetUserInfo.mobileVersion);
                resGetUserInfo.vipName = user.vipName;
                resGetUserInfo.setSuccess(ResponseCode.SUCCESS, user.mUid, user.loginName,
                        user.mAge, user.mIsMale, user.money, user.playsNumber, user.avatarID, isFriend, user.level, user.experience, user.xeeng, user.mUsername, user.cmnd, user.xePhoneNumber);
            } else {// non-existed user
                resGetUserInfo.setFailure(ResponseCode.FAILURE, "Tài khoản này không tồn tại!");
            }

        } catch (Throwable t) {
            resGetUserInfo.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu ");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resGetUserInfo != null)) {
                try {
                    //aResPkg.addMessage(resGetUserInfo);

                    aSession.write(resGetUserInfo);
                } catch (ServerException ex) {
                    java.util.logging.Logger.getLogger(GetUserInfoBusiness.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        return 1;
    }
}
