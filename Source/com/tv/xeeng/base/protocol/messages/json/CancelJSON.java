/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelRequest;
import com.tv.xeeng.base.protocol.messages.CancelResponse;
import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.databaseDriven.RoomDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class CancelJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(CancelJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            CancelRequest matchCancel = (CancelRequest) aDecodingObj;
            if (jsonData.has("v")) {
                matchCancel.mMatchId = Long.parseLong(jsonData.getString("v"));
                return true;
            }
            matchCancel.mMatchId = jsonData.getLong("match_id");
            matchCancel.uid = jsonData.getLong("uid");
            try {
                matchCancel.isLogout = jsonData.getBoolean("is_logout");
            } catch (Exception e) {
                matchCancel.isLogout = false;
            }
            try {
                matchCancel.roomID = jsonData.getInt("room_id");
            } catch (Exception e) {

            }
            try {
                matchCancel.isOutOfGame = jsonData.getBoolean("is_out_game");
            } catch (Exception e) {
                matchCancel.isOutOfGame = false;
            }
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public void getPhong(int zoneId, StringBuilder sb) {
        try {
            RoomDB db = new RoomDB();
            List<NRoomEntity> rooms = db.getRooms(zoneId);

            int roomSize = rooms.size();
            //                                    byte[] roomBytes = new byte[roomSize * 5];
            for (int i = 0; i < roomSize; i++) {
                NRoomEntity entity = rooms.get(i);
                int playing;
                if (entity.getPhong() == null) {
                    playing = 0;
                } else {
                    playing = entity.getPhong().getPlaying();
                }

                sb.append(entity.getId()).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(playing).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(entity.getLv()).append(AIOConstants.SEPERATOR_BYTE_2);
            }

            if (roomSize > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }

            sb.append(AIOConstants.SEPERATOR_BYTE_3);
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
        }
    }

    private void getMobileInfo(CancelResponse matchCancel, StringBuilder sb, String sepEl, String sepArr, String sepDif) {
        sb.append(Long.toString(matchCancel.uid)).append(sepEl);
        sb.append(Long.toString(matchCancel.newOwner)).append(sepDif);

        if (matchCancel.zone != null) {
            List<SimpleTable> lstTables = matchCancel.zone.dumpNewWaitingTables(matchCancel.phongId);
            if (lstTables != null) {
                int tableSize = lstTables.size();

                for (int i = 0; i < tableSize; i++) {
                    SimpleTable table = lstTables.get(i);
                    if (table != null) {
                        sb.append(table.getTableIndex()).append(sepEl);
                        sb.append(table.getTableSize()).append(sepEl);
                        sb.append(table.firstCashBet).append(sepEl);
                        sb.append(table.matchID).append(sepEl);
                        sb.append(table.maximumPlayer).append(sepArr);
                    }
                }

                if (tableSize > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
            }
        }

        sb.append(sepDif);

        if (matchCancel.zone_id == ZoneID.TIENLEN) {
            if (matchCancel.next_id != -1) {
                sb.append(Long.toString(matchCancel.next_id)).append(sepEl);
                sb.append(matchCancel.isNewRound ? "1" : "0");
            }
        }
    }

    private void getFlashInfo(CancelResponse matchCancel, StringBuilder sb) {
        sb.append(Long.toString(matchCancel.uid)).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(Long.toString(matchCancel.newOwner)).append(AIOConstants.SEPERATOR_BYTE_3);

        if (matchCancel.zone != null) {
            getPhong(matchCancel.zone.getZoneId(), sb);
            List<SimpleTable> lstTables = matchCancel.zone.dumpNewWaitingTables(matchCancel.phongId);
            if (lstTables != null) {
                int tableSize = lstTables.size();

                for (int i = 0; i < tableSize; i++) {
                    SimpleTable table = lstTables.get(i);
                    if (table != null) {
                        sb.append(table.getTableIndex()).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.getTableSize()).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.firstCashBet).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.matchID).append(AIOConstants.SEPERATOR_BYTE_1);

                        sb.append(table.name).append(AIOConstants.SEPERATOR_BYTE_1);
                        String ownerName;
                        ownerName = table.owner.username;

                        sb.append(ownerName).append(AIOConstants.SEPERATOR_BYTE_2);
                    }
                }

                if (tableSize > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
            }
        }

        sb.append(AIOConstants.SEPERATOR_BYTE_3);

        if (matchCancel.zone_id == ZoneID.TIENLEN) {

            if (matchCancel.next_id != -1) {
                sb.append(Long.toString(matchCancel.next_id)).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(matchCancel.isNewRound ? "1" : "0");
            }
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            CancelResponse matchCancel = (CancelResponse) aResponseMessage;
            if (matchCancel.session != null && matchCancel.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(matchCancel.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (matchCancel.mCode == ResponseCode.FAILURE) {
                    sb.append(matchCancel.mErrorMsg);
                } else {
                    if (matchCancel.session.isMobileDevice()) {
                        getMobileInfo(matchCancel, sb, AIOConstants.SEPERATOR_BYTE_1, AIOConstants.SEPERATOR_BYTE_2, AIOConstants.SEPERATOR_BYTE_3);
                    } else {
                        getFlashInfo(matchCancel, sb);
                    }

                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }

            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", matchCancel.mCode);
            if (matchCancel.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", matchCancel.mErrorMsg);
                encodingObj.put("is_game_playing", matchCancel.isGamePlaying);
                encodingObj.put("uid", matchCancel.uid);
            } else if (matchCancel.mCode == ResponseCode.SUCCESS) {

                if (matchCancel.session != null && matchCancel.session.getByteProtocol() > 0) {
                    StringBuilder sb = new StringBuilder();
                    if (matchCancel.session.isMobileDevice()) {
                        getMobileInfo(matchCancel, sb, AIOConstants.SEPERATOR_ELEMENT, AIOConstants.SEPERATOR_ARRAY, AIOConstants.SEPERATOR_DIFF_ELEMENT);
                    } else {
                        getFlashInfo(matchCancel, sb);
                    }
                    encodingObj.put("v", sb.toString());
                    return encodingObj;
                }

                encodingObj.put("uid", matchCancel.uid);
                encodingObj.put("is_user_playing", matchCancel.isUserPlaying);
                encodingObj.put("newOwner", matchCancel.newOwner);
                //thomc

                JSONArray arrTables = new JSONArray();
                if (matchCancel.zone != null) {
                    List<SimpleTable> lstTables = matchCancel.zone.dumpNewWaitingTables(matchCancel.phongId);

                    if (lstTables != null) {
                        int tableSize = lstTables.size();
                        if (matchCancel.session != null && matchCancel.session.getByteProtocol() > 0) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < tableSize; i++) {
                                SimpleTable table = lstTables.get(i);
                                if (table != null) {
                                    sb.append(table.getTableIndex()).append(AIOConstants.SEPERATOR_ELEMENT);
                                    sb.append(table.getTableSize()).append(AIOConstants.SEPERATOR_ELEMENT);
                                    sb.append(table.firstCashBet).append(AIOConstants.SEPERATOR_ELEMENT);
                                    sb.append(table.matchID).append(AIOConstants.SEPERATOR_ARRAY);
                                }
                            }

                            if (sb.length() > 0) {
                                //                    System.out.println("capacity " + sb.length());
                                sb.deleteCharAt(sb.length() - 1);
                            }
//                             enterRoom.debugSb.append(sb);
                            encodingObj.put("v", sb.toString());

                            return encodingObj;
                        }

                        for (int i = 0; i < tableSize; i++) {
                            try {
                                SimpleTable table = lstTables.get(i);
                                if (table != null) {
                                    JSONObject jRoom = new JSONObject();
                                    jRoom.put("id", table.getPhongID());
                                    jRoom.put("index", table.getTableIndex());
                                    jRoom.put("name", table.name);
                                    jRoom.put("money", table.firstCashBet);
                                    jRoom.put("match_id", table.matchID);
                                    jRoom.put("numberPlaying", table.getTableSize());
                                    jRoom.put("owner", table.owner.username);
                                    arrTables.put(jRoom);
                                }
                            } catch (Exception ex) {
                                try {
                                    mLog.error("error get all rooms ", ex);
                                } catch (Exception exx) {
                                }
                            }
                        }
                    }
                }

                encodingObj.put("rooms", arrTables);
                if (matchCancel.zone_id == ZoneID.TIENLEN) {
                    //System.out.println("Cancel match chạy vào zone tiến lên!");
                    if (matchCancel.next_id != -1) {
                        encodingObj.put("next_id", matchCancel.next_id);
                        encodingObj.put("isNewRound", matchCancel.isNewRound);
                    }
                }
                try {
                    encodingObj.put("message", matchCancel.message);
                } catch (Exception e) {
                }
            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
