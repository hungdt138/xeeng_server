package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.OutPhongResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class OutPhongJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			OutPhongJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		return true;
	}

	public Object encode(IResponseMessage aResponseMessage)
			throws ServerException {
		try {
			JSONObject encodingObj = new JSONObject();
			
			OutPhongResponse out = (OutPhongResponse) aResponseMessage;
			
			 if(out.session != null && out.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
	            {
	                StringBuilder sb = new StringBuilder();
	                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
	                sb.append(Integer.toString(out.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
	                if (out.mCode == ResponseCode.FAILURE) {
	                	 sb.append(out.mErrorMsg);
	                }
	                encodingObj.put("v", sb.toString());
	                return encodingObj;
	            }
			encodingObj.put("code", out.mCode);
			encodingObj.put("mid", aResponseMessage.getID());
			if (out.mCode == ResponseCode.FAILURE) {
				encodingObj.put("error_msg", out.mErrorMsg);
			}
			return encodingObj;
		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
}
