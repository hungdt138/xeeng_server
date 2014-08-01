package com.tv.xeeng.base.business;

import java.util.logging.Level;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.EnterFriendTableRequest;
import com.tv.xeeng.base.protocol.messages.EnterFriendTableResponse;
import com.tv.xeeng.base.protocol.messages.InviteMXHRequest;
import com.tv.xeeng.base.protocol.messages.InviteMXHResponse;
import com.tv.xeeng.base.protocol.messages.InviteRequest;
import com.tv.xeeng.base.protocol.messages.InviteResponse;
import com.tv.xeeng.base.protocol.messages.JoinRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.InviteEntity;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



import java.util.Vector;

public class EnterFriendTableBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(EnterFriendTableBusiness.class);
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) {
        int rtn = PROCESS_FAILURE;
        MessageFactory msgFactory = aSession.getMessageFactory();
        EnterFriendTableResponse resInvite = (EnterFriendTableResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        
        
        mLog.debug("[INVITE]: Catch");
        try {
            EnterFriendTableRequest rqInvite = (EnterFriendTableRequest) aReqMsg;
            
            long destID = rqInvite.friendId;

            ISession buddySession = aSession.getManager().findSession(destID);
            if(buddySession == null || buddySession.realDead())
            {
                throw new BusinessException("Bạn của bạn đã ofline rồi");
            }
            
            Room room = buddySession.getRoom();
            
            if(room == null)
            {
                throw new BusinessException("Bạn của bạn đang không chơi game nào cả");
            }
            
            MatchEntity matchEntity = CacheMatch.getMatch(room.getRoomId());
            
            if(matchEntity == null)
            {
                throw new BusinessException("Bạn của bạn đang không chơi game nào cả");
            }
            
            if(room.getAttactmentData().isFullTable())
            {
                throw new BusinessException("Bàn chơi bạn của bạn đã đầy rồi");
            }
           
            
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(matchEntity.getZoneId()));
            resInvite.setSuccess(sb.toString());
            
            aSession.write(resInvite);
            
           
            aSession.setChatRoom(0);
                        
            IResponsePackage joinResPkg = aSession.getDirectMessages();
            IBusiness joinBusiness = msgFactory
						.getBusiness(MessagesID.MATCH_JOIN);
            JoinRequest rqMatchJoin = (JoinRequest) msgFactory
                            .getRequestMessage(MessagesID.MATCH_JOIN);
                        

            rqMatchJoin.mMatchId = room.getRoomId();
            rqMatchJoin.roomID = matchEntity.getPhongID();
            rqMatchJoin.uid = aSession.getUID();
            rqMatchJoin.zone_id = matchEntity.getZoneId();
            aSession.setCurrentZone(matchEntity.getZoneId());
            
            joinBusiness.handleMessage(aSession, rqMatchJoin, joinResPkg);        
               
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
