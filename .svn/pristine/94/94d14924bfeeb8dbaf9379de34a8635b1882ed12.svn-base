/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import java.util.List;

import org.json.JSONException;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.EnterRoomResponse;
import com.tv.xeeng.base.protocol.messages.EnterZoneResponse;
import com.tv.xeeng.base.protocol.messages.HamVuiRequest;
import com.tv.xeeng.base.protocol.messages.HamVuiResponse;
import com.tv.xeeng.base.protocol.messages.JoinRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.RoomDB;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.Messages;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleException;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.phom.data.PhomException;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.room.Phong;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.tienlen.data.TienLenException;
import com.tv.xeeng.game.tienlen.data.TienLenPlayer;
import com.tv.xeeng.game.tienlen.data.TienLenTable;
import com.tv.xeeng.game.tienlen.data.Utils;
import com.tv.xeeng.memcached.data.CacheMatch;
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
public class HamVuiBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(HamVuiBusiness.class);

	// private static final int TIMES = 4;
	// private static final String MSG_NOT_ENOUGH_MONEY =
	// "Bạn không đủ tiền để tham gia. Số tiền bạn có nhỏ hơn 4 lần tiền bàn";
	// private static final String MSG_FULL_TABLE =
	// "Bàn này đầy rồi, bạn thông cảm chờ nhé!";
	 public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
		mLog.debug("[Reconnect ROOM]: Catch");
		MessageFactory msgFactory = aSession.getMessageFactory();
		HamVuiResponse resReconn = (HamVuiResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		aSession.setByteProtocol(2);
		try {
			HamVuiRequest rqReconn = (HamVuiRequest) aReqMsg;
			UserEntity newUser;
			UserDB db = new UserDB();

			switch (rqReconn.type) {
			case 1: { // home
				resReconn.setSuccess();
				resReconn.isNeeded = false;
				newUser = db.getUserInfo(rqReconn.username);
				aSession.setUID(newUser.mUid);
				aSession.setUserName(newUser.mUsername);
				aSession.setLoggedIn(true);
				aSession.write(resReconn);

				return 1;
			}
			case 2: { // Vao Game
				newUser = db.getUserInfo(rqReconn.username);
				aSession.setUID(newUser.mUid);
				aSession.setUserName(newUser.mUsername);
				aSession.setLoggedIn(true);
				enterZone(aSession, rqReconn.zone, resReconn);
				return 1;
			}
			case 3: { // Vao Phong
				newUser = db.getUserInfo(rqReconn.username);
				aSession.setUID(newUser.mUid);
				aSession.setUserName(newUser.mUsername);
				aSession.setLoggedIn(true);
				enterRoom(aSession, rqReconn.phong, rqReconn.zone, resReconn);
				return 1;
			}
			case 4: {// match
				MatchEntity matchEntity = CacheMatch.getMatch(rqReconn.matchId);
				if (matchEntity == null || matchEntity.getRoom() == null) {
					newUser = db.getUserInfo(rqReconn.username);
					aSession.setUID(newUser.mUid);
					aSession.setUserName(newUser.mUsername);
					aSession.setLoggedIn(true);
					enterRoom(aSession, rqReconn.phong, rqReconn.zone, resReconn);
					return 1;
				}

				Room room = null;
				int zoneID = 0;

				long uid = rqReconn.uid;
				room = matchEntity.getRoom();
				room.join(aSession);
				zoneID = matchEntity.getZoneId();
				aSession.setCurrentZone(zoneID);
				Phong enterPhong = aSession.findZone(zoneID).getPhong(
						matchEntity.getPhongID());
				if (enterPhong != null) {
					enterPhong.enterPhong(aSession);
				}
                                CacheUserInfo cacheUser = new CacheUserInfo();
				newUser = cacheUser.getUserInfo(uid);
				if (newUser == null) {
					throw new BusinessException(Messages.NONE_EXISTS_PLAYER);
				}
				SimpleTable tb = (SimpleTable) room.getAttactmentData();
				if (!tb.isPlaying) {
					resReconn.setFailure(ResponseCode.FAILURE,
							"Ván chơi đã kết thúc");
					aSession.setUID(newUser.mUid);
					aSession.setUserName(newUser.mUsername);
					aSession.setLoggedIn(true);
					joinTable(aSession, zoneID, rqReconn.matchId);
					return 1;
				}
				boolean isNewMatch = true;
				for (SimplePlayer p : tb.getNewPlayings()) {
					if (p.id == newUser.mUid) {
						isNewMatch = false;
						break;
					}

				}
				if (isNewMatch) {
					resReconn.setFailure(ResponseCode.FAILURE,
							"Ván chơi đã kết thúc và bắt đầu ván mới.");
					aSession.write(resReconn);
					aSession.setUID(newUser.mUid);
					aSession.setUserName(newUser.mUsername);
					aSession.setLoggedIn(true);
					return 1;
				}
				switch (zoneID) {

				case ZoneID.PHOM: {
					PhomTable table = (PhomTable) tb;

					if (!table.containPlayer(uid)) {
						throw new BusinessException(Messages.NONE_EXISTS_PLAYER);
					}

					boolean isResume = true;
					String cards = "";
					PhomPlayer reconnPlayer = table.findPlayer(uid);
					reconnPlayer.isOut = false;
					reconnPlayer.isReady = true;
					reconnPlayer.isAutoPlay = false;
					reconnPlayer.setAvatarID(newUser.avatarID);
					reconnPlayer.setLevel(newUser.level);
					reconnPlayer.setCash(newUser.money);
					reconnPlayer.setUsername(newUser.mUsername);
					reconnPlayer.moneyForBet = table.firstCashBet;
					reconnPlayer.currentMatchID = rqReconn.matchId;
					if(reconnPlayer.currentSession != null) {
						Room r = reconnPlayer.currentSession.getJoinedRooms().lastElement();
						//for(Room r : reconnPlayer.currentSession.getJoinedRooms()) {
							reconnPlayer.currentSession.leftRoom(r.getRoomId());
							//r.left(aSession);
						//}
						try {
							reconnPlayer.currentSession.close();
						}catch (Throwable e) {
							e.printStackTrace();
						}
					}
					reconnPlayer.currentSession = aSession;
					reconnPlayer.currentOwner = table.ownerSession;
					//reconnPlayer.currentSession = aSession;
					cards = reconnPlayer.allPokersToString();
					room.join(aSession);

					aSession.setRoom(room);
					if (table.isPlaying) {
						resReconn.isObserve = true;
					}

					resReconn.setSuccess(ResponseCode.SUCCESS, room.getName(),
							table.firstCashBet, ZoneID.PHOM);
					resReconn.setRoomID(rqReconn.matchId);
					resReconn.setCurrentPlayersPhom(table.getPlayings(),
							table.getWaitings(), table.owner);

					resReconn.setPhomInfo(table.anCayMatTien, table.taiGuiUDen,
							table.isPlaying, isResume, table.currentPlayer.id,
							cards, table.restCards.size());
					if (table.currentPlayer.id == uid) {
						resReconn.currCard = table.getCurrPoker().toInt();
					}
					resReconn.setCapacity(table.getMaximumPlayer());
					aSession.write(resReconn);
					break;
				}
				case ZoneID.TIENLEN: {
					TienLenTable table = (TienLenTable) tb;

					if (!table.containPlayer(uid)) {
						throw new BusinessException(Messages.NONE_EXISTS_PLAYER);
					}

					TienLenPlayer reconnPlayer = table.findPlayer(uid);
					reconnPlayer.isReady = true;
					reconnPlayer.isOut = false;
					reconnPlayer.isOutGame = false;
					reconnPlayer.setAvatarID(newUser.avatarID);
					reconnPlayer.setLevel(newUser.level);
					reconnPlayer.setCash(newUser.money);
					reconnPlayer.setUsername(newUser.mUsername);
					reconnPlayer.moneyForBet = table.firstCashBet;
					reconnPlayer.currentMatchID = rqReconn.matchId;
					if(reconnPlayer.currentSession != null) {
						Room r = reconnPlayer.currentSession.getJoinedRooms().lastElement();
							reconnPlayer.currentSession.leftRoom(r.getRoomId());
							r.left(aSession);
						//}
						try {
							reconnPlayer.currentSession.close();
						}catch (Throwable e) {
							e.printStackTrace();
						}
					}
					reconnPlayer.currentSession = aSession;
					reconnPlayer.currentOwner = table.ownerSession;
					//reconnPlayer.currentSession = aSession;

					room.join(aSession);
					aSession.setRoom(room);
					if (table.isPlaying) {
						resReconn.isObserve = true;
						
						resReconn.cards = Utils.bytesToString(table.lastCards);
						if(resReconn.cards == null) resReconn.cards = ""; 
						resReconn.turn = table.getCurrentTurnID();
						resReconn.myHandCards = Utils
								.bytesToString(reconnPlayer.myHand);
						resReconn.duty = table.getDuty().getType();
					}
					
					resReconn.setSuccess(ResponseCode.SUCCESS, room.getName(),
							table.firstCashBet, ZoneID.TIENLEN);
					resReconn.setRoomID(rqReconn.matchId);
					resReconn.setCurrentPlayersTienLen(table.getPlayings(),
							table.getWaitings(), table.owner);
					resReconn.isResume = true;
					resReconn.isHidePoker = table.isHidePoker();
					aSession.write(resReconn);
					break;
				}

				default: {
					resReconn.setFailure(ResponseCode.FAILURE, "Ván chơi đang diễn ra.");
					aSession.write(resReconn);
					break;
				}
				}

				aSession.setUID(newUser.mUid);
				aSession.setUserName(newUser.mUsername);
				aSession.setLoggedIn(true);
				return 1;
			}
			default:
				break;
			}

		} catch (BusinessException ex) {
			aSession.setLoggedIn(false);
			aSession.setCommit(false);
			resReconn.setFailure(ResponseCode.FAILURE, ex.getMessage());
			aResPkg.addMessage(resReconn);
			mLog.debug(ex.getMessage());
		} catch (Throwable t) {
			aSession.setLoggedIn(false);
			aSession.setCommit(false);
			mLog.error("Process message " + aReqMsg.getID() + " error.", t);
			resReconn.setFailure(ResponseCode.FAILURE, "Lỗi xảy ra");
			aResPkg.addMessage(resReconn);
		}
		return 1;
	}

	private void enterRoom(ISession aSession, int roomID, int zoneID, HamVuiResponse res) {
		MessageFactory msgFactory = aSession.getMessageFactory();
		EnterRoomResponse resEnter = (EnterRoomResponse) msgFactory
				.getResponseMessage(MessagesID.EnterRoom);
		try {
			aSession.setCurrentZone(zoneID);
			resEnter.setZoneID(zoneID);
			Zone zone = aSession.findZone(zoneID);
			Phong enterPhong;

			if (aSession.getPhongID() != 0) {
				enterPhong = zone.getPhong(aSession.getPhongID());
				if (enterPhong == null) {
				} else {
					enterPhong.outPhong(aSession);
				}
			}
			enterPhong = zone.getPhong(roomID);
			if (enterPhong == null) {
				mLog.warn("EnterRoomBussiness [RoomID]" + roomID);
			} else {
				enterPhong.enterPhong(aSession);
			}
			resEnter.session = aSession;
			List<SimpleTable> tables = zone.dumpNewWaitingTables(roomID);
			resEnter.setSuccess(ResponseCode.SUCCESS, tables);

			aSession.write(resEnter);
		} catch (Throwable e) {
			mLog.debug("Error!");
			res.setFailure(ResponseCode.FAILURE, e.getMessage());
			try {
				aSession.write(res);
			}catch (Throwable e1) {
			}
		}
	}

	private void enterZone(ISession aSession, int zoneID, HamVuiResponse res) {
		try {
			MessageFactory msgFactory = aSession.getMessageFactory();
			EnterZoneResponse resEnter = (EnterZoneResponse) msgFactory
					.getResponseMessage(MessagesID.ENTER_ZONE);
			aSession.setCurrentZone(zoneID);
			resEnter.timeout = ZoneID.getTimeout(zoneID);
			resEnter.session = aSession;
			RoomDB roomDB = new RoomDB();
			resEnter.lstRooms = roomDB.getRooms(zoneID);
			resEnter.setSuccess(ResponseCode.SUCCESS);
			aSession.setPhongID(0);
			aSession.write(resEnter);
		} catch (Throwable e) {
			mLog.debug("Error!");
			res.setFailure(ResponseCode.FAILURE, e.getMessage());
			try {
				aSession.write(res);
			}catch (Throwable e1) {
			}
		}
	}

	private void joinTable(ISession s, int game, long matchID)
			throws PhomException, TienLenException, ServerException,
			BusinessException, JSONException, SimpleException {
		MessageFactory msgFactory = s.getMessageFactory();
		JoinRequest rqMatchJoin = (JoinRequest) msgFactory
				.getRequestMessage(MessagesID.MATCH_JOIN);
		rqMatchJoin.mMatchId = matchID;
		rqMatchJoin.uid = s.getUID();
		rqMatchJoin.zone_id = game;
		rqMatchJoin.roomID = 0;
		IResponsePackage responsePkg = s.getDirectMessages();
		IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_JOIN);
		business.handleMessage(s, rqMatchJoin, responsePkg);
	}
}
