package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BuyLevelRequest;
import com.tv.xeeng.base.protocol.messages.BuyLevelResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class BuyLevelJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BuyLevelJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            BuyLevelRequest buy = (BuyLevelRequest) aDecodingObj;
            try {
                buy.level = jsonData.getInt("level");
                buy.uid = jsonData.getLong("uid");

            } catch (Exception e) {
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
            encodingObj.put("mid", aResponseMessage.getID());
            BuyLevelResponse buy = (BuyLevelResponse) aResponseMessage;
            encodingObj.put("code", buy.mCode);
            if (buy.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", buy.errMessage);

            } else if (buy.mCode == ResponseCode.SUCCESS) {
                encodingObj.put("cash", buy.new_cash);
                encodingObj.put("level", buy.new_level);
                encodingObj.put("moneyUpdateLevel", buy.new_moneyForLevel);
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
