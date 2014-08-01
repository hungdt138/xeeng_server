package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEGetAllShopItemsResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class XEGetAllShopItemsBusiness extends AbstractBusiness {
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEGetAllShopItemsBusiness.class);
	@Override
	public int handleMessage(ISession aSession,IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[GET  ALL SHOP ITEMS]: Catch : ");

        MessageFactory msgFactory = aSession.getMessageFactory();
        XEGetAllShopItemsResponse resShopItems = (XEGetAllShopItemsResponse)msgFactory.getResponseMessage(aReqMsg.getID());
        resShopItems.session = aSession;
        if( aSession != null ) {
        	String encodedShopItems = XEDataUtils.getAndEncodeAllShopItems();
        	if (encodedShopItems == null) {
        		resShopItems.mCode = ResponseCode.FAILURE;
        		resShopItems.setErrorMsg("Error in getting shop items");
        	}
        	else {
        		resShopItems.mCode = ResponseCode.SUCCESS;
	        	resShopItems.setEncodedItems(encodedShopItems);
        	}
        	aResPkg.addMessage(resShopItems);
        }
        return 1;
	}
}
