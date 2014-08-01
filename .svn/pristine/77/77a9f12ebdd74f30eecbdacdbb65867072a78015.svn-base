package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.XEExchangeXeengToGoldRequest;
import com.tv.xeeng.base.protocol.messages.XEExchangeXeengToGoldResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.base.shop.XEShopItem;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.data.XEExchangeLogEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import java.util.List;
import org.slf4j.Logger;

public class XEExchangeXeengToGoldBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEExchangeXeengToGoldBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[EXCHANGE XEENG -> GOLD]: ");

        MessageFactory msgFactory = aSession.getMessageFactory();
        XEExchangeXeengToGoldResponse resExchange = (XEExchangeXeengToGoldResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resExchange.session = aSession;
        XEExchangeXeengToGoldRequest rqExchange = (XEExchangeXeengToGoldRequest) aReqMsg;

        int numOfGold = 0;
        int numOfXeeng = 0;
        XEExchangeLogEntity exchangeLog = new XEExchangeLogEntity();

        CacheUserInfo cacheUser = new CacheUserInfo();
        UserDB userDB = new UserDB();
        UserEntity user = userDB.getUserInfoNoException(rqExchange.getUserId());

        if (user == null || user.mUsername == null) {
            resExchange.mCode = ResponseCode.FAILURE;
            resExchange.setEncodedData("Người dùng không tồn tại, uid = " + rqExchange.getUserId());
        } else {
            /* thông tin trước khi chuyển đổi */
            exchangeLog.setUserId(user.mUid);
            exchangeLog.setShopId(rqExchange.getShopId());
            exchangeLog.setFromValueBefore(user.xeeng);
            exchangeLog.setFromType("Xeeng");
            exchangeLog.setToValueBefore(user.money);
            exchangeLog.setToType("Gold");

            /* lấy giá trị quy đổi */
            List<XEShopItem> allShopItemsFromDB = XEDataUtils.getAllShopItems();
            boolean validAmount = false;
            for (XEShopItem item : allShopItemsFromDB) {
                if (item.getId() == rqExchange.getShopId()) {
                    numOfGold = item.getCardinality() * item.getMulFactor();
                    numOfXeeng = item.getPrice();

                    validAmount = true;
                    break;
                }
            }

            if (!validAmount) {
                resExchange.mCode = ResponseCode.FAILURE;
                resExchange.setEncodedData("Số lượng Xèng không hợp lệ.");
            } else {
                exchangeLog.setFromValue(numOfXeeng);
                exchangeLog.setToValue(numOfGold);

                if (user.xeeng < numOfXeeng) {
                    /* không đủ Xèng để chuyển đổi */
                    resExchange.mCode = ResponseCode.FAILURE;
                    resExchange.setEncodedData("Bạn không đủ Xèng để chuyển đổi.");
                } else {
                    user.xeeng -= numOfXeeng;
                    user.money += numOfGold;

                    boolean success = XEDataUtils.updateUserMoney(rqExchange.getUserId(), user.xeeng, user.money
                    );
                    if (success) {
                        resExchange.mCode = ResponseCode.SUCCESS;
                        resExchange.setEncodedData("Chuyển đổi thành công.");

                        /* update cache */
                        cacheUser.deleteCacheUser(user);
                    } else {
                        resExchange.mCode = ResponseCode.FAILURE;
                        resExchange.setEncodedData("Có lỗi xảy ra, chuyển đổi không thành công.");

                        /* rollback */
                        user.xeeng += numOfXeeng;
                        user.money -= numOfGold;
                    }
                }
            }

            /* thông tin sau khi chuyển đổi */
            exchangeLog.setFromValueAfter(user.xeeng);
            exchangeLog.setToValueAfter(user.money);
        }

        /* ghi log vào db */
        exchangeLog.setMessage(resExchange.getEncodedData());
        XEDataUtils.insertExchangeLog(exchangeLog);

        aResPkg.addMessage(resExchange);

        return 1;
    }
}
