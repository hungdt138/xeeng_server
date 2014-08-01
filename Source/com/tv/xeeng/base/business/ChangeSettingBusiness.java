package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.ChangeSettingRequest;
import com.tv.xeeng.base.protocol.messages.ChangeSettingResponse;
import com.tv.xeeng.base.protocol.messages.OutResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.RoomDB;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.tienlen.data.TienLenPlayer;
import com.tv.xeeng.game.tienlen.data.TienLenTable;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class ChangeSettingBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory()
            .getLogger(ChangeSettingBusiness.class);
    private static final int TIMES = 4;

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) {
        int rtn = PROCESS_FAILURE;
        MessageFactory msgFactory = aSession.getMessageFactory();
        ChangeSettingResponse resChange = (ChangeSettingResponse) msgFactory
                .getResponseMessage(aReqMsg.getID());
        mLog.debug("[CHANGE SETTING]: Catch");
        resChange.session = aSession;
        try {
            // request message and its values
            ChangeSettingRequest rqChange = (ChangeSettingRequest) aReqMsg;
            Room room;

            // broadcast
            int zoneID = aSession.getCurrentZone();
            Zone chatZone = aSession.findZone(zoneID);
            room = chatZone.findRoom(rqChange.matchID);

            if (room != null) {
                SimpleTable table = (SimpleTable) room.getAttactmentData();
                if (rqChange.money <= 0) {
                    resChange.setFailure(ResponseCode.FAILURE,
                            "Số tiền bạn cài đặt ít hơn quy định!");
                    aResPkg.addMessage(resChange);

                } else {
                    if (!table.isPlaying) {
                        if (table.getTableSize() > rqChange.size) {
                            throw new BusinessException("Số người trong bàn đang nhiều hơn số người cài đặt");
                        }
                        RoomDB roomDb = new RoomDB();
                        int validate = roomDb.validateMoneySetting(
                                room.getPhongID(), (int) rqChange.money,
                                aSession.getUID());
                        switch (validate) {
                            case 1:
                                resChange
                                        .setFailure(ResponseCode.FAILURE,
                                                "Số tiền bạn có phải lớn hơn 4 lần tiền đặt cược!");
                                aResPkg.addMessage(resChange);
                                break;
                            case 2:
                                resChange.setFailure(ResponseCode.FAILURE,
                                        "Số tiền bạn cài đặt ít hơn quy định!");
                                aResPkg.addMessage(resChange);
                                break;
                            case 0:
                                if (rqChange.size > 0) {
                                    table.setMaximumPlayer(rqChange.size);
                                }
                                table.firstCashBet = rqChange.money;
                                resChange.size = rqChange.size;
                                switch (zoneID) {
                                    case ZoneID.PHOM: {
                                        PhomTable tableP = (PhomTable) table;
                                        /*
                                         * if (tableP.isAnyReady()) { resChange
                                         * .setFailure(ResponseCode.FAILURE,
                                         * "Bàn chơi đã sẵn sàng, không thể thay đổi được đâu!"
                                         * ); aSession.write(resChange); return 1; }
                                         */
                                        tableP.resetAllReady();
                                        tableP.chageMoney(rqChange.money);
                                        tableP.anCayMatTien = rqChange.anCayMatTien;
                                        tableP.isUKhan = rqChange.isUKhan;
                                        tableP.taiGuiUDen = rqChange.taiGuiUDen;
                                        resChange
                                                .setAnCayMatTien(rqChange.anCayMatTien);
                                        resChange.setUKhan(rqChange.isUKhan);
                                        resChange.setTaiGuiUDen(rqChange.taiGuiUDen);

                                        try {
                                            int i = 0;
                                            while (i < tableP.getPlayings().size()) {
                                                PhomPlayer player = tableP
                                                        .getPlayings().get(i);
                                                ISession playerSession = player.currentSession;

                                                if (player.cash < TIMES
                                                        * table.firstCashBet) {
                                                    OutResponse rqsOut = (OutResponse) msgFactory
                                                            .getResponseMessage(MessagesID.OUT);
                                                    rqsOut.setSuccess(
                                                            ResponseCode.SUCCESS,
                                                            player.id,
                                                            "Bạn không còn đủ tiền để chơi game này nữa.",
                                                            player.username, 1);
                                                    mLog.debug("Khong du tien roi: "
                                                            + player.username + ":"
                                                            + player.id);
                                                    rqsOut.session = playerSession;
                                                    playerSession.write(rqsOut);
                                                    if (playerSession != null) {
                                                        playerSession
                                                                .leftRoom(tableP.matchID);
                                                        room.left(playerSession);
                                                        playerSession.setRoom(null);
                                                    }
                                                    // remove from players list
                                                    tableP.remove(player);
                                                    i--;
                                                    rqsOut.setSuccess(
                                                            ResponseCode.SUCCESS,
                                                            player.id,
                                                            player.username
                                                            + " không còn đủ tiền để chơi game này nữa.",
                                                            player.username, 1);
                                                    tableP.broadcastMsg(rqsOut, table.getNewPlayings(), table.getNewWaitings(), player, false);
                                                }
                                                i++;
                                                // else {
                                                // playerSession.write(resChange);
                                                // i++;
                                                // }
                                            }
                                        } catch (Exception eas) {
                                            eas.printStackTrace();
                                        }
                                        break;
                                        // return PROCESS_OK;
                                    }

                                    case ZoneID.TIENLEN: {
                                        TienLenTable tableT = (TienLenTable) table;
                                        tableT.changeMoney(rqChange.money);
                                        tableT.resetAllReady();
                                        /*
                                         * if (tableT.isAnyReady()) { resChange
                                         * .setFailure(ResponseCode.FAILURE,
                                         * "Bàn chơi đã sẵn sàng, không thể thay đổi được đâu!"
                                         * ); aSession.write(resChange); return 1; }
                                         */
                                        try {
                                            int i = 0;
                                            tableT.setHidePoker(rqChange.isHidePoker);
                                            resChange.isHidePoker = rqChange.isHidePoker;
                                            // resChange.mCode = ResponseCode.SUCCESS;
                                            while (i < tableT.getPlayings().size()) {
                                                TienLenPlayer player = tableT
                                                        .getPlayings().get(i);
                                                ISession playerSession = player.currentSession;
                                                if (player.cash < TIMES
                                                        * table.firstCashBet) {
                                                    OutResponse rqsOut = (OutResponse) msgFactory
                                                            .getResponseMessage(MessagesID.OUT);
                                                    rqsOut.setSuccess(
                                                            ResponseCode.SUCCESS,
                                                            player.id,
                                                            "Bạn không còn đủ tiền để chơi game này nữa.",
                                                            player.username, 1);
                                                    mLog.debug("Khong du tien roi: "
                                                            + player.username + ":"
                                                            + player.id);
                                                    rqsOut.session = playerSession;
                                                    playerSession.write(rqsOut);
                                                    if (playerSession != null) {
                                                        playerSession
                                                                .leftRoom(tableT.matchID);
                                                        room.left(playerSession);
                                                        playerSession.setRoom(null);
                                                    }
                                                    // remove from players list
                                                    player.isOutGame = true;
                                                    tableT.remove(player);
                                                    i--;
                                                    rqsOut.setSuccess(
                                                            ResponseCode.SUCCESS,
                                                            player.id,
                                                            player.username
                                                            + " không còn đủ tiền để chơi game này nữa.",
                                                            player.username, 1);
                                                    tableT.broadcastMsg(rqsOut, table.getNewPlayings(), table.getNewWaitings(), player, false);
                                                }
                                                i++;
                                                // else {
                                                // playerSession.write(resChange);
                                                // i++;
                                                // }
                                            }
                                        } catch (Exception eas) {
                                            eas.printStackTrace();
                                        }
                                        // return PROCESS_OK;
                                        break;
                                    }

                                    case ZoneID.NEW_BA_CAY:
                                    case ZoneID.BAU_CUA_TOM_CA:
                                    case ZoneID.AILATRIEUPHU:
                                    case ZoneID.PIKACHU: {
                                        table.removeNotEnoughMoney(room);
                                        break;
                                    }

                                    default:
                                        break;
                                }

                                // Response
                                resChange.setZoneID(zoneID);
                                resChange.setMatchID(rqChange.matchID);
                                resChange.setMoney(rqChange.money);
                                resChange.setSuccess(ResponseCode.SUCCESS);
                                table.broadcastMsg(resChange, table.getNewPlayings(), table.getNewWaitings(), table.owner, true);

                                break; // for switch

                        }

                    } else {
                        resChange.setFailure(ResponseCode.FAILURE,
                                "Đang chơi bạn ơi!");
                        aResPkg.addMessage(resChange);
                    }
                }
            } else {
                resChange.setFailure(ResponseCode.FAILURE,
                        "Bàn chơi đã bị hủy!");
                aResPkg.addMessage(resChange);
            }
            rtn = PROCESS_OK;
        } catch (BusinessException be) {
            // response failure
            resChange.setFailure(ResponseCode.FAILURE, be.getMessage());
            // aSession.setLoggedIn(false);
            rtn = PROCESS_OK;

            aResPkg.addMessage(resChange);
        } catch (Throwable t) {
            // response failure
            resChange.setFailure(ResponseCode.FAILURE, "Đang bị lỗi!");
            // aSession.setLoggedIn(false);
            rtn = PROCESS_OK;
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
            aResPkg.addMessage(resChange);
        }

        return rtn;
    }
}
