package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEResponseMessage;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public abstract class XEMessageJSON implements IMessageProtocol {
	private static Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			XEMessageJSON.class);

	@Override
	public boolean decode(Object paramObject,
			IRequestMessage paramIRequestMessage) throws ServerException {
		return true;
	}

	@Override
	public Object encode(IResponseMessage paramIResponseMessage)
			throws ServerException {
		try {
			JSONObject encodingObj = new JSONObject();
			XEResponseMessage response = (XEResponseMessage) paramIResponseMessage;
			StringBuilder sb = new StringBuilder();
			sb.append(Integer.toString(paramIResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
			sb.append(Integer.toString(response.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
			if (response.mCode == ResponseCode.FAILURE) {
				sb.append(response.getErrorMsg());
			} else {
				sb.append(response.toString());
			}
			encodingObj.put("v", sb.toString());
			return encodingObj;
		} catch (Throwable t) {
			mLog.error("[ENCODER] " + paramIResponseMessage.getID(), t);
		}
		return null;
	}
}
