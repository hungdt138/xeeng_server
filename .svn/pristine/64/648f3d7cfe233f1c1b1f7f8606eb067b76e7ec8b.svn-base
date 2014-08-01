package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XESearchTableByMatchIDRequest;
import com.tv.xeeng.protocol.IRequestMessage;

public class XESearchTableByMatchIDJSON extends XEMessageJSON {
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XESearchTableByMatchIDJSON.class);
	@Override
	public boolean decode(Object paramObject,
			IRequestMessage paramIRequestMessage) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) paramObject;
            XESearchTableByMatchIDRequest req = (XESearchTableByMatchIDRequest) paramIRequestMessage;
            req.setMatchID(Integer.parseInt(jsonData.getString("v")));
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + paramIRequestMessage.getID(), t);
            return false;
        }
	}
}
