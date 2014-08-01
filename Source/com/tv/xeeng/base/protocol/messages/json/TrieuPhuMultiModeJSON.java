package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.TrieuPhuMultiModeRequest;
import com.tv.xeeng.base.protocol.messages.TrieuPhuMultiModeResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class TrieuPhuMultiModeJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			AnPhomJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		try {
			JSONObject jsonData = (JSONObject) aEncodedObj;
			TrieuPhuMultiModeRequest an = (TrieuPhuMultiModeRequest) aDecodingObj;
			String v = jsonData.getString("v");
			an.mMatchId = Long.parseLong(v);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Object encode(IResponseMessage aResponseMessage)
			throws ServerException {
		try {
			JSONObject encodingObj = new JSONObject();
			TrieuPhuMultiModeResponse an = (TrieuPhuMultiModeResponse) aResponseMessage;
			StringBuilder sb = new StringBuilder();
			sb.append(Integer.toString(aResponseMessage.getID())).append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(Integer.toString(an.mCode)).append(
					AIOConstants.SEPERATOR_NEW_MID);
			if (an.mCode == ResponseCode.FAILURE) {
				sb.append(an.errMgs);
			}
			encodingObj.put("v", sb.toString());
			return encodingObj;

		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
}
