package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.AddFriendRequest;
import com.tv.xeeng.base.protocol.messages.AddFriendResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.FriendDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class AddFriendBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(AddFriendBusiness.class);
    private static final int BONUS_GOLD = 1000;
    private static final int MAX_BONUS_ALLOWED = 50000;

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Add Friend]");
        MessageFactory msgFactory = aSession.getMessageFactory();
        AddFriendResponse resAddFriend = (AddFriendResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            AddFriendRequest rqAddFriend = (AddFriendRequest) aReqMsg;
            FriendDB friendDb = new FriendDB();
            int addResult = friendDb.addFriend(rqAddFriend.currID, rqAddFriend.friendID, BONUS_GOLD, MAX_BONUS_ALLOWED);
            resAddFriend.session = aSession;
            if (addResult == 0) {
                resAddFriend.setSuccess(ResponseCode.SUCCESS);
                resAddFriend.setMessage("Kết bạn thành công.");
            } else if (addResult == 2) {
                resAddFriend.setSuccess(ResponseCode.SUCCESS);
                resAddFriend.setMessage(String.format("Kết bạn thành công. Bạn được tặng %d Gold miễn phí.", BONUS_GOLD));
            } else {
                resAddFriend.setSuccess(ResponseCode.FAILURE);
                resAddFriend.setMessage("Hai bạn đã là bạn của nhau rồi.");
            }
        } catch (Exception e) {
            resAddFriend.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cư sở dữ liệu");
        } finally {
            if ((resAddFriend != null)) {
                aResPkg.addMessage(resAddFriend);
            }
        }
        return 1;
    }
}
