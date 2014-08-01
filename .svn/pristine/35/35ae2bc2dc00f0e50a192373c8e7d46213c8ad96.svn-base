package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEGetTablesByLevelRequest;
import com.tv.xeeng.protocol.IRequestMessage;

public class XEGetTablesByLevelJSON extends XEMessageJSON {
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEGetTablesByLevelJSON.class);
	@Override
	public boolean decode(Object paramObject,
			IRequestMessage paramIRequestMessage) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) paramObject;
            XEGetTablesByLevelRequest req = (XEGetTablesByLevelRequest) paramIRequestMessage;
            req.setLevelID(Integer.parseInt(jsonData.getString("v")));
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + paramIRequestMessage.getID(), t);
            return false;
        }
	}
}
