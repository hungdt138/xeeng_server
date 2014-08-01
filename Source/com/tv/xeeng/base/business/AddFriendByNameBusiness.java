package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.AddFriendByNameRequest;
import com.tv.xeeng.base.protocol.messages.AddFriendByNameResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.FriendDB;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class AddFriendByNameBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(AddFriendByNameBusiness.class);
    private static final int BONUS_GOLD = 1000;
    private static final int MAX_BONUS_ALLOWED = 50000;

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) {
        mLog.debug("[Add Friend] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        AddFriendByNameResponse resAddFriend = (AddFriendByNameResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            resAddFriend.session = aSession;

            AddFriendByNameRequest rqAddFriend = (AddFriendByNameRequest) aReqMsg;
            UserDB userDb = new UserDB();
            UserEntity user = userDb.getUserInfo(rqAddFriend.friendName);
            long currID = aSession.getUID();
            FriendDB friendDb = new FriendDB();
            int addResult = friendDb.addFriend(currID, user.mUid, BONUS_GOLD, MAX_BONUS_ALLOWED);
            if (user == null) {
                resAddFriend.setFailure(ResponseCode.FAILURE,
                        "Không tìm được tên bạn ấy");
            } else {
                if (addResult == 0) {
                    resAddFriend.setSuccess(ResponseCode.SUCCESS, user);
                } else {
                    resAddFriend.setFailure(ResponseCode.FAILURE,
                            "Hai bạn đã là bạn của nhau rồi");
                }
            }

        } catch (Exception e) {

            mLog.error(e.getMessage());
            resAddFriend.setFailure(ResponseCode.FAILURE,
                    "Không tìm được tên bạn ấy");
        } finally {
            if ((resAddFriend != null)) {
                aResPkg.addMessage(resAddFriend);
            }
        }
        return 1;
    }
}
