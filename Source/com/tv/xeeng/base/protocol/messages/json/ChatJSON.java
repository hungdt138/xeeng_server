package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.ChatRequest;
import com.tv.xeeng.base.protocol.messages.ChatResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class ChatJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ChatJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            ChatRequest chat = (ChatRequest) aDecodingObj;
            
            if (jsonData.has("v")) {
				try {
					String v = jsonData.getString("v");
					String[] arrValues = v
							.split(AIOConstants.SEPERATOR_BYTE_1);
					
					chat.mRoomId = Long.parseLong(arrValues[0]);
					chat.mMessage = arrValues[1];
					chat.type = Integer.parseInt(arrValues[2]);
					
					try {
						chat.phongID = Integer.parseInt(arrValues[3]);
					} catch (Exception ee) {
						chat.phongID = 0;
					}
					
					return true;
				} catch (Exception ex) {
					mLog.error(ex.getMessage(), ex);
				}
			}
            
            chat.mMessage = jsonData.getString("message");
            chat.mRoomId = jsonData.getLong("room_id");
            chat.type = jsonData.getInt("type");
            try{
            	chat.phongID = jsonData.getInt("phong");
            }catch (Exception e) {
            	chat.phongID = 0;
			}
            
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            
            ChatResponse chat = (ChatResponse) aResponseMessage;
            if(chat.session != null && chat.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(chat.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (chat.mCode == ResponseCode.FAILURE) {
                	 sb.append(chat.mErrorMsg);
                }else {
                	sb.append(finalProtocol(chat));
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", chat.mCode);
            if (chat.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", chat.mErrorMsg);
            } else if (chat.mCode == ResponseCode.SUCCESS) {
//            	if (chat.session != null
//						&& chat.session.getByteProtocol() > AIOConstants.PROTOCOL_PRIMITIVE) {
//            		
//            		encodingObj.put("v", finalProtocol(chat));
//            		return encodingObj;
//            	}
                encodingObj.put("message", chat.mMessage);
                encodingObj.put("username", chat.mUsername);
                encodingObj.put("room_id", chat.roomid);
                encodingObj.put("type", chat.type);
                if(chat.phong != 0){
                	encodingObj.put("phong", chat.phong);
                }
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
    private String finalProtocol(ChatResponse chat){
    	StringBuilder sb = new StringBuilder();
		sb.append(chat.mMessage).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(chat.mUsername).append(AIOConstants.SEPERATOR_BYTE_1);
		sb.append(chat.uid);//.append(AIOConstants.SEPERATOR_BYTE_1);
//		sb.append(chat.roomid).append(AIOConstants.SEPERATOR_BYTE_1);
//		sb.append(chat.type);//.append(AIOConstants.SEPERATOR_BYTE_1);
//		if(chat.phong != 0){
//			sb.append(AIOConstants.SEPERATOR_BYTE_1).append(chat.phong);//.append(AIOConstants.SEPERATOR_BYTE_1);
//        }
		return sb.toString();
    }
}
