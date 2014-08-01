package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.SuggestRequest;
import com.tv.xeeng.base.protocol.messages.SuggestResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class SuggestJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			SuggestJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		try {
			JSONObject jsonData = (JSONObject) aEncodedObj;
			SuggestRequest matchStart = (SuggestRequest) aDecodingObj;
			if(jsonData.has("v")) {
				matchStart.note = jsonData.getString("v");
				return true;
			}
			matchStart.uid = jsonData.getLong("uid");
			matchStart.note = jsonData.getString("note");
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
			
			SuggestResponse suggest = (SuggestResponse) aResponseMessage;
			if(suggest.session != null && suggest.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(suggest.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (suggest.mCode == ResponseCode.FAILURE) {
                	 sb.append(suggest.mErrorMsg);
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
			encodingObj.put("mid", aResponseMessage.getID());
			encodingObj.put("code", suggest.mCode);
			if (suggest.mCode == ResponseCode.FAILURE) {
				encodingObj.put("error_msg", suggest.mErrorMsg);
			}
			return encodingObj;
		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
}
