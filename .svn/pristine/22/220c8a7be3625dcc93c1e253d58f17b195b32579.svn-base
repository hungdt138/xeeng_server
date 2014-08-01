/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEJoinEventItemsResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.EventItemsDB;
import com.tv.xeeng.databaseDriven.InventoryDB;
import com.tv.xeeng.databaseDriven.UserDB;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;

/**
 *
 * @author ThangTD
 */
public class XEJoinEventItemsBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEJoinEventItemsBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[JOIN EVENT ITEMS]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        XEJoinEventItemsResponse resJoin = (XEJoinEventItemsResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resJoin.session = aSession;

        UserDB userDB = new UserDB();
        UserEntity user = userDB.getUserInfoNoException(aSession.getUID());
        if (user == null || user.mUsername == null) {
            resJoin.setResult(ResponseCode.FAILURE, "Người dùng không tồn tại!");
        } else {
            List<InventoryItemEntity> inventory = InventoryDB.getUserInventory(user.mUid);
            HashMap<String, InventoryItemEntity> maps = new HashMap<String, InventoryItemEntity>();
            for (InventoryItemEntity item : inventory) {
                maps.put(item.getItemCode(), item);
            }

            List<EventItemEntity> joinLstItems = EventItemsDB.getJoinListItems();
            if (!joinLstItems.isEmpty()) {
                EventItemEntity item = (EventItemEntity) joinLstItems.get(0);
                if (item != null) {
                    if (user.money < item.getFee()) {
                        resJoin.setResult(ResponseCode.FAILURE, "Bạn cần " + item.getFee() + " Gold để đổi quà!");
                    } else {
                        boolean isSuccess = true;

                        String[] components = item.getComponents().split(";");
                        for (String component : components) {
                            String[] params = component.split("-");
                            if (params.length > 1) {
                                InventoryItemEntity entity = maps.get(params[0]);
                                if (entity == null) {
                                    isSuccess = false;
                                    break;
                                }

                                if (entity.getQuantity() < Integer.parseInt(params[1])) {
                                    isSuccess = false;
                                    break;
                                }
                            }
                        }

                        if (isSuccess) {
                            Random ran = new Random();
                            int value = ran.nextInt(101);
                            if (value >= 0 && value <= item.getRate()) {
                                if (XEDataUtils.joinEventItems(user.mUid, item.getCode(), item.getName(), item.getDescription(), item.getFee())) {
                                    resJoin.setResult(ResponseCode.SUCCESS, "Xin chúc mừng. Bạn đã nhận được 1 hộp quà!");

                                    /* update cache */
                                    CacheUserInfo cacheUser = new CacheUserInfo();
                                    cacheUser.deleteCacheUser(user);

                                    InventoryDB.deleteCacheUserInventory(user.mUid);
                                } else {
                                    resJoin.setResult(ResponseCode.FAILURE, "Đổi hộp quà không thành công!");
                                }
                            } else {
                                resJoin.setResult(ResponseCode.FAILURE, "Đổi hộp quà không thành công!");
                            }
                        } else {
                            resJoin.setResult(ResponseCode.FAILURE, "Đổi hộp quà không thành công!");
                        }
                    }
                }
            }
        }

        aResPkg.addMessage(resJoin);

        return 1;
    }
}
