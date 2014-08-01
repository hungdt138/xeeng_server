package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.TrieuPhuAnswerRequest;
import com.tv.xeeng.base.protocol.messages.TrieuPhuAnswerResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuPlayer;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class TrieuPhuAnswerJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			TrieuPhuAnswerJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		try {
			JSONObject jsonData = (JSONObject) aEncodedObj;
			TrieuPhuAnswerRequest gui = (TrieuPhuAnswerRequest) aDecodingObj;
			String s = jsonData.getString("v");
			String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
			gui.mMatchId = Long.parseLong(arr[0]);
			gui.answer = arr[1];
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Object encode(IResponseMessage aResponseMessage)
			throws ServerException {
		try {
			JSONObject encodingObj = new JSONObject();
			TrieuPhuAnswerResponse answer = (TrieuPhuAnswerResponse) aResponseMessage;

			StringBuilder sb = new StringBuilder();
			sb.append(Integer.toString(aResponseMessage.getID())).append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(Integer.toString(answer.mCode)).append(
					AIOConstants.SEPERATOR_NEW_MID);
			if (answer.mCode == ResponseCode.FAILURE) {
				sb.append(answer.mErrorMsg);
			}else {
				sb.append(answer.value);
			}
			encodingObj.put("v", sb.toString());
			return encodingObj;

		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
}
