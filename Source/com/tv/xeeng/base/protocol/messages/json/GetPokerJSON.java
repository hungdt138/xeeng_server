package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import org.json.JSONException;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetPokerResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class GetPokerJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GetPokerJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void getMidEncode(GetPokerResponse getPoker, JSONObject encodingObj) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(MessagesID.GET_POKER)).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(Integer.toString(getPoker.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
        if (getPoker.mCode == ResponseCode.SUCCESS) {
            switch (getPoker.zoneId) {
                case ZoneID.SAM: {
                    sb.append(getPoker.beginID).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(getPoker.samCards).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(getPoker.samPerfectType);

                    break;
                }
                case ZoneID.TIENLEN:
                    sb.append(getPoker.beginID).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(com.tv.xeeng.game.tienlen.data.Utils.bytesToString(getPoker.tienlenCards_new)).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(getPoker.dutyType);

                    break;
                case ZoneID.PHOM: {
                    sb.append(getPoker.beginID).append(AIOConstants.SEPERATOR_BYTE_1);
                    StringBuilder dtBuilder = new StringBuilder();

                    for (int i = 0; i < getPoker.phomCards.size(); i++) {
                        dtBuilder.append(getPoker.phomCards.get(i).toInt()).append("#");
                        //                                data += "#" + getPoker.phomCards.get(i).toInt();
                    }

                    dtBuilder.deleteCharAt(dtBuilder.length() - 1);

                    sb.append(dtBuilder.toString());
                    sb.append(AIOConstants.SEPERATOR_BYTE_1).append(getPoker.dutyType);

                    break;
                }
                case ZoneID.NEW_BA_CAY: {
                    sb.append(getPoker.cards).append(AIOConstants.SEPERATOR_BYTE_3);
                    for (int i = 0; i < getPoker.betInfo.length(); i++) {
                        JSONObject obj = getPoker.betInfo.getJSONObject(i);
                        sb.append(obj.get("uid")).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(obj.get("moneyBet")).append(AIOConstants.SEPERATOR_BYTE_2);
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }

                    break;
                }
            }
        }

        encodingObj.put("v", sb);
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            GetPokerResponse getPoker = (GetPokerResponse) aResponseMessage;
            if (getPoker.session != null && getPoker.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                getMidEncode(getPoker, encodingObj);
                return encodingObj;
            }

            // put response data into json object
            encodingObj.put("mid", aResponseMessage.getID());
            // cast response obj

            encodingObj.put("code", getPoker.mCode);
            if (getPoker.mCode == ResponseCode.FAILURE) {
            } else if (getPoker.mCode == ResponseCode.SUCCESS) {
                if (getPoker.session != null && getPoker.session.getByteProtocol() > AIOConstants.PROTOCOL_PRIMITIVE) {
                    StringBuilder sb = new StringBuilder();
                    switch (getPoker.zoneId) {
                        case ZoneID.SAM: {
                            sb.append(getPoker.beginID).append(AIOConstants.SEPERATOR_BYTE_1);
                            sb.append(getPoker.samCards).append(AIOConstants.SEPERATOR_BYTE_1);
                            sb.append("0");

                            break;
                        }
                        case ZoneID.TIENLEN:
                            sb.append(getPoker.beginID).append(AIOConstants.SEPERATOR_ELEMENT);
                            sb.append(com.tv.xeeng.game.tienlen.data.Utils.bytesToString(getPoker.tienlenCards_new)).append(AIOConstants.SEPERATOR_ELEMENT);
                            sb.append(getPoker.dutyType);
                            encodingObj.put("v", sb);

                            return encodingObj;
                        case ZoneID.PHOM: {
                            sb.append(getPoker.beginID).append(AIOConstants.SEPERATOR_ELEMENT);
                            StringBuilder dtBuilder = new StringBuilder();

                            for (int i = 0; i < getPoker.phomCards.size(); i++) {
                                dtBuilder.append(getPoker.phomCards.get(i).toInt()).append("#");
//                                data += "#" + getPoker.phomCards.get(i).toInt();
                            }

                            dtBuilder.deleteCharAt(dtBuilder.length() - 1);

                            sb.append(dtBuilder.toString());
                            sb.append(AIOConstants.SEPERATOR_ELEMENT).append(getPoker.dutyType);
                            encodingObj.put("v", sb);

                            return encodingObj;
                        }
                        case ZoneID.BINH: {
                            sb.append(getPoker.beginID).append(AIOConstants.SEPERATOR_ELEMENT);
                            StringBuilder dtBuilder = new StringBuilder();

                            for (int i = 0; i < getPoker.getBinhCards().size(); i++) {
                                dtBuilder.append(getPoker.getBinhCards().get(i).toInt()).append("#");
                            }

                            dtBuilder.deleteCharAt(dtBuilder.length() - 1);

                            sb.append(dtBuilder.toString());
                            sb.append(AIOConstants.SEPERATOR_ELEMENT).append(getPoker.dutyType);
                            encodingObj.put("v", sb);

                            return encodingObj;
                        }
                    }
                }

                if (getPoker.uid > 0) {
                    encodingObj.put("uid", getPoker.uid);
                }

                switch (getPoker.zoneId) {
                    case ZoneID.NEW_BA_CAY:
                        encodingObj.put("cards", getPoker.cards);
                        encodingObj.put("betInfo", getPoker.betInfo);
                        return encodingObj;
                }

                encodingObj.put("username", getPoker.name);
                encodingObj.put("matchNum", getPoker.matchNum);

                if (getPoker.phomCards.size() > 0) {
                    encodingObj.put("number_card", getPoker.phomCards.size());
                    String data = "" + getPoker.phomCards.get(0).toInt();
                    for (int i = 1; i < getPoker.phomCards.size(); i++) {
                        data += "#" + getPoker.phomCards.get(i).toInt();
                    }
                    encodingObj.put("cards", data);
                    if (getPoker.beginID >= 0) {
                        encodingObj.put("begin_uid", getPoker.beginID);
                    }
                } else if (getPoker.tienlenCards_new != null) {
                    encodingObj.put("number_card", getPoker.tienlenCards_new.length);
                    encodingObj.put("dutyType", getPoker.dutyType);
                    encodingObj.put("cards", com.tv.xeeng.game.tienlen.data.Utils.bytesToString(getPoker.tienlenCards_new));
                    if (getPoker.beginID >= 0) {
                        encodingObj.put("begin_uid", getPoker.beginID);
                    }
                    encodingObj.put("isNewMatch", getPoker.isNewMatch);
                } else if (getPoker.getBinhCards().size() > 0) {
                    encodingObj.put("number_card", getPoker.getBinhCards().size());
                    String data = "" + getPoker.getBinhCards().get(0).toInt();
                    for (int i = 1; i < getPoker.getBinhCards().size(); i++) {
                        data += "#" + getPoker.getBinhCards().get(i).toInt();
                    }
                    encodingObj.put("cards", data);
                    if (getPoker.beginID >= 0) {
                        encodingObj.put("begin_uid", getPoker.beginID);
                    }
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
