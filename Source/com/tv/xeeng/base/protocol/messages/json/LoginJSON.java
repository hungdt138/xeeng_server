package com.tv.xeeng.base.protocol.messages.json;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.LoginRequest;
import com.tv.xeeng.base.protocol.messages.LoginResponse;
import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.game.data.*;
import com.tv.xeeng.game.tienlen.data.TienLenTable;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.List;

public class LoginJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(LoginJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            // request data
            JSONObject jsonData = (JSONObject) aEncodedObj;
            // plain obj
            LoginRequest login = (LoginRequest) aDecodingObj;
            // decoding

            if (jsonData.has("username")) {
                login.loginName = jsonData.getString("username");
                if (jsonData.has("v")) {
                    String v = jsonData.getString("v");
                    String[] arrValues;

                    arrValues = v.split(AIOConstants.STRING_SEPERATOR_ELEMENT);
                    login.mPassword = arrValues[0];
                    login.mobileVersion = arrValues[1];
                    login.protocol = Integer.parseInt(arrValues[2]);
                    login.partnerId = Integer.parseInt(arrValues[3]);
                    return true;
                }
            }

            if (jsonData.has("v")) {
                try {
                    String v = jsonData.getString("v");
                    String[] arrValues;
                    arrValues = v.split(AIOConstants.SEPERATOR_BYTE_1);
                    int arrSize = arrValues.length;
                    if (arrSize > 4) {
                        //mobile version
                        login.loginName = arrValues[0];
                        login.mPassword = arrValues[1];
                        login.mobileVersion = arrValues[2];
                        login.protocol = Integer.parseInt(arrValues[3]);
                        login.partnerId = Integer.parseInt(arrValues[4]);
                        if (arrSize > 6) {
                            try {
                                //contain game position
                                login.isMxh = arrValues[5].equals("1");
                                login.gamePosition = Integer.parseInt(arrValues[6]);
                                String refCode = arrValues[7];
                                login.refCode = refCode;
                                login.device = arrValues[8];

                                String[] deviceParts = login.device.split(";");
                                if (deviceParts.length >= 3) {
                                    login.setOsName(deviceParts[0]);
                                    login.setOsVersion(deviceParts[1]);
                                    login.setOsMAC(deviceParts[2]);
                                }
//                                if (arrSize > 9) {
//                                    login.deviceId = Long.parseLong(arrValues[9]);
//                                }
                            } catch (Exception ex) {
                            }

                        }
                        return true;
                    } else if (arrValues.length == 4) {
                        //flash version
                        login.loginName = arrValues[0];
                        login.mPassword = arrValues[1];
                        login.protocol = Integer.parseInt(arrValues[2]);
                        login.zoneId = Integer.parseInt(arrValues[3]);

                        return true;
                    }
                } catch (Exception ex) {
                    mLog.error(ex.getMessage(), ex);
                }
            }

            if (jsonData.has("password")) {
                login.mPassword = jsonData.getString("password");
            }

            if (jsonData.has("cp")) {
                login.cp = jsonData.getString("cp");
            }

            if (jsonData.has("flashVersion")) {
                login.flashVersion = jsonData.getString("flashVersion");
            }
            if (jsonData.has("mobileVersion")) {
                login.mobileVersion = jsonData.getString("mobileVersion");
            }

            if (jsonData.has("partnerId")) {
                login.partnerId = jsonData.getInt("partnerId");
            }
            if (jsonData.has("screen")) {
                login.screen = jsonData.getString("screen");
            }
            if (jsonData.has("device")) {
                login.device = jsonData.getString("device");
            }

            if (jsonData.has("zone")) {
                login.zoneId = jsonData.getInt("zone");
            }

            if (jsonData.has("protocol")) {
                login.protocol = jsonData.getInt("protocol");
            }

            return true;

        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    private JSONArray getAllRooms(LoginResponse response) {
        //fill get All room to flash

        JSONArray arrRooms = new JSONArray();
        if (response.lstRooms != null) {
            for (int i = 0; i < response.lstRooms.size(); i++) {
                try {

                    NRoomEntity roomEntity = response.lstRooms.get(i);
                    JSONObject jRoom = new JSONObject();

                    jRoom.put("id", roomEntity.getId());

                    jRoom.put("level", roomEntity.getLevel());
                    jRoom.put("number", roomEntity.getNumber());
                    jRoom.put("playing", roomEntity.getPhong().getPlaying());
                    jRoom.put("numTables", roomEntity.getNumTables());
                    jRoom.put("capacity", roomEntity.getAvailable());
                    jRoom.put("minCash", roomEntity.getMinCash());

                    arrRooms.put(jRoom);
                } catch (JSONException ex) {
                    mLog.error(" get allRoom error", ex);
                }
            }
        }
        return arrRooms;
    }

    private JSONArray enterFirstRoom(LoginResponse response) {

        List<SimpleTable> tables = response.lstTables;
        JSONArray arrRooms = new JSONArray();

        if (tables != null) {
            for (int i = 0; i < tables.size(); i++) {
                SimpleTable table = tables.get(i);
                if (table != null) {
                    try {
                        JSONObject jRoom = new JSONObject();
                        jRoom.put("id", table.getPhongID());
                        jRoom.put("index", table.getTableIndex());
                        jRoom.put("name", table.name);
                        jRoom.put("money", table.firstCashBet);
                        jRoom.put("match_id", table.matchID);
                        jRoom.put("numberPlaying", table.getTableSize());
                        if (response.newZoneId == ZoneID.TIENLEN) {
                            jRoom.put("owner", ((TienLenTable) table).owner.username);
                        } else {
                            jRoom.put("owner", table.owner.username);
                        }
                        arrRooms.put(jRoom);

                    } catch (Exception ex) {
                        mLog.error("enter  room json error", ex);
                        try {
                            if (table.owner == null || table.owner.username == null || table.owner.username == "") {
                                mLog.error("delete invalid room when enterzone");
                                table.getRoom().allLeft();
                            }
                        } catch (Exception exx) {
                            mLog.error("retry enter tienlen room json error", exx);
                        }
                    }
                }
            }
        }

        return arrRooms;

    }

    private void getMobileLoginInfo(LoginResponse login, StringBuilder sb) {
        if (login.mCode == ResponseCode.SUCCESS) {
            sb.append(login.mUid).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(login.money)).append(AIOConstants.SEPERATOR_BYTE_1);
//            if (login.isMxh) {
//                sb.append(login.usrEntity.mIsMale ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
//                sb.append(login.usrEntity.xeeng).append(AIOConstants.SEPERATOR_BYTE_1);
//                sb.append(Integer.toString(login.usrEntity.hair)).append(AIOConstants.SEPERATOR_BYTE_1);
//                sb.append(Integer.toString(login.usrEntity.glasses)).append(AIOConstants.SEPERATOR_BYTE_1);
//                sb.append(Integer.toString(login.usrEntity.shirt)).append(AIOConstants.SEPERATOR_BYTE_1);
//                sb.append(Integer.toString(login.usrEntity.jeans)).append(AIOConstants.SEPERATOR_BYTE_1);
//                sb.append(Long.toString(login.usrEntity.avFileId));
//
//            } else {
                sb.append(login.avatarID).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(login.avatarVerion);
//            }

            if (login.newVer.length() > 0) {
                sb.append(AIOConstants.SEPERATOR_BYTE_1).append(login.linkDown);
                sb.append(AIOConstants.SEPERATOR_BYTE_1).append(login.version.newsUpdate);
                sb.append(AIOConstants.SEPERATOR_BYTE_1).append(login.isNeedUpdate ? "1" : "0");
            }

            if (login.session.getByteProtocol() > AIOConstants.PROTOCOL_MODIFY_MID) {
                if (login.active != null && !login.active.equals("")) {
                    sb.append(AIOConstants.SEPERATOR_BYTE_3).append(login.active);
                }
            }

            sb.append(AIOConstants.SEPERATOR_BYTE_3).append(login.numberOnline);
            
            // Ván chơi cũ chưa kết thúc
            if (login.lastMatchId > 0) {
                String mess = "Ván bài của bạn trong phòng " + login.lastMatchId + " chưa kết thúc. Hãy chiến tiếp bạn nhé!";
                sb.append(AIOConstants.SEPERATOR_BYTE_3).append(mess);
                sb.append(AIOConstants.SEPERATOR_BYTE_1).append(login.zone_id);
                sb.append(AIOConstants.SEPERATOR_BYTE_1).append(login.lastMatchId);
            }
        } else {
            sb.append(login.mErrorMsg);
        }
    }

    private void getPhong(LoginResponse response, StringBuilder sb) {
        if (response.lstRooms != null) {
            int phongSize = response.lstRooms.size();
            for (int i = 0; i < phongSize; i++) {

                NRoomEntity roomEntity = response.lstRooms.get(i);
                sb.append(roomEntity.getId()).append(AIOConstants.SEPERATOR_BYTE_1);
                int playing = 0;
                if (roomEntity.getPhong() != null) {
                    playing = roomEntity.getPhong().getPlaying();
                }

                sb.append(Integer.toString(playing)).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(roomEntity.getLv())).append(AIOConstants.SEPERATOR_BYTE_2);

            }
            if (phongSize > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }

        }

    }

    private void getRoom(LoginResponse response, StringBuilder sb) {

        List<SimpleTable> tables = response.lstTables;

        if (tables != null) {
            int tableSize = tables.size();
            for (int i = 0; i < tableSize; i++) {
                SimpleTable table = tables.get(i);
                if (table != null) {
                    sb.append(table.getTableIndex()).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(table.getTableSize()).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(table.firstCashBet).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(table.matchID).append(AIOConstants.SEPERATOR_BYTE_1);

                    sb.append(table.name).append(AIOConstants.SEPERATOR_BYTE_1);
                    String ownerName;
                    if (response.newZoneId == ZoneID.TIENLEN) {
                        ownerName = ((TienLenTable) table).owner.username;
                    } else {
                        ownerName = table.owner.username;
                    }

                    sb.append(ownerName).append(AIOConstants.SEPERATOR_BYTE_2);

                }
            }

            if (tableSize > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
    }

    private void getFlashLoginInfo(LoginResponse login, StringBuilder sb) {
        if (login.mCode == ResponseCode.SUCCESS) {
            sb.append(login.mUid).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(login.avatarID).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(login.level).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(login.money).append(AIOConstants.SEPERATOR_BYTE_3);
            getPhong(login, sb);
            sb.append(AIOConstants.SEPERATOR_BYTE_3);
            getRoom(login, sb);
        } else {
            sb.append(login.mErrorMsg).append(AIOConstants.SEPERATOR_BYTE_1);
        }

    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            LoginResponse login = (LoginResponse) aResponseMessage;
            if (login.session != null && login.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                StringBuilder valueSb = new StringBuilder();
                valueSb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                valueSb.append(Integer.toString(login.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (login.session.isMobileDevice()) {
                    getMobileLoginInfo(login, valueSb);
                } else {
                    getFlashLoginInfo(login, valueSb);
                }

                encodingObj.put("v", valueSb.toString());

                return encodingObj;
            }

            // put response data into json object
            encodingObj.put("mid", aResponseMessage.getID());

            encodingObj.put("code", login.mCode);
            if (login.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", login.mErrorMsg);
            } else if (login.mCode == ResponseCode.SUCCESS) {
                if (login.session != null && login.session.getByteProtocol() > AIOConstants.PROTOCOL_PRIMITIVE && login.session.isMobileDevice()) {
                    //write to log
                    StringBuilder valueSb = new StringBuilder();
                    valueSb.append(login.mUid).append(AIOConstants.SEPERATOR_ELEMENT);
                    valueSb.append(Long.toString(login.money)).append(AIOConstants.SEPERATOR_ELEMENT);
                    valueSb.append(login.avatarID).append(AIOConstants.SEPERATOR_ELEMENT);
                    valueSb.append(login.avatarVerion);
                    if (login.newVer.length() > 0) {
                        valueSb.append(AIOConstants.SEPERATOR_ELEMENT).append(login.newVer);
                        valueSb.append(AIOConstants.SEPERATOR_ELEMENT).append(login.linkDown);
                        valueSb.append(AIOConstants.SEPERATOR_ELEMENT).append(login.isNeedUpdate);
//                        valueSb.append(AIOConstants.SEPERATOR_ELEMENT).append(login.linkDown).
//                                append(AIOConstants.SEPERATOR_ELEMENT).append("Phiên bản mới giao diện mới");
                    }
//                    login.debugSb.append(valueSb);

                    encodingObj.put("v", valueSb.toString());

                    return encodingObj;
                }

                if (login.session != null && login.session.getByteProtocol() > AIOConstants.PROTOCOL_PRIMITIVE && !login.session.isMobileDevice()) {
                    //login for flash new version
                    StringBuilder sb = new StringBuilder();
                    sb.append(login.mUid).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(login.avatarID).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(login.level).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(login.money).append(AIOConstants.SEPERATOR_BYTE_3);
                    getPhong(login, sb);
                    sb.append(AIOConstants.SEPERATOR_BYTE_3);
                    getRoom(login, sb);
                    encodingObj.put("v", sb.toString());

                    return encodingObj;
                }

                encodingObj.put("uid", login.mUid);
                encodingObj.put("money", login.money);
                encodingObj.put("avatar", login.avatarID);
                encodingObj.put("avatarVersion", login.avatarVerion);
                encodingObj.put("level", login.level);
                encodingObj.put("tuoc_vi", login.TuocVi);
                encodingObj.put("playsNumber", login.playNumber);

                if (login.newVer.length() > 0) {
                    encodingObj.put("linkDown", login.linkDown);
                    encodingObj.put("newVer", login.newVer);
                }

                if (login.cellPhone != null) {
                    encodingObj.put("phone", login.cellPhone);
                } else {
                    encodingObj.put("phone", "");
                }
                JSONArray arr = new JSONArray();

                encodingObj.put("Adv", arr);
                if (login.newVer.length() > 0) {
                    JSONObject ver = new JSONObject();
                    ver.put("link", login.linkDown);
                    ver.put("description", "");
                    ver.put("id", login.newVer);
                    encodingObj.put("latestVersion", ver);
                }

                if (login.isMobile) {
                    JSONArray advertisingArr = new JSONArray();

                    JSONObject jAdv1 = new JSONObject();
                    if (login.newVer.length() > 0) {
                        jAdv1.put("content", "Vào " + login.linkDown + " để tải phiên bản chính thức 3.0");
                    } else {
                        jAdv1.put("content", "");
                    }

                    advertisingArr.put(jAdv1);
                    encodingObj.put("advertising", advertisingArr);

                } else {
                    //fill get All room to flash
                    encodingObj.put("rooms", getAllRooms(login));
                    encodingObj.put("tables", enterFirstRoom(login));

                }

                JSONArray arrValues = new JSONArray();
                int idDauso = 0;
                for (int i = 0; i < login.chargingInfo.size(); i++) {
                    idDauso++;
                    ChargingInfo info = login.chargingInfo.get(i);
                    JSONObject jCell = new JSONObject();
                    jCell.put("number", info.number);
                    jCell.put("value", info.value);
                    jCell.put("description", info.desc);
                    jCell.put("id", idDauso);
                    arrValues.put(jCell);

                    if (!login.isMobile) //flash get only message with money = 15000
                    {
                        break;
                    }

                }
                encodingObj.put("dausoInfo", arrValues);
            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
