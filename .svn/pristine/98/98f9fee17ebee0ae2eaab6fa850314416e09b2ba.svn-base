package com.tv.xeeng.base.protocol.messages.json;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.AddFriendRequest;
import com.tv.xeeng.base.protocol.messages.AddFriendResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import org.json.JSONObject;
import org.slf4j.Logger;

public class AddFriendJSON implements IMessageProtocol {

    private final Logger mLog
            = LoggerContext.getLoggerFactory().getLogger(AddFriendJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            AddFriendRequest addFriend = (AddFriendRequest) aDecodingObj;
            addFriend.friendID = jsonData.getLong("v");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();

            AddFriendResponse addFriend = (AddFriendResponse) aResponseMessage;
            if (addFriend.session != null && addFriend.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(addFriend.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                sb.append(addFriend.getMessage());
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }

            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", addFriend.mCode);

            encodingObj.put("error_msg", addFriend.getMessage());
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
