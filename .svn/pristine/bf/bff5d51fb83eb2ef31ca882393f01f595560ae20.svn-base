package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.KickOutRequest;
import com.tv.xeeng.base.protocol.messages.KickOutResponse;
import com.tv.xeeng.base.protocol.messages.OutResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaException;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaTable;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.lieng.data.LiengTable;
import com.tv.xeeng.game.newbacay.data.NewBaCayException;
import com.tv.xeeng.game.newbacay.data.NewBaCayTable;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.pikachu.datta.PikachuTable;
import com.tv.xeeng.game.poker.data.PokerPlayer;
import com.tv.xeeng.game.poker.data.PokerTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.tienlen.data.TienLenPlayer;
import com.tv.xeeng.game.tienlen.data.TienLenTable;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuTable;
import com.tv.xeeng.game.xam.data.SamPlayer;
import com.tv.xeeng.game.xam.data.SamTable;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class KickOutBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(KickOutBusiness.class);
    private static final String NOT_OWNER_PERSON="Bạn không phải là chủ bàn - không có quyền đuổi người khác!";
    private static final String PLAYER_OUT="Người chơi này đã thoát rồi!";
    private static final String PLAYING_TABLE="Bạn không thể đuổi người khác khi bàn đang chơi!";
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[KICK OUT] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        KickOutResponse resKickOut = (KickOutResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resKickOut.session = aSession;
        try {
            KickOutRequest rqKickOut = (KickOutRequest) aReqMsg;
            Zone bacayZone = aSession.findZone(aSession.getCurrentZone());
            Room currentRoom = bacayZone.findRoom(rqKickOut.mMatchId);
            if (currentRoom != null) {
                OutResponse broadcastMsg = (OutResponse) msgFactory.getResponseMessage(MessagesID.OUT);
                switch (aSession.getCurrentZone()) {
                    
                    case ZoneID.PHOM: {
                        PhomTable currentTable = (PhomTable) currentRoom.getAttactmentData();
                        if (aSession.getUID() == currentTable.owner.id) {
                            PhomPlayer player = (PhomPlayer) currentTable.findPlayer(rqKickOut.uid);
                            if (player == null) {
                                resKickOut.setFailure(ResponseCode.FAILURE, "Người chơi này đã thoát rồi!");
                            } else if (currentTable.isPlaying) {
                                resKickOut.setFailure(ResponseCode.FAILURE, "Bàn đang chơi và " + player.username + " đang chơi. Bạn không thể đuổi hắn ra ngoài được. Chờ hết ván đi!");
                            } else {
//                                resKickOut.session = player.currentSession;
                                broadcastMsg.setSuccess(ResponseCode.SUCCESS,rqKickOut.uid, player.username + " bị chủ bàn đá ra ngoài", player.username, 0);
                                // send broadcast msg to friends
                                currentTable.broadcastMsg(broadcastMsg, currentTable.getNewPlayings(), currentTable.getNewWaitings(), player, true);
                                /*currentRoom.broadcastMessage(broadcastMsg,
                                        aSession, true);*/

                                resKickOut.setSuccess(ResponseCode.SUCCESS);                                

                                Room room = player.currentSession.leftRoom(rqKickOut.mMatchId);
                                player.currentSession.setLastFP(System.currentTimeMillis() - 20000); //for fast play
                                
                                if (room != null) {
                                    room.left(player.currentSession);
                                } else {
                                    mLog.error("Kick out error room is null : " + rqKickOut.mMatchId);
                                }
                                currentTable.remove(player);
                                currentTable.dontWantAnyUser = true;
                                player.currentSession.setRoom(null);

                            }
                        } else {
                            resKickOut.setFailure(ResponseCode.FAILURE, "Bạn không phải là chủ bàn - không có quyền đuổi người khác!");
                        }
                        break;
                    }
                    case ZoneID.TIENLEN: {
                        TienLenTable currentTable = (TienLenTable) currentRoom.getAttactmentData();
                        if (aSession.getUID() == currentTable.owner.id) {
                            TienLenPlayer player = (TienLenPlayer) currentTable.findPlayer(rqKickOut.uid);
                            if (player == null) {
                                resKickOut.setFailure(
                                        ResponseCode.FAILURE, "Người chơi này đã thoát rồi!");
                            } else if (currentTable.isPlaying) {
                                resKickOut.setFailure(
                                        ResponseCode.FAILURE, "Bàn đang chơi và " + player.username + " đang chơi. Bạn không thể đuổi ra ngoài được. Chờ hết ván đi!");
                            } else {
                                resKickOut.session = player.currentSession;
                                broadcastMsg.setSuccess(ResponseCode.SUCCESS, rqKickOut.uid, player.username + " bị chủ bàn đá ra ngoài", player.username, 0);
                                // send broadcast msg to friends
                                currentTable.broadcastMsg(broadcastMsg, currentTable.getNewPlayings(), currentTable.getNewWaitings(), player, true);
                                /*currentRoom.broadcastMessage(broadcastMsg,
                                        aSession, true);*/

                                resKickOut.setSuccess(ResponseCode.SUCCESS);

                                Room room = player.currentSession.leftRoom(rqKickOut.mMatchId);
                                player.currentSession.setLastFP(System.currentTimeMillis() - 20000); //for fast play
                                if (room != null) {
                                    room.left(player.currentSession);
                                } else {
                                    mLog.error("Kick out error room is null : " + rqKickOut.mMatchId);
                                }
                                player.isOutGame = true;
                                currentTable.remove(player);
                                currentTable.dontWantAnyUser = true;
                                player.currentSession.setRoom(null);                                

                            }
                        } else {
                            resKickOut.setFailure(ResponseCode.FAILURE, "Bạn không phải là chủ bàn - không có quyền đuổi người khác!");
                        }
                        break;
                    }
                    case ZoneID.SAM: {
                        SamTable currentTable = (SamTable) currentRoom.getAttactmentData();
                        if (aSession.getUID() == currentTable.owner.id) {
                            SamPlayer player = (SamPlayer) currentTable.findPlayer(rqKickOut.uid);
                            if (player == null) {
                                resKickOut.setFailure(ResponseCode.FAILURE, "Người chơi này đã thoát rồi!");
                            } else if (currentTable.isPlaying) {
                                resKickOut.setFailure(ResponseCode.FAILURE, "Bàn đang chơi và " + player.username + " đang chơi. Bạn không thể đuổi ra ngoài được. Chờ hết ván đi!");
                            } else {
                                resKickOut.session = player.currentSession;
                                broadcastMsg.setSuccess(ResponseCode.SUCCESS, rqKickOut.uid, player.username + " bị chủ bàn đá ra ngoài", player.username, 0);
                                // send broadcast msg to friends
                                currentTable.broadcastMsg(broadcastMsg, currentTable.getNewPlayings(), currentTable.getNewWaitings(), player, true);
                                /*currentRoom.broadcastMessage(broadcastMsg,
                                        aSession, true);*/

                                resKickOut.setSuccess(ResponseCode.SUCCESS);

                                Room room = player.currentSession.leftRoom(rqKickOut.mMatchId);
                                player.currentSession.setLastFP(System.currentTimeMillis() - 20000); //for fast play
                                if (room != null) {
                                    room.left(player.currentSession);
                                } else {
                                    mLog.error("Kick out error room is null : " + rqKickOut.mMatchId);
                                }
                                player.isOut = true;
                                currentTable.remove(player);
                                currentTable.dontWantAnyUser = true;
                                player.currentSession.setRoom(null);
                            }
                        } else {
                            resKickOut.setFailure(ResponseCode.FAILURE, "Bạn không phải là chủ bàn - không có quyền đuổi người khác!");
                        }
                        break;
                    }
                    case ZoneID.POKER:
                    {
                    	PokerTable currentTable = (PokerTable) currentRoom.getAttactmentData();
                    	if (aSession.getUID() == currentTable.owner.id) {
                    		PokerPlayer player = (PokerPlayer) currentTable.findPlayer(rqKickOut.uid);
                    		if (player == null) {
                    			resKickOut.setFailure(ResponseCode.FAILURE, "Người chơi này đã thoát rồi");
                    		} else if (currentTable.isPlaying) {
                    			resKickOut.setFailure(ResponseCode.FAILURE, "Bàn đang chơi và " + player.username + " đang chơi. Bạn không thể đuổi hắn ra ngoài được. Chờ hết ván đi!");
                    		} else {
                    			resKickOut.session = player.currentSession;
                    			
                    			// Gửi bản tin broadcast tới tất cả người chơi trong phòng
                    			broadcastMsg.setSuccess(ResponseCode.SUCCESS, rqKickOut.uid, player.username + " bị chủ bàn đá ra ngoài", player.username, 0);
                    			currentTable.broadcastMsg(broadcastMsg, currentTable.getNewPlayings(), currentTable.getNewWaitings(), player, true);
                    			
                    			resKickOut.setSuccess(ResponseCode.SUCCESS);
                    			
                    			Room room = player.currentSession.leftRoom(rqKickOut.mMatchId);
                    			player.currentSession.setLastFP(System.currentTimeMillis() - 20000);
                    			
                    			if (room != null) {
                    				room.left(player.currentSession);
                    			} else {
                    				mLog.error("Kick out error room is null : " + rqKickOut.mMatchId);
                    			}
                    			
                    			currentTable.remove(player);
                    			currentTable.dontWantAnyUser = true;
                    			player.currentSession.setRoom(null);
                    		}
                    	} else {
                    		resKickOut.setFailure(ResponseCode.FAILURE, "Bạn không phải là chủ bàn - không có quyền đuổi người khác!");
                    	}
                    	break;
                    }
                    case ZoneID.BAU_CUA_TOM_CA:
                    {
                        BauCuaTomCaTable currentTable =(BauCuaTomCaTable) currentRoom.getAttactmentData();
                        currentTable.kickout(aSession.getUID(), rqKickOut);
                        resKickOut = null;
                        break;
                    }
                    case ZoneID.NEW_BA_CAY:
                    {
                        NewBaCayTable currentTable =(NewBaCayTable) currentRoom.getAttactmentData();
                        currentTable.kickout(aSession.getUID(), rqKickOut);
                        resKickOut = null;
                        break;
                    }
                     case ZoneID.AILATRIEUPHU:
                    {
                        TrieuPhuTable currentTable =(TrieuPhuTable) currentRoom.getAttactmentData();
                        currentTable.kickout(aSession.getUID(), rqKickOut);
                        resKickOut = null;
                        break;
                    } 

                    case ZoneID.PIKACHU:
                    {
                        PikachuTable currentTable =(PikachuTable) currentRoom.getAttactmentData();
                        currentTable.kickout(aSession.getUID(), rqKickOut);
                        resKickOut = null;
                        break;
                    } 	
                    
                    default:
                        break;
                }

            } else {
                resKickOut.setFailure(ResponseCode.FAILURE,
                        "Bạn đã thoát khỏi bàn!");
            }

        } 
        catch(NewBaCayException ex)
        {
            mLog.debug(ex.getMessage());
            resKickOut.setFailure(ResponseCode.FAILURE, ex.getMessage());
        }
        catch(BauCuaTomCaException ex)
        {
            mLog.debug(ex.getMessage());
            resKickOut.setFailure(ResponseCode.FAILURE, ex.getMessage());
        }
        catch(BusinessException ex)
        {
            mLog.debug(ex.getMessage());
            resKickOut.setFailure(ResponseCode.FAILURE, ex.getMessage());
        }
        catch (Throwable t) {
            resKickOut.setFailure(ResponseCode.FAILURE, "Bị lỗi kick out");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resKickOut != null && resKickOut.mCode == ResponseCode.FAILURE)) {
                aResPkg.addMessage(resKickOut);
            }
        }
        return 1;
    }
}
