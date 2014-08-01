package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetPersonInfoRequest;
import com.tv.xeeng.base.protocol.messages.GetPersonInfoResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.data.UserInfoEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class GetPersonInfoBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetPersonInfoBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[GET USER INFOS]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetPersonInfoResponse resGetUserInfo = (GetPersonInfoResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            GetPersonInfoRequest rqGetUserInfo = (GetPersonInfoRequest) aReqMsg;
            long uid = rqGetUserInfo.uid;
            
//            UserDB db = new UserDB();
            CacheUserInfo cache = new CacheUserInfo();
            UserEntity userEntity = cache.getFullUserInfo(uid);
                    
             //db.getUserMxhInfo(uid);
            if (userEntity != null) 
            {
                UserInfoEntity user =userEntity.usrInfoEntity;
                StringBuilder sb = new StringBuilder();
                sb.append(user.cityId).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(user.address==null ? "":user.address).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(user.jobId ).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(user.birthDay==null?"" :user.birthDay.getTime()).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(user.hobby==null ? "":user.hobby).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(user.nickSkype==null ? "":user.nickSkype).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(user.nickYahoo==null ? "":user.nickYahoo).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(user.phoneNumber==null ? "":user.phoneNumber).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(user.characterId).append(AIOConstants.SEPERATOR_BYTE_1);  
                sb.append(userEntity.money);
                resGetUserInfo.setSuccess(sb.toString());
                
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
