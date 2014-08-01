/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.ChooseAvatarRequest;
import com.tv.xeeng.base.protocol.messages.ChooseAvatarResponse;
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
public class ChooseAvatarBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(ChooseAvatarBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Choose_AVATARS]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        ChooseAvatarResponse resBuyAvatar =
                (ChooseAvatarResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resBuyAvatar.session = aSession;
        try {
            ChooseAvatarRequest rq = (ChooseAvatarRequest) aReqMsg;
            long uid = aSession.getUID();
            AvatarManager.chooseAvatar(uid, rq.id);
            resBuyAvatar.setSuccess(rq.id+"");
        } catch (Throwable t) {
            //t.printStackTrace();
            resBuyAvatar.setFailure("Có lỗi xảy ra với avatar bạn muốn chọn.");
        } finally {
            if ((resBuyAvatar != null)) {
                aResPkg.addMessage(resBuyAvatar);
            }
        }
        return 1;
    }
}
