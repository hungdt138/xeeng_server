/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.LineGetOtherTableOutRequest;
import com.tv.xeeng.base.protocol.messages.LineGetOtherTableOutResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 * 
 * @author tuanda
 */
public class LineGetOtherTableOutJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			LineGetOtherTableOutJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		try {
			JSONObject jsonData = (JSONObject) aEncodedObj;
			LineGetOtherTableOutRequest matchTurn = (LineGetOtherTableOutRequest) aDecodingObj;
			if (jsonData.has("v")) {
				String s = jsonData.getString("v");
				String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
				matchTurn.mMatchId = Long.parseLong(arr[0]);
				matchTurn.uid = Long.parseLong(arr[1]);
				matchTurn.matrix = arr[2];
				return true;
			}
			matchTurn.mMatchId = jsonData.getLong("match_id");
			matchTurn.matrix = jsonData.getString("matrix");
			matchTurn.uid = jsonData.getLong("uid");
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
			
			LineGetOtherTableOutResponse matchTurn = (LineGetOtherTableOutResponse) aResponseMessage;
			if(matchTurn.session != null && matchTurn.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(matchTurn.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                sb.append(matchTurn.uid);
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
			
			encodingObj.put("mid", aResponseMessage.getID());
			encodingObj.put("code", ResponseCode.SUCCESS);
			encodingObj.put("uid", matchTurn.uid);
			return encodingObj;
		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
}
