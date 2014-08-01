/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import java.util.logging.Level;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BetRequest;
import com.tv.xeeng.base.protocol.messages.BetResponse;
import com.tv.xeeng.base.protocol.messages.ChallengeRequest;
import com.tv.xeeng.base.protocol.messages.ChallengeResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class ChallengeJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ChallengeJSON.class);
    

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            ChallengeRequest challengeRequest = (ChallengeRequest)aDecodingObj;
            
            
            
            challengeRequest.matchID = jsonData.getLong("match_id");
            challengeRequest.uid = jsonData.getLong("uid");
            challengeRequest.money = jsonData.getLong("money");
            challengeRequest.isChan = jsonData.getBoolean("isChan");
            
            return true;
        } catch (JSONException ex) {
            mLog.error(ex.getStackTrace().toString());
            return false;
        }
    }
    
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try
        {
            JSONObject encodingObj = new JSONObject();
            // put response data into json object
            encodingObj.put("mid", aResponseMessage.getID());
            // cast response obj
            ChallengeResponse challenge = (ChallengeResponse) aResponseMessage;
            encodingObj.put("code", challenge.mCode);
            if(challenge.mCode == ResponseCode.SUCCESS){
                
                encodingObj.put("money", challenge.money);
                encodingObj.put("isChan", challenge.isChan);
                encodingObj.put("uid", challenge.uid);
            }else {
            	encodingObj.put("error", challenge.errMsg);
            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t)
        {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
    
}
