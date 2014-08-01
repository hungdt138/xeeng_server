package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.SendMessageRequest;
import com.tv.xeeng.base.protocol.messages.SendMessageResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class SendMessageJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			SendMessageJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		try {
			JSONObject jsonData = (JSONObject) aEncodedObj;
			SendMessageRequest send = (SendMessageRequest) aDecodingObj;
			if(jsonData.has("v")) {
				String s = jsonData.getString("v");
				String arr[] = s.split(AIOConstants.SEPERATOR_BYTE_1);
				send.dUID = Long.parseLong(arr[0]);
				send.message = arr[1];
				if(arr.length==3)
					send.title = arr[2];
				else 
					send.title = "";		
				return true;
			}
			send.dUID = jsonData.getLong("d_uid");
			send.sUID = jsonData.getLong("s_uid");
			send.message = jsonData.getString("message");
			if (jsonData.has("title")) {
				send.title = jsonData.getString("title");
			} else {
				send.title = "";
			}
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
			
			SendMessageResponse send = (SendMessageResponse) aResponseMessage;
			if(send.session != null && send.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
                        {
                            StringBuilder sb = new StringBuilder();
                            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                            sb.append(Integer.toString(send.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                            if (send.mCode == ResponseCode.FAILURE) {
                                     sb.append(send.mErrorMsg);
                            }
                            encodingObj.put("v", sb.toString());
                            return encodingObj;
                        }
			encodingObj.put("mid", aResponseMessage.getID());
			encodingObj.put("code", send.mCode);
			if (send.mCode == ResponseCode.FAILURE) {
				encodingObj.put("error_msg", send.mErrorMsg);
			}
			return encodingObj;
		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
}
