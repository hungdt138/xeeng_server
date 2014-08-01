/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.LostRequest;
import com.tv.xeeng.base.protocol.messages.LostResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



/**
 *
 * @author Admin
 */
public class LostJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(TimeOutJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
            throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            LostRequest rqLost = (LostRequest) aDecodingObj;
            rqLost.player_friend_id = jsonData.getLong("player_friend_id");
            rqLost.mMatchId = jsonData.getLong("match_id");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage)
            throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            encodingObj.put("mid", aResponseMessage.getID());
            LostResponse resLost = (LostResponse) aResponseMessage;
            encodingObj.put("code", resLost.mCode);
            encodingObj.put("player_friend_id", resLost.player_friend_id);
            encodingObj.put("lost_player_name", resLost.lost_player_name);
            encodingObj.put("ownerMoney", resLost.ownerMoney);
            encodingObj.put("playerMoney", resLost.playerMoney);

            if (resLost.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", resLost.errMgs);
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
