package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;


import org.json.JSONException;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BotFastPlayResponse;
import com.tv.xeeng.base.protocol.messages.FastPlayRequest;
import com.tv.xeeng.base.protocol.messages.FastPlayResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class BotFastPlayJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BotFastPlayJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
//        try {
//            // request data
//            JSONObject jsonData = (JSONObject) aEncodedObj;
//            FastPlayRequest fast = (FastPlayRequest) aDecodingObj;
//            
//            if(jsonData.has("v"))
//            {
//                fast.zoneId = jsonData.getInt("v");
//                return true;
//                
//            }
//            
//            // plain obj
//            
//            // decoding
//            fast.zoneId = jsonData.getInt("room_id");
//            return true;
//        } catch (Throwable t) {
//            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
//            return false;
//        }
        return true;
    }
    private void getMidEncode(BotFastPlayResponse fast, JSONObject encodingObj) throws JSONException
    {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(fast.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(Integer.toString(fast.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
        if(fast.mCode == ResponseCode.FAILURE)
        {
            sb.append(fast.message);
        }
        
        encodingObj.put("v", sb.toString());
    }
    
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            BotFastPlayResponse fast = (BotFastPlayResponse) aResponseMessage;
            
                getMidEncode(fast, encodingObj);
                
                return encodingObj;
            
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
