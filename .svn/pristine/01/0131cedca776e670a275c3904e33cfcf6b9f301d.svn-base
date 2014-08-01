/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetGiftGameByTypeRequest;
import com.tv.xeeng.base.protocol.messages.GetGiftGameByTypeResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.shop.giftgame.GiftGameManager;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class GetGiftGameByTypeBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetGiftGameByTypeBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[GET_GiftGameByType]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetGiftGameByTypeResponse resBuyAvatar =
                (GetGiftGameByTypeResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resBuyAvatar.session = aSession;
        try {
            GetGiftGameByTypeRequest rqBuyAvatar = (GetGiftGameByTypeRequest) aReqMsg;
            long uid = aSession.getUID();
            int type = rqBuyAvatar.idType;
            String res = GiftGameManager.getGiftGameByType(type, aSession.isMobileDevice());
            resBuyAvatar.setSuccess(res);
        } catch (Throwable t) {
            t.printStackTrace();
            resBuyAvatar.setFailure("Có lỗi xảy ra ");
        } finally {
            if ((resBuyAvatar != null)) {
                aResPkg.addMessage(resBuyAvatar);
            }
        }
        return 1;
    }
}
