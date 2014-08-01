package com.tv.xeeng.base.protocol.messages.json;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XERemoveSocialFriendRequest;
import com.tv.xeeng.base.protocol.messages.XERemoveSocialFriendResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import org.json.JSONObject;
import org.slf4j.Logger;

public class XERemoveSocialFriendJSON implements IMessageProtocol {

    private final Logger mLog
            = LoggerContext.getLoggerFactory().getLogger(XERemoveSocialFriendJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            XERemoveSocialFriendRequest rq = (XERemoveSocialFriendRequest) aDecodingObj;

            String[] arr = jsonData.getString("v").split(AIOConstants.SEPERATOR_BYTE_1);
            rq.friendID = Long.valueOf(arr[0]);

            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();

            XERemoveSocialFriendResponse res = (XERemoveSocialFriendResponse) aResponseMessage;
//            if (removeFriend.session != null && removeFriend.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(res.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
            sb.append(res.getMessage());

            encodingObj.put("v", sb.toString());
            return encodingObj;
//            }
//            encodingObj.put("mid", aResponseMessage.getID());
//            encodingObj.put("code", removeFriend.mCode);
//            if (removeFriend.mCode == ResponseCode.FAILURE) {
//                encodingObj.put("error_msg", removeFriend.mErrorMsg);
//            } else if (removeFriend.mCode == ResponseCode.SUCCESS) {
//            }
//            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
