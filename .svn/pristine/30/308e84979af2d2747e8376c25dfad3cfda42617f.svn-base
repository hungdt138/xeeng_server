package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetBestPlayerResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class GetBestPlayerJSON implements IMessageProtocol {

    private final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetBestPlayerJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }
    
    private String finalProtocol(GetBestPlayerResponse getFrientList) {
            StringBuilder sb = new StringBuilder();
            for (UserEntity userEntity : getFrientList.mBestPlayerList) {
                    sb.append(userEntity.mUid).append(
                                    AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(userEntity.mUsername).append(
                                    AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(userEntity.avatarID).append(
                                    AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(userEntity.level).append(
                                    AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(userEntity.experience).append(
                                    AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(userEntity.playsNumber).append(
                                    AIOConstants.SEPERATOR_BYTE_2);
            }
            if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
    }


    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            // put response data into json object
            GetBestPlayerResponse getFrientList = (GetBestPlayerResponse) aResponseMessage;
            
            if(getFrientList.session != null && getFrientList.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(getFrientList.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (getFrientList.mCode == ResponseCode.FAILURE) {
                	 sb.append(getFrientList.mErrorMsg);
                }else {
                	sb.append(finalProtocol(getFrientList));
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            
            encodingObj.put("mid", aResponseMessage.getID());
            // cast response obj
            
            encodingObj.put("code", getFrientList.mCode);
            if (getFrientList.mCode == ResponseCode.FAILURE) {
            } else if (getFrientList.mCode == ResponseCode.SUCCESS) {
                JSONArray arrRooms = new JSONArray();
                if (getFrientList.mBestPlayerList != null) {
                    for (UserEntity userEntity : getFrientList.mBestPlayerList) {
                        // with each playing room
                        JSONObject jRoom = new JSONObject();
                        // attached object
                        jRoom.put("username", userEntity.mUsername);
                        jRoom.put("uid", userEntity.mUid);
                        jRoom.put("avatar", userEntity.avatarID);
                        jRoom.put("level", userEntity.level);
                        jRoom.put("money", userEntity.money);
                        jRoom.put("PlaysNumber", userEntity.playsNumber);
                        arrRooms.put(jRoom);
                    }
                }
                encodingObj.put("frient_list", arrRooms);
            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
