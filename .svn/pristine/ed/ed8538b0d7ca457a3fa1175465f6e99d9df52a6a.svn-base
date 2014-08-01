package com.tv.xeeng.base.protocol.messages.json;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEGetAllLevelItemsRequest;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.protocol.IRequestMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

public class XEGetAllLevelItemsJSON extends XEMessageJSON {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEGetAllLevelItemsJSON.class);

    @Override
    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            XEGetAllLevelItemsRequest request = (XEGetAllLevelItemsRequest) aDecodingObj;

            String[] arr = jsonData.getString("v").split(AIOConstants.SEPERATOR_BYTE_1);
            request.setZoneId(Integer.valueOf(arr[0]));

            return true;

        } catch (NumberFormatException | JSONException t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }
}
