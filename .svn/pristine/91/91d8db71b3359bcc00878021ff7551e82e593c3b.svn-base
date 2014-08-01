/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.*;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaTable;
import com.tv.xeeng.game.binh.data.BinhPlayer;
import com.tv.xeeng.game.binh.data.BinhTable;
import com.tv.xeeng.game.data.*;
import com.tv.xeeng.game.lieng.data.LiengTable;
import com.tv.xeeng.game.newbacay.data.NewBaCayTable;
import com.tv.xeeng.game.newpika.data.NewPikaTable;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.pikachu.datta.PikachuTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.tienlen.data.TienLenPlayer;
import com.tv.xeeng.game.tienlen.data.TienLenTable;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuTable;
import com.tv.xeeng.game.xam.data.SamTable;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

import java.util.ArrayList;

/**
 *
 * @author tuanda
 */
public class StartBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(StartBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        // boolean isFail = false;
        mLog.debug("[START] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        StartResponse resMatchStart = (StartResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resMatchStart.session = aSession;
        try {
            StartRequest rqMatchStart = (StartRequest) aReqMsg;
            MatchEntity matchEntity = CacheMatch.getMatch(rqMatchStart.mMatchId);
            Room room = null;
            int zoneId = 0;

            if (matchEntity != null) {
                room = matchEntity.getRoom();
                zoneId = matchEntity.getZoneId();
            }

            if (room == null) {
                // retry to find by old method
                zoneId = aSession.getCurrentZone();
                room = aSession.getRoom();
            }

            if (room == null || (room.getAttactmentData().matchID != rqMatchStart.mMatchId)) {
                Zone zone = aSession.findZone(zoneId);
                room = zone.findRoom(rqMatchStart.mMatchId);
            }
            // Room room = aSession.getRoom();
            // if(room == null ||(room.getAttactmentData().matchID !=
            // rqMatchStart.mMatchId))
            // {
            // Zone zone = aSession.findZone(aSession.getCurrentZone());
            // room = zone.findRoom(rqMatchStart.mMatchId);
            // }

            if (room != null) {
                mLog.debug("[START] : MatchID - " + rqMatchStart.mMatchId);
                SimpleTable table = (SimpleTable) room.getAttactmentData();
                if (table.isPlaying) {
                    resMatchStart.setFailure(ResponseCode.FAILURE, "Bàn đang chơi rồi!");
                } else {
                    switch (zoneId) {
                        case ZoneID.PHOM: {
                            PhomTable pTable = (PhomTable) table;
                            if (!pTable.isAnyReady()) {
                                resMatchStart.setFailure(ResponseCode.FAILURE, "Bạn không thể bắt đầu vì còn người chơi chưa sẵn sàng!");
                                aResPkg.addMessage(resMatchStart);
                                mLog.error("Bạn không thể bắt đầu vì còn người chơi chưa sẵn sàng!");
                                return 1;
                            }
                            pTable.start();
                            // Send poker to client
                            for (PhomPlayer player : pTable.getPlayings()) {
                                long playerID = player.id;
                                // ISession playerSession = aSession.getManager()
                                // .findSession(playerID);
                                try {
                                    if (!pTable.superUsers.isEmpty()) {
                                        GetOtherPokerResponse getOtherPoker = (GetOtherPokerResponse) msgFactory.getResponseMessage(MessagesID.GET_OTHER_POKER);
                                        getOtherPoker.setSuccess(ResponseCode.SUCCESS, playerID,true);
                                        getOtherPoker.setPhomCards(player.allCurrentCards);
                                        for (PhomPlayer p : pTable.superUsers) {
                                            if (p.id != playerID) {
                                                p.write(getOtherPoker);
                                            }
                                        }
                                    }
                                } catch (Exception ex) {
                                }

                                ISession playerSession = player.currentSession;
                                GetPokerResponse getPoker = (GetPokerResponse) msgFactory.getResponseMessage(MessagesID.GET_POKER);
                                getPoker.setSuccess(ResponseCode.SUCCESS, player.id, player.username);
                                getPoker.session = player.currentSession;
                                getPoker.zoneId = ZoneID.PHOM;

                                getPoker.setBeginID(pTable.getCurrentPlayer().id);
                                getPoker.matchNum = pTable.matchNum;

                                // comment duty by datuan
//                                getPoker.dutyType = pTable.duty.getType();
//                                getPoker.dutyType = 0;

                                getPoker.setPhomCards(player.allCurrentCards);

                                if (playerSession == null) {
                                    mLog.error(pTable.turnInfo() + " : [" + player.username + "]");
                                    playerSession = player.currentSession;
                                }

                                if (playerID == aSession.getUID()) {
                                    if (aResPkg != null) {
                                        aResPkg.addMessage(getPoker);
                                    } else {
                                        aSession.write(getPoker);
                                    }
                                } else {
                                    playerSession.write(getPoker);
                                }
                            }
                            if (pTable.checkUKhan() != null) {
                                EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory.getResponseMessage(MessagesID.MATCH_END);
                                // set the result
                                endMatchRes.setZoneID(ZoneID.PHOM);
                                endMatchRes.uType = 2;
                                endMatchRes.session = aSession;
                                if (pTable.owner.isOut || pTable.owner.notEnoughMoney()) {
                                    PhomPlayer p1 = pTable.ownerQuit();
                                    if (p1 != null) {
                                        room.setOwnerName(p1.username);
                                        pTable.owner = p1;
                                        endMatchRes.newOwner = p1.id;
                                    }
                                }
                                endMatchRes.setSuccess(ResponseCode.SUCCESS, pTable.getPlayings(), pTable.getWinner());

                                // room.broadcastMessage(endMatchRes, aSession,
                                // true);
                                pTable.broadcastMsg(endMatchRes, pTable.getNewPlayings(), pTable.getWaitings(), pTable.getWinner(), true);

                                room.setPlaying(false);
                                //pTable.removeNotEnoughMoney(room);
                                pTable.resetPlayers();
                                pTable.reset();
                                pTable.gameStop();
                                return 1;
                            }
                            // room.broadcastMessage(broadcastMsg, aSession, true);
                            pTable.processAuto();
                            //resMatchStart = null;

                            return 1;
                        }
                        case ZoneID.TIENLEN: {
                            TienLenTable tTable = (TienLenTable) table;
                            if (!tTable.isAnyReady()) {
                                resMatchStart.setFailure(ResponseCode.FAILURE, "Bạn không thể bắt đầu vì còn người chơi chưa sẵn sàng!");
                                aResPkg.addMessage(resMatchStart);
                                mLog.error("Bạn không thể bắt đầu vì còn người chơi chưa sẵn sàng!");
                                return 1;
//                                throw new BusinessException("Bạn không thể bắt đầu vì còn người chơi chưa sẵn sàng");
                            }
                            if (aSession.getUID() == tTable.owner.id) {
                                long[] L = tTable.startMatch();
                                long idPerfectWin = L[0];
                                if (idPerfectWin > 0) {// Tới trắng khi chia bài!!!!
                                    tTable.resetAutoKickOut();
                                    tTable.isPlaying = false;
                                    
                                    EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory.getResponseMessage(MessagesID.MATCH_END);
                                    // set the result
                                    endMatchRes.setZoneID(ZoneID.TIENLEN);
                                    endMatchRes.session = aSession;
                                    endMatchRes.setSuccessTienLen(ResponseCode.SUCCESS, tTable.GetEndGamePerfect(idPerfectWin),
                                            idPerfectWin, (ArrayList<TienLenPlayer>) tTable.getPlayings().clone());
                                    endMatchRes.perfectType = L[1];
                                    // room.broadcastMessage(endMatchRes, aSession,
                                    // true);
                                    long newOwnerId = 0;
                                    if (((TienLenPlayer) tTable.owner).isOut || ((TienLenPlayer) tTable.owner).notEnoughMoney()) {
                                        SimplePlayer newOwnerPlayer = tTable.ownerQuit();
                                        if (newOwnerPlayer != null) {
                                            newOwnerId = newOwnerPlayer.id;
                                        }
                                    }

                                    endMatchRes.newOwner = newOwnerId;
                                    table.broadcastMsg(endMatchRes, table.getNewPlayings(), table.getNewWaitings(), table.findPlayer(idPerfectWin), true);
                                    //tTable.removeNotEnoughMoney(room);
                                    tTable.resetPlayers();
                                } else {
                                    // Send poker to client
                                    ArrayList<TienLenPlayer> players = tTable.getPlayings();
                                    long beginUid = tTable.getCurrentTurnID();

                                    int playingSize = players.size();
                                    for (int i = 0; i < playingSize; i++) {
                                        TienLenPlayer player = players.get(i);
                                        if (!tTable.superUsers.isEmpty()) {
                                            try {
                                                GetOtherPokerResponse getOtherPoker = (GetOtherPokerResponse) msgFactory.getResponseMessage(MessagesID.GET_OTHER_POKER);
                                                getOtherPoker.setSuccess(ResponseCode.SUCCESS, player.id, true);
                                                getOtherPoker.setTienLenCards(player.myHand);
                                                // getOtherPoker.isByteProtocol =
                                                // aSession.isByteProtocol();
                                                int superSize = tTable.superUsers.size();
                                                for (int j = 0; j < superSize; j++) {
                                                    TienLenPlayer p = tTable.superUsers.get(j);
                                                    if (p.id != player.id) {
                                                        p.write(getOtherPoker);
                                                    }
                                                }
                                            } catch (Exception ex) {
                                                mLog.error(ex.getMessage(), ex);
                                            }
                                        }

                                        long playerID = player.id;
                                        ISession playerSession = player.currentSession;

                                        // ISession playerSession = aSession
                                        // .getManager().findSession(playerID);
                                        GetPokerResponse getPoker = (GetPokerResponse) msgFactory.getResponseMessage(MessagesID.GET_POKER);
                                        getPoker.setSuccess(ResponseCode.SUCCESS, player.id, player.username);
                                        getPoker.tienlenCards_new = new byte[13];
                                        getPoker.matchNum = tTable.matchNum;
                                        getPoker.setBeginID(beginUid);
                                        getPoker.isNewMatch = tTable.isNewMatch;
                                        getPoker.setTienLenCards(player.myHand);
                                        getPoker.session = player.currentSession;
                                        // comment duty by datuan	
//                                        getPoker.dutyType = tTable.getDuty().getType();
                                        getPoker.zoneId = ZoneID.TIENLEN;

                                        if (playerSession == null) {
                                            // mLog.error(pTable.turnInfo() + " : ["
                                            // +
                                            // player.username + "]");
                                            // playerSession =
                                            // player.currentSession;
                                            tTable.remove(player);
                                        } else if (playerID == aSession.getUID()) {
                                            if (aResPkg != null) {
                                                aResPkg.addMessage(getPoker);
                                            } else {
                                                // for auto reset
                                                aSession.write(getPoker);
                                            }
                                        } else {
                                            playerSession.write(getPoker);
                                        }
                                    }
                                    tTable.isNewMatch = false;
                                    // tTable.startTime();
                                }
                                // room.broadcastMessage(broadcastMsg, aSession,
                                // true);
                                //resMatchStart = null;
                                return 1;

                            } else {
                                resMatchStart.setFailure(ResponseCode.FAILURE, "Bạn không phải là chủ bàn, không được quyền bắt đầu!");
                            }
                            //aResPkg.addMessage(resMatchStart);
                            break;
                        }
                        
                        case ZoneID.SAM: {
                            SamTable samTable = (SamTable) table;
                            if (!samTable.isAllReady()) {
                                resMatchStart.setFailure(ResponseCode.FAILURE, "Bạn không thể bắt đầu vì còn người chơi chưa sẵn sàng!");
                                aResPkg.addMessage(resMatchStart);
                                mLog.error("Bạn không thể bắt đầu vì còn người chơi chưa sẵn sàng!");
                                return 1;
                            }
                            
                            samTable.start();
                            
                            return 1;
                        }
                        
                        case ZoneID.NEW_BA_CAY: {
                            NewBaCayTable bcTable = (NewBaCayTable) table;
                            if (!bcTable.isAllReady()) {
                                resMatchStart.setFailure(ResponseCode.FAILURE, "Bạn không thể bắt đầu vì còn người chơi chưa sẵn sàng!");
                                aResPkg.addMessage(resMatchStart);
                                mLog.error("Bạn không thể bắt đầu vì còn người chơi chưa sẵn sàng!");
                                return 1;
                            }

                            bcTable.start();

                            resMatchStart.setSuccess(ResponseCode.SUCCESS, ZoneID.NEW_BA_CAY);

                            resMatchStart.lstplayer = bcTable.getPlayings();
                            resMatchStart.setSession(aSession);
                            table.broadcastMsg(resMatchStart, table.getNewPlayings(), table.getNewWaitings(), table.findPlayer(aSession.getUID()), true);
                            //room.broadcastMessage(resMatchStart, aSession, false);
                            return 1;
                        }

                        case ZoneID.BAU_CUA_TOM_CA: {
                            BauCuaTomCaTable bcTable = (BauCuaTomCaTable) table;
                            if (!bcTable.isAllReady()) {
                                resMatchStart.setFailure(ResponseCode.FAILURE, "Bạn không thể bắt đầu vì còn người chơi chưa sẵn sàng!");
                                aResPkg.addMessage(resMatchStart);
                                mLog.error("Bạn không thể bắt đầu vì còn người chơi chưa sẵn sàng!");
                                return 1;
                            }
                            bcTable.start();

                            resMatchStart.setSuccess(ResponseCode.SUCCESS, ZoneID.NEW_BA_CAY);

                            resMatchStart.lstplayer = bcTable.getPlayings();
                            table.broadcastMsg(resMatchStart, table.getNewPlayings(), table.getNewWaitings(), table.findPlayer(aSession.getUID()), true);
                            //room.broadcastMessage(resMatchStart, aSession, false);
                            return 1;
                        }

                        case ZoneID.PIKACHU: {
                            PikachuTable pTable = (PikachuTable) table;
                            pTable.start();
                            resMatchStart.pLevel = pTable.pikaLevel;
                            resMatchStart.setSuccess(ResponseCode.SUCCESS, ZoneID.PIKACHU);
                            // lTable.broadCast(resMatchStart);
                            table.broadcastMsg(resMatchStart, table.getNewPlayings(), new ArrayList<SimplePlayer>(), table.findPlayer(aSession.getUID()), true);
                            //room.broadcastMessage(resMatchStart, aSession, false);
                            return 1;
                        }

                        case ZoneID.LIENG: {
                            LiengTable xTable = (LiengTable) table;
                            xTable.start();

                            //resMatchStart = null;
                            return 1;
                        }

                        case ZoneID.AILATRIEUPHU: {
                            TrieuPhuTable xTable = (TrieuPhuTable) table;
                            xTable.start();
                            //resMatchStart = null;
                            return 1;
                        }

                        case ZoneID.NEW_PIKA: {
                            NewPikaTable xTable = (NewPikaTable) table;
                            xTable.start();
                            resMatchStart.pLevel = xTable.typeClientPlay;
                            resMatchStart.setSuccess(ResponseCode.SUCCESS, ZoneID.NEW_PIKA);
                            // lTable.broadCast(resMatchStart);
                            table.broadcastMsg(resMatchStart, table.getNewPlayings(), new ArrayList<SimplePlayer>(), table.findPlayer(aSession.getUID()), true);
                            return 1;
                        }
                        case ZoneID.BINH: {
                            BinhTable bTable = (BinhTable) table;
                            bTable.start();

                            // gửi bài về người chơi
                            for (BinhPlayer player : bTable.getPlayings()) {
                                long playerID = player.id;

                                ISession playerSession = player.currentSession;
                                GetPokerResponse getPoker = (GetPokerResponse) msgFactory.getResponseMessage(MessagesID.GET_POKER);
                                getPoker.setSuccess(ResponseCode.SUCCESS, player.id, player.username);
                                getPoker.session = player.currentSession;
                                getPoker.zoneId = ZoneID.BINH;

//                                getPoker.setBeginID(bTable.getCurrentPlayer().id);
                                getPoker.matchNum = bTable.matchNum;

                                getPoker.setBinhCards(player.getPokers());

                                if (playerSession == null) {
                                    playerSession = player.currentSession;
                                }

                                if (playerID == aSession.getUID()) {
                                    if (aResPkg != null) {
                                        aResPkg.addMessage(getPoker);
                                    } else {
                                        aSession.write(getPoker);
                                    }
                                } else {
                                    playerSession.write(getPoker);
                                }
                            }

                            return 1;
                        }

                        default:
                            break;
                    }
                    table.isPlaying = true;
                    room.setPlaying(true);
                }
            } else {
                resMatchStart.setFailure(ResponseCode.FAILURE, "Bàn đã bị huỷ. Bạn vui lòng chọn trận khác hoặc tạo ra bàn riêng của bạn.");
                aResPkg.addMessage(resMatchStart);
            }
        } catch (BusinessException ex) {
            resMatchStart.setFailure(ResponseCode.FAILURE, ex.getMessage());
//			mLog.error("warning: " + ex.getMessage());
            aResPkg.addMessage(resMatchStart);
        } catch (Throwable t) {
            resMatchStart.setFailure(ResponseCode.FAILURE, "Bị lỗi bắt đầu");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
            aResPkg.addMessage(resMatchStart);
        }
        /*finally {
         if (resMatchStart != null && aResPkg != null && isFail ) {
         aResPkg.addMessage(resMatchStart);
         }

         }*/
        return 1;
    }
}
