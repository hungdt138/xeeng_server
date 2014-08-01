package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetOnlineMemberResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.chat.data.ChatRoom;
import com.tv.xeeng.game.data.Messages;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import com.tv.xeeng.server.Server;

public class GetOnlineMemberBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetOnlineMemberBusiness.class);
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetOnlineMemberResponse resChat = (GetOnlineMemberResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        try {
//            GetOnlineMemberRequest request = (GetOnlineMemberRequest)aReqMsg;
            resChat.mCode = ResponseCode.SUCCESS;       
            
            ChatRoom chatRoom = Server.getChatRoomZone().findChatRoom(aSession.getChatRoom());
            if(chatRoom == null)
            {
                throw new BusinessException(Messages.INVALID_CHAT_ROOM);
            }
            resChat.value = chatRoom.getOnlinePlayer(aSession);
            //send to sender first
            aSession.write(resChat);
            
        } 
        catch(BusinessException ex)
        {
            mLog.warn(ex.getMessage());
            resChat.setFailure(ex.getMessage());
            
        }
        catch (ServerException ex) {
            mLog.error(ex.getMessage(), ex);
                        
        }
        
             
        return 1;
    }
}
