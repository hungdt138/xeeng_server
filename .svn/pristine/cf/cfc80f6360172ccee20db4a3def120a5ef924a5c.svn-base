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
import com.tv.xeeng.base.protocol.messages.GiftForUserRequest;
import com.tv.xeeng.base.protocol.messages.GiftForUserResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;


/**
 *
 * @author tuanda
 */
public class GiftForUserJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GiftForUserJSON.class);
@Override
    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            GiftForUserRequest newsRequest = (GiftForUserRequest) aDecodingObj;

            String v = jsonData.getString("v");
            String[] args = v.split(AIOConstants.SEPERATOR_BYTE_1);
            newsRequest.objectID = args[0];
            newsRequest.type = Integer.parseInt(args[1]);
            return true;
        } catch (JSONException ex) {
            mLog.error(ex.getMessage(), ex);
        }
        return true;
    }
@Override
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();


            GiftForUserResponse res = (GiftForUserResponse) aResponseMessage;

            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(res.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);

            if (res.mCode == ResponseCode.FAILURE) {
                sb.append(res.errMsg);
            } else {
                if (res.value != null && !res.value.equals("")) {
                    sb.append(res.value);
                }
            }

            encodingObj.put("v", sb.toString());



            return encodingObj;
        } catch (JSONException ex) {
            mLog.error(ex.getMessage(), ex);
        }

        return null;
    }
}
