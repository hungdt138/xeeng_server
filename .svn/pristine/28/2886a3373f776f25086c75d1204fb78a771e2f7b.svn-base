package com.tv.xeeng.base.protocol.messages.json;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetTournementListResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.tournement.TournementEntity;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class GetTournementListJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			GetTournementListJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		return true;
	}

	public Object encode(IResponseMessage aResponseMessage)
			throws ServerException {
		try {
			JSONObject encodingObj = new JSONObject();
			GetTournementListResponse boc = (GetTournementListResponse) aResponseMessage;
			StringBuilder sb = new StringBuilder();
			sb.append(Integer.toString(aResponseMessage.getID())).append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(Integer.toString(boc.mCode)).append(
					AIOConstants.SEPERATOR_NEW_MID);
			if (boc.mCode == ResponseCode.FAILURE) {
				sb.append(boc.errMsg);
			} else {
				SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat(
						"HH:mm dd/MM/yyyy");
				for (TournementEntity t : boc.tours) {
					sb.append(t.id).append(AIOConstants.SEPERATOR_BYTE_1);
					sb.append(t.game).append(AIOConstants.SEPERATOR_BYTE_1);
					sb.append(t.minBet).append(AIOConstants.SEPERATOR_BYTE_1);
					sb.append(t.name).append(AIOConstants.SEPERATOR_BYTE_1);
					sb.append(dateformatYYYYMMDD.format(t.startDate)).append(AIOConstants.SEPERATOR_BYTE_1);
					sb.append(dateformatYYYYMMDD.format(t.endDate)).append(AIOConstants.SEPERATOR_BYTE_1);
					sb.append(t.isBook?"1":"0").append(AIOConstants.SEPERATOR_BYTE_1);
					sb.append(isTurnOn(t.startDate,t.endDate)).append(AIOConstants.SEPERATOR_BYTE_2);
				}
				if(boc.tours.size()>0) {
					sb.deleteCharAt(sb.length()-1);
					sb.append(AIOConstants.SEPERATOR_BYTE_3);
				}
				
				sb.append("210.211.101.106").append(AIOConstants.SEPERATOR_BYTE_3);
				sb.append("9999");
			}
			encodingObj.put("v", sb.toString());

			return encodingObj;
		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
	public String isTurnOn(Date startDate, Date endDate){
		Calendar calendar = Calendar.getInstance();
		calendar.add( Calendar.MINUTE ,  10 );
		Date d = calendar.getTime();
		boolean isBefore10minute = d.after(startDate);
		calendar.add( Calendar.MINUTE ,  -10 );
		d = calendar.getTime();
		boolean isAfterEnd = d.after(endDate);
		return (isBefore10minute && !isAfterEnd)?"1":"0";
	}
}
