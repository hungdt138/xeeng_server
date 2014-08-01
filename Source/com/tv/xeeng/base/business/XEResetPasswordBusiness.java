package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.MD5;
import com.tv.xeeng.base.protocol.messages.XEResetPasswordRequest;
import com.tv.xeeng.base.protocol.messages.XEResetPasswordResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import java.sql.SQLException;
import org.slf4j.Logger;

public class XEResetPasswordBusiness extends AbstractBusiness {

    private static final Logger mLog
            = LoggerContext.getLoggerFactory().getLogger(XEResetPasswordBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Send Private Message] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        XEResetPasswordResponse res = (XEResetPasswordResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        XEResetPasswordRequest rq = (XEResetPasswordRequest) aReqMsg;

        try {
            UserDB userDB = new UserDB();
            UserEntity user = userDB.getUserInfo(rq.getUsername());

            if (user == null) {
                res.mCode = ResponseCode.FAILURE;
                res.setMessage("Người dùng không tồn tại.");
            } else {
                if (!user.cmnd.equals(rq.getCmnd()) || !user.xePhoneNumber.equals(rq.getPhone())) {
                    res.mCode = ResponseCode.FAILURE;
                    res.setMessage("Số CMND hoặc số điện thoại không chính xác.");
                } else {

                    boolean success = userDB.updateUserPassword(user.mUid, MD5.toMD5(rq.getNewPassword()));
                    if (success) {
                        res.mCode = ResponseCode.SUCCESS;
                        res.setMessage("Thay đổi mật khẩu thành công.");
                    } else {
                        res.mCode = ResponseCode.FAILURE;
                        res.setMessage("Không thay đổi được mật khẩu.");
                    }
                }
            }
        } catch (SQLException | DBException ex) {
            mLog.debug("[+] Exception", ex);

            res.mCode = ResponseCode.FAILURE;
            res.setMessage("Có lỗi xảy ra, vui lòng thử lại sau.");
        }

        aResPkg.addMessage(res);

        return 1;
    }
}
