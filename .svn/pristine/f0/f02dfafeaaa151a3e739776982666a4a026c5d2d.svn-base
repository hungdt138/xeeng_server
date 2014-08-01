package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEGetAllLevelItemsRequest;
import com.tv.xeeng.base.protocol.messages.XEGetAllLevelItemsResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import org.slf4j.Logger;

public class XEGetAllLevelItemsBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEGetAllLevelItemsBusiness.class);

    @Override
    public int handleMessage(ISession paramISession, IRequestMessage paramIRequestMessage, IResponsePackage paramIResponsePackage) throws ServerException {
        mLog.debug("[GET ALL LEVEL ITEMS]: Catch : ");

        MessageFactory msgFactory = paramISession.getMessageFactory();
        XEGetAllLevelItemsRequest request = (XEGetAllLevelItemsRequest) paramIRequestMessage;
        XEGetAllLevelItemsResponse resLevelItems = (XEGetAllLevelItemsResponse) msgFactory.getResponseMessage(paramIRequestMessage.getID());
        resLevelItems.session = paramISession;
        if (paramISession != null) {
            String encodedLevelItems = XEDataUtils.getAndEncodeAllLevelItems(request.getZoneId());
            if (encodedLevelItems == null) {
                resLevelItems.mCode = ResponseCode.FAILURE;
                resLevelItems.setErrorMsg("Không lấy được danh sách phòng.");
            } else {
                resLevelItems.mCode = ResponseCode.SUCCESS;
                resLevelItems.setEncodedItems(encodedLevelItems);
            }
            paramIResponsePackage.addMessage(resLevelItems);
        }
        return 1;
    }

}
