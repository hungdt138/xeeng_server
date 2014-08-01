package com.tv.xeeng.base.protocol.messages.json;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.FastPlayRequest;
import com.tv.xeeng.base.protocol.messages.FastPlayResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

public class FastPlayJSON implements IMessageProtocol {
    
    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(FastPlayJSON.class);
    
    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            // request data
            JSONObject jsonData = (JSONObject) aEncodedObj;
            FastPlayRequest fast = (FastPlayRequest) aDecodingObj;
            
            if (jsonData.has("v")) {
                String params = jsonData.getString("v");
                String[] arrValues = params.split(AIOConstants.SEPERATOR_BYTE_1);
                
                if (arrValues.length > 0) {
                    fast.zoneId = Integer.valueOf(arrValues[0]);
                }
                if (arrValues.length > 1) {
                    fast.setLevelId(Integer.valueOf(arrValues[1]));
                } else {
                    fast.setLevelId(1);
                }
                
                return true;
                
            }

            // plain obj
            // decoding
            fast.zoneId = jsonData.getInt("room_id");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    private void getMidEncode(FastPlayResponse fast, JSONObject encodingObj) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(MessagesID.FastPlay)).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(Integer.toString(fast.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
        if (fast.mCode == ResponseCode.SUCCESS) {
            sb.append(Long.toString(fast.matchID)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(fast.tableID));
        } else {
            sb.append(fast.message);
        }
        encodingObj.put("v", sb.toString());
    }
    
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            FastPlayResponse fast = (FastPlayResponse) aResponseMessage;
            if (fast.session != null && fast.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                getMidEncode(fast, encodingObj);
                
                return encodingObj;
            }

            // put response data into json object
            encodingObj.put("mid", aResponseMessage.getID());
            
            encodingObj.put("code", fast.mCode);
            // System.out.println(" chat.mUsername : " +  chat.mUsername);
            if (fast.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", fast.message);
            } else if (fast.mCode == ResponseCode.SUCCESS) {
                encodingObj.put("match_id", fast.matchID);
                encodingObj.put("nTable", fast.tableID);
                
            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
