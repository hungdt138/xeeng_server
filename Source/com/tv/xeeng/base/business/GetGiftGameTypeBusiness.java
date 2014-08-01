/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetGiftGameTypeResponse;
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
public class GetGiftGameTypeBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetGiftGameTypeBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[GET_AVATARS]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetGiftGameTypeResponse resBuyAvatar =
                (GetGiftGameTypeResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resBuyAvatar.session = aSession;
        try {
            String res = GiftGameManager.getGiftGameType();
            resBuyAvatar.setSuccess(res);
        } catch (Throwable t) {
            t.printStackTrace();
            resBuyAvatar.setFailure("Có lỗi xảy ra .");
        } finally {
            if ((resBuyAvatar != null)) {
                aResPkg.addMessage(resBuyAvatar);
            }
        }
        return 1;
    }
}
