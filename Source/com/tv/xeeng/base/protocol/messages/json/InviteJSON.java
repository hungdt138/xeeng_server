package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.InviteRequest;
import com.tv.xeeng.base.protocol.messages.InviteResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class InviteJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(InviteJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            InviteRequest invite = (InviteRequest) aDecodingObj;
            if(jsonData.has("v")){
            	String s = jsonData.getString("v");
            	String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
            	invite.roomID = Long.parseLong(arr[0]);
            	invite.destUid = Long.parseLong(arr[1]);
            	return true;
            }
            invite.roomID = jsonData.getLong("room_id");
            invite.destUid = jsonData.getLong("dest_uid");
            invite.sourceUid = jsonData.getInt("source_uid");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
           
            InviteResponse invite = (InviteResponse) aResponseMessage;
            if(invite.session != null && invite.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(invite.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (invite.mCode == ResponseCode.FAILURE) {
                	 sb.append(invite.mErrorMsg);
                }else {
                	sb.append(invite.sourceID).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(invite.roomID).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(invite.roomName).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(invite.sourceUserName).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(invite.minBet).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(invite.level).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(invite.currentZone).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(invite.timeout).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(invite.phongId);
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", invite.mCode);
            if (invite.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", invite.mErrorMsg);
            } else if (invite.mCode == ResponseCode.SUCCESS) {
            	encodingObj.put("source_uid", invite.sourceID);
            	encodingObj.put("room_id", invite.roomID);
            	encodingObj.put("room_name", invite.roomName);
            	encodingObj.put("source_username", invite.sourceUserName);
            	encodingObj.put("minBet", invite.minBet);
                encodingObj.put("level", invite.level);
                encodingObj.put("zone", invite.currentZone);
                
                encodingObj.put("timeout", invite.timeout);
                encodingObj.put("phongId", invite.phongId);
                                
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
