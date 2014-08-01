package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GuiPhomRequest;
import com.tv.xeeng.base.protocol.messages.GuiPhomResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class GuiPhomJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GuiPhomJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
		try {
			JSONObject jsonData = (JSONObject) aEncodedObj;
			GuiPhomRequest gui = (GuiPhomRequest) aDecodingObj;
			if (jsonData.has("v")) {
				String s = jsonData.getString("v");
				String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
				gui.matchID = Long.parseLong(arr[0]);
				gui.dUID = Long.parseLong(arr[1]);
				gui.phomID = Integer.parseInt(arr[3]);
				gui.cards = arr[2];
				return true;
			}
			gui.matchID = jsonData.getLong("match_id");
			gui.dUID = jsonData.getLong("d_uid");
			gui.phomID = jsonData.getInt("phom");
			gui.cards = jsonData.getString("cards");

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Object encode(IResponseMessage aResponseMessage)
			throws ServerException {
		try {
			JSONObject encodingObj = new JSONObject();
			GuiPhomResponse gui = (GuiPhomResponse) aResponseMessage;
			if (gui.session != null
					&& gui.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
				StringBuilder sb = new StringBuilder();
				sb.append(Integer.toString(aResponseMessage.getID())).append(
						AIOConstants.SEPERATOR_BYTE_1);
				sb.append(Integer.toString(gui.mCode)).append(
						AIOConstants.SEPERATOR_NEW_MID);
				if (gui.mCode == ResponseCode.FAILURE) {
					sb.append(gui.message);
				} else {
					sb.append(Long.toString(gui.dUID)).append(
							AIOConstants.SEPERATOR_BYTE_1);
					sb.append(gui.cards).append(AIOConstants.SEPERATOR_BYTE_1);
					sb.append(Integer.toString(gui.phomID));
				}
				encodingObj.put("v", sb.toString());
				return encodingObj;
			}
			encodingObj.put("code", gui.mCode);
			encodingObj.put("mid", aResponseMessage.getID());
			if (gui.mCode == ResponseCode.SUCCESS) {

				if (gui.session != null
						&& gui.session.getByteProtocol() > AIOConstants.PROTOCOL_PRIMITIVE) {
					StringBuilder sb = new StringBuilder();
					sb.append(Long.toString(gui.dUID)).append(
							AIOConstants.SEPERATOR_ELEMENT);
					// sb.append(Long.toString(gui.sUID)).append(AIOConstants.SEPERATOR_ELEMENT);
					sb.append(gui.cards).append(AIOConstants.SEPERATOR_ELEMENT);
					sb.append(Integer.toString(gui.phomID));

					encodingObj.put("v", sb.toString());

					return encodingObj;
				}

				encodingObj.put("d_uid", gui.dUID);
				encodingObj.put("s_uid", gui.sUID);
				encodingObj.put("phomID", gui.phomID);

				// JSONArray cardsJSON = new JSONArray();
				// for(int c : gui.cards){
				// JSONObject obj = new JSONObject();
				// obj.put("card", c);
				// cardsJSON.put(obj);
				// }
				encodingObj.put("cards", gui.cards);
			} else {
				encodingObj.put("error", gui.message);
			}
			// response encoded obj
			return encodingObj;
		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
}
