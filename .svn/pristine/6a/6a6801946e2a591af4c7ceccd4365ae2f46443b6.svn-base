/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BetRequest;
import com.tv.xeeng.base.protocol.messages.BetResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class BetJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BetJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            BetRequest betRequest = (BetRequest) aDecodingObj;

            if (jsonData.has("v")) {
                try {
                    String v = jsonData.getString("v");
                    String[] arrValues = v.split(AIOConstants.SEPERATOR_BYTE_1);
                    int game = Integer.parseInt(arrValues[0]);
                    betRequest.matchID = Long.parseLong(arrValues[1]);
                    switch (game) {
                        case ZoneID.NEW_BA_CAY:
                            betRequest.money = Long.parseLong(arrValues[2]);
                            break;

                        case ZoneID.BAU_CUA_TOM_CA:
                            betRequest.holo = Long.parseLong(arrValues[2]);
                            betRequest.cua = Long.parseLong(arrValues[3]);
                            betRequest.tom = Long.parseLong(arrValues[4]);
                            betRequest.ca = Long.parseLong(arrValues[5]);
                            betRequest.ga = Long.parseLong(arrValues[6]);
                            betRequest.huou = Long.parseLong(arrValues[7]);
                            break;

                        default:
                            break;
                    }
                    return true;
                } catch (Exception ex) {
                    mLog.error(ex.getMessage(), ex);
                }
            }

            if (jsonData.has("money"))// for game ba cay
            {
                betRequest.money = jsonData.getLong("money");
            }

            if (jsonData.has("holo"))// for game bau tom cua ca
            {
                betRequest.holo = jsonData.getLong("holo");
            }

            if (jsonData.has("tom"))// for game bau tom cua ca
            {
                betRequest.tom = jsonData.getLong("tom");
            }

            if (jsonData.has("cua"))// for game bau tom cua ca
            {
                betRequest.cua = jsonData.getLong("cua");
            }

            if (jsonData.has("ca"))// for game bau tom cua ca
            {
                betRequest.ca = jsonData.getLong("ca");
            }

            if (jsonData.has("ga"))// for game bau tom cua ca
            {
                betRequest.ga = jsonData.getLong("ga");
            }

            if (jsonData.has("huou"))// for game bau tom cua ca
            {
                betRequest.huou = jsonData.getLong("huou");
            }

            if (jsonData.has("playings")) {

                JSONArray playersArr = jsonData.getJSONArray("playings");

                List<SimplePlayer> players = new ArrayList<SimplePlayer>();

                for (int i = 0; i < playersArr.length(); i++) {
                    JSONObject jPlayer = playersArr.getJSONObject(i);
                    SimplePlayer player = new SimplePlayer();

                    if (jPlayer.has("uid")) {
                        player.id = jPlayer.getLong("uid");
                    }

                    if (jPlayer.has("money")) {
                        player.setBetOther(jPlayer.getLong("money"));
                    }

                    if (jPlayer.has("isChan")) {
                        player.setChan(jPlayer.getBoolean("isChan"));
                    }

                    players.add(player);
                }

                betRequest.playings = players;

            }

            if (jsonData.has("match_id")) {
                betRequest.matchID = jsonData.getLong("match_id");
            }

            if (jsonData.has("uid")) {
                betRequest.uid = jsonData.getLong("uid");
            }

            if (jsonData.has("type")) {
                betRequest.type = jsonData.getInt("type");
            }

            return true;
        } catch (JSONException ex) {
            mLog.error(ex.getStackTrace().toString());
            return false;
        }
    }

    private String dataBacay(long money, String cards) {
        StringBuilder sb = new StringBuilder();
        sb.append(money).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(cards);// .append(AIOConstants.SEPERATOR_BYTE_1);
        return sb.toString();
    }

    private String dataXiTo(JSONObject encodingObj) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(encodingObj.get("uid")).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(encodingObj.get("type")).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(encodingObj.get("call")).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(encodingObj.get("total")).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(encodingObj.get("money"));
        if (encodingObj.has("nextId")) {
            sb.append(AIOConstants.SEPERATOR_BYTE_1).append(encodingObj.get("nextId"));//
        }
        return sb.toString();
    }

    private String dataXocDia(JSONArray data) throws JSONException {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            JSONObject encodingObj = data.getJSONObject(i);
            sb.append(encodingObj.get("uid")).append(
                    AIOConstants.SEPERATOR_BYTE_1);
            /*sb.append(encodingObj.get("money")).append(
             AIOConstants.SEPERATOR_BYTE_1);*/
            if (encodingObj.has("chan")) {
                sb.append(encodingObj.get("chan")).append(
                        AIOConstants.SEPERATOR_BYTE_1);
            } else {
                sb.append(0).append(AIOConstants.SEPERATOR_BYTE_1);
            }

            if (encodingObj.has("le")) {
                sb.append(encodingObj.get("le")).append(
                        AIOConstants.SEPERATOR_BYTE_2);
            } else {
                sb.append(0).append(AIOConstants.SEPERATOR_BYTE_2);
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private String dataBauCuaMobile(JSONArray data) throws JSONException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            JSONObject encodingObj = data.getJSONObject(i);
            sb.append(encodingObj.get("uid")).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(encodingObj.get("money")).append(AIOConstants.SEPERATOR_BYTE_2);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private String dataBauCuaFlash(JSONArray data) throws JSONException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            JSONObject encodingObj = data.getJSONObject(i);
            sb.append(encodingObj.get("uid")).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(encodingObj.get("holo")).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(encodingObj.get("cua")).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(encodingObj.get("tom")).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(encodingObj.get("ca")).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(encodingObj.get("ga")).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(encodingObj.get("huou")).append(AIOConstants.SEPERATOR_BYTE_2);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    // Added by ThangTD
    private String dataBauCuaNew(JSONArray data) throws JSONException {
        StringBuilder sb = new StringBuilder();
        if (data.length() != 0) {
            JSONObject encodingObj = data.getJSONObject(0);
            sb.append(encodingObj.get("holo")).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(encodingObj.get("cua")).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(encodingObj.get("tom")).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(encodingObj.get("ca")).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(encodingObj.get("ga")).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(encodingObj.get("huou")).append(AIOConstants.SEPERATOR_BYTE_2);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
    // End added by ThangTD

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            BetResponse bet = (BetResponse) aResponseMessage;
            if (bet.session != null && bet.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(bet.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (bet.mCode == ResponseCode.FAILURE) {
                    sb.append(bet.errMsg);
                } else {
                    String v = "";
                    switch (bet.zoneId) {
                        case ZoneID.NEW_BA_CAY:
                            v = dataBacay(bet.money, bet.cards);
                            break;
                        case ZoneID.BAU_CUA_TOM_CA:
                            v = dataBauCuaNew(bet.betInfo);
//                            if (bet.session.isMobileDevice()) {
//                                v = dataBauCuaMobile(bet.betInfo);
//                            } else {
//                                v = dataBauCuaFlash(bet.betInfo);
//                            }
                            break;
                        default:
                            break;
                    }
                    sb.append(v);
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", bet.mCode);
            if (bet.mCode == ResponseCode.SUCCESS) {
                switch (bet.zoneId) {
                    case ZoneID.NEW_BA_CAY:
                        encodingObj.put("money", bet.money);
                        if (bet.cards != null && !bet.cards.equals("")) {
                            encodingObj.put("cards", bet.cards);
                        }
                        break;
                    case ZoneID.BAU_CUA_TOM_CA:
                        encodingObj.put("betInfo", bet.betInfo);
                        break;
                }
                encodingObj.put("uid", bet.uid);
            } else {
                encodingObj.put("error", bet.errMsg);
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
