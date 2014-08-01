package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BookTourRequest;
import com.tv.xeeng.base.protocol.messages.BookTourResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.Couple;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class BookTourJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BookTourJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            BookTourRequest boc = (BookTourRequest) aDecodingObj;
            boc.tourID = Integer.parseInt(jsonData.getString("v"));
            return true;
        } catch (Exception e) {
        	mLog.debug(e.getMessage());
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            
            BookTourResponse boiC = (BookTourResponse) aResponseMessage;
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(boiC.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
            if (boiC.mCode == ResponseCode.FAILURE) {
            	 sb.append(boiC.eRRMess);
            }else {
            	 sb.append(boiC.tour);
            }
            encodingObj.put("v", sb.toString());
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
