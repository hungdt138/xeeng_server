package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.SetSocialAvatarRequest;
import com.tv.xeeng.base.protocol.messages.SetSocialAvatarResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class SetSocialAvatarBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(SetSocialAvatarBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[set social avatar] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        SetSocialAvatarResponse resSocialAvar =
                (SetSocialAvatarResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            SetSocialAvatarRequest rqSocialAvar = (SetSocialAvatarRequest) aReqMsg;
            UserDB userDb = new UserDB();
            userDb.setSocialAvatar(aSession.getUID(), rqSocialAvar.fileId, rqSocialAvar.type);
            resSocialAvar.mCode = ResponseCode.SUCCESS;
            
            CacheUserInfo cacheUserInfo = new CacheUserInfo();
            cacheUserInfo.deleteCacheUser(aSession.getUserEntity());
            
        } catch (Exception e) {
            resSocialAvar.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cư sở dữ liệu");
            mLog.error(e.getMessage(), e);
            
        } finally {
            if ((resSocialAvar != null)) {
                aResPkg.addMessage(resSocialAvar);
            }
        }
        return 1;
    }
}
