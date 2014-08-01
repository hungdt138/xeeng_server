/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.PlayTopicNewPikachuRequest;
import com.tv.xeeng.base.protocol.messages.PlayTopicNewPikachuResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.newpika.data.TopicManager;
import com.tv.xeeng.game.shop.giftgame.GiftGameManager;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class PlayTopicNewPikachuBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(PlayTopicNewPikachuBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[PlayTopicNewPikachuBusiness]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        PlayTopicNewPikachuResponse resBuyAvatar =
                (PlayTopicNewPikachuResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resBuyAvatar.session = aSession;
        try {
            PlayTopicNewPikachuRequest rqBuyAvatar = (PlayTopicNewPikachuRequest) aReqMsg;
            long uid = aSession.getUID();
            int type = rqBuyAvatar.topicId;
            String res = GiftGameManager.getGiftGameByType(type, aSession.isMobileDevice());
            CacheUserInfo cacheUser = new CacheUserInfo();
            UserEntity user = cacheUser.getUserInfo(uid);
            resBuyAvatar.setSuccess(TopicManager.getDetailTopic(type, user.money));
        } catch (Throwable t) {
            t.printStackTrace();
            resBuyAvatar.setFailure(t.getMessage());
        } finally {
            if ((resBuyAvatar != null)) {
                aResPkg.addMessage(resBuyAvatar);
            }
        }
        return 1;
    }
}
