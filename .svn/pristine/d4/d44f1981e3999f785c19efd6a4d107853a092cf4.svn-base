package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.RestartRequest;
import com.tv.xeeng.base.protocol.messages.RestartResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class RestartJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(RestartJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            // request data
            JSONObject jsonData = (JSONObject) aEncodedObj;
            // request messsage
            RestartRequest matchTurn = (RestartRequest) aDecodingObj;
            // parsing
            matchTurn.mMatchId = jsonData.getLong("match_id");
            try {
                matchTurn.money = jsonData.getLong("money");
            } catch (Exception e) {
            }
            matchTurn.uid = jsonData.getLong("uid");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            // put response data into json object
            encodingObj.put("mid", aResponseMessage.getID());
            // cast response obj
            RestartResponse matchReturn = (RestartResponse) aResponseMessage;
            encodingObj.put("code", matchReturn.mCode);

            if (matchReturn.mCode == ResponseCode.FAILURE) {
                encodingObj.put("isEmptyRoom", matchReturn.isEmptyRoom);
                encodingObj.put("error_msg", matchReturn.mErrorMsg);
            } else if (matchReturn.mCode == ResponseCode.SUCCESS) {
                encodingObj.put("match_id", matchReturn.matchID);
                encodingObj.put("minBet", matchReturn.roomOwner.moneyForBet);
                encodingObj.put("roomOwner_id", matchReturn.roomOwner.id);
                encodingObj.put("roomOwner_username", matchReturn.roomOwner.username);
                encodingObj.put("roomOwner_level", matchReturn.roomOwner.level);
                encodingObj.put("roomOwner_avatar", matchReturn.roomOwner.avatarID);
                encodingObj.put("roomOwner_money", matchReturn.roomOwner.cash);
                encodingObj.put("roomname", matchReturn.roomName);
                encodingObj.put("available", matchReturn.available);
                encodingObj.put("begin_id", matchReturn.begin_id);
                if(matchReturn.zoneID == ZoneID.TIENLEN){
                	JSONArray arrValues = new JSONArray();
                    //mLog.info("[DEBUG] " + matchReturn.players.size());
                    for (SimplePlayer player : matchReturn.tienLenPlayer) {
                        //mLog.info("[Player] " + player.username);
                        JSONObject jCell = new JSONObject();
                        jCell.put("id", player.id);
                        jCell.put("username", player.username);
                        jCell.put("level", player.level);
                        jCell.put("avatar", player.avatarID);
                        jCell.put("money", player.cash);
                        // put to array
                        arrValues.put(jCell);
                    }
                    encodingObj.put("table_values", arrValues);
                }
                else if (matchReturn.zoneID == ZoneID.PHOM) {
                    JSONArray arrValues = new JSONArray();
                    //mLog.error("[DEBUG] " + matchReturn.phomPlayers.size());
                    for (SimplePlayer player : matchReturn.phomPlayers) {
                        //mLog.error("[Player] " + player.username);
                        JSONObject jCell = new JSONObject();
                        jCell.put("id", player.id);
                        jCell.put("username", player.username);
                        jCell.put("level", player.level);
                        jCell.put("avatar", player.avatarID);
                        jCell.put("money", player.cash);
                        // put to array
                        arrValues.put(jCell);
                    }
                    encodingObj.put("table_values", arrValues);
                }
            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            mLog.error("[ENCODER] " + t.toString());

            return null;
        }
    }
}
