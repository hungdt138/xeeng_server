package com.tv.xeeng.base.protocol.messages.json;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.OfflineMessageResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.Message;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class OfflineMessageJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(OfflineMessageJSON.class);

    public boolean decode(Object paramObject, IRequestMessage paramIRequestMessage) throws ServerException {
        try {
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + paramIRequestMessage.getID(), t);
            return false;
        }
    }

    private String data(Vector<Message> msList){
    	StringBuilder sb = new StringBuilder();
    	for (Message ms : msList) {
    		sb.append(ms.id).append(AIOConstants.SEPERATOR_BYTE_1);
    		sb.append(ms.sName).append(AIOConstants.SEPERATOR_BYTE_1);
    		sb.append(ms.title).append(AIOConstants.SEPERATOR_BYTE_2);
    	}
    	if(sb.length()>0) sb.deleteCharAt(sb.length()-1);
    	return sb.toString();
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {            
        	
            JSONObject encodingObj = new JSONObject();
            OfflineMessageResponse rp = (OfflineMessageResponse) aResponseMessage;
            
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(rp.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
            if (rp.mCode == ResponseCode.FAILURE) {
            	 sb.append(rp.mErrorMsg);
            }else {
            	sb.append(data(rp.mPostList));
            }
            encodingObj.put("v", sb.toString());
            return encodingObj;
            
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
