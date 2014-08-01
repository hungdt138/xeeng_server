package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.ChatRequest;
import com.tv.xeeng.base.protocol.messages.ChatResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class ChatBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ChatBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        int rtn = PROCESS_FAILURE;

        MessageFactory msgFactory = aSession.getMessageFactory();
        ChatResponse resChat = (ChatResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resChat.session = aSession;
        mLog.debug("[CHAT]: Catch");

        try {
            // request message and its values
            ChatRequest rqChat = (ChatRequest) aReqMsg;
            rqChat.mMessage = unicodeEscape(rqChat.mMessage);
            if (rqChat.mMessage == null || rqChat.mMessage.trim().equals("")) {
                return 1;
            }

            String message = rqChat.mMessage;

            Room currentRoom;

            Zone chatZone = aSession.findZone(aSession.getCurrentZone());
            currentRoom = chatZone.findRoom(rqChat.mRoomId);

            if (currentRoom != null) {
                SimpleTable t = (SimpleTable) currentRoom.getAttactmentData();
                ChatResponse broadcastMsg = (ChatResponse) msgFactory.getResponseMessage(aReqMsg.getID());
                broadcastMsg.setMessage(message);
                broadcastMsg.setUsername(aSession.getUserName());
                broadcastMsg.setRoomID(rqChat.mRoomId);
                broadcastMsg.setType(rqChat.type);
                broadcastMsg.setSuccess(ResponseCode.SUCCESS);
                broadcastMsg.uid = aSession.getUID();

                mLog.debug("Code:" + broadcastMsg.mCode);
                SimplePlayer player = t.findPlayer(aSession.getUID());
                if (player == null) {
                    player = t.owner;
                }

                t.broadcastMsg(broadcastMsg, t.getNewPlayings(), t.getNewWaitings(), player, false);
                return 1;
            } else {
                resChat.setFailure(ResponseCode.FAILURE, "Bàn chơi đã bị hủy, bạn không thể chat trong bàn này được.!");
                aResPkg.addMessage(resChat);
            }
            rtn = PROCESS_OK;
        } catch (Throwable t) {
            // response failure
            resChat.setFailure(ResponseCode.FAILURE, "Phần Chat đang bị lỗi!");
            //aSession.setLoggedIn(false);
            rtn = PROCESS_OK;
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
            aResPkg.addMessage(resChat);
        }

        return rtn;
    }

    public String unicodeEscape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.BASIC_LATIN
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.LATIN_1_SUPPLEMENT
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.LATIN_EXTENDED_A
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.LATIN_EXTENDED_B
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.LATIN_EXTENDED_C
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.LATIN_EXTENDED_D
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL) {
                sb.append(c);
            } else {
                sb.append(' ');
            }
        }
        return sb.toString();
    }
}
