/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.LineEndMatchRequest;
import com.tv.xeeng.base.protocol.messages.LineEndMatchResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 * 
 * @author tuanda
 */
public class LineEndMatchJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			LineEndMatchJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		try {
			JSONObject jsonData = (JSONObject) aEncodedObj;
			LineEndMatchRequest matchTurn = (LineEndMatchRequest) aDecodingObj;
			if (jsonData.has("v")) {
				String s = jsonData.getString("v");
				String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
				matchTurn.mMatchId = Long.parseLong(arr[0]);
				matchTurn.isWin = Integer.parseInt(arr[1])==1? true:false;
				matchTurn.message = arr[2];
				if (matchTurn.isWin) {
					matchTurn.type = Integer.parseInt(arr[3]);
				} else {
					if(arr.length == 4) matchTurn.pikaPoint = Integer.parseInt(arr[3]);
					else matchTurn.pikaPoint = -1;
					matchTurn.type = 0;
				}
				return true;
			}
			matchTurn.mMatchId = jsonData.getLong("match_id");
			matchTurn.isWin = jsonData.getBoolean("isWin");
			matchTurn.message = jsonData.getString("message");
			if (matchTurn.isWin) {
				matchTurn.type = jsonData.getInt("type");
			} else {
				if (jsonData.has("pika_point")) {
					matchTurn.pikaPoint = jsonData.getInt("pika_point");
				} else {
					matchTurn.pikaPoint = -1;
				}
				matchTurn.type = 0;
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

			LineEndMatchResponse matchTurn = (LineEndMatchResponse) aResponseMessage;
			if(matchTurn.session != null && matchTurn.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(matchTurn.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (matchTurn.mCode == ResponseCode.FAILURE) {
                	 sb.append(matchTurn.mErrorMsg);
                }else {
                	sb.append(matchTurn.currID).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(matchTurn.winID).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(matchTurn.number).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(matchTurn.message);
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
			
			encodingObj.put("mid", aResponseMessage.getID());
			encodingObj.put("code", matchTurn.mCode);
			if (matchTurn.mCode == ResponseCode.FAILURE) {
				encodingObj.put("error_msg", matchTurn.mErrorMsg);
			} else if (matchTurn.mCode == ResponseCode.SUCCESS) {
				encodingObj.put("curr_id", matchTurn.currID);
				encodingObj.put("win_id", matchTurn.winID);
				encodingObj.put("matrix", matchTurn.number);
				encodingObj.put("message", matchTurn.message);
			}
			// response encoded obj
			return encodingObj;
		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
}
