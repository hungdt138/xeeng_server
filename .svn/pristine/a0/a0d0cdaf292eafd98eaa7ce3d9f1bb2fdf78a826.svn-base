package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.TransferCashRequest;
import com.tv.xeeng.base.protocol.messages.TransferCashResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class TransferCashJSON implements IMessageProtocol
{

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(TransferCashJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException
    {
        try
        {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            TransferCashRequest transfer = (TransferCashRequest) aDecodingObj;
            if(jsonData.has("v")) {
            	String s = jsonData.getString("v");
            	String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
            	transfer.destName = arr[0];
            	transfer.money = Long.parseLong(arr[1]);
            	return true;
            }
            try {
            	transfer.money = jsonData.getLong("money");
            	transfer.source_uid = jsonData.getLong("source_uid");   
                transfer.destName = jsonData.getString("username");   
            }catch (Exception e) {
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
            
            TransferCashResponse transfer = (TransferCashResponse) aResponseMessage;
            if(transfer.session != null && transfer.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(transfer.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
//                if (transfer.mCode == ResponseCode.FAILURE) {
                	 sb.append(transfer.errMessage);
//                }
//                else {
//                	 sb.append(transfer.errMessage);

//                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", transfer.mCode);
            if (transfer.mCode == ResponseCode.FAILURE)
            {
                encodingObj.put("error_msg", transfer.errMessage);
               
            } else if (transfer.mCode == ResponseCode.SUCCESS)
            {
            	encodingObj.put("is_source", transfer.is_source);
                encodingObj.put("desc_uid", transfer.desc_uid);
            	encodingObj.put("money", transfer.money);
            	encodingObj.put("source_uid", transfer.source_uid);
            	encodingObj.put("source_name", transfer.src_name);
            }
            return encodingObj;
        } catch (Throwable t)
        {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
