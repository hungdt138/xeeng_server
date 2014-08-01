/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetAvatarDetailRequest;
import com.tv.xeeng.base.protocol.messages.GetAvatarDetailResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.shop.avatar.AvatarManager;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class GetAvatarDetailBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetAvatarDetailBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[GET_AVATARS_DETAIL]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetAvatarDetailResponse resBuyAvatar =
                (GetAvatarDetailResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resBuyAvatar.session = aSession;
        try {
            GetAvatarDetailRequest rqBuyAvatar = (GetAvatarDetailRequest) aReqMsg;
            long uid = aSession.getUID();
            int idAvatar = rqBuyAvatar.id;
            String res = AvatarManager.avatarDetail(idAvatar, aSession.isMobileDevice());
            resBuyAvatar.setSuccess(res);
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
