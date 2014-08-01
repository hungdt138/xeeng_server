/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;


import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.ReplyRequest;
import com.tv.xeeng.base.protocol.messages.ReplyResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class ReplyJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ReplyJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            ReplyRequest matchReply = (ReplyRequest) aDecodingObj;
            if(jsonData.has("v"))
            {
                String[] arr = jsonData.getString("v").split(AIOConstants.SEPERATOR_BYTE_1);
                matchReply.mMatchId = Long.parseLong(arr[0]);
                matchReply.mIsAccept = arr[1].equals("1");
                matchReply.buddy_uid = Long.parseLong(arr[2]);
                return true;
            }
            
            matchReply.mMatchId = jsonData.getLong("match_id");
            matchReply.mIsAccept = jsonData.getBoolean("is_accept");
            matchReply.buddy_uid = jsonData.getLong("buddy_uid");
            matchReply.uid = jsonData.getLong("uid");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            ReplyResponse matchReply = (ReplyResponse) aResponseMessage;
            if(matchReply.session != null && matchReply.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(matchReply.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (matchReply.mCode == ResponseCode.FAILURE) {
                	 sb.append(matchReply.mErrorMsg);
                }
                
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            
            
            encodingObj.put("mid", aResponseMessage.getID());
            
            encodingObj.put("code", matchReply.mCode);
            if (matchReply.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", matchReply.mErrorMsg);
            } else if (matchReply.mCode == ResponseCode.SUCCESS) {
                encodingObj.put("is_accept", matchReply.mIsAccept);
                if (matchReply.mIsAccept) {
                    encodingObj.put("source_uid", matchReply.source_uid);
                    encodingObj.put("name", matchReply.username);
                }
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
