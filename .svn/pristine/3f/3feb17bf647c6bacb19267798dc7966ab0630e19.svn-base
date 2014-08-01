package com.tv.xeeng.base.protocol.messages.json;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.EndMatchResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class EndMatchJSON implements IMessageProtocol {
    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(EndMatchJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            EndMatchResponse matchEnded = (EndMatchResponse) aResponseMessage;
            if (matchEnded.session != null && matchEnded.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(matchEnded.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (matchEnded.mCode == ResponseCode.FAILURE) {
                    sb.append(matchEnded.mErrorMsg);
                } else {
                    sb.append(finalProtocol(matchEnded, AIOConstants.SEPERATOR_BYTE_1, AIOConstants.SEPERATOR_BYTE_2, AIOConstants.SEPERATOR_BYTE_3));
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", matchEnded.mCode);
            if (matchEnded.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error", matchEnded.mErrorMsg);
            } else if (matchEnded.mCode == ResponseCode.SUCCESS) {
                if (matchEnded.session != null
                        && matchEnded.session.getByteProtocol() > AIOConstants.PROTOCOL_PRIMITIVE
                        && (matchEnded.zoneID == ZoneID.PHOM || matchEnded.zoneID == ZoneID.TIENLEN)) {
                    encodingObj.put("v", (finalProtocol(matchEnded, AIOConstants.SEPERATOR_ELEMENT, AIOConstants.SEPERATOR_ARRAY, AIOConstants.SEPERATOR_DIFF_ELEMENT)));
                    return encodingObj;
                }
                //if (matchEnded.zoneID != ZoneID.XOC_DIA) // no need this
                // parameter from
                // client side
                encodingObj.put("match_id", matchEnded.mMatchId);
                switch (matchEnded.zoneID) {
                    case ZoneID.PHOM: {
                        encodingObj.put("winner", matchEnded.phomWinner.id);
                        encodingObj.put("u_type", matchEnded.uType);
                        if (matchEnded.newOwner > 0) {
                            encodingObj.put("newOwner", matchEnded.newOwner);
                        }
                        JSONArray players = new JSONArray();
                        for (PhomPlayer p : matchEnded.phomPlayers) {
                            try {
                                boolean isExpiredNew = p.currentSession.isExpiredNew();
                                
                                JSONObject jO = new JSONObject();
                                jO.put("uid", p.id);
                                jO.put("is_win", p.isWin);
                                jO.put("point", p.point);
                                jO.put("cards", p.allPokersToString());
                                jO.put("money", p.money);
                                jO.put("cash", p.cash);
                                jO.put("hetTien", p.notEnoughMoney());
                                jO.put("isOut", ( p.currentSession.isExpiredNew() || p.isAutoPlay ));
                                
                                if (isExpiredNew)
                                {
                                    mLog.debug("");
                                }
                                players.put(jO);
                            } catch (Exception ex) {
                                mLog.error(ex.getMessage(), ex);
                            }
                        }
                        encodingObj.put("Players", players);
                        break;
                    }
                    case ZoneID.TIENLEN: {
                        encodingObj.put("idWin", matchEnded.idWin);
                        encodingObj.put("perfectType", matchEnded.perfectType);
                        if (matchEnded.perfectType == 0) {
                            if (matchEnded.newOwner > 0) {
                                encodingObj.put("newOwner", matchEnded.newOwner);
                            }
                            encodingObj.put("uid", matchEnded.uid);
                            encodingObj.put("lastCards", matchEnded.lastCards);
                            JSONArray resultTienlen = new JSONArray();
                            for (int i = 0; i < matchEnded.tienlenResult.size(); i++) {
                                JSONObject jO = new JSONObject();
                                Object[] o = matchEnded.tienlenResult.get(i);
                                long uid = Long.parseLong(o[0].toString());
                                jO.put("uid", uid);
                                jO.put("money", Long.parseLong(o[1].toString()));
                                jO.put("note", o[2].toString());
                                if (uid != matchEnded.idWin) {
                                    jO.put("cards", o[3].toString());
                                }
                                resultTienlen.put(jO);
                            }
                            encodingObj.put("result", resultTienlen);
                            // Chặt chém
                            if (matchEnded.fightInfo != null && matchEnded.fightInfo.size() > 0) {
                                encodingObj.put("isFight", true);
                                long[] data = matchEnded.fightInfo.get(0);
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
                        } else {
                            JSONArray resultTienlen = new JSONArray();
                            for (int i = 0; i < matchEnded.tienlenResult.size(); i++) {
                                JSONObject jO = new JSONObject();
                                Object[] o = matchEnded.tienlenResult.get(i);
                                jO.put("uid", Long.parseLong(o[0].toString()));
                                jO.put("cards", o[1].toString());
                                jO.put("money", Long.parseLong(o[2].toString()));
                                jO.put("note", o[3].toString());
                                resultTienlen.put(jO);
                            }
                            encodingObj.put("result", resultTienlen);
                        }
                        break;
                    }
                    
                    case ZoneID.NEW_BA_CAY:
                    	
                    case ZoneID.BAU_CUA_TOM_CA:
                    
                    case ZoneID.PIKACHU:
                    
                    default:
                        break;
                }
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }

    private String tableValue(JSONArray arr, boolean isPlaying, int zone, String seperator_element, String seperator_ar) throws JSONException {
        StringBuilder sb = new StringBuilder();
        if (isPlaying) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                sb.append(obj.get("uid")).append(seperator_element);
                sb.append(obj.get("wonMOney")).append(seperator_element);
                sb.append(obj.get("cash"));// .append(AIOConstants.SEPERATOR_BYTE_1);
                // sb.append(obj.get("isOut"));//.append(AIOConstants.SEPERATOR_BYTE_1);
                if (zone == ZoneID.NEW_BA_CAY) {
                    sb.append(seperator_element).append(obj.get("point"));
                    if (obj.has("cards")) {
                        sb.append(seperator_element).append(obj.get("cards"));// .append(AIOConstants.SEPERATOR_BYTE_1);
                    }
                }
                else if (zone == ZoneID.AILATRIEUPHU) {
                    if (obj.has("point")) {
                        sb.append(seperator_element).append(obj.get("cauhoi")).append(seperator_element);
                        sb.append(obj.get("help")).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(obj.get("point")).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(obj.get("pos"));
                    }
                }

                sb.append(seperator_ar);
            }
        } else {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                sb.append(obj.get("uid")).append(seperator_element);
                sb.append(obj.get("cash")).append(seperator_element);
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private String finalProtocol(EndMatchResponse matchEnded, String seperator_element, String seperator_ar, String seperator_dif_element) throws JSONException {
        StringBuilder sb = new StringBuilder();
        switch (matchEnded.zoneID) {
            case ZoneID.PHOM: {
                sb.append(matchEnded.phomWinner.id).append(seperator_element);
                sb.append(matchEnded.uType).append(seperator_element);
                sb.append(matchEnded.newOwner).append(seperator_element);
                sb.append(matchEnded.phomDuty).append(seperator_dif_element);
                int playingSize = matchEnded.phomPlayers.size();
                StringBuilder sb1 = new StringBuilder();
                for (int i = 0; i < playingSize; i++) {
                    PhomPlayer p = matchEnded.phomPlayers.get(i);
                    sb1.append(p.id).append(seperator_element);
                    sb1.append(p.point).append(seperator_element);
                    sb1.append(p.allPokersToString()).append(seperator_element);
                    sb1.append(p.money).append(seperator_element);
//                    sb1.append(p.resultMoney).append(seperator_element);
                    sb1.append(p.cash).append(seperator_element);
                    sb1.append(p.notEnoughMoney() ? "1" : "0").append(seperator_element);
//                    sb1.append((p.currentSession.isExpiredNew() || p.isAutoPlay) ? "1" : "0").append(seperator_ar);
//                    sb1.append(p.isAutoPlay ? "1" : "0").append(seperator_ar);
                    sb1.append(p.isOut ? "1" : "0").append(seperator_ar);
                    mLog.debug("---RESULT MONEY END GAME---" + p.username + " " + p.resultMoney);
                }
                sb1.deleteCharAt(sb1.length() - 1);
                sb.append(sb1);
                return sb.toString();
            }
            case ZoneID.TIENLEN: {
                sb.append(matchEnded.idWin).append(seperator_element);
                sb.append(matchEnded.perfectType).append(seperator_element);
                sb.append(matchEnded.uid).append(seperator_element);

                if (matchEnded.perfectType == 0) {
                    if (matchEnded.lastCards == null) {
                        sb.append("").append(seperator_element);
                    } else {
                        sb.append(matchEnded.lastCards).append(seperator_element);
                    }

                    sb.append(Long.toString(matchEnded.newOwner)).append(seperator_dif_element);

                    int resultSize = matchEnded.tienlenResult.size();
                    for (int i = 0; i < resultSize; i++) {
                        String[] o = matchEnded.tienlenResult.get(i);
                        long uid = Long.parseLong(o[0]);
                        sb.append(o[0]).append(seperator_element);
                        sb.append(o[1]).append(seperator_element);
                        sb.append(o[2]).append(seperator_element);
                        String cards = "";
                        if (uid != matchEnded.idWin) {
                            cards = o[3];
                        }
                        sb.append(cards).append(seperator_element);

                        int playerIndex = Integer.parseInt(o[4]);
                        SimplePlayer player = matchEnded.playings.get(playerIndex);
                        if (player != null) {
//                            sb.append((player.currentSession.isExpiredNew() || player.isOut) ? "1" : "0").append(seperator_element);
                            sb.append(player.isOut ? "1" : "0").append(seperator_element);
//                            sb.append(player.notEnoughMoney() ? "1" : "0").append(seperator_ar);
                            sb.append(player.notEnoughMoney() ? "1" : "0").append(seperator_element);
                            sb.append(player.cash).append(seperator_ar);
                        }
                    }

                    sb.deleteCharAt(sb.length() - 1);
                    
                    if (matchEnded.fightInfo != null && matchEnded.fightInfo.size() > 0) {
                        long[] data = matchEnded.fightInfo.get(0);
                        sb.append(data[0]).append(seperator_element);
                        sb.append(data[1]).append(seperator_element);
                        sb.append(data[2]).append(seperator_element);
                        if (data.length == 5) {
                            sb.append(1).append(seperator_element);
                            sb.append(data[3]).append(seperator_element);
                            sb.append(data[4]).append(seperator_element);
                        } else {
                            sb.append(0).append(seperator_element);
                        }
                        sb.deleteCharAt(sb.length() - 1);
                    }
                } else {
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append(seperator_dif_element);
                    int resultSize = matchEnded.tienlenResult.size();
                    // int count = 0;
                    for (int i = 0; i < resultSize; i++) {
                        String[] o = matchEnded.tienlenResult.get(i);
                        sb.append(o[0]).append(seperator_element);
                        sb.append(o[2]).append(seperator_element);

                        String cards = o[3];
                        sb.append(cards).append(seperator_element);
                        sb.append(o[1]).append(seperator_element);
                        int playerIndex = Integer.parseInt(o[4]);
                        SimplePlayer player = matchEnded.playings.get(playerIndex);
//                        sb.append((player.currentSession.isExpiredNew() || player.isOut) ? "1" : "0").append(seperator_element);
                        sb.append(player.isOut ? "1" : "0").append(seperator_element);
//                        sb.append(player.notEnoughMoney() ? "1" : "0").append(seperator_ar);
                        sb.append(player.notEnoughMoney() ? "1" : "0").append(seperator_element);
                        sb.append(player.cash).append(seperator_ar);
                    }

                    sb.deleteCharAt(sb.length() - 1);
                    sb.append(seperator_dif_element);
                    sb.append(Long.toString(matchEnded.newOwner)).append(seperator_dif_element);
                }

                return sb.toString();
            }

            /*
             case ZoneID.BAU_CUA_TOM_CA: 
             case ZoneID.NEW_BA_CAY:    
             case ZoneID.AILATRIEUPHU:   
             case ZoneID.PIKACHU:
             case ZoneID.LIENG: 
            */

            default: {
                return matchEnded.value;
            }
        }
    }
}
