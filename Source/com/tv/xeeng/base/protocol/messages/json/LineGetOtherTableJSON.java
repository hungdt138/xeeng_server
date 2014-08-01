/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.LineGetOtherTableRequest;
import com.tv.xeeng.base.protocol.messages.LineGetOtherTableResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class LineGetOtherTableJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
            LineEatJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
            throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            LineGetOtherTableRequest matchTurn = (LineGetOtherTableRequest) aDecodingObj;
            if (jsonData.has("v")) {
				String s = jsonData.getString("v");
				String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
				matchTurn.mMatchId = Long.parseLong(arr[0]);
				matchTurn.uid = Long.parseLong(arr[1]);
				return true;
			}
            matchTurn.mMatchId = jsonData.getLong("match_id");
            matchTurn.uid = jsonData.getLong("uid");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage)
            throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
           
            LineGetOtherTableResponse matchTurn = (LineGetOtherTableResponse) aResponseMessage;
            if(matchTurn.session != null && matchTurn.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(matchTurn.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (matchTurn.mCode == ResponseCode.FAILURE) {
                	 sb.append(matchTurn.mErrorMsg);
                }else {
                	sb.append(matchTurn.uid).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(matchTurn.number);
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", matchTurn.mCode);
            if (matchTurn.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", matchTurn.mErrorMsg);
            } else if (matchTurn.mCode == ResponseCode.SUCCESS) {
                encodingObj.put("matrix", matchTurn.number);
                 encodingObj.put("uid", matchTurn.uid);
            }    
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
