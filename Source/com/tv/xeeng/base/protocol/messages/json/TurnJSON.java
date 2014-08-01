/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.TurnRequest;
import com.tv.xeeng.base.protocol.messages.TurnResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import com.tv.xeeng.server.Server;
import static com.tv.xeeng.server.Server.REAL_GOT_MONEY;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

/**
 *
 * @author tuanda
 */
public class TurnJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(TurnJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            // request data
            JSONObject jsonData = (JSONObject) aEncodedObj;
            // request messsage
            TurnRequest matchTurn = (TurnRequest) aDecodingObj;
            if (jsonData.has("v")) {
                String[] arrV = jsonData.getString("v").split(AIOConstants.SEPERATOR_BYTE_1);
                int zoneId = Integer.parseInt(arrV[0]);
                long match = Long.parseLong(arrV[1]);
                matchTurn.mMatchId = match;
                if (zoneId == ZoneID.PHOM) {
                    matchTurn.phomCard = Integer.parseInt(arrV[2]);
                } else {
                    //tien len || sam
                    if (arrV.length > 2) {
                        matchTurn.tienlenCards = arrV[2];
                        mLog.debug("---THANGTD DEBUG SAM---RECEIVED RAW CARDS: " + matchTurn.tienlenCards);
                    } else {
                        matchTurn.isGiveup = true;
                    }
                }

                return true;
            }

            // parsing
            if (jsonData.has("match_id")) {
                matchTurn.mMatchId = jsonData.getLong("match_id");
                matchTurn.uid = jsonData.getLong("uid");
            } else {
                matchTurn.mMatchId = jsonData.getLong("match");
            }

            if (jsonData.has("card")) {
                matchTurn.phomCard = jsonData.getInt("card");
            }// phom

//			try {
//				matchTurn.mRow = jsonData.getInt("row");
//				matchTurn.mCol = jsonData.getInt("col");
//				matchTurn.mType = jsonData.getInt("type");
//			} catch (Exception e) {
//			}
            // Tienlen
            if (jsonData.has("cards")) {
                matchTurn.tienlenCards = jsonData.getString("cards");
            } else {
                matchTurn.isGiveup = true;
//                            matchTurn.
            }
//			} else {
//				matchTurn.tienlenCards = "";
//			}
            try {
                if (jsonData.has("isGiveup")) {
                    matchTurn.isGiveup = jsonData.getBoolean("isGiveup");
                }
            } catch (Exception eTienlen) {
            }

            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    private String ConvBoolToString(boolean value) {
        if (value) {
            return "1";
        } else {
            return "0";
        }
    }

    private void getMidEncode(TurnResponse matchTurn, JSONObject encodingObj) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(MessagesID.MATCH_TURN)).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(Integer.toString(matchTurn.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
        if (matchTurn.mCode == ResponseCode.SUCCESS) {
            switch (matchTurn.zoneID) {
                case ZoneID.PHOM: {
                    sb.append(matchTurn.phomCard).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(matchTurn.nextID);
                    encodingObj.put("v", sb.toString());
                    break;
                }

                case ZoneID.SAM:
                case ZoneID.TIENLEN: {
                    sb.append(matchTurn.currID).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(matchTurn.nextID).append(AIOConstants.SEPERATOR_BYTE_1);
                    if (matchTurn.tienlenCards != null) {
                        sb.append(matchTurn.tienlenCards).append(AIOConstants.SEPERATOR_BYTE_1);
                    } else {
                        sb.append("").append(AIOConstants.SEPERATOR_BYTE_1);
                    }

                    sb.append(ConvBoolToString(matchTurn.isDuty)).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(ConvBoolToString(matchTurn.isGiveup)).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(ConvBoolToString(matchTurn.isNewRound)).append(AIOConstants.SEPERATOR_BYTE_1);
                    if (matchTurn.fightInfo.size() > 0) {
                        long[] data = matchTurn.fightInfo.get(0);
                        sb.append(data[0]).append(AIOConstants.SEPERATOR_BYTE_1); //preID - người bị chặt
                        sb.append(data[1]).append(AIOConstants.SEPERATOR_BYTE_1); //fightID - người chặt
                        
                        long fightMoneyAfter = (long)((double)data[2] * REAL_GOT_MONEY);
                        sb.append(data[2]).append("#").append(fightMoneyAfter).append(AIOConstants.SEPERATOR_BYTE_1); //fightMoney - tiền chặt ThangTD
                        if (data.length == 5) { //TLMN
                            sb.append(1).append(AIOConstants.SEPERATOR_BYTE_1);
                            sb.append(data[3]).append(AIOConstants.SEPERATOR_BYTE_1); //firstID - người bị chặt lúc đầu - Client: pre_fight_id
                            sb.append(data[4]).append(AIOConstants.SEPERATOR_BYTE_1); //returnMoney - số tiền trả lại - Client: old_money
                        } else { //Sam
                            sb.append(0).append(AIOConstants.SEPERATOR_BYTE_1);
                        }
                    }

                    sb.deleteCharAt(sb.length() - 1);
                    encodingObj.put("v", sb.toString());

                    break;
                }
            }
        } else {
            sb.append(matchTurn.mErrorMsg);
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            TurnResponse matchTurn = (TurnResponse) aResponseMessage;
            if (matchTurn.session != null && matchTurn.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                getMidEncode(matchTurn, encodingObj);
                return encodingObj;
            }

            // put response data into json object
            encodingObj.put("mid", aResponseMessage.getID());
            // cast response obj

            encodingObj.put("code", matchTurn.mCode);

            if (matchTurn.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", matchTurn.mErrorMsg);
            } else if (matchTurn.mCode == ResponseCode.SUCCESS) {
                // encodingObj.put("is_end", matchTurn.mIsEnd);
                if (matchTurn.session != null && matchTurn.session.getByteProtocol() > AIOConstants.PROTOCOL_PRIMITIVE) {
                    StringBuilder sb = new StringBuilder();
                    switch (matchTurn.zoneID) {
                        case ZoneID.PHOM: {
                            sb.append(matchTurn.phomCard).append(AIOConstants.SEPERATOR_ELEMENT);
                            sb.append(matchTurn.nextID);

                            encodingObj.put("v", sb.toString());
                            return encodingObj;
                        }
                        case ZoneID.TIENLEN: {
                            sb.append(matchTurn.currID).append(AIOConstants.SEPERATOR_ELEMENT);
                            sb.append(matchTurn.nextID).append(AIOConstants.SEPERATOR_ELEMENT);
                            if (matchTurn.tienlenCards != null) {
                                sb.append(matchTurn.tienlenCards).append(AIOConstants.SEPERATOR_ELEMENT);
                            } else {
                                sb.append("").append(AIOConstants.SEPERATOR_ELEMENT);
                            }
//                                        sb.append(ConvBoolToString(matchTurn.isDuty)).append(AIOConstants.SEPERATOR_ELEMENT);
                            sb.append(ConvBoolToString(false)).append(AIOConstants.SEPERATOR_ELEMENT);
                            sb.append(ConvBoolToString(matchTurn.isGiveup)).append(AIOConstants.SEPERATOR_ELEMENT);
                            sb.append(ConvBoolToString(matchTurn.isNewRound)).append(AIOConstants.SEPERATOR_ELEMENT);
                            if (matchTurn.fightInfo.size() > 0) {
                                long[] data = matchTurn.fightInfo.get(0);
                                sb.append(data[0]).append(AIOConstants.SEPERATOR_ELEMENT);
                                sb.append(data[1]).append(AIOConstants.SEPERATOR_ELEMENT);
                                sb.append(data[2]).append(AIOConstants.SEPERATOR_ELEMENT);
                                if (data.length == 5) {
                                    sb.append(1).append(AIOConstants.SEPERATOR_ELEMENT);
                                    sb.append(data[3]).append(AIOConstants.SEPERATOR_ELEMENT);
                                    sb.append(data[4]).append(AIOConstants.SEPERATOR_ELEMENT);
                                } else {
                                    sb.append(0).append(AIOConstants.SEPERATOR_ELEMENT);
                                }
                            }

                            sb.deleteCharAt(sb.length() - 1);
                            encodingObj.put("v", sb.toString());

                            return encodingObj;
                        }
                    }
                }

                switch (matchTurn.zoneID) {
                    case ZoneID.PHOM: {
                        encodingObj.put("card", matchTurn.phomCard);
                        encodingObj.put("next_id", matchTurn.nextID);
                        encodingObj.put("curr_id", matchTurn.preID);
                        break;
                    }

                    case ZoneID.TIENLEN: {
					// Thomc
                        // encodingObj.put("number_card",
                        // matchTurn.tienlenCards.length);
                        // String data = "" + matchTurn.tienlenCards[0];
                        // for (int i = 1; i < matchTurn.tienlenCards.length; i++) {
                        // data += "#" + matchTurn.tienlenCards[i];
                        // }
                        // encodingObj.put("cards", data);
                        encodingObj.put("cards", matchTurn.tienlenCards);
                        encodingObj.put("next_id", matchTurn.nextID);
                        encodingObj.put("isNewRound", matchTurn.isNewRound);
                        encodingObj.put("currID", matchTurn.currID);
                        encodingObj.put("isGiveup", matchTurn.isGiveup);
                        encodingObj.put("isDuty", matchTurn.isDuty);
                        // Chặt chém
                        if (matchTurn.fightInfo.size() > 0) {
                            encodingObj.put("isFight", true);
                            long[] data = matchTurn.fightInfo.get(0);
                            // người bị chặt
                            encodingObj.put("be_fight", data[0]);
                            // người chặt
                            encodingObj.put("fighter", data[1]);
                            // tiền chặt
                            encodingObj.put("money", data[2]);
                            if (data.length == 5) {
                                encodingObj.put("isOverFight", true);
                                // người bị chặt trước
                                encodingObj.put("pre_be_fight", data[3]);
                                // trả lại tiền
                                encodingObj.put("oldMoney", data[4]);
                            } else {
                                encodingObj.put("isOverFight", false);
                            }
                        } else {
                            encodingObj.put("isFight", false);
                        }
                        break;
                    }

                    default:
                        break;
                }

            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
