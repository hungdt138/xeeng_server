/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.ChatResponse;
import com.tv.xeeng.base.protocol.messages.MakeGiftGameRequest;
import com.tv.xeeng.base.protocol.messages.MakeGiftGameResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.base.session.SessionManager;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.Couple;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.shop.giftgame.GiftGameManager;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class MakeGiftGameBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(MakeGiftGameBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[MAKE_GIFT]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        MakeGiftGameResponse resBuyAvatar =
                (MakeGiftGameResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resBuyAvatar.session = aSession;
        try {
            MakeGiftGameRequest rqBuyAvatar = (MakeGiftGameRequest) aReqMsg;
            long uid = aSession.getUID();
            int gift = rqBuyAvatar.giftID;
            long matchID = rqBuyAvatar.matchID;
            String uids = rqBuyAvatar.uids;
            String[] uIDs = uids.split(",");
            int number = uIDs.length;
            Room currentRoom;
            // broadcast
            Zone chatZone = aSession.findZone(aSession.getCurrentZone());
            currentRoom = chatZone.findRoom(matchID);
            SimpleTable t = (SimpleTable) currentRoom.getAttactmentData();

            Couple<String, String> res = GiftGameManager.gift(gift, matchID, uid, uids, number);
            resBuyAvatar.setSuccess(res.e1 + AIOConstants.SEPERATOR_BYTE_1 + res.e2);
            ChatResponse activeChat = (ChatResponse) msgFactory.getResponseMessage(MessagesID.CHAT);
            ChatResponse passiveChat = (ChatResponse) msgFactory.getResponseMessage(MessagesID.CHAT);
            activeChat.setMessage(res.e1);
            activeChat.setUsername(aSession.getUserName());
            activeChat.setSuccess(ResponseCode.SUCCESS);
            activeChat.uid = aSession.getUID();

            passiveChat.setMessage(res.e1);

            passiveChat.setSuccess(ResponseCode.SUCCESS);

            t.broadcastMsg(activeChat, t.getNewPlayings(), t.getNewWaitings(), t.findPlayer(uid), false);
            SessionManager manager = aSession.getManager();
            for (String s : uIDs) {
                try {
                    long pUid = Long.parseLong(s);
                    ISession pSession = manager.findSession(pUid);
                    passiveChat.setUsername(pSession.getUserName());
                    passiveChat.uid = pSession.getUID();
                    t.broadcastMsg(passiveChat, t.getNewPlayings(), t.getNewWaitings(), t.findPlayer(pUid), false);
                } catch (Throwable e) {
                    continue;
                }
            }
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
