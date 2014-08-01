package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.SetSttRequest;
import com.tv.xeeng.base.protocol.messages.SetSttResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class SetSttBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(SetSttBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[set stt] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        SetSttResponse resSocialAvar =
                (SetSttResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            SetSttRequest rqSocialAvar = (SetSttRequest) aReqMsg;
            UserDB userDb = new UserDB();
            userDb.setStt(aSession.getUID(), rqSocialAvar.status);
            if(CacheUserInfo.isUseCache)
            {
                CacheUserInfo cacheUser = new CacheUserInfo();
                UserEntity entity = cacheUser.getUserInfo(aSession.getUID());
                entity.stt = rqSocialAvar.status;
                CacheUserInfo cache = new CacheUserInfo();
                cache.updateCacheUserInfo(entity);
            }
            resSocialAvar.mCode = ResponseCode.SUCCESS;
            
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
