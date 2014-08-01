package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.AcceptJoinRequest;
import com.tv.xeeng.base.protocol.messages.AcceptJoinResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class AcceptJoinJSON implements IMessageProtocol
{

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(AcceptJoinJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException
    {
        try
        {
            // request data
            JSONObject jsonData = (JSONObject) aEncodedObj;
            // plain obj
            AcceptJoinRequest acceptJoin = (AcceptJoinRequest) aDecodingObj;
            // decoding
            acceptJoin.uid = jsonData.getLong("uid");
            acceptJoin.isAccept = jsonData.getBoolean("isAccept");
            acceptJoin.mMatchId = jsonData.getLong("match_id");
            try {
            	acceptJoin.password = jsonData.getString("password");
            } catch (Exception e) {
				
			}

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
            // put response data into json object
            encodingObj.put("mid", aResponseMessage.getID());
            AcceptJoinResponse acceptJoin = (AcceptJoinResponse) aResponseMessage;
            encodingObj.put("code", acceptJoin.mCode);
            if (acceptJoin.mCode == ResponseCode.FAILURE)
            {
                encodingObj.put("error_msg", acceptJoin.message);
            }
            else if (acceptJoin.mCode == ResponseCode.SUCCESS)
            {
            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t)
        {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
