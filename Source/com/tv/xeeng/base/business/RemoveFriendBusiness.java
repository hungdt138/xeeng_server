package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.RemoveFriendRequest;
import com.tv.xeeng.base.protocol.messages.RemoveFriendResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.databaseDriven.FriendDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class RemoveFriendBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(RemoveFriendBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Remove Friend] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        RemoveFriendResponse resRemoveFriend =
                (RemoveFriendResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            RemoveFriendRequest rqRemoveFriend = (RemoveFriendRequest) aReqMsg;
            FriendDB friendDb = new FriendDB();
            friendDb.removeFriend(rqRemoveFriend.currID, rqRemoveFriend.friendID);
            resRemoveFriend.setSuccess(ResponseCode.SUCCESS);
//            if(DatabaseDriver.isFriend(rqRemoveFriend.currID, rqRemoveFriend.friendID)) {
//	            DatabaseDriver.removeFriend(rqRemoveFriend.currID, rqRemoveFriend.friendID);
//	            DatabaseDriver.removeFriend(rqRemoveFriend.friendID, rqRemoveFriend.currID);
//	            resRemoveFriend.setSuccess(ResponseCode.SUCCESS);
//            } else {
//            	resRemoveFriend.setFailure(ResponseCode.FAILURE, "Bạn đã thêm bạn ý vào danh sách bạn chưa nhỉ?");
//            }

        } catch (Exception e) {
            resRemoveFriend.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu");
            mLog.debug("Loi: "+e.getCause());
        } finally {
            if ((resRemoveFriend != null)) {
                aResPkg.addMessage(resRemoveFriend);
            }
        }
        return 1;
    }
}
