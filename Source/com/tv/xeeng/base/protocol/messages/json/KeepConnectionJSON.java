package com.tv.xeeng.base.protocol.messages.json;



//import allinone.protocol.messages.KeepConnectionResponse;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.KeepConnectionResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class KeepConnectionJSON implements IMessageProtocol
{
    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(StartJSON.class);
    
    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException
    {
            return true;
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException
    {
         try {
            
            JSONObject encodingObj = new JSONObject();
            KeepConnectionResponse res = (KeepConnectionResponse)aResponseMessage;
//            if(res.session != null && res.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
//            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(MessagesID.KEEP_CONNECTION)).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append("1").append(AIOConstants.SEPERATOR_NEW_MID);
                encodingObj.put("v", sb.toString());
                return encodingObj;
//            }
//             
//             encodingObj.put("mid", aResponseMessage.getID());
//            encodingObj.put("code", 1);
//            return encodingObj;
            
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }	
}
