package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetOtherPokerResponse;
import com.tv.xeeng.base.protocol.messages.GetPokerResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



@SuppressWarnings("unused")
public class GetOtherPokerJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			GetOtherPokerJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Object encode(IResponseMessage aResponseMessage)
			throws ServerException {
		try {
			JSONObject encodingObj = new JSONObject();

			GetOtherPokerResponse getPoker = (GetOtherPokerResponse) aResponseMessage;

			StringBuilder sb = new StringBuilder();
			sb.append(Integer.toString(aResponseMessage.getID())).append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(Integer.toString(getPoker.mCode)).append(
					AIOConstants.SEPERATOR_NEW_MID);
			if (getPoker.mCode == ResponseCode.SUCCESS) {
				sb.append(getPoker.uid).append(
						AIOConstants.SEPERATOR_BYTE_1);
				sb.append(getPoker.isNew).append(
						AIOConstants.SEPERATOR_BYTE_1);
				StringBuilder sb1 = new StringBuilder();
				if (getPoker.phomCards != null && 
                                        !getPoker.phomCards.isEmpty()) {
					for (int i = 0; i < getPoker.phomCards.size(); i++) {
						sb1.append(getPoker.phomCards.get(i).toInt()).append(
								"#");
					}
				} else if (getPoker.tienlenCards != null &&
                                        getPoker.tienlenCards.length > 0) {
					for (int i = 0; i < getPoker.tienlenCards.length; i++) {
						sb1.append(getPoker.tienlenCards[i]).append("#");
					}
				} else { // Sam
                                    sb1.append(getPoker.samCard).append("#");
                                }
				if (sb1.length() > 0)
					sb1.deleteCharAt(sb1.length() - 1);
				sb.append(sb1);
			}
			encodingObj.put("v", sb.toString());
			return encodingObj;
		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
}
