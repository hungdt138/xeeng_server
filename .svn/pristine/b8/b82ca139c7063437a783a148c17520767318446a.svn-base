package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.UploadAvatarRequest;
import com.tv.xeeng.base.protocol.messages.UploadAvatarResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class UploadAvatarJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(UploadAvatarJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            UploadAvatarRequest rq = (UploadAvatarRequest)aDecodingObj;
            
//            String[] arrValues = jsonData.getString("v").split(AIOConstants.SEPERATOR_BYTE_1);
//            rq.albumId = Long.parseLong(arrValues[0]);
//            rq.isCancel = arrValues[1].equals("1");
            String[] arrValues = jsonData.getString("v").split(AIOConstants.SEPERATOR_BYTE_1);
            if(arrValues.length>2)
            {
                rq.albumId = Long.parseLong(arrValues[0]);
                rq.imageId = Integer.parseInt(arrValues[1]);
                rq.maxParts = Integer.parseInt(arrValues[2]);
            }
            else
            {
                rq.sequence = Integer.parseInt(arrValues[0]);
                rq.detail = arrValues[1];
                
            }
            
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
//            encodingObj.put("mid", aResponseMessage.getID());
            UploadAvatarResponse mua = (UploadAvatarResponse) aResponseMessage;
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(mua.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
            
            if (mua.mCode == ResponseCode.FAILURE) {
                     sb.append(mua.mErrorMsg);
            }
            else
            {
                sb.append(mua.value);
            }
            encodingObj.put("v", sb.toString());
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
