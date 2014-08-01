package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.BuyItemRequest;
import com.tv.xeeng.base.protocol.messages.BuyItemResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.ItemDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class BuyItemBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(BuyItemBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[BUY_ITEM]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        BuyItemResponse resBuyAvatar =
                (BuyItemResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        
        try {
            BuyItemRequest rqBuyAvatar = (BuyItemRequest) aReqMsg;
            ItemDB db = new ItemDB();
            int ret = db.buyItem(aSession.getUID(), rqBuyAvatar.itemId);
            
            if (ret == -1) {
                resBuyAvatar.setFailure("Không tồn tại người chơi");
            } else if(ret == -2){
                resBuyAvatar.setFailure("Bạn không có đủ tiền mua đồ");
            }
            else
            {
                resBuyAvatar.mCode = ResponseCode.SUCCESS;
                CacheUserInfo cacheUserInfo = new CacheUserInfo();
                cacheUserInfo.deleteFullCacheUser(aSession.getUserEntity());
            }
                        
                    
        } catch (Throwable t) {
            mLog.error(t.getMessage(), t);
            resBuyAvatar.setFailure("Có lỗi xảy ra");
        } finally {
            if ((resBuyAvatar != null)) {
                aResPkg.addMessage(resBuyAvatar);
            }
        }
        return 1;
    }
}
