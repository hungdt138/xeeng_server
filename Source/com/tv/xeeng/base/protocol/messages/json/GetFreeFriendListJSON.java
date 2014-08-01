package com.tv.xeeng.base.protocol.messages.json;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetFreeFriendListResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class GetFreeFriendListJSON implements IMessageProtocol {

    private final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetFreeFriendListJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
            return true;
    }
    
    private String data(Vector<UserEntity> us){
    	StringBuilder sb = new StringBuilder();
    	for (UserEntity u : us) {
    		sb.append(u.mUid).append(AIOConstants.SEPERATOR_BYTE_1);
    		sb.append(u.mUsername).append(AIOConstants.SEPERATOR_BYTE_1);
    		sb.append(u.money).append(AIOConstants.SEPERATOR_BYTE_2);
    	}
    	if(sb.length()>0) sb.deleteCharAt(sb.length()-1);
    	return sb.toString();
    }
    
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            
            GetFreeFriendListResponse getFrientList = (GetFreeFriendListResponse) aResponseMessage;
            if(getFrientList.session != null && getFrientList.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(getFrientList.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (getFrientList.mCode == ResponseCode.FAILURE) {
                	 sb.append(getFrientList.mErrorMsg);
                }else {
                	sb.append(data(getFrientList.mFrientList));
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", getFrientList.mCode);
            if (getFrientList.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", getFrientList.mErrorMsg);
            } else if (getFrientList.mCode == ResponseCode.SUCCESS) {
            	
                JSONArray arrRooms = new JSONArray();
                //if (getFrientList.mFrientList != null) {
                    for (UserEntity userEntity : getFrientList.mFrientList) {
                        JSONObject jRoom = new JSONObject();
                        jRoom.put("username", userEntity.mUsername);
                        jRoom.put("uid", userEntity.mUid);
                        jRoom.put("avatar", userEntity.avatarID);
                        jRoom.put("level", userEntity.level);
                        jRoom.put("money", userEntity.money);
                        jRoom.put("PlaysNumber", userEntity.playsNumber);
                        arrRooms.put(jRoom);
                    }
                //}
                encodingObj.put("frient_list", arrRooms);
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
