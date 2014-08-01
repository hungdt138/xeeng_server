/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelRequest;
import com.tv.xeeng.base.protocol.messages.CancelResponse;
import com.tv.xeeng.base.protocol.messages.EndMatchResponse;
import com.tv.xeeng.base.protocol.messages.TurnResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaPlayer;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaTable;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.newbacay.data.NewBaCayPlayer;
import com.tv.xeeng.game.newbacay.data.NewBaCayTable;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.pikachu.datta.PikachuPlayer;
import com.tv.xeeng.game.pikachu.datta.PikachuTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.tienlen.data.TienLenPlayer;
import com.tv.xeeng.game.tienlen.data.TienLenTable;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuPlayer;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuTable;
import com.tv.xeeng.game.xam.data.SamPlayer;
import com.tv.xeeng.game.xam.data.SamTable;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import java.util.ArrayList;
import org.slf4j.Logger;

/**
 *
 * @author tuanda
 */
public class CancelBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(CancelBusiness.class);

    private void writeWithRooms(ISession aSession, CancelRequest request, CancelResponse response, int phongId, Zone zone) throws ServerException {
        if (request.isSendMe) {
            response.phongId = phongId;
            response.zone = zone;
            response.session = aSession;
            aSession.write(response);
        }

//        if (response.mCode == ResponseCode.SUCCESS) {
//            aSession.setRoom(null);
//        }
        response.mCode = ResponseCode.SUCCESS;
//        
        aSession.setRoom(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[CANCEL]: Catch");
        MessageFactory msgFactory;//aSession.getMessageFactory();
        CancelResponse resMatchCancel = null;//(CancelResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        Room room = null;
        int zoneID = aSession.getCurrentZone();
        CancelRequest rqMatchCancel = (CancelRequest) aReqMsg;
        long matchId = rqMatchCancel.mMatchId;

        //fix wrong zone
        if (matchId > 0) {
            MatchEntity matchEntity = CacheMatch.getMatch(matchId);
            if (matchEntity != null) {
                room = matchEntity.getRoom(); //find room by new method
                zoneID = matchEntity.getZoneId();
            }
        }

        Zone bacayZone = aSession.findZone(zoneID);
        try {
//            CancelRequest rqMatchCancel = (CancelRequest) aReqMsg;
//            long matchId = rqMatchCancel.mMatchId;
            long uid = aSession.getUID();
//            mLog.debug("[CANCEL]: ID - " + uid + ", match id - " + matchId);

            if (matchId > 0) {
                if (room == null) //found by new method
                {
                    room = aSession.getRoom();
                }

                if (room == null || (room.getAttactmentData().matchID != matchId)) {
                    //find by old method
                    room = bacayZone.findRoom(matchId);
                }

                if (room != null) {
                    //int zoneId = aSession.getCurrentZone();
                    if (zoneID == ZoneID.PHOM || zoneID == ZoneID.TIENLEN || zoneID == ZoneID.BAU_CUA_TOM_CA || zoneID == ZoneID.NEW_BA_CAY) {
                        msgFactory = room.getAttactmentData().getNotNullSession().getMessageFactory();
                    } else {
                        msgFactory = aSession.getMessageFactory();
                    }

                    resMatchCancel = (CancelResponse) msgFactory.getResponseMessage(aReqMsg.getID());
                    resMatchCancel.session = aSession;
                    resMatchCancel.phongId = room.phongID;
//                    resMatchCancel.zone = bacayZone;
//					RoomDB roomDB = new RoomDB();
//					roomDB.userOut(zoneID, aSession.getPhongID());
                    if (aSession.getRoomID() != 0) {
                        //bacayZone.outRoom(aSession.getRoomID());
                        aSession.setRoomID(0);
                    }

                    aSession.setLastFP(System.currentTimeMillis() - 20000); //for fast play

                    switch (zoneID) {
                        case ZoneID.PHOM: {
                            PhomTable table = (PhomTable) room.getAttactmentData();
                            uid = aSession.getUID();

//                            System.out.println("Came here auto play: "
//                                    + table.isPlaying);
                            resMatchCancel.setUid(uid);
                            PhomPlayer player = table.findPlayer(uid);
                            if (table.isPlaying) {
                                if (player != null && !player.isObserve) {
                                    resMatchCancel.setFailure(ResponseCode.FAILURE, "Bạn vui lòng chờ hết ván bài nhé!");
                                    resMatchCancel.session = aSession;
                                    resMatchCancel.uid = aSession.getUID();
                                    aSession.write(resMatchCancel);
//                                    writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                    mLog.error("---THANGTD CANCEL DEBUG---" + player.username + " try to quit game PHOM while playing");
                                    return 1;
                                }

                                resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);

                                /*
                                 if (player != null && !player.isObserve) {
                                 player.isAutoPlay = true;
                                 player.isReady = false;
                                 player.isOut = true;
                                    
                                 if (table.playings.size() == 1 || table.numRealPlaying() == 0) {
                                 //                                        room.broadcastMessage(resMatchCancel, aSession, false);

                                 writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                 //table.broadcastMsg(resMatchCancel, table.getNewPlayings(), table.getNewWaitings(), player, false);  

                                 table.destroy();
                                 room.allLeft();

                                 return 1;
                                 } else if (table.numRealPlaying() == 1) {
                                 // Nếu có 2 người chơi mà 1 người thoát
                                 // --> kết thúc ván
                                 table.resetAutoKickOut();
                                 table.isPlaying = false;
                                 EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory.getResponseMessage(MessagesID.MATCH_END);
                                 endMatchRes.setZoneID(ZoneID.PHOM);

                                 // Begin fix error found winner 
                                 PhomPlayer winner = null;
                                 for (int i = 0; i < table.playings.size(); i++) {
                                 if (!table.playings.get(i).isAutoPlay) {
                                 winner = table.playings.get(i);
                                 break;
                                 }
                                 }

                                 ArrayList<PhomPlayer> players = table.allOutJustOne(winner.id);
                                 // End fix error found winner 
                                 endMatchRes.mMatchId = table.matchID;
                                 endMatchRes.setSuccess(ResponseCode.SUCCESS, (ArrayList<PhomPlayer>) players.clone(), winner);
                                 if (table.owner.isOut) {
                                 PhomPlayer p1 = table.ownerQuit();
                                 if (p1 != null) {
                                 room.setOwnerName(p1.username);
                                 table.owner = p1;
                                 endMatchRes.newOwner = p1.id;
                                 resMatchCancel.newOwner = p1.id;
                                 }
                                 }

                                 resMatchCancel.mCode = ResponseCode.SUCCESS;

                                 writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                 //table.broadcastMsg(resMatchCancel, table.getNewPlayings(), table.getNewWaitings(), player, false);
                                 //                                        room.broadcastMessage(resMatchCancel,
                                 //                                                aSession, true);
                                 room.left(aSession);
                                 //									/Thread.sleep(300);
                                 //                                        room.broadcastMessage(endMatchRes,
                                 //                                                aSession, false);

                                 table.broadcastMsg(endMatchRes, table.getNewPlayings(), table.getNewWaitings(), player, false);
                                 table.supRemOldVer(endMatchRes.newOwner, AIOConstants.PROTOCOL_MODIFY_MID);
                                 room.setPlaying(false);
                                 // begin fix dead session()
                                 table.resetPlayers();
                                 // End fix dead session()
                                 return 1;
                                 } else if (table.getCurrentPlayer().id == player.id) {
                                 table.processAuto();
                                 //table.doTimeout();
                                 } else {
                                 player.currentSession = null;
                                 }
                                 }
                                 */
                                if (uid == table.owner.id) {
                                    PhomPlayer p1 = table.ownerQuit();
                                    if (p1 != null) {
                                        room.setOwnerName(p1.username);
                                        room.setName(p1.username);
                                        resMatchCancel.newOwner = p1.id;
//                                        table.owner = p1;
                                    }
                                }

                                if (player != null && player.isObserve) {
                                    table.removeObserver(player);

                                    writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);

                                    table.broadcastMsg(resMatchCancel, table.getNewPlayings(), table.getNewWaitings(), player, false);
                                    room.left(aSession);
                                    return 1;
                                }

                                resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);

                                writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                room.left(aSession);
                                return 1;
                            } else {
                                if (player != null) {
                                    table.remove(player);
                                }

                                if (table.playings.isEmpty()) {
//                                    room.broadcastMessage(resMatchCancel, aSession, false);

                                    writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                    //table.broadcastMsg(resMatchCancel, table.getNewPlayings(), table.getNewWaitings(), player, false);

                                    table.destroy();
                                    room.allLeft();
                                    return 1;
                                }

                                if (uid == table.owner.id) {
                                    PhomPlayer p1 = table.ownerQuit();
                                    if (p1 != null) {
                                        room.setOwnerName(p1.username);
                                        room.setName(p1.username);
                                        resMatchCancel.newOwner = p1.id;
                                        table.owner = p1;
                                    }
                                }
                                if (uid == table.currentPlayer.id) {
                                    table.setNewStarter(table.playings.get(0));
                                }

                                resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);

                                writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                table.broadcastMsg(resMatchCancel, table.getNewPlayings(), table.getNewWaitings(), player, false);

                                room.left(aSession);
                                return 1;
                            }
                        }

                        case ZoneID.TIENLEN: {
                            resMatchCancel.setZone(ZoneID.TIENLEN);
                            TienLenTable table = (TienLenTable) room.getAttactmentData();
                            uid = aSession.getUID();

                            resMatchCancel.setUid(uid);
                            if (table == null) {
                                mLog.error("Table is null ! uid : " + uid);
                            } else {
                                TienLenPlayer player = table.findPlayer(uid);
                                if (table.isPlaying) {
                                    if (player != null && !player.isObserve) {
                                        resMatchCancel.setFailure(ResponseCode.FAILURE, "Bạn vui lòng chờ hết ván bài nhé!");
                                        resMatchCancel.session = aSession;
                                        resMatchCancel.uid = aSession.getUID();
                                        aSession.write(resMatchCancel);
//                                        writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                        mLog.error("---THANGTD CANCEL DEBUG---" + player.username + " try to quit game TLMN while playing");
                                        return 1;
                                    }

                                    /*
                                     EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory.getResponseMessage(MessagesID.MATCH_END);

                                     if (player != null && !player.isObserve) {
                                     player.isOutGame = true;
                                     player.isReady = false;
                                     player.isOut = true;
                                        
                                     //                                        System.out.println("Thằng ở vị trí: " + table.getUserIndex(player.id) + " mới bằng " + player.isOutGame);
                                     if (table.getPlayings().isEmpty()) {
                                     resMatchCancel.mCode = 1;
                                     //                                            room.broadcastMessage(resMatchCancel, aSession, false);

                                     writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                     //table.broadcastMsg(resMatchCancel, table.getNewPlayings(), table.getNewWaitings(), player, false);
                                     table.destroy();
                                     room.allLeft();
                                     //table = null;

                                     return 1;
                                     } else if (table.numRealPlaying() == 1) {
                                     // Nếu có 2 người chơi mà 1 người thoát
                                     // --> kết thúc ván
                                     table.isPlaying = false;
                                     table.isNewMatch = true;
                                     endMatchRes.setZoneID(ZoneID.TIENLEN);
                                     endMatchRes.uid = uid;
                                     long idWin = 0;
                                     for (TienLenPlayer p : table.getPlayings()) {
                                     if (p.id != uid && !p.isOutGame) {
                                     idWin = p.id;
                                     break;
                                     }
                                     }

                                     if (idWin != 0) {
                                     if (table.owner.isOut) {
                                     TienLenPlayer p1 = table.ownerQuit();
                                     if (p1 != null) {
                                     room.setOwnerName(p1.username);
                                     room.setName(p1.username);
                                     resMatchCancel.newOwner = p1.id;
                                     endMatchRes.newOwner = p1.id;
                                     table.owner = p1;
                                     }
                                     }
                                     table.winner = table.findPlayer(idWin);
                                     endMatchRes.setSuccessTienLen(ResponseCode.SUCCESS, table.GetEndGame(idWin), idWin, (ArrayList<TienLenPlayer>) table.getPlayings().clone());
                                     endMatchRes.session = aSession;
                                     table.broadcastMsg(endMatchRes, table.getNewPlayings(), table.getNewWaitings(), player, false);
                                     //                                                room.broadcastMessage(endMatchRes,
                                     //                                                        aSession, false);
                                     table.supRemOldVer(endMatchRes.newOwner, AIOConstants.PROTOCOL_MODIFY_MID);
                                     table.resetPlayers();

                                     table.isPlaying = false;
                                     }
                                     resMatchCancel.mCode = ResponseCode.SUCCESS;

                                     writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                     //table.broadcastMsg(resMatchCancel, table.getNewPlayings(), table.getNewWaitings(), player, false);

                                     //                                            room.broadcastMessage(resMatchCancel,
                                     //                                                    aSession, true);
                                     room.left(aSession);

                                     return 1;

                                     }
                                     //* Bỏ lượt khi thoát game 
                                     //- Nếu đang là lượt của nó: 
                                     //+ Nếu được đánh lượt mới: chuyển lượt cho đứa bên cạnh 
                                     //+ Nếu chặt: chuyển lượt bình thường
                                     //- Nếu không phải lượt của nó:
                                     //+ Người đánh vừa xong là nó (tức là đánh xong rồi quit)  chuyển lastTurnID cho đứa bên cạnh 
                                     //+ Nếu không :set isgiveup = true
                                     // else if (table.getCurrentTurnID() == player.id) {
                                     // player.isOutGame = true;
                                     if (table.isNewRound) {
                                     table.nextUser(table.getUserIndex(uid));
                                     //                                                System.out.println("table.getUserIndex(uid)"
                                     //                                                        + table.getUserIndex(uid));
                                     table.isNewRound = true;
                                     resMatchCancel.setNextPlayer(table.getCurrentTurnID(), true);
                                     } else {
                                     table.nextUser(table.getUserIndex(uid));

                                     resMatchCancel.setNextPlayer(table.getCurrentTurnID(), table.isNewRound);
                                     }
                                     // Gui ban tin Turn ve  
                                     sendNextTurn(table, uid, aSession, player);
                                     } else {
                                     if (table.lastTurnID == uid) {
                                     //                                                System.out.println("đánh xong rùi thoát nè!");
                                     //table.lastTurnID = table.getPlayings().get(table.findNext(table.getUserIndex(uid))).id;
                                     }
                                     }
                                        
                                     resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);
                                     }
                                     */
                                    if (player != null && player.isObserve) {
//                                        System.out.println("Player bị remove tại đây!!híc híc");
                                        table.remove(player);

                                        writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);

                                        table.broadcastMsg(resMatchCancel, table.getNewPlayings(), table.getNewWaitings(), player, false);
                                        room.left(aSession);
                                        return 1;
                                    }

                                    writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                    room.left(aSession);
                                    return 1;
                                } else {
                                    if (player != null) {
                                        table.remove(player);
                                    }
//                                    System.out.println("so nguoi con lai : "
//                                            + table.numRealPlaying());

                                    if (table.numRealPlaying() == 0) {
                                        resMatchCancel.mCode = 1;
//                                        room.broadcastMessage(resMatchCancel,
//                                                aSession, false);

                                        writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);

                                        //table.broadcastMsg(resMatchCancel, table.getNewPlayings(), table.getNewWaitings(), player, false);
                                        table.destroy();
                                        room.allLeft();
                                        //table = null;
                                        return 1;
                                    } // Trường hợp khi người chơi thoát hết, chỉ
                                    // còn chủ room thì lần sau sẽ bắt đầu ván
                                    // mới
                                    else if (table.getPlayings().size() == 1 && player != null && !player.isObserve) {
                                        table.isNewMatch = true;
                                    }

                                    if (uid == table.owner.id) {
                                        TienLenPlayer p1 = table.ownerQuit();
                                        if (p1 != null) {
                                            room.setOwnerName(p1.username);
                                            resMatchCancel.newOwner = p1.id;
                                            table.owner = p1;
                                        }
                                        resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);
                                    }

                                    writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                    table.broadcastMsg(resMatchCancel, table.getNewPlayings(), table.getNewWaitings(), player, false);

                                    room.left(aSession);
                                    return 1;
                                }
                            }

                            break;
                        }

                        case ZoneID.SAM: {
                            SamTable table = (SamTable) room.getAttactmentData();
                            uid = aSession.getUID();
                            SamPlayer player = table.findPlayer(uid);
                            mLog.error("---THANGTD CANCEL DEBUG---" + player.username + " is observe:" + player.isObserve + " try to quit game SAM while playing: " + table.isPlaying);
                            if (table.isPlaying && player != null && !player.isObserve) {
                                resMatchCancel.setFailure(ResponseCode.FAILURE, "Bạn vui lòng chờ hết ván bài nhé!");
                                resMatchCancel.session = aSession;
                                resMatchCancel.uid = aSession.getUID();
                                aSession.write(resMatchCancel);
//                                writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                mLog.error("---THANGTD CANCEL DEBUG---" + player.username + " try to quit game SAM while playing");
                                return 1;
                            }

                            resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);
                            writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                            table.cancel(uid);
                            room.left(aSession);

                            break;
                        }
                        
                        case ZoneID.NEW_BA_CAY: {
                            NewBaCayTable table = (NewBaCayTable) room.getAttactmentData();
                            uid = aSession.getUID();
                            NewBaCayPlayer player = table.findPlayer(uid);
                            mLog.error("---THANGTD CANCEL DEBUG---" + player.username + " is observe:" + player.isMonitor + " try to quit game BA CAY while playing: " + table.isPlaying);
                            if (table.isPlaying && player != null && !player.isMonitor) {
                                resMatchCancel.setFailure(ResponseCode.FAILURE, "Bạn vui lòng chờ hết ván bài nhé!");
                                resMatchCancel.session = aSession;
                                resMatchCancel.uid = aSession.getUID();
                                aSession.write(resMatchCancel);
//                                writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                mLog.error("---THANGTD CANCEL DEBUG---" + player.username + " try to quit game BA CAY while playing");
                                return 1;
                            }

                            resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);
                            writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                            table.cancel(uid);
                            room.left(aSession);

                            break;
                        }
                        
                        case ZoneID.BAU_CUA_TOM_CA: {
                            BauCuaTomCaTable table = (BauCuaTomCaTable) room.getAttactmentData();
                            uid = aSession.getUID();
                            BauCuaTomCaPlayer player = table.findPlayer(uid);
                            mLog.error("---THANGTD CANCEL DEBUG---" + player.username + " is observe:" + player.isMonitor + " try to quit game BAU CUA while playing: " + table.isPlaying);
                            if (table.isPlaying && player != null && !player.isMonitor) {
                                resMatchCancel.setFailure(ResponseCode.FAILURE, "Bạn vui lòng chờ hết ván bài nhé!");
                                resMatchCancel.session = aSession;
                                resMatchCancel.uid = aSession.getUID();
                                aSession.write(resMatchCancel);
//                                writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                mLog.error("---THANGTD CANCEL DEBUG---" + player.username + " try to quit game BAU CUA while playing");
                                return 1;
                            }

                            resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);
                            writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                            table.cancel(uid);
                            room.left(aSession);

                            break;
                        }
                        
                        case ZoneID.AILATRIEUPHU: {
                            TrieuPhuTable table = (TrieuPhuTable) room.getAttactmentData();
                            uid = aSession.getUID();
                            TrieuPhuPlayer player = table.findPlayer(uid);
                            mLog.error("---THANGTD CANCEL DEBUG---" + player.username + " is observe:" + player.isMonitor + " try to quit game ALTP while playing: " + table.isPlaying);
                            if (table.isPlaying && player != null && !player.isMonitor) {
                                resMatchCancel.setFailure(ResponseCode.FAILURE, "Bạn vui lòng chờ hết ván bài nhé!");
                                resMatchCancel.session = aSession;
                                resMatchCancel.uid = aSession.getUID();
                                aSession.write(resMatchCancel);
//                                writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                mLog.error("---THANGTD CANCEL DEBUG---" + player.username + " try to quit game ALTP while playing");
                                return 1;
                            }

                            resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);
                            writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                            table.cancel(uid);
                            room.left(aSession);

                            break;
                        }

                        case ZoneID.PIKACHU: {
                            PikachuTable table = (PikachuTable) room.getAttactmentData();
                            uid = aSession.getUID();
                            PikachuPlayer player = table.findPlayer(uid);
                            mLog.error("---THANGTD CANCEL DEBUG---" + player.username + " is observe:" + player.isMonitor + " try to quit game PIKACHU while playing: " + table.isPlaying);
                            if (table.isPlaying && player != null && !player.isMonitor) {
                                resMatchCancel.setFailure(ResponseCode.FAILURE, "Bạn vui lòng chờ hết ván bài nhé!");
                                resMatchCancel.session = aSession;
                                resMatchCancel.uid = aSession.getUID();
                                aSession.write(resMatchCancel);
//                                writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                                mLog.error("---THANGTD CANCEL DEBUG---" + player.username + " try to quit game PIKACHU while playing");
                                return 1;
                            }

                            resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);
                            writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                            table.cancel(uid);
                            room.left(aSession);

                            break;
                        }
                        
                        default:
                            break;
                    }
                    // Finally

                    if (aSession.getCurrentZone() == ZoneID.NEW_BA_CAY || aSession.getCurrentZone() == ZoneID.BAU_CUA_TOM_CA
                            || aSession.getCurrentZone() == ZoneID.AILATRIEUPHU || aSession.getCurrentZone() == ZoneID.PIKACHU) {
//                        resMatchCancel.setSuccess(ResponseCode.SUCCESS, uid);
//                        writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
//                        aSession.leftRoom(matchId);
//                        room.left(aSession);
//
//                        SimpleTable table = room.getAttactmentData();
//                        if (table != null) {
//                            table.broadcastMsg(resMatchCancel, table.getNewPlayings(), new ArrayList<SimplePlayer>(), null, false);
//                        }
                    } else {
                        Room currentRoom = aSession.leftRoom(matchId);
                        if (currentRoom != null) {
                            currentRoom.left(aSession);
                            //System.out.println("chạy qua finally!");

                            if (aSession.getCurrentZone() == ZoneID.TIENLEN) {
                                resMatchCancel.mCode = 1;
                            }

                            writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);

                            if (room != null) {
                                //TODO:  fix don't send resmatchcancel to this session
                               /* if( aSession.getCurrentZone() == ZoneID.PHOM || aSession.getCurrentZone() == ZoneID.TIENLEN)
                                 {*/
                                SimpleTable table = room.getAttactmentData();
                                if (table != null) {
                                    table.broadcastMsg(resMatchCancel, table.getNewPlayings(), new ArrayList<SimplePlayer>(), null, false);
                                }
                                /* }
                                 else
                                 {
                                 room.broadcastMessage(resMatchCancel, aSession,
                                 false);
                                 }*/
                            }
                        }
                    }
                } else {
                    msgFactory = aSession.getMessageFactory();
                    resMatchCancel = (CancelResponse) msgFactory.getResponseMessage(aReqMsg.getID());
                    resMatchCancel.mCode = 1;
                    resMatchCancel.session = aSession;
                    resMatchCancel.uid = aSession.getUID();
                    aSession.write(resMatchCancel);
                }
            }
        } catch (Throwable t) {
            try {
                writeWithRooms(aSession, rqMatchCancel, resMatchCancel, room.phongID, bacayZone);
                room.left(aSession);
            } catch (ServerException ex) {
                mLog.error("!!!!!Error cancel ", ex);
            } catch (Exception ex) {
                mLog.error("Cancel business error", ex);
            }
        }

        return 1;
    }

    private void sendNextTurn(TienLenTable table, long currID, ISession aSession, TienLenPlayer player) {
        MessageFactory msgFactory = aSession.getMessageFactory();

        TurnResponse resMatchTurn = (TurnResponse) msgFactory.getResponseMessage(MessagesID.MATCH_TURN);
        resMatchTurn.setSuccessTienLen(ResponseCode.SUCCESS, "", table.getCurrentTurnID(), table.isNewRound, ZoneID.TIENLEN);
        resMatchTurn.setcurrID(currID);
        resMatchTurn.setIsGiveup(true);
        resMatchTurn.isDuty = table.getDuty().isYourDuty();
        resMatchTurn.session = aSession;
        if (table.fightOccur) {
            resMatchTurn.setFightInfo(table.fightInfo);
        }

        table.broadcastMsg(resMatchTurn, table.getNewPlayings(), table.getNewWaitings(), player, true);
    }
}
