package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEGetTablesByLevelResponse;
import com.tv.xeeng.base.protocol.messages.XESearchTableByMatchIDRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class XESearchTableByMatchIDBusiness extends AbstractBusiness {
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XESearchTableByMatchIDBusiness.class);
	@Override
	public int handleMessage(ISession paramISession,
			IRequestMessage paramIRequestMessage,
			IResponsePackage paramIResponsePackage) throws ServerException {
		XESearchTableByMatchIDRequest req = (XESearchTableByMatchIDRequest) paramIRequestMessage;
       	
        mLog.debug("[SEARCH TABLE by MATCH_ID " + req.getMatchID() + "]: Catch : ");

        MessageFactory msgFactory = paramISession.getMessageFactory();
        XEGetTablesByLevelResponse tablesResp = (XEGetTablesByLevelResponse)msgFactory.getResponseMessage(paramIRequestMessage.getID());
        tablesResp.session = paramISession;
        if( paramISession != null ) {
	        String encodedTable = null;
	        encodedTable = XEDataUtils.getAndEncodeTable(req.getMatchID());
        	if (encodedTable == null) {
        		tablesResp.mCode = ResponseCode.FAILURE;
        		tablesResp.setErrorMsg("No table with " + req.getMatchID() + " found");
        	}
        	else {
        		tablesResp.mCode = ResponseCode.SUCCESS;
	        	tablesResp.setEncodedItems(encodedTable);
        	}
        	paramIResponsePackage.addMessage(tablesResp);
        }
        return 1;
	}
}
