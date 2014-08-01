/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BidRequest;
import com.tv.xeeng.base.protocol.messages.BidResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class BidJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
            BidJSON.class);

    @Override
    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
            throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            BidRequest bidRequest = (BidRequest) aDecodingObj;
            String v = jsonData.getString("v");
            String[] arrValues = v
                    .split(AIOConstants.SEPERATOR_BYTE_1);
            if (arrValues.length >= 2) {
                bidRequest.bidId = Integer.parseInt(arrValues[0]);
                bidRequest.money = Long.parseLong(arrValues[1]);
                if(arrValues.length > 2) {
                    bidRequest.cellPhone = arrValues[2];
                }
            } else {
                return false;
            }
            return true;
        } catch (JSONException ex) {
            mLog.error(ex.getStackTrace().toString());
            return false;
        }
    }
 @Override
    public Object encode(IResponseMessage aResponseMessage)
            throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            BidResponse bid = (BidResponse) aResponseMessage;

            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(
                    AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(bid.mCode)).append(
                    AIOConstants.SEPERATOR_NEW_MID);
            if (bid.mCode == ResponseCode.FAILURE) {
                sb.append(bid.errMsg);
            } else {
                sb.append(bid.mess);
            }
            encodingObj.put("v", sb.toString());
            return encodingObj;

        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
