/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.HamVuiRequest;
import com.tv.xeeng.base.protocol.messages.HamVuiResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.tienlen.data.TienLenPlayer;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



/**
 * 
 * @author tuanda
 */
public class HamVuiJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			HamVuiJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		try {
			JSONObject jsonData = (JSONObject) aEncodedObj;
			HamVuiRequest reconnectRq = (HamVuiRequest) aDecodingObj;
			reconnectRq.username = jsonData.getString("u");
			// reconnectRq.uid = jsonData.getLong("uid");
			if (jsonData.has("v")) {
				try {
					String v = jsonData.getString("v");
					String[] arrValues = v
							.split(AIOConstants.STRING_SEPERATOR_ELEMENT);
					int type = Integer.parseInt(arrValues[0]);
					reconnectRq.type = type;
					switch (type) {
					case 1:
					case 2:
					case 3:
						// reconnectRq.username = arrValues[1];
						reconnectRq.pass = arrValues[1];
						if (type == 2 || type == 3) {
							reconnectRq.zone = Integer.parseInt(arrValues[2]);
							if (type == 3) {
								reconnectRq.phong = Integer
										.parseInt(arrValues[3]);
							}
						}
						break;
					case 4:
					case 5:
						reconnectRq.uid = Long.parseLong(arrValues[1]);
						reconnectRq.matchId = Long.parseLong(arrValues[2]);
						if (type == 5)
							reconnectRq.tourID = Integer.parseInt(arrValues[3]);
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					mLog.error(ex.getMessage(), ex);
				}
			}
			return true;
		} catch (Throwable t) {
			mLog.error("[DECODER] " + aDecodingObj.getID(), t);
			return false;
		}
	}

	

	private void newProtocol(HamVuiResponse matchJoin, JSONObject encodingObj)
			throws JSONException {
		StringBuilder sb = new StringBuilder();
		sb.append(matchJoin.minBet).append(AIOConstants.SEPERATOR_BYTE_1);
		/*
		 * sb.append(matchJoin.roomOwner.id).append(
		 * AIOConstants.SEPERATOR_BYTE_1);
		 */
		switch (matchJoin.zoneID) {
		case ZoneID.PHOM: {
			sb.append(matchJoin.isPlaying ? "1" : "0").append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(matchJoin.isAn ? "1" : "0").append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(matchJoin.isTaiGui ? "1" : "0");// .append(AIOConstants.SEPERATOR_BYTE_1);
			// if(matchJoin.isObserve){
			/*sb.append(AIOConstants.SEPERATOR_BYTE_1)
			.append(matchJoin.duty)
			.append(AIOConstants.SEPERATOR_BYTE_1);*/
			sb.append(AIOConstants.SEPERATOR_BYTE_1)
					.append(matchJoin.turn)
					.append(AIOConstants.SEPERATOR_BYTE_1);
			sb.append(matchJoin.deck)
					.append(AIOConstants.SEPERATOR_BYTE_1);
			sb.append(matchJoin.cards).append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(matchJoin.currCard).append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(matchJoin.mMatchId);
			// }
			sb.append(AIOConstants.SEPERATOR_BYTE_3);
			sb.append(phomData(matchJoin, matchJoin.mPlayerPhom, false));// .append(AIOConstants.SEPERATOR_BYTE_2);
			if (!matchJoin.mWaitingPlayerPhom.isEmpty()) {
				sb.append(AIOConstants.SEPERATOR_BYTE_2)
						.append(phomData(matchJoin,
								matchJoin.mWaitingPlayerPhom, true));// .append(AIOConstants.SEPERATOR_BYTE_1);
			}

			break;
		}
		case ZoneID.TIENLEN: {
			sb.append(matchJoin.isPlaying ? "1" : "0").append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(matchJoin.isHidePoker ? "1" : "0");// .append(AIOConstants.SEPERATOR_BYTE_1);

			// if(matchJoin.isObserve){
			sb.append(AIOConstants.SEPERATOR_BYTE_1)
					.append(matchJoin.duty)
					.append(AIOConstants.SEPERATOR_BYTE_1);
			sb.append(matchJoin.turn)
					.append(AIOConstants.SEPERATOR_BYTE_1);
			if(matchJoin.cards.compareTo("") != 0)
				sb.append(matchJoin.cards).append(
						AIOConstants.SEPERATOR_BYTE_1);
			else 
				sb.append(0).append(
						AIOConstants.SEPERATOR_BYTE_1);
			
			sb.append(matchJoin.myHandCards).append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(matchJoin.mMatchId);
			// }
			sb.append(AIOConstants.SEPERATOR_BYTE_3);
			sb.append(tienLenData(matchJoin, matchJoin.mTienLenPlayer, false));// .append(AIOConstants.SEPERATOR_BYTE_1);
			if (!matchJoin.mWaitingPlayerTienlen.isEmpty())
				sb.append(AIOConstants.SEPERATOR_BYTE_2).append(
						tienLenData(matchJoin, matchJoin.mWaitingPlayerTienlen,
								true));// .append(AIOConstants.SEPERATOR_BYTE_1);

			break;
		}
		default: {
			break;
		}
		}
		encodingObj.put("v", sb.toString());
		// return encodingObj;
	}

	private StringBuilder tienLenData(HamVuiResponse matchJoin,
			ArrayList<TienLenPlayer> players, boolean isObserver) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < players.size(); i++) {
			TienLenPlayer player = players.get(i);
			sb.append(player.id).append(AIOConstants.SEPERATOR_BYTE_1);
			sb.append(player.username).append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(player.avatarID).append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(player.cash).append(AIOConstants.SEPERATOR_BYTE_1);
			sb.append(player.isReady ? "1" : "0").append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(isObserver ? "1" : "0");//
			if (!isObserver) {
				sb.append(AIOConstants.SEPERATOR_BYTE_1).append(
						player.numHand);
			}
			if (i < players.size() - 1)
				sb.append(AIOConstants.SEPERATOR_BYTE_2);
		}
		return sb;
	}

	private StringBuilder phomData(HamVuiResponse matchJoin,
			ArrayList<PhomPlayer> players, boolean isObserver) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < players.size(); i++) {
			PhomPlayer player = players.get(i);
			sb.append(player.id).append(AIOConstants.SEPERATOR_BYTE_1);
			sb.append(player.username).append(
					AIOConstants.SEPERATOR_BYTE_1);
			sb.append(player.level).append(AIOConstants.SEPERATOR_BYTE_1);
			sb.append(player.cash).append(AIOConstants.SEPERATOR_BYTE_1);
			sb.append(player.isReady ? "1" : "0").append(
					AIOConstants.SEPERATOR_BYTE_1);

			sb.append(isObserver ? "1" : "0");// .append(AIOConstants.SEPERATOR_BYTE_1);
			if (!isObserver) {
				sb.append(AIOConstants.SEPERATOR_BYTE_1);
				sb.append(player.cardToString(player.playingCards)).append(
						AIOConstants.SEPERATOR_BYTE_1);
				sb.append(player.cardToString(player.eatingCards));// .append(AIOConstants.SEPERATOR_BYTE_1);
				if (player.haPhom) {
					sb.append(AIOConstants.SEPERATOR_BYTE_1).append(
							player.cardToString(player.eatingCards));
				} else {
					sb.append(AIOConstants.SEPERATOR_BYTE_1).append(0);
				}
			}

			if (i < players.size() - 1)
				sb.append(AIOConstants.SEPERATOR_BYTE_2);
		}
		return sb;
	}

	public Object encode(IResponseMessage aResponseMessage)
			throws ServerException {
		try {
			JSONObject encodingObj = new JSONObject();
			encodingObj.put("mid", aResponseMessage.getID());
			HamVuiResponse reconnectRes = (HamVuiResponse) aResponseMessage;
			encodingObj.put("code", reconnectRes.mCode);
			if (reconnectRes.mCode == ResponseCode.FAILURE) {
				encodingObj.put("error_msg", reconnectRes.mErrorMsg);
			} else if (reconnectRes.mCode == ResponseCode.SUCCESS) {
				if (reconnectRes.isNeeded) {
					newProtocol(reconnectRes, encodingObj);
					return encodingObj;
				}
			}
			return encodingObj;
		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
}
