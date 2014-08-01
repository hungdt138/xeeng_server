/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.TimeOutRequest;
import com.tv.xeeng.base.protocol.messages.TimeOutResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



/**
 *
 * @author Admin
 */
public class TimeOutJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(TimeOutJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
            throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            TimeOutRequest timeOut = (TimeOutRequest) aDecodingObj;
            timeOut.player_friend_id = jsonData.getLong("player_friend_id");
            timeOut.mMatchId = jsonData.getLong("match_id");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage)
            throws ServerException {
        try {
            TimeOutResponse resTimeOut = (TimeOutResponse) aResponseMessage;
            JSONObject encodingObj = new JSONObject();
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", resTimeOut.mCode);
            encodingObj.put("player_friend_id", resTimeOut.player_friend_id);
            encodingObj.put("timeout_player_name", resTimeOut.timeout_player_name);
            if (resTimeOut.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", resTimeOut.errMgs);
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
