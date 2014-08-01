package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEGetTablesByLevelRequest;
import com.tv.xeeng.base.protocol.messages.XEGetTablesByLevelResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class XEGetTablesByLevelBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEGetTablesByLevelBusiness.class);

    @Override
    public int handleMessage(ISession paramISession,
            IRequestMessage paramIRequestMessage,
            IResponsePackage paramIResponsePackage) throws ServerException {
        XEGetTablesByLevelRequest req = (XEGetTablesByLevelRequest) paramIRequestMessage;

        mLog.debug("[GET TABLES by LEVEL " + req.getLevelID() + "]: Catch : ");

        MessageFactory msgFactory = paramISession.getMessageFactory();
        XEGetTablesByLevelResponse tablesResp = (XEGetTablesByLevelResponse) msgFactory.getResponseMessage(paramIRequestMessage.getID());
        tablesResp.session = paramISession;
        if (paramISession != null) {
            Zone zone = paramISession.findZone(paramISession.getCurrentZone());
            String encodedTableList = null;
            if (zone != null) {
                encodedTableList = XEDataUtils.getAndEncodeTablesInZone(zone, req.getLevelID());
            } else {
                encodedTableList = XEDataUtils.getAndEncodeTables(req.getLevelID());
            }
            if (encodedTableList == null) {
                tablesResp.mCode = ResponseCode.FAILURE;
                tablesResp.setErrorMsg("Error in getting tables list by level");
            } else {
                tablesResp.mCode = ResponseCode.SUCCESS;
                tablesResp.setEncodedItems(encodedTableList);
            }
            paramIResponsePackage.addMessage(tablesResp);
        }
        return 1;
    }
}
