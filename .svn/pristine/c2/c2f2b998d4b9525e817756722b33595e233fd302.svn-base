/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.JoinRequest;
import com.tv.xeeng.base.protocol.messages.JoinResponse;
import com.tv.xeeng.base.protocol.messages.ReplyRequest;
import com.tv.xeeng.base.protocol.messages.ReplyResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.room.Phong;
import com.tv.xeeng.game.room.Room;
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
public class ReplyBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(ReplyBusiness.class);

	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {
		MessageFactory msgFactory = aSession.getMessageFactory();
		ReplyResponse resMatchReply = (ReplyResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
                
                resMatchReply.session = aSession;
		try {
			mLog.debug("[REPLY]: Catch");
			long uid = aSession.getUID();
			ReplyRequest rqMatchReply = (ReplyRequest) aReqMsg;
			long buddy_uid = rqMatchReply.buddy_uid;
			ISession buddy_session = aSession.getManager().findSession(
					buddy_uid);

			if (buddy_session != null) {
                            aSession.setReplyInvite(true);
                            
				Zone zone = aSession.findZone(buddy_session.getCurrentZone());
				Room currentRoom = zone.findRoom(rqMatchReply.mMatchId);
				if (currentRoom != null) {
					SimpleTable table = currentRoom.getAttactmentData();
					if (!table.isPlaying) {
						Phong tmpPhong = zone.getPhong(aSession.getPhongID());
						if (rqMatchReply.mIsAccept) {
							aSession.setCurrentZone(zone.getZoneId());
							make(buddy_session, aSession,
									rqMatchReply.mMatchId, uid, buddy_uid,
									aResPkg);
							if (aSession.getPhongID() != 0) {
								zone.getPhong(aSession.getPhongID()).outPhong(
										aSession);

								if (tmpPhong != null)
									tmpPhong.enterPhong(buddy_session);
								else {
									buddy_session.setPhongID(aSession
											.getPhongID());
									zone.getPhong(aSession.getPhongID())
											.enterPhong(buddy_session);
								}

							}
						} else {
						}
					} 
				} else {
					if (rqMatchReply.mIsAccept) {
						resMatchReply
								.setFailure(ResponseCode.FAILURE,
										"Bạn kia đã đặt lệnh hủy trận này rồi. Bạn chờ trận khác nhé!");
						aSession.write(resMatchReply);
					}
				}
                        aSession.setNumInvite(0);
                                
			} else {
				if (rqMatchReply.mIsAccept) {
					resMatchReply.setFailure(ResponseCode.FAILURE,
							"Không tìm lại được người mời");
					aSession.write(resMatchReply);
				}
			}

		}
		catch (Throwable t) {
			resMatchReply.setFailure(ResponseCode.FAILURE,
					"Bị lỗi " + t.toString());
			aResPkg.addMessage(resMatchReply);
			mLog.error("Process message " + aReqMsg.getID() + " error.", t);
		}
		return 1;
	}

	private void make(ISession aSession, ISession buddySession, long matchID,
			long playerID, long ownerID, IResponsePackage aResPkg) {
		MessageFactory msgFactory = aSession.getMessageFactory();
		// Feedback to Player
		JoinResponse resMatchJoin = (JoinResponse) msgFactory
				.getResponseMessage(MessagesID.MATCH_JOIN);
		resMatchJoin.isInvite = true;
		// Feedback to Room's Owner

		try {
			mLog.debug("[ACCEPT INVITE]: Catch");
			Zone bacayZone = aSession.findZone(aSession.getCurrentZone());
			Room room = bacayZone.findRoom(matchID);
//			UserDB userDb = new UserDB();
                        CacheUserInfo cacheUser = new CacheUserInfo();
			UserEntity newUser = cacheUser.getUserInfo(playerID);
			if (newUser != null) {
				long uid = newUser.mUid;

				if (room != null) {
					switch (aSession.getCurrentZone()) {

//					case ZoneID.PHOM:
//					case ZoneID.TIENLEN:
//					case ZoneID.NEW_BA_CAY:
//					case ZoneID.PIKACHU:
//                  case ZoneID.BAU_CUA_TOM_CA:
//                  case ZoneID.AILATRIEUPHU:

					default:
                    {
						IBusiness business = msgFactory
								.getBusiness(MessagesID.MATCH_JOIN);
						JoinRequest rqMatchJoin = (JoinRequest) msgFactory
								.getRequestMessage(MessagesID.MATCH_JOIN);
						rqMatchJoin.mMatchId = matchID;
						rqMatchJoin.roomID = room.phongID;
						rqMatchJoin.uid = uid;
						rqMatchJoin.zone_id = buddySession.getCurrentZone();
						business.handleMessage(buddySession, rqMatchJoin,
								aResPkg);
                        break;
                     }
					}
				}
			}

		} catch (Throwable t) {
			// resAcceptJoin.setFailure(ResponseCode.FAILURE, "Bị lỗi ");
			mLog.error("Process error.", t);
		}

	}
}
