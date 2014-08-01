package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.AnnounceRequest;
import com.tv.xeeng.base.protocol.messages.AnnounceResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class AnnounceJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(AnnounceJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            AnnounceRequest anno = (AnnounceRequest) aDecodingObj;
            anno.message = jsonData.getString("Message");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] ", t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {            
            JSONObject encodingObj = new JSONObject();
            encodingObj.put("mid", aResponseMessage.getID());
            AnnounceResponse anno = (AnnounceResponse) aResponseMessage;
            encodingObj.put("code", anno.mCode);
            if (anno.mCode == ResponseCode.FAILURE) {
            } else if (anno.mCode == ResponseCode.SUCCESS) {
                encodingObj.put("message", anno.message);
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
