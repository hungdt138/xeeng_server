package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetRichestsResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.memcached.data.XEGlobalCache;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import java.util.Vector;
import org.slf4j.Logger;

public class GetRichestsBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GetRichestsBusiness.class);
    private static final String MEMCACHED_NAME = "TopPlayers";

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[GET RICHEST]: Catch");
//        aSession.getCollectInfo().append("->GetRichest: ");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetRichestsResponse resGetRichestList = (GetRichestsResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resGetRichestList.session = aSession;
        try {
            long uid = aSession.getUID();
            mLog.debug("[GET RICHEST]: for" + uid);
            CacheUserInfo cacheUser = new CacheUserInfo();
            UserEntity currEntity = cacheUser.getUserInfo(uid);

            Object raw = XEGlobalCache.getCache(MEMCACHED_NAME);
            Vector<UserEntity> richests = null;

            if (raw != null) {
                richests = (Vector<UserEntity>) raw;
            }

            if (richests == null) {
                if (currEntity.partnerId == AIOConstants.M4V_PARTNER) {
                    UserDB db = new UserDB();
                    richests = (Vector<UserEntity>) db.getRichests(currEntity.partnerId);
                } else {
                    richests = DatabaseDriver.getRichests();
                }

                XEGlobalCache.setCache(MEMCACHED_NAME, richests, XEGlobalCache.TIMEOUT_5_MIN);
            }

            resGetRichestList.setSuccess(ResponseCode.SUCCESS, richests);

        } catch (Throwable t) {
            resGetRichestList.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu ");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resGetRichestList != null)) {
                aResPkg.addMessage(resGetRichestList);
            }
        }

        return 1;
    }
}
