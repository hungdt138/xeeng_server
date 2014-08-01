package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BanTruongRequest;
import com.tv.xeeng.base.protocol.messages.BanTruongResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class BanTruongJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BanTruongJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            // request data
            JSONObject jsonData = (JSONObject) aEncodedObj;
            // plain obj
            BanTruongRequest ban = (BanTruongRequest) aDecodingObj;
            // decoding
            ban.money = jsonData.getLong("money");
            ban.matchID = jsonData.getLong("match_id");

            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            // put response data into json object
            encodingObj.put("mid", aResponseMessage.getID());
            BanTruongResponse ban = (BanTruongResponse) aResponseMessage;
            encodingObj.put("code", ban.mCode);
            // System.out.println(" chat.mUsername : " +  chat.mUsername);
            if (ban.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", ban.mErrorMsg);
            } else if (ban.mCode == ResponseCode.SUCCESS) {
                encodingObj.put("money", ban.money);
                encodingObj.put("uid", ban.mUid);

            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
