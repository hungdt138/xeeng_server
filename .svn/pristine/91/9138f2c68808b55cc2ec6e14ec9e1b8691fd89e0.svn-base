package com.tv.xeeng.base.protocol.messages.json;

import org.slf4j.Logger;


import org.json.JSONObject;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.JoinPlayerRequest;
import com.tv.xeeng.base.protocol.messages.JoinPlayerResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author Dinhpv
 */
public class JoinPlayerJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(JoinPlayerJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            JoinPlayerRequest joinPlayer = (JoinPlayerRequest) aDecodingObj;
            joinPlayer.mMatchId = jsonData.getLong("match_id");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            encodingObj.put("mid", aResponseMessage.getID());
            JoinPlayerResponse acceptJoin = (JoinPlayerResponse) aResponseMessage;
            encodingObj.put("code", acceptJoin.mCode);
            if (acceptJoin.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", acceptJoin.mErrorMsg);
            } else if (acceptJoin.mCode == ResponseCode.SUCCESS) {
                encodingObj.put("uid", acceptJoin.mUid);
                encodingObj.put("money", acceptJoin.money);
                encodingObj.put("avatar", acceptJoin.avatarID);
                encodingObj.put("level", acceptJoin.level);
                encodingObj.put("username", acceptJoin.username);
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
