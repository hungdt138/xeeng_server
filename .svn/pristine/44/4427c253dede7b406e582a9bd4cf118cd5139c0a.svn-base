/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.*;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaPlayer;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaTable;
import com.tv.xeeng.game.binh.data.BinhPlayer;
import com.tv.xeeng.game.binh.data.BinhTable;
import com.tv.xeeng.game.data.*;
import com.tv.xeeng.game.newbacay.data.NewBaCayPlayer;
import com.tv.xeeng.game.newbacay.data.NewBaCayTable;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.pikachu.datta.PikachuPlayer;
import com.tv.xeeng.game.pikachu.datta.PikachuTable;
import com.tv.xeeng.game.poker.data.PokerPlayer;
import com.tv.xeeng.game.poker.data.PokerTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.tienlen.data.TienLenPlayer;
import com.tv.xeeng.game.tienlen.data.TienLenTable;
import com.tv.xeeng.game.tienlen.data.Utils;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuPlayer;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuTable;
import com.tv.xeeng.game.xam.data.SamPlayer;
import com.tv.xeeng.game.xam.data.SamTable;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.*;
import org.slf4j.Logger;

import java.util.ArrayList;

/**
 *
 * @author tuanda
 */
public class JoinBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(JoinBusiness.class);
    private static final int TIMES = 4;
    private static final String MSG_NOT_ENOUGH_MONEY = "Bạn không đủ tiền để tham gia. Số tiền bạn có nhỏ hơn 4 lần tiền bàn";
    private static final String MSG_FULL_TABLE = "Bàn này đầy rồi, bạn thông cảm chờ nhé!";

    private void correctWrongTable(ISession aSession, MessageFactory msgFactory, long matchId) {
        try {
            if (aSession.getRoom() != null && aSession.getRoom().getRoomId() != matchId && matchId > 0 && aSession.getRoom().getAttactmentData() != null) {
                IResponsePackage responsePkg = aSession.getDirectMessages();
                IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_CANCEL);

                CancelRequest cancelRq = (CancelRequest) msgFactory.getRequestMessage(MessagesID.MATCH_CANCEL);
                cancelRq.mMatchId = aSession.getRoom().getRoomId();
                cancelRq.isSendMe = false;
                business.handleMessage(aSession, cancelRq, responsePkg);
            }
        } catch (Throwable ex) {
            if (ex != null) {
                mLog.error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        MessageFactory msgFactory = aSession.getMessageFactory();
        JoinResponse resMatchJoin = (JoinResponse) msgFactory.getResponseMessage(MessagesID.MATCH_JOIN);
        resMatchJoin.setSession(aSession);

        try {
            mLog.debug("[JOIN ROOM]: Catch");
            JoinRequest rqMatchJoin = (JoinRequest) aReqMsg;
            if (rqMatchJoin.roomID > 0) {
                resMatchJoin.phongID = rqMatchJoin.roomID;
            }

            correctWrongTable(aSession, msgFactory, rqMatchJoin.mMatchId);

//			else {
//				RoomDB db = new RoomDB();
//                                db.getRooms(aSession.getCurrentZone()).get(0).getId();
//			}
//			if (rqMatchJoin.zone_id >= 0) {
//				aSession.setCurrentZone(rqMatchJoin.zone_id);
//			}
//			aSession.setRoomID(rqMatchJoin.roomID); // what 'stand for?
            MatchEntity matchEntity = CacheMatch.getMatch(rqMatchJoin.mMatchId);
            aSession.setChatRoom(0);
            Room room = null;
            int zoneID = 0;

            if (matchEntity != null) {
                room = matchEntity.getRoom();
                zoneID = matchEntity.getZoneId();
                resMatchJoin.phongID = matchEntity.getPhongID();
            }

            if (room == null) {
                // retry to find by old method
                zoneID = aSession.getCurrentZone();
                Zone zone = aSession.findZone(zoneID);
                // get the current room to notify to the opponent
                room = zone.findRoom(rqMatchJoin.mMatchId);
            }

            // zone.enterRoom(rqMatchJoin.roomID);
            if (room != null) {
                mLog.debug("[JOIN ROOM] : match_id = " + rqMatchJoin.mMatchId);
                // Get user information
                // UserDB userDb = new UserDB();
                // UserEntity newUser = userDb.getUserInfo(rqMatchJoin.uid);
                CacheUserInfo cacheUser = new CacheUserInfo();
                UserEntity newUser = cacheUser.getUserInfo(aSession.getUID());

                if (newUser == null) {
                    throw new BusinessException(Messages.NONE_EXISTS_PLAYER);
                }

                // RoomDB roomDB = new RoomDB();
                // roomDB.userIn(zoneID, rqMatchJoin.roomID);
                switch (zoneID) {
                    case ZoneID.PHOM: {
                        // PhomPlayer newPlayer = new PhomPlayer(newUser.mUid);
                        // room.getAttactmentData().join(aSession, rqMatchJoin,
                        // newUser, newPlayer);
                        if (newUser != null) {
                            long moneyOfPlayer = newUser.money;
                            long uid = newUser.mUid;
                            JoinedResponse broadcastMsg = (JoinedResponse) msgFactory.getResponseMessage(MessagesID.MATCH_JOINED);
                            broadcastMsg.setSession(aSession);
                            PhomTable table = (PhomTable) room.getAttactmentData();
                            if (moneyOfPlayer < TIMES * table.firstCashBet) {
                                resMatchJoin.setFailure(ResponseCode.FAILURE, MSG_NOT_ENOUGH_MONEY);
                                aSession.write(resMatchJoin);

                                return 1;
                            }

                            if (table.containPlayer(uid)) {
                                try {
                                    if (aSession.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                                        rejoinPhom(table, aSession, newUser, rqMatchJoin.mMatchId, room);
                                    } else {
                                        resMatchJoin.setFailure(ResponseCode.FAILURE, "Bạn đã thóat khỏi bàn. Nên không thể quay lại!");
                                        aSession.leftRoom(rqMatchJoin.mMatchId);
                                        aSession.write(resMatchJoin);

                                        return 1;
                                    }
                                } catch (Throwable e) {
                                    resMatchJoin.setFailure(ResponseCode.FAILURE, "Bạn đã thóat khỏi bàn. Nên không thể quay lại!");
                                    aSession.leftRoom(rqMatchJoin.mMatchId);
                                    aSession.write(resMatchJoin);
                                    return 1;
                                }

                                return 1;
                            }

                            if (table.isFullTable()) {
                                resMatchJoin.setFailure(ResponseCode.FAILURE, "Bàn này đầy rồi, bạn thông cảm chờ nhé!");
                                aSession.write(resMatchJoin);

                                return 1;
                            }

                            // mLog.debug("********came != join_FULL");
                            boolean isResume = false;
                            String cards = "";
                            PhomPlayer newPlayer = new PhomPlayer(uid);
                            newPlayer.setAvatarID(newUser.avatarID);
                            newPlayer.setLevel(newUser.level);
                            newPlayer.setCash(newUser.money);
                            newPlayer.setUsername(newUser.mUsername);
                            newPlayer.moneyForBet = table.firstCashBet;
                            newPlayer.currentMatchID = rqMatchJoin.mMatchId;
                            newPlayer.currentSession = aSession;
                            newPlayer.currentOwner = table.ownerSession;
                            
                            table.join(newPlayer);
                            room.join(aSession);
                            aSession.setRoom(room);
                            
                            if (table.isPlaying) {
                                resMatchJoin.isObserve = true;
                                resMatchJoin.duty = table.duty.getType();
                                if (table.getCurrPlayPoker() != null) {
                                    resMatchJoin.currCard = table.getCurrPlayPoker().toInt();
                                }
                            }

                            broadcastMsg.setSuccess(ResponseCode.SUCCESS, uid, newUser.mUsername, newUser.level, newUser.avatarID, newUser.money, ZoneID.PHOM);
                            resMatchJoin.setSuccess(ResponseCode.SUCCESS, room.getName(), table.firstCashBet, aSession.getCurrentZone());
                            resMatchJoin.setRoomID(rqMatchJoin.mMatchId);
                            resMatchJoin.setCurrentPlayersPhom(table.getPlayings(), table.getWaitings(), table.owner);

                            resMatchJoin.setPhomInfo(table.anCayMatTien, table.taiGuiUDen, table.isPlaying, isResume, table.currentPlayer.id, cards, table.restCards.size());
                            resMatchJoin.setCapacity(table.getMaximumPlayer());

                            // send broadcast msg to friends
                            table.broadcastMsg(broadcastMsg, table.getNewPlayings(), table.getNewWaitings(), newPlayer, false);
                            // room.broadcastMessage(broadcastMsg, aSession, false);

                            // fix dead session
                            // Feedback to Player
                            aSession.write(resMatchJoin);
                        }
                        break;
                    }
                    case ZoneID.TIENLEN: {
                        if (newUser != null) {
                            long moneyOfPlayer = newUser.money;
                            long uid = newUser.mUid;
                            JoinedResponse broadcastMsg = (JoinedResponse) msgFactory.getResponseMessage(MessagesID.MATCH_JOINED);
                            broadcastMsg.setSession(aSession);
                            TienLenTable table = (TienLenTable) room.getAttactmentData();
                            // Check money of player
                            if (moneyOfPlayer < TIMES * table.firstCashBet) {
                                resMatchJoin.setFailure(ResponseCode.FAILURE, MSG_NOT_ENOUGH_MONEY);
                                aSession.write(resMatchJoin);
                                return 1;
                            }

                            // Bấm vào bàn n lần!!!
                            if (table.containPlayer(uid)) {
                                try {
                                    if (aSession.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                                        reJoinTienLen(table, aSession, newUser, rqMatchJoin.mMatchId, room);
                                    } else {
                                        resMatchJoin.setFailure(ResponseCode.FAILURE, "Bạn vẫn đang còn trong bàn, vui lòng chờ hết ván nhé!");
                                        aSession.write(resMatchJoin);
                                        return 1;
                                    }
                                } catch (Throwable e) {
                                    resMatchJoin.setFailure(ResponseCode.FAILURE, "Bạn vẫn đang còn trong bàn, vui lòng chờ hết ván nhé!");
                                    aSession.write(resMatchJoin);
                                    return 1;
                                }

                                return 1;
                            }

                            if ((table.getPlayings().size() + table.getWaitings().size()) >= table.getMaximumPlayer()) {
                                resMatchJoin.setFailure(ResponseCode.FAILURE, "Bàn này đầy rồi, bạn thông cảm chờ nhé!");
                                aSession.write(resMatchJoin);

                                return 1;
                            }

                            TienLenPlayer newPlayer = new TienLenPlayer(uid);
                            newPlayer.setAvatarID(newUser.avatarID);
                            newPlayer.setLevel(newUser.level);
                            newPlayer.setCash(newUser.money);
                            newPlayer.setUsername(newUser.mUsername);
                            newPlayer.moneyForBet = table.firstCashBet;
                            newPlayer.currentMatchID = rqMatchJoin.mMatchId;
                            newPlayer.currentSession = aSession;
                            newPlayer.setCurrentOwner(table.ownerSession);
                            
                            table.join(newPlayer);
                            room.join(aSession);
                            aSession.setRoom(room);

                            resMatchJoin.isPlaying = table.isPlaying;
                            if (table.isPlaying) {
                                if (table.getDuty() != null) {
                                    resMatchJoin.duty = table.getDuty().getType();
                                }
                                resMatchJoin.isObserve = true;
                                resMatchJoin.cards = Utils.bytesToString(table.lastCards);
                                resMatchJoin.turn = table.getCurrentTurnID();
                            }
                            
                            broadcastMsg.setSuccess(ResponseCode.SUCCESS, uid, newUser.mUsername, newUser.level, newUser.avatarID, newUser.money, ZoneID.TIENLEN);

                            // join's values
                            resMatchJoin.setSuccess(ResponseCode.SUCCESS, room.getName(), table.firstCashBet, aSession.getCurrentZone());
                            resMatchJoin.setRoomID(rqMatchJoin.mMatchId);
                            resMatchJoin.setCurrentPlayersTienLen(table.getPlayings(), table.getWaitings(), table.owner);
                            resMatchJoin.isHidePoker = table.isHidePoker();

                            // send broadcast msg to friends
                            table.broadcastMsg(broadcastMsg, table.getNewPlayings(), table.getNewWaitings(), newPlayer, false);
                            // room.broadcastMessage(broadcastMsg, aSession, false);

                            aSession.write(resMatchJoin);
                        }
                        
                        break;
                    }

                    case ZoneID.SAM: {
                        if (newUser != null) {
                            long moneyOfPlayer = newUser.money;
                            long uid = newUser.mUid;
                            
                            JoinedResponse broadcastMsg = (JoinedResponse) msgFactory.getResponseMessage(MessagesID.MATCH_JOINED);
                            broadcastMsg.setSession(aSession);
                            SamTable table = (SamTable) room.getAttactmentData();
                            
                            // Check money of player
                            if (moneyOfPlayer < TIMES * table.firstCashBet) {
                                resMatchJoin.setFailure(ResponseCode.FAILURE, MSG_NOT_ENOUGH_MONEY);
                                aSession.write(resMatchJoin);
                                return 1;
                            }

                            // Bấm vào bàn n lần!!!
                            if (table.containPlayer(uid)) {
                                try {
                                    if (aSession.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                                        reJoinSam(table, aSession, newUser, rqMatchJoin.mMatchId, room);
                                    } else {
                                        resMatchJoin.setFailure(ResponseCode.FAILURE, "Bạn vẫn đang còn trong bàn, vui lòng chờ hết ván nhé!");
                                        aSession.write(resMatchJoin);
                                        return 1;
                                    }
                                } catch (Throwable e) {
                                    resMatchJoin.setFailure(ResponseCode.FAILURE, "Bạn vẫn đang còn trong bàn, vui lòng chờ hết ván nhé!");
                                    aSession.write(resMatchJoin);
                                    return 1;
                                }

                                return 1;
                            }

                            if (table.isFullTable()) {
                                resMatchJoin.setFailure(ResponseCode.FAILURE, "Bàn này đầy rồi, bạn thông cảm chờ nhé!");
                                aSession.write(resMatchJoin);

                                return 1;
                            }

                            SamPlayer newPlayer = new SamPlayer(uid);
                            newPlayer.setAvatarID(newUser.avatarID);
                            newPlayer.setLevel(newUser.level);
                            newPlayer.setCash(newUser.money);
                            newPlayer.setUsername(newUser.mUsername);
                            newPlayer.moneyForBet = table.firstCashBet;
                            newPlayer.currentMatchID = rqMatchJoin.mMatchId;
                            newPlayer.currentSession = aSession;
                            newPlayer.currentOwner = table.ownerSession;
                            newPlayer.setTable(table);
                            
                            table.join(newPlayer);
                            room.join(aSession);
                            aSession.setRoom(room);

                            resMatchJoin.isPlaying = table.isPlaying;
                            if (table.isPlaying) {
                                resMatchJoin.isObserve = true;
                                resMatchJoin.cards = table.lastCardToString();
                                resMatchJoin.turn = table.getCurrID();
                            }
                            
                            broadcastMsg.setSuccess(ResponseCode.SUCCESS, uid, newUser.mUsername, newUser.level, newUser.avatarID, newUser.money, ZoneID.SAM);

                            // join's values
                            resMatchJoin.setSuccess(ResponseCode.SUCCESS, room.getName(), table.firstCashBet, aSession.getCurrentZone());
                            resMatchJoin.setRoomID(rqMatchJoin.mMatchId);
                            resMatchJoin.setCurrentPlayersSam(table.getPlayings(), table.getWaitings(), table.owner);
                            resMatchJoin.isHidePoker = false;

                            // send broadcast msg to friends
                            table.broadcastMsg(broadcastMsg, table.getNewPlayings(), table.getNewWaitings(), newPlayer, false);
                            // room.broadcastMessage(broadcastMsg, aSession, false);

                            aSession.write(resMatchJoin);
                        }
                        
                        break;
                    }
                    
                    case ZoneID.NEW_BA_CAY: {
                        long moneyOfPlayer = newUser.money;
                        long uid = newUser.mUid;
                        JoinedResponse broadcastMsg = (JoinedResponse) msgFactory.getResponseMessage(MessagesID.MATCH_JOINED);
                        broadcastMsg.setSession(aSession);
                        NewBaCayTable table = (NewBaCayTable) room.getAttactmentData();
                        // Check money of player
                        if (moneyOfPlayer < TIMES * table.firstCashBet) {
                            throw new BusinessException(MSG_NOT_ENOUGH_MONEY);
                        }

                        if (table.isFullTable()) {
                            throw new BusinessException(MSG_FULL_TABLE);
                        }
                        // Bấm vào bàn n lần!!!
                        if (table.containPlayer(uid)) {
                            throw new BusinessException("Bạn vẫn đang còn trong bàn, vui lòng chờ hết ván nhé!");
                        }

                        NewBaCayPlayer newPlayer = new NewBaCayPlayer(uid);
                        newPlayer.avatarID = newUser.avatarID;
                        newPlayer.level = newUser.level;
                        newPlayer.cash = newUser.money;
                        newPlayer.username = newUser.mUsername;
                        newPlayer.moneyForBet = table.firstCashBet;
                        newPlayer.currentMatchID = rqMatchJoin.mMatchId;
                        newPlayer.currentSession = aSession;
                        // newPlayer.setCurrentOwner(table.ownerSession);
                        table.join(newPlayer);
                        room.join(aSession);
                        aSession.setRoom(room);

                        broadcastMsg.setSuccess(ResponseCode.SUCCESS, uid, newUser.mUsername, newUser.level, newUser.avatarID, newUser.money, ZoneID.NEW_BA_CAY);

                        // join's values
                        resMatchJoin.setSuccess(ResponseCode.SUCCESS, room.getName(), table.firstCashBet, aSession.getCurrentZone());
                        resMatchJoin.isPlaying = table.isPlaying;
                        resMatchJoin.setRoomID(rqMatchJoin.mMatchId);
                        resMatchJoin.setCurrentPlayers(table.getPlayings(), table.getWaitings(), table.owner);

                        resMatchJoin.roomOwner = table.owner;
                        // send broadcast msg to friends
                        table.broadcastMsg(broadcastMsg, table.getNewPlayings(), table.getNewWaitings(), newPlayer, false);
                        // room.broadcastMessage(broadcastMsg, aSession, false);

                        aSession.write(resMatchJoin);
                    }
                    break;

                    case ZoneID.POKER: {
                        if (newUser != null) {
                    		// Người chơi có trạng thái mặc định là quan sát (waiting)
                            // do đó việc join bàn chơi không đòi hỏi bất kỳ điều kiện nào
                            long uid = newUser.mUid;
                            JoinedResponse broadcastMsg = (JoinedResponse) msgFactory.getResponseMessage(MessagesID.MATCH_JOINED);
                            broadcastMsg.setSession(aSession);
                            PokerTable table = (PokerTable) room.getAttactmentData();

                            // Bấm vào bàn n lần
                            if (table.containPlayer(uid)) {
                                try {
                                    if (aSession.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                                        // rejoin
                                    } else {
                                        resMatchJoin.setFailure(ResponseCode.FAILURE, "Bạn đã thoát khỏi bàn nên không thể quay lại!");
                                        aSession.leftRoom(rqMatchJoin.mMatchId);
                                        aSession.write(resMatchJoin);

                                        return 1;
                                    }
                                } catch (Throwable e) {
                                    resMatchJoin.setFailure(ResponseCode.FAILURE, "Bạn đã thoát khỏi bàn nên không thể quay lại!");
                                    aSession.leftRoom(rqMatchJoin.mMatchId);
                                    aSession.write(resMatchJoin);

                                    return 1;
                                }
                            }

                            // Khởi tạo người chơi Poker
                            PokerPlayer newPlayer = new PokerPlayer(uid);
                            newPlayer.avatarID = newUser.avatarID;
                            newPlayer.level = newUser.level;
                            newPlayer.cash = newUser.money;
                            newPlayer.username = newUser.mUsername;
                            newPlayer.moneyForBet = table.firstCashBet;
                            newPlayer.currentMatchID = rqMatchJoin.mMatchId;
                            newPlayer.currentSession = aSession;
                            newPlayer.currentOwner = table.ownerSession;
                            table.join(newPlayer);
                            room.join(aSession);
                            aSession.setRoom(room);

                    		// Người chơi mới vào phòng mặc định sẽ có vai trò là người quan sát
                            // do đó không cần gửi broadcast message
                            // chỉ cần gửi phản hồi cho người chơi hiện tại
                            resMatchJoin.isObserve = true;
                            resMatchJoin.setSuccess(ResponseCode.SUCCESS, room.getName(), table.firstCashBet, aSession.getCurrentZone());
                            resMatchJoin.setRoomID(rqMatchJoin.mMatchId);
                            resMatchJoin.setCapacity(table.getMaximumPlayer());
                            aSession.write(resMatchJoin);
                        }
                    }
                    break;

                    case ZoneID.BAU_CUA_TOM_CA: {
                        long moneyOfPlayer = newUser.money;
                        long uid = newUser.mUid;
                        JoinedResponse broadcastMsg = (JoinedResponse) msgFactory.getResponseMessage(MessagesID.MATCH_JOINED);
                        broadcastMsg.setSession(aSession);
                        BauCuaTomCaTable table = (BauCuaTomCaTable) room.getAttactmentData();
                        // Check money of player
                        if (moneyOfPlayer < TIMES * table.firstCashBet) {
                            throw new BusinessException(MSG_NOT_ENOUGH_MONEY);
                        }

                        if (table.isFullTable()) {
                            throw new BusinessException(MSG_FULL_TABLE);
                        }
                        // Bấm vào bàn n lần!!!
                        if (table.containPlayer(uid)) {
                            throw new BusinessException("Bạn vẫn đang còn trong bàn, vui lòng chờ hết ván nhé!");
                        }

                        // in cases
                        BauCuaTomCaPlayer newPlayer = new BauCuaTomCaPlayer(uid);
                        newPlayer.avatarID = newUser.avatarID;
                        newPlayer.level = newUser.level;
                        newPlayer.cash = newUser.money;
                        newPlayer.username = newUser.mUsername;
                        newPlayer.moneyForBet = table.firstCashBet;
                        newPlayer.currentMatchID = rqMatchJoin.mMatchId;
                        newPlayer.currentSession = aSession;
                        // newPlayer.setCurrentOwner(table.ownerSession);
                        table.join(newPlayer);
                        room.join(aSession);
                        aSession.setRoom(room);
                        // in cases

                        broadcastMsg.setSuccess(ResponseCode.SUCCESS, uid, newUser.mUsername, newUser.level, newUser.avatarID, newUser.money, ZoneID.BAU_CUA_TOM_CA);

                        // join's values
                        resMatchJoin.setSuccess(ResponseCode.SUCCESS, room.getName(), table.firstCashBet, aSession.getCurrentZone());
                        resMatchJoin.isPlaying = table.isPlaying;
                        resMatchJoin.setRoomID(rqMatchJoin.mMatchId);
                        resMatchJoin.setCurrentPlayers(table.getPlayings(), table.getWaitings(), table.owner);

                        // send broadcast msg to friends
                        aSession.setRoom(room);

                        // room.broadcastMessage(broadcastMsg, aSession, false);
                        table.broadcastMsg(broadcastMsg, table.getNewPlayings(), table.getNewWaitings(), newPlayer, false);
                        aSession.write(resMatchJoin);
                    }
                    break;

                    case ZoneID.PIKACHU: {
                        long moneyOfPlayer = newUser.money;
                        long uid = newUser.mUid;
                        JoinedResponse broadcastMsg = (JoinedResponse) msgFactory.getResponseMessage(MessagesID.MATCH_JOINED);
                        PikachuTable table = (PikachuTable) room.getAttactmentData();
                        broadcastMsg.setSession(aSession);
                        // Check money of player
                        if (moneyOfPlayer < 2 * table.firstCashBet) {
                            throw new BusinessException(MSG_NOT_ENOUGH_MONEY);
                        }

                        if (table.isFullTable()) {
                            throw new BusinessException(MSG_FULL_TABLE);
                        }

                        if (table.containPlayer(uid)) {
                            try {
                                if (aSession.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                                    rejoinPikachu(table, aSession, newUser,
                                            rqMatchJoin.mMatchId, room);
                                } else {
                                    resMatchJoin.setFailure(ResponseCode.FAILURE, "Bạn đã thóat khỏi bàn. Nên không thể quay lại!");
                                    aSession.leftRoom(rqMatchJoin.mMatchId);
                                    aSession.write(resMatchJoin);

                                    return 1;
                                }
                            } catch (Throwable e) {
                                resMatchJoin.setFailure(ResponseCode.FAILURE, "Bạn đã thóat khỏi bàn. Nên không thể quay lại!");
                                aSession.leftRoom(rqMatchJoin.mMatchId);
                                aSession.write(resMatchJoin);
                                return 1;
                            }

                            return 1;

                        }

                        // in cases
                        PikachuPlayer newPlayer = new PikachuPlayer(uid);

                        newPlayer.avatarID = newUser.avatarID;
                        newPlayer.level = newUser.level;
                        newPlayer.cash = newUser.money;
                        newPlayer.username = newUser.mUsername;
                        newPlayer.moneyForBet = table.firstCashBet;
                        newPlayer.currentMatchID = rqMatchJoin.mMatchId;
                        newPlayer.currentSession = aSession;
                        // newPlayer.setCurrentOwner(table.ownerSession);
                        table.join(newPlayer);
                        room.join(aSession);
                        aSession.setRoom(room);

                        broadcastMsg.setSuccess(ResponseCode.SUCCESS, uid, newUser.mUsername, newUser.level, newUser.avatarID, newUser.money, ZoneID.PIKACHU);

                        // join's values
                        resMatchJoin.setSuccess(ResponseCode.SUCCESS, room.getName(), table.firstCashBet, aSession.getCurrentZone());
                        resMatchJoin.isPlaying = table.isPlaying;
                        resMatchJoin.setRoomID(rqMatchJoin.mMatchId);

//                      resMatchJoin.setCurrentPlayers(table.waitings, null, table.owner);
                        
                        resMatchJoin.setCurrentPlayers(table.playings, table.waitings, table.owner);

                        // send broadcast msg to friends
                        table.broadcastMsg(broadcastMsg, table.getNewPlayings(), new ArrayList<SimplePlayer>(), newPlayer, false);
                        // room.broadcastMessage(broadcastMsg, aSession, false);

                        aSession.write(resMatchJoin);
                    }
                    break;

                    case ZoneID.AILATRIEUPHU: {
                        SimpleTable sTable = room.getAttactmentData();
                        TrieuPhuPlayer newPlayer = new TrieuPhuPlayer(newUser.mUid, sTable.firstCashBet, false);
                        newPlayer.table = (TrieuPhuTable) sTable;
                        rqMatchJoin.phongId = matchEntity.getPhongID();
                        sTable.join(aSession, rqMatchJoin, newUser, newPlayer);

                        if (newPlayer.table.isPlaying) {
                            newPlayer.table.sendResultForViewer(aSession);
                        }
                    }
                    break;

                    case ZoneID.BINH: {
                        // PhomPlayer newPlayer = new PhomPlayer(newUser.mUid);
                        // room.getAttactmentData().join(aSession, rqMatchJoin,
                        // newUser, newPlayer);
                        if (newUser != null) {
                            long moneyOfPlayer = newUser.money;
                            long uid = newUser.mUid;
                            JoinedResponse broadcastMsg = (JoinedResponse) msgFactory.getResponseMessage(MessagesID.MATCH_JOINED);
                            broadcastMsg.setSession(aSession);
                            BinhTable table = (BinhTable) room.getAttactmentData();
                            if (moneyOfPlayer < TIMES * table.firstCashBet) {
                                resMatchJoin.setFailure(ResponseCode.FAILURE, MSG_NOT_ENOUGH_MONEY);
                                aSession.write(resMatchJoin);

                                return 1;
                            }

                            if (table.containsPlayer(uid)) {
                                try {
                                    if (aSession.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                                        rejoinBinh(table, aSession, newUser, rqMatchJoin.mMatchId, room);
                                    } else {
                                        resMatchJoin.setFailure(ResponseCode.FAILURE, "Bạn đã thóat khỏi bàn. Nên không thể quay lại!");
                                        aSession.leftRoom(rqMatchJoin.mMatchId);
                                        aSession.write(resMatchJoin);

                                        return 1;
                                    }
                                } catch (Throwable e) {
                                    resMatchJoin.setFailure(ResponseCode.FAILURE, "Bạn đã thóat khỏi bàn. Nên không thể quay lại!");
                                    aSession.leftRoom(rqMatchJoin.mMatchId);
                                    aSession.write(resMatchJoin);
                                    return 1;
                                }

                                return 1;
                            }

                            if (table.isFullTable()) {
                                resMatchJoin.setFailure(ResponseCode.FAILURE, "Bàn này đầy rồi, bạn thông cảm chờ nhé!");
                                aSession.write(resMatchJoin);

                                return 1;
                            }

                            boolean isResume = false;
                            String cards = "";
                            BinhPlayer newPlayer = new BinhPlayer(uid);
                            newPlayer.setUserAvatarId(newUser.avatarID);
                            newPlayer.setUserLevel(newUser.level);
                            newPlayer.setUserCash(newUser.money);
                            newPlayer.setUsername(newUser.mUsername);
                            newPlayer.moneyForBet = table.firstCashBet;
                            newPlayer.currentMatchID = rqMatchJoin.mMatchId;
                            newPlayer.currentSession = aSession;
                            newPlayer.currentOwner = table.ownerSession;

                            table.join(newPlayer);
                            room.join(aSession);
                            aSession.setRoom(room);

                            if (table.isPlaying) {
                                resMatchJoin.isObserve = true;
//                                resMatchJoin.duty = table.duty.getType();
//                                if (table.getCurrPlayPoker() != null) {
//                                    resMatchJoin.currCard = table.getCurrPlayPoker().toInt();
//                                }
                            }

                            broadcastMsg.setSuccess(ResponseCode.SUCCESS, uid, newUser.mUsername, newUser.level, newUser.avatarID, newUser.money, ZoneID.BINH);
                            resMatchJoin.setSuccess(ResponseCode.SUCCESS, room.getName(), table.firstCashBet, aSession.getCurrentZone());
                            resMatchJoin.setRoomID(rqMatchJoin.mMatchId);
                            resMatchJoin.setCapacity(table.getMaximumPlayer());

                            table.broadcastMsg(broadcastMsg, table.getNewPlayings(), table.getNewWaitings(), newPlayer, false);

                            aSession.write(resMatchJoin);
                        }
                        break;
                    }

                    default:
                        break;
                }

            } else { // send back only player
                resMatchJoin.setFailure(ResponseCode.FAILURE, "Bàn đã bị hủy!");

                aResPkg.addMessage(resMatchJoin);
            }
        } catch (BusinessException ex) {
            resMatchJoin.setFailure(ResponseCode.FAILURE, ex.getMessage());
            aSession.write(resMatchJoin);
            mLog.debug(ex.getMessage());
        } catch (Throwable t) {
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
            resMatchJoin.setFailure(ResponseCode.FAILURE, "Lỗi xảy ra");
            aSession.write(resMatchJoin);
        }
        return 1;
    }

    private void rejoinPhom(PhomTable table, ISession aSession, UserEntity newUser, long matchId, Room room) throws SimpleException, ServerException {
        MessageFactory msgFactory = aSession.getMessageFactory();
        ReconnectResponse resReconn = (ReconnectResponse) msgFactory.getResponseMessage(MessagesID.RECONNECT);

        boolean isResume = true;
        String cards;
        PhomPlayer reconnPlayer = table.findPlayer(newUser.mUid);
        if (reconnPlayer == null) {
            mLog.debug("Error:  Can rejoin Phom");
            return;
        }
        if (table.currentPlayer.id == newUser.mUid) {
            table.setLastActivated(System.currentTimeMillis());
            table.setCurrentTimeOut(25000);
        }
        reconnPlayer.isOut = false;
        reconnPlayer.isAutoPlay = false;
        reconnPlayer.isObserve = false;
        reconnPlayer.setAvatarID(newUser.avatarID);
        reconnPlayer.setLevel(newUser.level);
        reconnPlayer.setCash(newUser.money);
        reconnPlayer.setUsername(newUser.mUsername);
        reconnPlayer.moneyForBet = table.firstCashBet;
        reconnPlayer.currentMatchID = matchId;
        if ((reconnPlayer.currentSession != null) && (reconnPlayer.currentSession.getID() != null)) {
            if (reconnPlayer.currentSession.getID().compareTo(aSession.getID()) != 0) {
                Room r = reconnPlayer.currentSession.getJoinedRooms().lastElement();
                // for(Room r : reconnPlayer.currentSession.getJoinedRooms()) {
                reconnPlayer.currentSession.leftRoom(r.getRoomId());
                r.left(aSession);
                // }
                try {
                    reconnPlayer.currentSession.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        reconnPlayer.currentSession = aSession;
        reconnPlayer.currentOwner = table.ownerSession;
        // reconnPlayer.currentSession = aSession;
        cards = reconnPlayer.allPokersToString();
        room.join(aSession);

        aSession.setRoom(room);
        if (table.isPlaying) {
            resReconn.isObserve = true;
            resReconn.isPlaying = true;
        }

        resReconn.setSuccess(ResponseCode.SUCCESS, room.getName(), table.firstCashBet, ZoneID.PHOM);
        resReconn.setRoomID(matchId);
        resReconn.setCurrentPlayersPhom(table.getPlayings(), table.getWaitings(), table.owner);

        resReconn.setPhomInfo(table.anCayMatTien, table.taiGuiUDen, table.isPlaying, isResume, table.currentPlayer.id, cards, table.restCards.size());
        if (table.currentPlayer.id == newUser.mUid) {
            if (table.getCurrPlayPoker() != null) {
                resReconn.currCard = table.getCurrPlayPoker().toInt();
            }
        }

        resReconn.setCapacity(table.getMaximumPlayer());
        aSession.write(resReconn);
    }

    private void reJoinTienLen(TienLenTable table, ISession aSession, UserEntity newUser, long matchId, Room room) throws SimpleException, ServerException {
        MessageFactory msgFactory = aSession.getMessageFactory();
        ReconnectResponse resReconn = (ReconnectResponse) msgFactory.getResponseMessage(MessagesID.RECONNECT);

        if (table.getCurrPlayer().id == newUser.mUid) {
            table.setLastActivated(System.currentTimeMillis());
            table.setCurrentTimeOut(25000);
        }
        
        TienLenPlayer reconnPlayer = table.findPlayer(newUser.mUid);
        reconnPlayer.isReady = true;
        reconnPlayer.isObserve = false;
        reconnPlayer.isOut = false;
        reconnPlayer.isOutGame = false;
        reconnPlayer.setAvatarID(newUser.avatarID);
        reconnPlayer.setLevel(newUser.level);
        reconnPlayer.setCash(newUser.money);
        reconnPlayer.setUsername(newUser.mUsername);
        reconnPlayer.moneyForBet = table.firstCashBet;
        reconnPlayer.currentMatchID = matchId;

        if ((reconnPlayer.currentSession != null) && (reconnPlayer.currentSession.getID() != null)) {
            if (reconnPlayer.currentSession.getID().compareTo(aSession.getID()) != 0) {
                Room r = reconnPlayer.currentSession.getJoinedRooms().lastElement();
                reconnPlayer.currentSession.leftRoom(r.getRoomId());
                r.left(aSession);
                // }
                try {
                    reconnPlayer.currentSession.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        reconnPlayer.currentSession = aSession;
        reconnPlayer.currentOwner = table.ownerSession;
        // reconnPlayer.currentSession = aSession;

        room.join(aSession);
        aSession.setRoom(room);
        if (table.isPlaying) {
            resReconn.isObserve = true;
            resReconn.isPlaying = true;

            resReconn.cards = Utils.bytesToString(table.lastCards);
            if (resReconn.cards == null) {
                resReconn.cards = "";
            }

            resReconn.turn = table.getCurrentTurnID();
            resReconn.myHandCards = Utils.bytesToString(reconnPlayer.myHand);
            resReconn.duty = table.getDuty().getType();
            resReconn.currUserHasDuty = table.getDuty().getCurrDutyPlayerId();
        }

        resReconn.setSuccess(ResponseCode.SUCCESS, room.getName(), table.firstCashBet, ZoneID.TIENLEN);
        resReconn.setRoomID(matchId);
        resReconn.setCurrentPlayersTienLen(table.getPlayings(), table.getWaitings(), table.owner);
        resReconn.isResume = true;
        resReconn.isHidePoker = table.isHidePoker();
        aSession.write(resReconn);
    }

    // Added by ThangTD
    private void reJoinSam(SamTable table, ISession aSession, UserEntity newUser, long matchId, Room room) throws SimpleException, ServerException {
        MessageFactory msgFactory = aSession.getMessageFactory();
        ReconnectResponse resReconn = (ReconnectResponse) msgFactory.getResponseMessage(MessagesID.RECONNECT);

        if (table.getCurrPlayer().id == newUser.mUid) {
            table.setLastActivated(System.currentTimeMillis());
            table.setCurrentTimeOut(25000);
        }
        
        SamPlayer reconnPlayer = table.findPlayer(newUser.mUid);
        reconnPlayer.isReady = true;
        reconnPlayer.isObserve = false;
        reconnPlayer.isOut = false;
        reconnPlayer.setAvatarID(newUser.avatarID);
        reconnPlayer.setLevel(newUser.level);
        reconnPlayer.setCash(newUser.money);
        reconnPlayer.setUsername(newUser.mUsername);
        reconnPlayer.moneyForBet = table.firstCashBet;
        reconnPlayer.currentMatchID = matchId;

        if ((reconnPlayer.currentSession != null) && (reconnPlayer.currentSession.getID() != null)) {
            if (reconnPlayer.currentSession.getID().compareTo(aSession.getID()) != 0) {
                Room r = reconnPlayer.currentSession.getJoinedRooms().lastElement();
                reconnPlayer.currentSession.leftRoom(r.getRoomId());
                r.left(aSession);
                // }
                try {
                    reconnPlayer.currentSession.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        reconnPlayer.currentSession = aSession;
        reconnPlayer.currentOwner = table.ownerSession;
        // reconnPlayer.currentSession = aSession;

        room.join(aSession);
        aSession.setRoom(room);
        if (table.isPlaying) {
            resReconn.isObserve = true;
            resReconn.isPlaying = true;

            resReconn.cards = table.lastCardToString();
            if (resReconn.cards == null) {
                resReconn.cards = "";
            }

            resReconn.turn = table.getCurrID();
            resReconn.myHandCards = reconnPlayer.cardsToString();
        }

        resReconn.setSuccess(ResponseCode.SUCCESS, room.getName(), table.firstCashBet, ZoneID.SAM);
        resReconn.setRoomID(matchId);
        resReconn.setCurrentPlayersSam(table.getPlayings(), table.getWaitings(), table.owner);
        resReconn.isResume = true;
        resReconn.isHidePoker = false;
        aSession.write(resReconn);
    }
    
    private void rejoinPikachu(PikachuTable table, ISession aSession, UserEntity newUser, long matchId, Room room) throws SimpleException, ServerException {
        MessageFactory msgFactory = aSession.getMessageFactory();
        ReconnectResponse resReconn = (ReconnectResponse) msgFactory.getResponseMessage(MessagesID.RECONNECT);

        PikachuPlayer reconnPlayer = table.findPlayer(newUser.mUid);

        if (reconnPlayer == null) {
            mLog.debug("Error:  Can rejoin Pika");
            return;
        }

        reconnPlayer.isOut = false;
        reconnPlayer.avatarID = newUser.avatarID;
        reconnPlayer.level = newUser.level;
        reconnPlayer.cash = newUser.money;
        reconnPlayer.username = newUser.mUsername;
        reconnPlayer.moneyForBet = table.firstCashBet;
        reconnPlayer.currentMatchID = matchId;

        if ((reconnPlayer.currentSession != null) && (reconnPlayer.currentSession.getID() != null)) {
            if (reconnPlayer.currentSession.getID().compareTo(aSession.getID()) != 0) {
                Room r = reconnPlayer.currentSession.getJoinedRooms()
                        .lastElement();
                reconnPlayer.currentSession.leftRoom(r.getRoomId());
                r.left(aSession);
                try {
                    reconnPlayer.currentSession.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        reconnPlayer.currentSession = aSession;
        reconnPlayer.currentOwner = table.ownerSession;
        room.join(aSession);
        aSession.setRoom(room);

        if (table.isPlaying) {
            resReconn.isObserve = true;
            resReconn.isPlaying = true;
        }

        resReconn.setSuccess(ResponseCode.SUCCESS, room.getName(),
                table.firstCashBet, ZoneID.PIKACHU);

        resReconn.setRoomID(matchId);
        resReconn.setCurrentPlayers(table.playings,
                table.waitings, table.owner);

        resReconn.setCapacity(table.getMaximumPlayer());
        aSession.write(resReconn);
    }

    private void rejoinAltp(TrieuPhuTable table, ISession aSession, UserEntity newUser, long matchId, Room room) throws SimpleException, ServerException {
        MessageFactory msgFactory = aSession.getMessageFactory();
        ReconnectResponse resReconn = (ReconnectResponse) msgFactory.getResponseMessage(MessagesID.RECONNECT);

        TrieuPhuPlayer reconnPlayer = table.findPlayer(newUser.mUid);

        if (reconnPlayer == null) {
            mLog.debug("Error:  Can rejoin ALTP");
            return;
        }

        reconnPlayer.isOut = false;
        reconnPlayer.avatarID = newUser.avatarID;
        reconnPlayer.level = newUser.level;
        reconnPlayer.cash = newUser.money;
        reconnPlayer.username = newUser.mUsername;
        reconnPlayer.moneyForBet = table.firstCashBet;
        reconnPlayer.currentMatchID = matchId;

        if ((reconnPlayer.currentSession != null) && (reconnPlayer.currentSession.getID() != null)) {
            if (reconnPlayer.currentSession.getID().compareTo(aSession.getID()) != 0) {
                Room r = reconnPlayer.currentSession.getJoinedRooms()
                        .lastElement();
                reconnPlayer.currentSession.leftRoom(r.getRoomId());
                r.left(aSession);
                try {
                    reconnPlayer.currentSession.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        reconnPlayer.currentSession = aSession;
        reconnPlayer.currentOwner = table.ownerSession;
        room.join(aSession);
        aSession.setRoom(room);

        if (table.isPlaying) {
            table.sendResultForViewer(aSession);
        }

        resReconn.setSuccess(ResponseCode.SUCCESS, room.getName(), table.firstCashBet, ZoneID.AILATRIEUPHU);

        resReconn.setRoomID(matchId);
        resReconn.setCurrentPlayers(table.playings, table.waitings, table.owner);

        resReconn.setCapacity(table.getMaximumPlayer());
        aSession.write(resReconn);
    }

    private void rejoinBinh(BinhTable table, ISession aSession, UserEntity newUser, long matchId, Room room) throws SimpleException, ServerException {
        MessageFactory msgFactory = aSession.getMessageFactory();
        ReconnectResponse resReconn = (ReconnectResponse) msgFactory.getResponseMessage(MessagesID.RECONNECT);

        boolean isResume = true;
        String cards;
        BinhPlayer reconnPlayer = table.findPlayer(newUser.mUid);
        if (reconnPlayer == null) {
            mLog.debug("Error:  Can rejoin Binh");
            return;
        }

        reconnPlayer.isOut = false;
        reconnPlayer.setViewing(false);
        reconnPlayer.setUserAvatarId(newUser.avatarID);
        reconnPlayer.setUserLevel(newUser.level);
        reconnPlayer.setUserCash(newUser.money);
        reconnPlayer.setUsername(newUser.mUsername);
        reconnPlayer.moneyForBet = table.firstCashBet;
        reconnPlayer.currentMatchID = matchId;
        if ((reconnPlayer.currentSession != null) && (reconnPlayer.currentSession.getID() != null)) {
            if (reconnPlayer.currentSession.getID().compareTo(aSession.getID()) != 0) {
                Room r = reconnPlayer.currentSession.getJoinedRooms().lastElement();
                reconnPlayer.currentSession.leftRoom(r.getRoomId());
                r.left(aSession);
                // }
                try {
                    reconnPlayer.currentSession.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        reconnPlayer.currentSession = aSession;
        reconnPlayer.currentOwner = table.ownerSession;
        // reconnPlayer.currentSession = aSession;
        room.join(aSession);

        aSession.setRoom(room);
        if (table.isPlaying) {
            resReconn.isObserve = true;
            resReconn.isPlaying = true;
        }

        resReconn.setSuccess(ResponseCode.SUCCESS, room.getName(), table.firstCashBet, ZoneID.PHOM);
        resReconn.setRoomID(matchId);

        resReconn.setCapacity(table.getMaximumPlayer());
        aSession.write(resReconn);
    }
}
