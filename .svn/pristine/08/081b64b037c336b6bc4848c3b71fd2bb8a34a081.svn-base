package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.LatBaiRequest;
import com.tv.xeeng.base.protocol.messages.LatBaiResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class LatBaiJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(LatBaiJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            LatBaiRequest latbai = (LatBaiRequest) aDecodingObj;
            if(jsonData.has("v")) {
            	String s = jsonData.getString("v");
            	String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
            	latbai.matchID = Long.parseLong(arr[0]);
            	if(arr.length==2) 
            		latbai.card = Integer.parseInt(arr[1]);
            	return true;
            }
            latbai.matchID = jsonData.getLong("match_id");
            if(jsonData.has("card"))
            {
                latbai.card = jsonData.getInt("card");
            }
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            
            LatBaiResponse latbai = (LatBaiResponse) aResponseMessage;
            if(latbai.session != null && latbai.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(latbai.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (latbai.mCode == ResponseCode.FAILURE) {
                	 sb.append(latbai.mErrorMsg);
                }else {
                	switch(latbai.zoneId)
                    {
                        case ZoneID.NEW_BA_CAY:
                        	sb.append(latbai.uid).append(AIOConstants.SEPERATOR_BYTE_1);
                        	sb.append(latbai.bcPlayer.pokersToString());
                            break;
                        case ZoneID.LIENG:
                        	sb.append(latbai.value);
                        	
                            break;
                    }
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            encodingObj.put("code", latbai.mCode);
            encodingObj.put("mid", aResponseMessage.getID());
            if (latbai.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", latbai.mErrorMsg);
            } else if (latbai.mCode == ResponseCode.SUCCESS) {
                encodingObj.put("uid", latbai.uid);
                 switch(latbai.zoneId)
                 {
                     case ZoneID.NEW_BA_CAY:
                         encodingObj.put("cards", latbai.bcPlayer.pokersToString());
                         break;
                 }
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
