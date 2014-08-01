/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.NewRequest;
import com.tv.xeeng.base.protocol.messages.NewResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class NewJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(NewJSON.class);

    @Override
    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            NewRequest matchNew = (NewRequest) aDecodingObj;

            String v = jsonData.getString("v");
            String[] arrValues = v.split(AIOConstants.SEPERATOR_BYTE_1);

            matchNew.tableIndex = Integer.parseInt(arrValues[0]);
            matchNew.phongID = Integer.parseInt(arrValues[1]);
            matchNew.moneyBet = Long.parseLong(arrValues[2]);
            /* ADDED by TUNG */
            if (arrValues.length < 5) {
                matchNew.size = 4;
                matchNew.roomName = "Table";
            } else {
                matchNew.size = Integer.parseInt(arrValues[3]);
                matchNew.roomName = arrValues[4];
            }

            /* COMMENTED by TUNG            
             if(arrValues.length > 3) {//For new Pikachu
             matchNew.advevntureMode = Integer.parseInt(arrValues[3]) == 1;
             matchNew.matrixSize = Integer.parseInt(arrValues[4]);
             if(matchNew.advevntureMode) {
             matchNew.pikaLevel =   Integer.parseInt(arrValues[5]);
             }
             }
             */
            return true;

        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    private String getMidNew(NewResponse matchNew) {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(MessagesID.MATCH_NEW)).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(Integer.toString(matchNew.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);

        if (matchNew.mCode == ResponseCode.SUCCESS) {
            sb.append(Long.toString(matchNew.mMatchId)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(matchNew.minBet)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(matchNew.ownerCash));
        } else {
            sb.append(matchNew.mErrorMsg);
        }

        return sb.toString();

    }

    @Override
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            NewResponse matchNew = (NewResponse) aResponseMessage;

            encodingObj.put("v", getMidNew(matchNew));
            return encodingObj;

        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
