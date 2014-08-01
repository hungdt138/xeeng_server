package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.CheckGiftCodeRequest;
import com.tv.xeeng.base.protocol.messages.CheckGiftCodeResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

import java.sql.SQLException;

public class CheckGiftCodeBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory()
            .getLogger(CheckGiftCodeBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) {

        MessageFactory msgFactory = aSession.getMessageFactory();
        CheckGiftCodeResponse resBoc = (CheckGiftCodeResponse) msgFactory
                .getResponseMessage(aReqMsg.getID());
        try {
            CheckGiftCodeRequest rqEvent = (CheckGiftCodeRequest) aReqMsg;

//                    if()
//                    {
//                        throw new BusinessException("Gift code không đúng");
//                    }
            UserDB db = new UserDB();
            int type = 0;
            if (aSession.isMXHDevice()) {
                type = 1;
            } else if (!aSession.isMobileDevice()) {
                type = 2;
            }

            int ret = db.useGiftcode(aSession.getUID(), rqEvent.giffCode, type);
            if (ret == 0) {
                throw new BusinessException("Gift Code không tồn tại hoặc đã được sử dụng");
            } else if (ret == -1) {
                throw new BusinessException("Bạn chỉ được sử dụng 1 mã duy nhất đối với mỗi loại Gift Code.");
            }

            /* Update cache */
            UserDB userDB = new UserDB();
            UserEntity user = userDB.getUserInfo(aSession.getUID());
            CacheUserInfo cache = new CacheUserInfo();
            cache.updateCacheUserInfo(user);
            /* Update cache */

            resBoc.setSuccess(Integer.toString(ret));
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
            resBoc.setFailure("Có lỗi xảy ra.");
        } catch (BusinessException be) {
            resBoc.setFailure(be.getMessage());
        } finally {
            aResPkg.addMessage(resBoc);
        }
        return 1;
    }

}
