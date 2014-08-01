package com.tv.xeeng.base.protocol.messages.json;

//import org.json.JSONArray;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

//import bacay.data.PlayerInMatch;


import org.json.JSONException;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.StartRequest;
import com.tv.xeeng.base.protocol.messages.StartResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class StartJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(StartJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            // request data
            JSONObject jsonData = (JSONObject) aEncodedObj;
            // request messsage
            StartRequest matchStart = (StartRequest) aDecodingObj;
            if (jsonData.has("v")) {
                matchStart.mMatchId = jsonData.getLong("v");
                return true;
            }

            // parsing
            if (jsonData.has("match_id")) {
                matchStart.mMatchId = jsonData.getLong("match_id");
            } else {
                matchStart.mMatchId = jsonData.getLong("match");
            }

            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    private void getMidEncode(StartResponse matchStart, JSONObject encodingObj) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(MessagesID.MATCH_START)).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(Integer.toString(matchStart.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);

        if (matchStart.mCode == ResponseCode.FAILURE) {
            sb.append(matchStart.mErrorMsg);
        } else {
            switch (matchStart.zoneID) {
                case ZoneID.NEW_BA_CAY: {
                    break;
                }
                
                case ZoneID.NEW_PIKA:
                case ZoneID.PIKACHU: {
                    sb.append(matchStart.pLevel);
                    break;
                }
                
                case ZoneID.LIENG: {
                    sb.append(matchStart.value);
                    break;
                }
                
                default:
                    break;
            }
        }

        encodingObj.put("v", sb.toString());
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            StartResponse matchStart = (StartResponse) aResponseMessage;
            // put response data into json object

            getMidEncode(matchStart, encodingObj);

            return encodingObj;

        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
