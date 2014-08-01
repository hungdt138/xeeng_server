/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEExchangeXeengToGoldRequest;
import com.tv.xeeng.base.protocol.messages.XEExchangeXeengToGoldResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

/**
 *
 * @author thanhnvt
 */
public class XEExchangeXeengToGoldJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEExchangeXeengToGoldJSON.class);

    @Override
    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            XEExchangeXeengToGoldRequest rqExchange = (XEExchangeXeengToGoldRequest) aDecodingObj;

            String[] arr = jsonData.getString("v").split(AIOConstants.SEPERATOR_BYTE_1);
            rqExchange.setUserId(Long.valueOf(arr[0]));
            rqExchange.setShopId(Integer.valueOf(arr[1]));

            return true;

        } catch (NumberFormatException | JSONException t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    @Override
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            XEExchangeXeengToGoldResponse resExchange = (XEExchangeXeengToGoldResponse) aResponseMessage;

            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(resExchange.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
            sb.append(resExchange.getEncodedData());

            encodingObj.put("v", sb.toString());
            return encodingObj;

        } catch (JSONException t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
