package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelResponse;
import com.tv.xeeng.base.protocol.messages.OutResponse;
import com.tv.xeeng.base.protocol.messages.RestartRequest;
import com.tv.xeeng.base.protocol.messages.RestartResponse;
import com.tv.xeeng.base.protocol.messages.StartRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.tienlen.data.TienLenTable;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;







public class RestartBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(RestartBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[RESTART] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        RestartResponse resMatchReturn = (RestartResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        CancelResponse resCancelMatch = (CancelResponse) msgFactory.getResponseMessage(MessagesID.MATCH_CANCEL);
        try {
            RestartRequest rqReturn = (RestartRequest) aReqMsg;
            Zone bacayZone = aSession.findZone(aSession.getCurrentZone());
            Room room = bacayZone.findRoom(rqReturn.mMatchId);
            resMatchReturn.setZoneID(aSession.getCurrentZone());
            if (room != null) {
                mLog.debug("[RESTART] Current room = " + room.getName());

                switch (aSession.getCurrentZone()) {
                    
                    case ZoneID.PHOM: {
                        PhomTable table = (PhomTable) room.getAttactmentData();
                        //Remove player has not enough money
                        System.out.println("So nguoi trong room:" + table.getPlayings().size() + "  min bet  " + table.firstCashBet);
//                        try {
//                            for (PhomPlayer player : table.getPlayings()) {
//                                if (player.cash < table.firstCashBet) {
//                                    OutResponse rqsOut =
//                                            (OutResponse) msgFactory.getResponseMessage(MessagesID.OUT);
//                                    rqsOut.setSuccess(ResponseCode.SUCCESS, player.id,
//                                            "Bạn không còn đủ tiền để chơi game này nữa.", player.username, 1);
//                                    mLog.debug("Khong du tien roi: " + player.username + ":" + player.id);
//                                    ISession playerSession = aSession.getManager().findSession(player.id);
//                                    playerSession.write(rqsOut);
//                                    if (playerSession != null) {
//                                        playerSession.leftRoom(rqReturn.mMatchId);
//                                        room.left(playerSession);
//                                    }
//                                    //remove from players list
//                                    table.remove(player);
//                                    rqsOut.setSuccess(ResponseCode.SUCCESS, player.id,
//                                            player.username + " không còn đủ tiền để chơi game này nữa.", player.username, 1);
//                                    room.broadcastMessage(rqsOut, aSession, true);
//                                }
//                            }
//                        } catch (Exception eas) {
//                            eas.printStackTrace();
//                        }
                        if (table.getPlayings().size() + table.getWaitings().size() < 2) {
                            resMatchReturn.setFailure(ResponseCode.FAILURE,
                                    "Chưa đủ người chơi.", false);
                        } else {
                            table.reset();
                            resMatchReturn.setPhomSuccess(ResponseCode.SUCCESS, table.owner,
                                    table.getPlayings(), rqReturn.mMatchId, room.getName());

                            //room.broadcastMessage(resMatchReturn, aSession, false);

                            IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_START);
                            StartRequest rqMatchStart =
                                    (StartRequest) msgFactory.getRequestMessage(MessagesID.MATCH_START);
                            rqMatchStart.mMatchId = rqReturn.mMatchId;
                            try {
                                business.handleMessage(aSession, rqMatchStart, aResPkg);
                            } catch (ServerException se) {
                                se.printStackTrace();
                            }
                        }
                        break;
                    }
                    case ZoneID.TIENLEN: {
                        TienLenTable table = (TienLenTable) room.getAttactmentData();
                        //Remove player has not enough money
                        System.out.println("So nguoi trong room:" + table.getPlayings().size() + "  min bet  " + table.firstCashBet);
//                        try {
//                            for (PhomPlayer player : table.getPlayings()) {
//                                if (player.cash < table.firstCashBet) {
//                                    OutResponse rqsOut =
//                                            (OutResponse) msgFactory.getResponseMessage(MessagesID.OUT);
//                                    rqsOut.setSuccess(ResponseCode.SUCCESS, player.id,
//                                            "Bạn không còn đủ tiền để chơi game này nữa.", player.username, 1);
//                                    mLog.debug("Khong du tien roi: " + player.username + ":" + player.id);
//                                    ISession playerSession = aSession.getManager().findSession(player.id);
//                                    playerSession.write(rqsOut);
//                                    if (playerSession != null) {
//                                        playerSession.leftRoom(rqReturn.mMatchId);
//                                        room.left(playerSession);
//                                    }
//                                    //remove from players list
//                                    table.remove(player);
//                                    rqsOut.setSuccess(ResponseCode.SUCCESS, player.id,
//                                            player.username + " không còn đủ tiền để chơi game này nữa.", player.username, 1);
//                                    room.broadcastMessage(rqsOut, aSession, true);
//                                }
//                            }
//                        } catch (Exception eas) {
//                            eas.printStackTrace();
//                        }
                        if (aSession.getUID() == table.owner.id) {
                            if (table.getPlayings().size() + table.getWaitings().size() < 2) {
                                resMatchReturn.setFailure(ResponseCode.FAILURE,
                                        "Chưa đủ người chơi.", false);
                            } else if (table.owner.notEnoughMoney()) {
                                resMatchReturn.setFailure(ResponseCode.FAILURE,
                                        "Bạn không đủ tiền để chơi tiếp!", false);
                            } else {
                                table.resetTable();
                                resMatchReturn.setTienLenSuccess(ResponseCode.SUCCESS, table.owner,
                                        table.getPlayings(), rqReturn.mMatchId, room.getName());
//                                if (aSession.getMobile()) {
//                                    aSession.write(resMatchReturn);
//                                }
//                            room.broadcastMessage(resMatchReturn, aSession, false);
                                IBusiness business = msgFactory.getBusiness(MessagesID.MATCH_START);
                                StartRequest rqMatchStart =
                                        (StartRequest) msgFactory.getRequestMessage(MessagesID.MATCH_START);
                                rqMatchStart.mMatchId = rqReturn.mMatchId;
                                try {
                                    business.handleMessage(aSession, rqMatchStart, aResPkg);
                                } catch (ServerException se) {
                                    se.printStackTrace();
                                }
                            }
                        } else {
                            resMatchReturn.setFailure(ResponseCode.FAILURE,
                                    "Bạn không phải là chủ bàn, không được quyền bắt đầu!", false);
                        }
                        break;
                    }
                    
                  
                    default:
                        break;
                }


            } else {
                resMatchReturn.setFailure(ResponseCode.FAILURE,
                        "Bạn cần tham gia vào một trận trước khi chơi.", false);
            }

        } catch (Throwable t) {
            resMatchReturn.setFailure(ResponseCode.FAILURE, "Bị lỗi " + t.toString(), false);
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resMatchReturn != null)) {
                //aResPkg.addMessage(resMatchReturn);
            }
        }
        return 1;
    }
}
