package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.FaceLoginRequest;
import com.tv.xeeng.base.protocol.messages.FaceLoginResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class FaceLoginJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(FaceLoginJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            FaceLoginRequest login = (FaceLoginRequest) aDecodingObj;

            if (jsonData.has("v")) {
                try {

                	String v = jsonData.getString("v");
                    String[] arrValues;
                    arrValues = v.split(AIOConstants.SEPERATOR_BYTE_1);

                    login.faceId = arrValues[0];
                    login.partnerId = Integer.parseInt(arrValues[1]);
                    login.refCode =  Integer.parseInt(arrValues[2]);
                    login.mobileVersion = arrValues[3];
                    
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

    private void getMobileLoginInfo(FaceLoginResponse login, StringBuilder sb) {        
    	if (login.mCode == ResponseCode.SUCCESS) {
            sb.append(login.mUid).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(login.usrEntity.mUsername).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(login.money)).append(AIOConstants.SEPERATOR_BYTE_1);

            if (login.active != null && !login.active.equals("")) {
                sb.append(AIOConstants.SEPERATOR_BYTE_3).append(login.active);
            }
            
        } else {
            sb.append(login.mErrorMsg);
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
        	
            JSONObject encodingObj = new JSONObject();
            FaceLoginResponse login = (FaceLoginResponse) aResponseMessage;
            if (login.session != null) {
                StringBuilder valueSb = new StringBuilder();
                valueSb.append(Integer.toString(1001)).append(AIOConstants.SEPERATOR_BYTE_1);
                valueSb.append(Integer.toString(login.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                getMobileLoginInfo(login, valueSb);
                encodingObj.put("v", valueSb.toString());
            }

            // response encoded obj
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
