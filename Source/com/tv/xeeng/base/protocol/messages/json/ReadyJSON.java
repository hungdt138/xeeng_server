package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;


import org.json.JSONArray;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.ReadyRequest;
import com.tv.xeeng.base.protocol.messages.ReadyResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class ReadyJSON implements IMessageProtocol {

	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			ReadyJSON.class);

	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		try {
			// request data
			JSONObject jsonData = (JSONObject) aEncodedObj;
			// request messsage
			ReadyRequest matchReady = (ReadyRequest) aDecodingObj;
			// parsing
                        if(jsonData.has("v"))
                        {
                            String v= jsonData.getString("v");
                            String[] arrValues = v.split(AIOConstants.SEPERATOR_BYTE_1);
                            
                            if(arrValues.length < 2)
                            {
                                arrValues = v.split(AIOConstants.STRING_SEPERATOR_ELEMENT);
                            }
                            
                            matchReady.ready = Integer.parseInt(arrValues[1])==1;
                            matchReady.matchID = Long.parseLong(arrValues[0]);
                            
                            return true;
                        }
                        
                        
                        if(jsonData.has("match_id"))
			    matchReady.matchID = jsonData.getLong("match_id");
                            
                        if(jsonData.has("uid"))
			    matchReady.uid = jsonData.getLong("uid");
			if (jsonData.has("ready")) {
				matchReady.ready = jsonData.getBoolean("ready");
			} else {
				matchReady.ready = true;
			}
                        
                        
                        

			return true;
		} catch (Throwable t) {
			mLog.error("[DECODER] " + aDecodingObj.getID(), t);
			return false;
		}
	}

	public void addPhomData(ReadyResponse matchJoin, JSONObject jCell,
			PhomPlayer player) {
		try {
			jCell.put("id", player.id);
			jCell.put("username", player.username);
			jCell.put("level", player.level);
			jCell.put("avatar", player.avatarID);
			jCell.put("money", player.cash);
			jCell.put("isReady", player.isReady);
			jCell.put("isAuto", player.isAutoPlay);
		} catch (Exception e) {
		}

	}
        
        private String getMidReady(ReadyResponse  matchReady)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(MessagesID.MATCH_READY)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(matchReady.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
            if(matchReady.mCode == ResponseCode.SUCCESS)
            {
            
                sb.append(Long.toString(matchReady.mUid)).append(AIOConstants.SEPERATOR_BYTE_1);
                if(matchReady.ready)
                {
                    sb.append("1");
                }
                else
                {
                    sb.append("0");
                }
            }
            else
            {
                sb.append(matchReady.mErrorMsg);
            }
            
            return sb.toString();
                                
        }
        
        
	public Object encode(IResponseMessage aResponseMessage)
			throws ServerException {
		try {
			JSONObject encodingObj = new JSONObject();
                        ReadyResponse matchReady = (ReadyResponse) aResponseMessage;
                        
                        if(matchReady.session != null && matchReady.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
                        {
                            encodingObj.put("v", getMidReady(matchReady));
                            return encodingObj;
                        }

			encodingObj.put("mid", aResponseMessage.getID());

			

			encodingObj.put("code", matchReady.mCode);
			encodingObj.put("uid", matchReady.mUid);

			if (matchReady.mCode == ResponseCode.FAILURE) {
				encodingObj.put("error_msg", matchReady.mErrorMsg);

			} else if (matchReady.mCode == ResponseCode.SUCCESS) {
                            if(matchReady.session != null && matchReady.session.getByteProtocol()> AIOConstants.PROTOCOL_PRIMITIVE)
                            {
                                StringBuilder sb = new StringBuilder();
                                sb.append(Long.toString(matchReady.mUid)).append(AIOConstants.SEPERATOR_ELEMENT);
                                if(matchReady.ready)
                                {
                                    sb.append("1");
                                }
                                else
                                {
                                    sb.append("0");
                                }
                                
                                encodingObj.put("v", sb.toString());
                                return encodingObj;
                            }
                            
                            
				encodingObj.put("ready", matchReady.ready);
		
				if (matchReady.zone == ZoneID.PHOM) {
					if (matchReady.mPlayerPhom != null) {
						JSONArray arrValues = new JSONArray();

						for (PhomPlayer player : matchReady.mPlayerPhom) {
							JSONObject jCell = new JSONObject();
							addPhomData(matchReady, jCell, player);
							jCell.put("isObserve", false);
							arrValues.put(jCell);
						}

						for (PhomPlayer player : matchReady.mWaitingPlayerPhom) {
							JSONObject jCell = new JSONObject();
							addPhomData(matchReady, jCell, player);
							jCell.put("isObserve", true);
							arrValues.put(jCell);
						}
						encodingObj.put("table_values", arrValues);
					}
				}
			}
			return encodingObj;
		} catch (Throwable t) {
			mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
			return null;
		}
	}
}
