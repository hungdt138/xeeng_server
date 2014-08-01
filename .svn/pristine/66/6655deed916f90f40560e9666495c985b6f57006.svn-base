/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEGetInventoryRequest;
import com.tv.xeeng.base.protocol.messages.XEGetInventoryResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.InventoryItemEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.memcached.data.XEGlobalCache;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import java.util.List;
import org.slf4j.Logger;
import com.tv.xeeng.databaseDriven.InventoryDB;

/**
 *
 * @author ThangTD
 */
public class XEGetInventoryBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEGetInventoryBusiness.class);
    
    
    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[GET INVENTORY]: Catch");

        MessageFactory msgFactory = aSession.getMessageFactory();
        XEGetInventoryResponse respone = (XEGetInventoryResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        respone.session = aSession;
        
        List<InventoryItemEntity> itemsList = InventoryDB.getUserInventory(aSession.getUID());
        if (itemsList == null) {
            respone.setFailure(ResponseCode.FAILURE, "Không lấy được hòm đồ");
        } else {
            respone.setSuccess(ResponseCode.SUCCESS, itemsList);
        }

        aResPkg.addMessage(respone);
        
        return 1;
    }

}
