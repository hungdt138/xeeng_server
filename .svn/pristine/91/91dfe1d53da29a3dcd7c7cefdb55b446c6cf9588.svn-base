package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.OutResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class OutJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			OutJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Object encode(IResponseMessage aResponseMessage)
			throws ServerException {
		try {
			JSONObject encodingObj = new JSONObject();

			OutResponse matchOut = (OutResponse) aResponseMessage;
			if (matchOut.session != null
					&& matchOut.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
				StringBuilder sb = new StringBuilder();
				sb.append(Integer.toString(aResponseMessage.getID())).append(
						AIOConstants.SEPERATOR_BYTE_1);
				sb.append(Integer.toString(matchOut.mCode)).append(
						AIOConstants.SEPERATOR_NEW_MID);
				if (matchOut.mCode == ResponseCode.FAILURE) {
					sb.append(matchOut.message);
				} else {
					sb.append(matchOut.mUid).append(
							AIOConstants.SEPERATOR_BYTE_1);
					sb.append(matchOut.username).append(
							AIOConstants.SEPERATOR_BYTE_1);
					sb.append(matchOut.message).append(
							AIOConstants.SEPERATOR_BYTE_1);
					sb.append(matchOut.out).append(
							AIOConstants.SEPERATOR_BYTE_1);
					sb.append(matchOut.newRoomOwner > 0 ? matchOut.newRoomOwner
									: "0").append(
							AIOConstants.SEPERATOR_BYTE_1);
					sb.append(matchOut.type);
				}
				encodingObj.put("v", sb.toString());
				return encodingObj;
			}
			encodingObj.put("mid", aResponseMessage.getID());
			encodingObj.put("code", matchOut.mCode);
			if (matchOut.mCode == ResponseCode.FAILURE) {
			} else if (matchOut.mCode == ResponseCode.SUCCESS) {
				encodingObj.put("uid", matchOut.mUid);
				encodingObj.put("username", matchOut.username);
				encodingObj.put("message", matchOut.message);
				encodingObj.put("out_room", matchOut.out);
				if (matchOut.newRoomOwner > 0) {
					encodingObj.put("newOwner", matchOut.newRoomOwner);
				}
				encodingObj.put("type", matchOut.type);

			}
			// response encoded obj
			return encodingObj;
		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
}
