package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.XERemoveSocialFriendRequest;
import com.tv.xeeng.base.protocol.messages.XERemoveSocialFriendResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.FriendDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.CacheFriendsInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class XERemoveSocialFriendBusiness extends AbstractBusiness {

    private static final Logger mLog
            = LoggerContext.getLoggerFactory().getLogger(XERemoveSocialFriendBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Remove Friend] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        XERemoveSocialFriendResponse res
                = (XERemoveSocialFriendResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            XERemoveSocialFriendRequest rq = (XERemoveSocialFriendRequest) aReqMsg;
            FriendDB friendDb = new FriendDB();
            boolean success = friendDb.removeSocialFriend(aSession.getUID(), rq.friendID);

            if (success) {
                res.mCode = ResponseCode.SUCCESS;
                res.setMessage("Xóa thành công.");

                /* cập nhật lại cache */
                CacheFriendsInfo cache = new CacheFriendsInfo();
                cache.refreshCache(aSession.getUID());
                cache.refreshCache(rq.friendID);
            } else {
                res.mCode = ResponseCode.FAILURE;
                res.setMessage("Xóa không thành công.");
            }

        } catch (Exception e) {
            res.mCode = ResponseCode.FAILURE;
            res.setMessage("Không thể kết nối đến cơ sở dữ liệu");
            mLog.debug("Loi: " + e.getCause());
        } finally {
            if ((res != null)) {
                aResPkg.addMessage(res);
            }
        }
        return 1;
    }
}
