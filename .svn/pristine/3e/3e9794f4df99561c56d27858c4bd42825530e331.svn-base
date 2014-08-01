/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import java.util.List;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetItemRequest;
import com.tv.xeeng.base.protocol.messages.GetItemResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.ItemDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ItemEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

/**
 *
 * @author tuanda
 */
public class GetItemBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetItemBusiness.class);
    
    private static final int CACHE_ITEM_VERSION = 1;
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("get albums");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetItemResponse resItem = (GetItemResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        try {
            
            GetItemRequest rqAlb = (GetItemRequest) aReqMsg;
            resItem.mCode = ResponseCode.SUCCESS;        
            StringBuilder sb = new StringBuilder();
            if(rqAlb.cacheVersion < CACHE_ITEM_VERSION)
            {
                List<ItemEntity> items = ItemDB.getItems();
                int itemSize = items.size();
                for(int i = 0; i< itemSize; i++)
                {
                    ItemEntity entity = items.get(i);
                    sb.append(Integer.toString(entity.getItemId())).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(Integer.toString(entity.getType())).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(Integer.toString(entity.getCategoryId())).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(Integer.toString(entity.getPrice())).append(AIOConstants.SEPERATOR_BYTE_2);
                }
                
                if (itemSize>0)
                {
                    sb.deleteCharAt(sb.length() -1);
                }
            }
            
            resItem.value = sb.toString();
            
        } 
//        catch(BusinessException ex)
//        {
//            mLog.warn(ex.getMessage());
//            resalbum.setFailure(ex.getMessage());
//            
//        }
        finally
        {
            if (resItem!= null)
            {
                try {
                    aSession.write(resItem);
                } catch (ServerException ex) {
                    mLog.error(ex.getMessage(), ex);
                }
                        
            }
        }        
        return 1;
    }
}
