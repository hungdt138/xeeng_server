package com.tv.xeeng.base.business;

import java.util.Vector;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.InviteMXHRequest;
import com.tv.xeeng.base.protocol.messages.InviteMXHResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.InviteEntity;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class InviteMXHBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(InviteMXHBusiness.class);
    private static int requestId = 1;
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        int rtn = PROCESS_FAILURE;
        MessageFactory msgFactory = aSession.getMessageFactory();
        InviteMXHResponse resInvite = (InviteMXHResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        InviteMXHResponse resBuddy = (InviteMXHResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        
        mLog.debug("[INVITE]: Catch");
        try {
            InviteMXHRequest rqInvite = (InviteMXHRequest) aReqMsg;
            long sourceID = aSession.getUID();
            long destID = rqInvite.destUid;

            ISession buddySession = aSession.getManager().findSession(destID);
            if (buddySession == null || buddySession.realDead())
            {
                throw new BusinessException("Người chơi hiện nay không online");
            }
            
            if (buddySession.isRejectInvite())
                throw new BusinessException(buddySession.getUserName() + " không chấp nhận mời chơi nữa");
            
            Vector<Room> joinedRoom = buddySession.getJoinedRooms();

            if (buddySession.getCurrentZone() > 0 && buddySession.getCurrentZone() != aSession.getCurrentZone()) {
//            if ((buddySession.getCurrentZone() != rqInvite.gameId) && (buddySession.getCurrentZone()>0)) {
                throw new BusinessException(buddySession.getUserName() + " đang ở Game khác rồi");
            } 

            if (joinedRoom.size() > 0) {
                throw new BusinessException(buddySession.getUserName() + " đang chơi ở bàn khác rồi");
            } 

            CacheUserInfo cacheUser = new CacheUserInfo();
            UserEntity destUser = cacheUser.getUserInfo(destID);
            UserEntity sourceUser = cacheUser.getUserInfo(sourceID);
            long minimumMoney = AIOConstants.TIMES * rqInvite.betMoney;
            if (destUser.money < minimumMoney)
            {
                throw new BusinessException("Người chơi không đủ tiền để tham gia");
            }

            if (sourceUser.money < minimumMoney)
            {
                throw new BusinessException("Bạn không đủ tiền để mời chơi");
            }

            //room.addWaitingSessionByID(buddySession);
            resInvite.setSuccess("");
            int inviteId = requestId++;
            
            InviteEntity entity = new InviteEntity(rqInvite.gameId, inviteId, rqInvite.betMoney, rqInvite.matchId);
            aSession.setInviteEntity(entity); //avoid conflict invitation
            aSession.write(resInvite);

            StringBuilder sb = new StringBuilder();
            sb.append(Long.toString(sourceID)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(sourceUser.mUsername).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(rqInvite.gameId)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(rqInvite.betMoney)).append(AIOConstants.SEPERATOR_BYTE_1);
//            sb.append(Integer.toString(inviteId));
            sb.append(Integer.toString(inviteId)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(rqInvite.matchId)); // Added by ThangTD
            
            resBuddy.setSuccess(sb.toString());
           
            buddySession.write(resBuddy);
        }
        catch(BusinessException be)
        {
            resInvite.setFailure(be.getMessage());
            aResPkg.addMessage(resInvite);
        }
        catch (Throwable t) {
            resInvite.setFailure("Không thực hiện mời được!");
            //aSession.setLoggedIn(false);
            aResPkg.addMessage(resInvite);
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        }
        return rtn;
    }
}
