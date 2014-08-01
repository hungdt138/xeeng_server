/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEGetEventGiftHistoryRequest;
import com.tv.xeeng.base.protocol.messages.XEGetEventGiftHistoryResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.XELogEventGiftEntity;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.memcached.data.XEGlobalCache;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import java.util.List;
import org.slf4j.Logger;

/**
 *
 * @author Windows7
 */
public class XEGetEventGiftHistoryBusiness extends AbstractBusiness {
    
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEGetEventGiftHistoryBusiness.class);
    private static final String MEMCACHED_NAME = "EventGiftHistory";
    
    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[GET EVENT GIFT HISTORY]: Catch");
        
        MessageFactory msgFactory = aSession.getMessageFactory();
        XEGetEventGiftHistoryRequest request = (XEGetEventGiftHistoryRequest) aReqMsg;
        XEGetEventGiftHistoryResponse respone = (XEGetEventGiftHistoryResponse) msgFactory.getResponseMessage(request.getID());
        respone.session = aSession;
        
        String key = MEMCACHED_NAME + "-" + aSession.getUID();
        Object raw = XEGlobalCache.getCache(key);
        List<XELogEventGiftEntity> itemsList = null;
        
        if (raw != null) {
            itemsList = (List<XELogEventGiftEntity>) raw;
        }
        
        if (itemsList == null) {
            itemsList = XEDataUtils.getEventGiftLogOfUser(aSession.getUID());
            
            XEGlobalCache.setCache(key, itemsList, XEGlobalCache.TIMEOUT_2_MIN);
        }
        
        if (itemsList == null) {
            respone.setFailure(ResponseCode.FAILURE, "Không lấy được lịch sử sự kiện");
        } else {
            respone.setSuccess(ResponseCode.SUCCESS, itemsList);
        }

        aResPkg.addMessage(respone);
        
        return 1;
    }
    
}
