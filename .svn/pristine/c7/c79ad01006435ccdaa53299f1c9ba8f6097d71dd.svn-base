/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.JoinedResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class JoinedJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(JoinedJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void newProtocol(JoinedResponse matchJoin, JSONObject encodingObj) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(MessagesID.MATCH_JOINED)).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(Integer.toString(matchJoin.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
        if (matchJoin.mCode == ResponseCode.SUCCESS) {
            sb.append(matchJoin.mUid).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.username).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.avatar).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(matchJoin.cash).append(AIOConstants.SEPERATOR_BYTE_1);
        }

        encodingObj.put("v", sb.toString());
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            JoinedResponse matchJoined = (JoinedResponse) aResponseMessage;
            if (matchJoined.session != null && matchJoined.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                newProtocol(matchJoined, encodingObj);
                return encodingObj;
            }
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", matchJoined.mCode);
            if (matchJoined.mCode == ResponseCode.FAILURE) {
            } else if (matchJoined.mCode == ResponseCode.SUCCESS) {
                switch (matchJoined.zoneID) {
                    case ZoneID.PHOM:
                    case ZoneID.BACAY:
                    case ZoneID.TIENLEN:
                    case ZoneID.NEW_BA_CAY:
                    case ZoneID.BAU_CUA_TOM_CA:
                    case ZoneID.PIKACHU:
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
