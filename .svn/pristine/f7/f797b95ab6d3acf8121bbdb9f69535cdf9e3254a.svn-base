package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BocPhomRequest;
import com.tv.xeeng.base.protocol.messages.BocPhomResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class BocPhomJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BocPhomJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            BocPhomRequest boc = (BocPhomRequest) aDecodingObj;
            if(jsonData.has("v")) {
                    boc.matchID = jsonData.getLong("v");
                    return true;
                
            }
            boc.matchID = jsonData.getLong("match_id");            

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            BocPhomResponse boc = (BocPhomResponse) aResponseMessage;
            if(boc.session != null && boc.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(boc.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (boc.mCode == ResponseCode.FAILURE) {
                	 sb.append(boc.message);
                }else {
                	sb.append((boc.card != null)? boc.card.toInt():0).append(AIOConstants.SEPERATOR_BYTE_1);
                	 sb.append(boc.isHabai?"1":"0");//.append(AIOConstants.SEPERATOR_BYTE_1);
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", boc.mCode);
            if (boc.mCode == ResponseCode.SUCCESS) {
                if(boc.card != null){
                    encodingObj.put("card", boc.card.toInt());
                }else {
                    encodingObj.put("card", 0);
                }
                
                if(boc.isHabai)
                {
                    encodingObj.put("isHaBai", boc.isHabai);
                }
                
                
                //encodingObj.put("currentId", boc.currentId);
            } else {
                encodingObj.put("error", boc.message);
            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
