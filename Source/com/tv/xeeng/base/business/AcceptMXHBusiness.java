/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.AcceptMXHRequest;
import com.tv.xeeng.base.protocol.messages.AcceptMXHResponse;
import com.tv.xeeng.base.protocol.messages.JoinRequest;
import com.tv.xeeng.base.protocol.messages.NewRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.InviteEntity;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.room.Phong;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

/**
 *
 * @author tuanda
 */
public class AcceptMXHBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(AcceptMXHBusiness.class);
    private static String PLAYING_TABLE = "Bàn đang chơi mất rồi. Bạn chờ nhé!";

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        MessageFactory msgFactory = aSession.getMessageFactory();
        AcceptMXHResponse resAccept = (AcceptMXHResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            mLog.debug("[REPLY]: Catch");

            AcceptMXHRequest rqAccept = (AcceptMXHRequest) aReqMsg;
            long destUid = rqAccept.destUid;
            ISession destSession = aSession.getManager().findSession(destUid);

            if ((destSession == null || destSession.realDead() || !destSession.isLoggedIn()) && rqAccept.isAccept) {
                throw new BusinessException("Bạn đó đã offline rồi");
            }

            if (!rqAccept.isAccept) {
                AcceptMXHResponse resDestAccept = (AcceptMXHResponse) msgFactory.getResponseMessage(aReqMsg.getID());
                resDestAccept.setFailure(aSession.getUserName() + " đã từ chối lời mời của bạn");
                destSession.write(resDestAccept);
                return 1;
            }

            InviteEntity inviteEntity = destSession.getInviteEntity();

            if (inviteEntity != null) {
                mLog.warn(" requestId " + inviteEntity.getInviteId() + "  " + rqAccept.requestId);

            } else {
                mLog.warn(" invite entity is null ");
            }

            if (inviteEntity == null || inviteEntity.getInviteId() != rqAccept.requestId) {
                throw new BusinessException(destSession.getUserName() + " đã mời người chơi khác rồi");
            }

            mLog.warn(" gameid " + destSession.getCurrentZone() + "  " + inviteEntity.getGameId());
            if (destSession.getCurrentZone() > 0 && destSession.getCurrentZone() != inviteEntity.getGameId()) {
                throw new BusinessException(destSession.getUserName() + " đã chơi game khác rồi");
            }

            mLog.warn(" joined rooms size " + destSession.getJoinedRooms().size());
            
            Zone zone = aSession.findZone(inviteEntity.getGameId());

            //create new Table and send create new table to this user
//            Phong phongAvailable = zone.phongAvailable();

            destSession.setRoom(null);
            IResponsePackage responsePkg = destSession.getDirectMessages();
            IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_NEW);

//            NewRequest newRequest = (NewRequest) msgFactory.getRequestMessage(MessagesID.MATCH_NEW);;
//            newRequest.moneyBet = inviteEntity.getBetMoney();
//            newRequest.phongID = phongAvailable.id;
//            newRequest.tableIndex = (int) phongAvailable.avaiableTable();

//            if (newRequest.tableIndex == 0) {
//                throw new BusinessException("Không còn bàn trống nào");
//            }

//            aSession.setChatRoom(0);
//            destSession.setCurrentZone(inviteEntity.getGameId());
//            business.handleMessage(destSession, newRequest, responsePkg);

//            if (destSession.getRoom() == null) {
//                throw new BusinessException("Người chơi không thể tạo bàn");
//            }

            IBusiness joinBusiness = msgFactory.getBusiness(MessagesID.MATCH_JOIN);
            JoinRequest rqMatchJoin = (JoinRequest) msgFactory.getRequestMessage(MessagesID.MATCH_JOIN);
            // resMatchNew.setFailure(ResponseCode.FAILURE,
            // "Bàn này đã có người tạo rồi");

            destSession.setChatRoom(0);
            IResponsePackage joinResPkg = aSession.getDirectMessages();

//            rqMatchJoin.mMatchId = destSession.getRoom().getRoomId();
//            rqMatchJoin.roomID = phongAvailable.id;
            rqMatchJoin.mMatchId = rqAccept.matchId;
//            rqMatchJoin.roomID = rqAccept.matchId;
            rqMatchJoin.uid = aSession.getUID();
            rqMatchJoin.zone_id = inviteEntity.getGameId();
            aSession.setCurrentZone(inviteEntity.getGameId());
            joinBusiness.handleMessage(aSession, rqMatchJoin, joinResPkg);

            return 1;
        } catch (BusinessException be) {
            resAccept.setFailure(be.getMessage());

            aResPkg.addMessage(resAccept);

        } catch (Throwable t) {
            resAccept.setFailure("có lỗi xảy ra");
            aResPkg.addMessage(resAccept);
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        }
        return 1;
    }
}
