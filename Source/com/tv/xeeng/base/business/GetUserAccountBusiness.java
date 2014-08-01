package com.tv.xeeng.base.business;

import java.util.logging.Level;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.ChatRequest;
import com.tv.xeeng.base.protocol.messages.GetPersonInfoRequest;
import com.tv.xeeng.base.protocol.messages.GetPersonInfoResponse;
import com.tv.xeeng.base.protocol.messages.GetUserAccountRequest;
import com.tv.xeeng.base.protocol.messages.GetUserAccountResponse;
import com.tv.xeeng.base.protocol.messages.GetUserInfoRequest;
import com.tv.xeeng.base.protocol.messages.GetUserInfoResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.databaseDriven.FriendDB;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.data.UserInfoEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class GetUserAccountBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetUserAccountBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[GET USER Account]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetUserAccountResponse resGetUserInfo = (GetUserAccountResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            GetUserAccountRequest rqGetUserInfo = (GetUserAccountRequest) aReqMsg;
            long uid = rqGetUserInfo.uid;
            
//            UserDB db = new UserDB();
           CacheUserInfo cacheUser = new CacheUserInfo();
            UserEntity user = cacheUser.getUserInfo(uid);
            if (user != null) 
            {
                StringBuilder sb = new StringBuilder();
                sb.append(user.mUid).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(user.stt==null?"":user.stt).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(user.avFileId);
                
                resGetUserInfo.setSuccess(sb.toString());
                if(user.avFileId >0)
                {
//                    GetSocialAvatarRequest rqSocial = (GetSocialAvatarRequest) msgFactory
//						.getRequestMessage(MessagesID.GET_SOCIAL_AVATAR);
//                    rqSocial.type = 0;
//                    rqSocial.uid = user.mUid;
//                    IResponsePackage responsePkg = aSession.getDirectMessages();
//                    
//                    IBusiness business = msgFactory.getBusiness(MessagesID.GET_SOCIAL_AVATAR);
//                    business.handleMessage(aSession, rqSocial, responsePkg);
//                    
                }
               
                
                
            } else {
                resGetUserInfo.setFailure("Tài khoản này không tồn tại!");
            }

        } catch (Throwable t) {
            resGetUserInfo.setFailure("Có lỗi xảy ra");
            
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if(resGetUserInfo != null)
                aResPkg.addMessage(resGetUserInfo);
        }

        return 1;
    }
}
