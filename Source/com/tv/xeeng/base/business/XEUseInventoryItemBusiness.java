/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEUseInventoryItemRequest;
import com.tv.xeeng.base.protocol.messages.XEUseInventoryItemResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.EventGiftsDB;
import com.tv.xeeng.databaseDriven.EventItemsDB;
import com.tv.xeeng.databaseDriven.InventoryDB;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.EventGiftEntity;
import com.tv.xeeng.game.data.EventItemEntity;
import com.tv.xeeng.game.data.InventoryItemEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;

/**
 *
 * @author ThangTD
 */
public class XEUseInventoryItemBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEUseInventoryItemBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[USE INVENTORY ITEM]: Catch");

        MessageFactory msgFactory = aSession.getMessageFactory();
        XEUseInventoryItemResponse resUse = (XEUseInventoryItemResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resUse.session = aSession;
        XEUseInventoryItemRequest rqUse = (XEUseInventoryItemRequest) aReqMsg;
        
        UserDB userDB = new UserDB();
        UserEntity user = userDB.getUserInfoNoException(aSession.getUID());
        if (user == null || user.mUsername == null) {
            resUse.setResult(ResponseCode.FAILURE, "Người dùng không tồn tại!");
        } else {
            List<InventoryItemEntity> itemsList = InventoryDB.getUserInventory(user.mUid);

            boolean isItemFound = false;

            for (InventoryItemEntity item : itemsList) {
                if (item.getItemCode().equalsIgnoreCase(rqUse.getItemCode()) && item.getQuantity() >= 1 && item.getIsUsable()) {
                    isItemFound = true;
                    break;
                }
            }

            if (!isItemFound) {
                resUse.setResult(ResponseCode.FAILURE, "Vật phẩm không tồn tại!");
            } else {
                // Collect all the gifts correspond to the item
                List<EventGiftEntity> giftsList = new ArrayList<EventGiftEntity>();
                for (EventGiftEntity gift : EventGiftsDB.getItemsList()) {
                    if (gift.getEvitCode().equalsIgnoreCase(rqUse.getItemCode())) {
                        giftsList.add(gift);
                    }
                }

                if (!giftsList.isEmpty()) {
                    // Get random gift with fixed rate
                    Random ran = new Random();
                    float sum = 0;
                    float value = ran.nextFloat() * 100;
                    int index = -1;

                    for (int i = 0; i < giftsList.size(); i++) {
                        EventGiftEntity gift = (EventGiftEntity) giftsList.get(i);
                        if ((value >= sum) && (value < (sum + gift.getRate())) && (gift.getQuantity() != 0)) {
                            index = i;
                            break;
                        }

                        sum += gift.getRate();
                    }

                    // Gift found
                    if (index != -1) {
                        EventGiftEntity gift = (EventGiftEntity) giftsList.get(index);
                        EventItemEntity event = null;
                        
                        for (EventItemEntity entity : EventItemsDB.getJoinListItems()) {
                            if (rqUse.getItemCode().equalsIgnoreCase(entity.getCode())) {
                                event = entity;
                                break;
                            }
                        }
                        
                        if (gift != null && event != null) {
                            int result = XEDataUtils.useInventoryItem(user.mUid, rqUse.getItemCode(), gift.getEvgfCode(), event.getLimit());
                            if (result == 1) {
                                resUse.setResult(ResponseCode.SUCCESS, "Xin chúc mừng! Bạn đã nhận được " + gift.getValue() + " " + gift.getType());

                                /* update cache */
                                CacheUserInfo cacheUser = new CacheUserInfo();
                                cacheUser.deleteCacheUser(user);
                                
                                EventGiftsDB.reload();
                                
                                InventoryDB.deleteCacheUserInventory(user.mUid);
                            } else if (result == -2) {
                                resUse.setResult(ResponseCode.FAILURE, "Bạn đã sử dụng đủ " + event.getLimit() + " " + event.getName());
                            } else {
                                resUse.setResult(ResponseCode.FAILURE, "Chúc bạn may mắn lần sau!");
                            }
                        }
                    } else {
                        resUse.setResult(ResponseCode.FAILURE, "Chúc bạn may mắn lần sau!");
                    }
                }
            }
        }
        
        aResPkg.addMessage(resUse);

        return 1;
    }

}
