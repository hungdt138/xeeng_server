package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.TrieuPhuHelpRequest;
import com.tv.xeeng.base.protocol.messages.TrieuPhuHelpResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class TrieuPhuHelpJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			TrieuPhuHelpJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		try {
			JSONObject jsonData = (JSONObject) aEncodedObj;
			TrieuPhuHelpRequest gui = (TrieuPhuHelpRequest) aDecodingObj;
			String s = jsonData.getString("v");
			String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
			gui.mMatchId = Long.parseLong(arr[0]);
                        if(arr.length>1)
                            gui.type = Integer.parseInt(arr[1]);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Object encode(IResponseMessage aResponseMessage)
			throws ServerException {
		try {
			JSONObject encodingObj = new JSONObject();
			TrieuPhuHelpResponse gui = (TrieuPhuHelpResponse) aResponseMessage;

			StringBuilder sb = new StringBuilder();
			sb.append(Integer.toString(aResponseMessage.getID())).append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(Integer.toString(gui.mCode)).append(
					AIOConstants.SEPERATOR_NEW_MID);
			if (gui.mCode == ResponseCode.FAILURE) {
				sb.append(gui.mErrorMsg);
			}
                        else
                        {
                            sb.append(gui.value);
                        }
			encodingObj.put("v", sb.toString());
			return encodingObj;

		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
}
