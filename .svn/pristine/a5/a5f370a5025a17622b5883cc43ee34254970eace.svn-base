package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.SendAdvResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class SendAdvJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(JoinedJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            SendAdvResponse adv = (SendAdvResponse) aResponseMessage;
            if(adv.session != null && adv.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(adv.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (adv.mCode == ResponseCode.FAILURE) {
                     sb.append(adv.message);
                }else {
                    sb.append(adv.adv);
                }
                
                encodingObj.put("v", sb.toString());
                return encodingObj;
            
            }
            
            
            encodingObj.put("mid", aResponseMessage.getID());
            // cast response obj
            
            encodingObj.put("code", adv.mCode);
            if (adv.mCode == ResponseCode.FAILURE) {
            } else if (adv.mCode == ResponseCode.SUCCESS) {
                encodingObj.put("v", adv.adv);
                
            }
            // response encoded obj
            return encodingObj;
            
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
