package com.tv.xeeng.base.protocol.messages.json;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.AddSocialFriendRequest;
import com.tv.xeeng.base.protocol.messages.AddSocialFriendResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import org.json.JSONObject;
import org.slf4j.Logger;

public class AddSocialFriendJSON implements IMessageProtocol {

    private final Logger mLog
            = LoggerContext.getLoggerFactory().getLogger(AddSocialFriendJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            AddSocialFriendRequest addFriend = (AddSocialFriendRequest) aDecodingObj;
            String v = jsonData.getString("v");
            String[] arr = v.split(AIOConstants.SEPERATOR_BYTE_1);

            addFriend.friendID = Integer.parseInt(arr[0]);
//            if (arr.length > 1) {
//                addFriend.isConfirmed = arr[1].equals("1");
//            } else {
//                addFriend.isConfirmed = true;
//            }

            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            AddSocialFriendResponse addFriend = (AddSocialFriendResponse) aResponseMessage;
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(addFriend.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);

//            if (addFriend.mCode == ResponseCode.FAILURE) {
            sb.append(addFriend.mMsg);
//            }
//            else
//            {
//                sb.append(addFriend.value);
//            }            

            encodingObj.put("v", sb.toString());

            // response encoded obj
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
