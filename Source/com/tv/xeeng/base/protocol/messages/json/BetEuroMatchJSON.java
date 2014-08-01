/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BetEuroMatchRequest;
import com.tv.xeeng.base.protocol.messages.BetEuroMatchResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



/**
 *
 * @author tuanda
 */
public class BetEuroMatchJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BetEuroMatchJSON.class);
    

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            BetEuroMatchRequest betRequest = (BetEuroMatchRequest)aDecodingObj;
            if(jsonData.has("v")) {
            	 String v= jsonData.getString("v");
            	 String[] arr = v.split(AIOConstants.SEPERATOR_BYTE_1);
            	 betRequest.matchID = Integer.parseInt(arr[0]);
            	 betRequest.type = Integer.parseInt(arr[1]);
            	 betRequest.bet = Integer.parseInt(arr[2]);
            	 betRequest.money = Long.parseLong(arr[3]);
            	 return true;
            }
            return false;
        } catch (JSONException ex) {
            mLog.error(ex.getMessage(), ex);
            return false;
        }
    }
    
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try
        {
            JSONObject encodingObj = new JSONObject();
            BetEuroMatchResponse bet = (BetEuroMatchResponse) aResponseMessage;
           
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(bet.mCode));//.append(AIOConstants.SEPERATOR_NEW_MID);
            if (bet.mCode == ResponseCode.FAILURE) {
                     sb.append(AIOConstants.SEPERATOR_NEW_MID).append(bet.message);
            }
            encodingObj.put("v", sb.toString());
            return encodingObj;
            
            
        } catch (Throwable t)
        {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
    
}
