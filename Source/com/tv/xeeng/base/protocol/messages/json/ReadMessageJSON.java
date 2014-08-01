package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.ReadMessageRequest;
import com.tv.xeeng.base.protocol.messages.ReadMessageResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class ReadMessageJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ReadMessageJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
        	// request data
            JSONObject jsonData = (JSONObject) aEncodedObj;
        	ReadMessageRequest req = (ReadMessageRequest) aDecodingObj;
        	if(jsonData.has("v")) {
        		req.messID = jsonData.getLong("v");
        		return true;
        	}
        	req.messID = jsonData.getLong("mess_id");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
           
            ReadMessageResponse read = (ReadMessageResponse) aResponseMessage;
            
            if(read.session != null && read.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(read.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (read.mCode == ResponseCode.FAILURE) {
                	 sb.append(read.mErrorMsg);
                }
                else
                {
                    sb.append(read.value);
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", read.mCode);
            if (read.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", read.mErrorMsg);
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
