package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GuestLoginRequest;
import com.tv.xeeng.base.protocol.messages.GuestLoginResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class GuestLoginJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GuestLoginJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            GuestLoginRequest login = (GuestLoginRequest) aDecodingObj;

            if (jsonData.has("v")) {
                try {
                    String v = jsonData.getString("v");
                    String[] arrValues;
                    arrValues = v.split(AIOConstants.SEPERATOR_BYTE_1);

                    login.deviceUId = arrValues[0];
                    login.partnerId = Integer.parseInt(arrValues[1]);
                    login.refCode =  Integer.parseInt(arrValues[2]);
                    login.mobileVersion = arrValues[3];
                    
                    if (arrValues.length > 4) {
                        login.regTime =  Integer.parseInt(arrValues[4]);
                    }

                    if (arrValues.length > 5) {
                        String device = arrValues[5];
                        String[] deviceParts = device.split(";");
                        if (deviceParts.length >= 3) {
                            login.setOsName(deviceParts[0]);
                            login.setOsVersion(deviceParts[1]);
                            login.setOsMAC(deviceParts[2]);
                        }
                    }

                } catch (Exception ex) {
                    mLog.error(ex.getMessage(), ex);
                }
            }
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    private void getMobileLoginInfo(GuestLoginResponse login, StringBuilder sb) {
        if (login.mCode == ResponseCode.SUCCESS) {
            sb.append(login.mUid).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(login.usrEntity.mUsername).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(login.money)).append(AIOConstants.SEPERATOR_BYTE_1);

            if (login.newVer.length() > 0) {
                sb.append(login.linkDown).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(login.version.newsUpdate).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(login.isNeedUpdate ? "1" : "0");
            }
            
            if (login.active != null && !login.active.equals("")) {
                sb.append(AIOConstants.SEPERATOR_BYTE_3).append(login.active);
            }
            
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

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        
    	try {
            JSONObject encodingObj = new JSONObject();
            GuestLoginResponse login = (GuestLoginResponse) aResponseMessage;
            
            if (login.session != null) {
                StringBuilder valueSb = new StringBuilder();
                valueSb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                valueSb.append(Integer.toString(login.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                
                getMobileLoginInfo(login, valueSb);

                encodingObj.put("v", valueSb.toString());
            } 

            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
