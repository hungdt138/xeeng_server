package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.AddFriendByNameRequest;
import com.tv.xeeng.base.protocol.messages.AddFriendByNameResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class AddFriendByNameJSON implements IMessageProtocol
{

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(AddFriendByNameJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException
    {
        try
        {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            AddFriendByNameRequest addF = (AddFriendByNameRequest) aDecodingObj;
            
            if(jsonData.has("v"))
                addF.friendName = jsonData.getString("v");
            else
                addF.friendName = jsonData.getString("friend_name");
            
            return true;
        } catch (Throwable t)
        {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException
    {
        try
        {
            JSONObject encodingObj = new JSONObject();
            
            AddFriendByNameResponse addF = (AddFriendByNameResponse) aResponseMessage;
            if(addF.session != null && addF.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(addF.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (addF.mCode == ResponseCode.FAILURE) {
                	 sb.append(addF.mErrorMsg);
                }else {
                	UserEntity user = addF.user;
                	sb.append(user.mUid).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(user.money).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(user.avatarID).append(AIOConstants.SEPERATOR_BYTE_1);
                	sb.append(user.level).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(user.playsNumber);
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", addF.mCode);
            if (addF.mCode == ResponseCode.FAILURE)
            {
                encodingObj.put("error_msg", addF.mErrorMsg);
            }
            else if (addF.mCode == ResponseCode.SUCCESS)
            {
            	UserEntity user = addF.user;
                encodingObj.put("uid", user.mUid);
                encodingObj.put("money", user.money);
                encodingObj.put("avatar", user.avatarID);
                encodingObj.put("level", user.level);
                encodingObj.put("last_login", user.lastLogin);
                encodingObj.put("playsNumber", user.playsNumber);
                
            }
            return encodingObj;
        } catch (Throwable t)
        {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
