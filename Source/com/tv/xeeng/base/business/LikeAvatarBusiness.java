/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.LikeAvatarRequest;
import com.tv.xeeng.base.protocol.messages.LikeAvatarResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.shop.avatar.AvatarManager;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class LikeAvatarBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(LikeAvatarBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Like_AVATARS]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        LikeAvatarResponse resBuyAvatar =
                (LikeAvatarResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resBuyAvatar.session = aSession;
        try {
            LikeAvatarRequest rq = (LikeAvatarRequest) aReqMsg;
            long uid = aSession.getUID();
            int likeR = AvatarManager.likeAvatar(rq.id, uid);
            resBuyAvatar.setSuccess(rq.id + AIOConstants.SEPERATOR_BYTE_1+String.valueOf(likeR));
        } catch (Throwable t) {
            //t.printStackTrace();
            resBuyAvatar.setFailure(t.getMessage());
        } finally {
            if ((resBuyAvatar != null)) {
                aResPkg.addMessage(resBuyAvatar);
            }
        }
        return 1;
    }
}
