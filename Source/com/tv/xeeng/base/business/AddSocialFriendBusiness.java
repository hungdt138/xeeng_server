package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.AddSocialFriendRequest;
import com.tv.xeeng.base.protocol.messages.AddSocialFriendResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.FriendDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.CacheFriendsInfo;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class AddSocialFriendBusiness extends AbstractBusiness {
    
    private static final Logger mLog
            = LoggerContext.getLoggerFactory().getLogger(AddSocialFriendBusiness.class);
    private static final int BONUS_GOLD = 1000;
    private static final int MAX_BONUS_ALLOWED = 50000;
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Add social Friend] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        AddSocialFriendResponse resAddFriend
                = (AddSocialFriendResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            AddSocialFriendRequest rqAddFriend = (AddSocialFriendRequest) aReqMsg;
            if (rqAddFriend.friendID < 1) {
                throw new BusinessException("Không tồn tại người chơi");
            }
            
            if (rqAddFriend.friendID == aSession.getUID()) {
                throw new BusinessException("Không thể kết bạn với chính mình");
            }
            
            FriendDB friendDb = new FriendDB();
            int addResult = friendDb.addSocialFriend(aSession.getUID(), rqAddFriend.friendID, BONUS_GOLD, MAX_BONUS_ALLOWED);
            
            resAddFriend.setSuccess(ResponseCode.SUCCESS);
            resAddFriend.value = rqAddFriend.isConfirmed ? "1" : "0";

//            if (rqAddFriend.isConfirmed) {
            if (addResult == 1) {
                resAddFriend.setFailure(ResponseCode.FAILURE, "Hai bạn đã là bạn của nhau rồi");
            } else if (addResult == 2) {
                resAddFriend.setSuccess(ResponseCode.SUCCESS);
                resAddFriend.mMsg = String.format("Kết bạn thành công. Bạn được tặng %d Gold miễn phí.", BONUS_GOLD);

                // refresh cache
                CacheUserInfo.deleteCacheUserById(aSession.getUID());
            } else if (addResult == 3) {
                resAddFriend.mMsg = "Kết bạn thành công";
                /* cập nhật lại cache */
                CacheFriendsInfo cache = new CacheFriendsInfo();
                cache.refreshCache(aSession.getUID());
                cache.refreshCache(rqAddFriend.friendID);
            } else {
                ISession buddySession = aSession.getManager().findPrvChatSession(rqAddFriend.friendID);
                if (buddySession != null && buddySession.getByteProtocol() > AIOConstants.PROTOCOL_MXH) {
                    if (buddySession.getRoom() == null || (buddySession.getRoom() != null && buddySession.getRoom().isPlaying())) {
                        AddSocialFriendResponse resBuddyAddFriend
                                = (AddSocialFriendResponse) msgFactory.getResponseMessage(aReqMsg.getID());
                        resBuddyAddFriend.setSuccess(ResponseCode.SUCCESS);
                        StringBuilder sb = new StringBuilder();
                        sb.append("1").append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(Long.toString(aSession.getUID())).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(aSession.getUserName());
                        resBuddyAddFriend.value = sb.toString();
                        try {
                            buddySession.write(resBuddyAddFriend);
                        } catch (ServerException ex) {
                            mLog.error(ex.getMessage(), ex);
                        }
                    }
                }
                
            }

//            }
        } catch (BusinessException e) {
            resAddFriend.setFailure(ResponseCode.FAILURE, e.getMessage());
        } catch (Exception e) {
            resAddFriend.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu");
            mLog.error("Error", e);
        } finally {
            if ((resAddFriend != null)) {
                aResPkg.addMessage(resAddFriend);
            }
        }
        return 1;
    }
}
