package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.BuyAvatarRequest;
import com.tv.xeeng.base.protocol.messages.BuyAvatarResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.shop.avatar.AvatarManager;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


public class BuyAvatarBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(BuyAvatarBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[BUY_AVATAR]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        BuyAvatarResponse resBuyAvatar =
                (BuyAvatarResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resBuyAvatar.session = aSession;
        try {
            BuyAvatarRequest rqBuyAvatar = (BuyAvatarRequest) aReqMsg;
            long uid = aSession.getUID();
            int avatarID = rqBuyAvatar.avatarID;
            int b00 = AvatarManager.buyAvatar(avatarID, uid);
            if (b00 == 1) {
                resBuyAvatar.setSuccess("OK");
            } else if (b00 == -1) {
                resBuyAvatar.setFailure("Avatar không tồn tại");
            } else {
                resBuyAvatar.setFailure("Bạn không thể mua avatar này được");
            }

        } catch (Throwable t) {
            t.printStackTrace();
            resBuyAvatar.setFailure("Có lỗi xảy ra với avatar bạn muốn mua.");
        } finally {
            if ((resBuyAvatar != null)) {
                aResPkg.addMessage(resBuyAvatar);
            }
        }
        return 1;
    }
}
