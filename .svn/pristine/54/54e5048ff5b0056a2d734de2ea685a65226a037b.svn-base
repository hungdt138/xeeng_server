package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetTopPlayerTourRequest;
import com.tv.xeeng.base.protocol.messages.GetTopPlayerTourResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class GetTopPlayerTourJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BocPhomJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            GetTopPlayerTourRequest boc = (GetTopPlayerTourRequest) aDecodingObj;
            boc.tourID = jsonData.getInt("v");            
            return true;
        } catch (Exception e) {
        	mLog.debug(e.getMessage());
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
           
            GetTopPlayerTourResponse boc = (GetTopPlayerTourResponse) aResponseMessage;
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(boc.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
            if (boc.mCode == ResponseCode.FAILURE) {
            	 sb.append(boc.eRRMess);
            }else {
            	for(UserEntity u : boc.top10){
            		sb.append(u.mUsername).append(AIOConstants.SEPERATOR_BYTE_1);
            		sb.append(u.money).append(AIOConstants.SEPERATOR_BYTE_2);
        		}
            	if(boc.top10.size() > 0) sb.deleteCharAt(sb.length()-1);
            }
            encodingObj.put("v", sb.toString());
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
