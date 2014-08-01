/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;


import java.util.Date;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.SendChatRoomRequest;
import com.tv.xeeng.base.protocol.messages.SendChatRoomResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.chat.data.ChatEntity;
import com.tv.xeeng.game.chat.data.ChatRoom;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.Messages;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import com.tv.xeeng.server.Server;


/**
 *
 * @author tuanda
 */
public class SendChatRoomBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(SendChatRoomBusiness.class);
    
    
    
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
//        mLog.debug("get albums");
        MessageFactory msgFactory = aSession.getMessageFactory();
        SendChatRoomResponse resChat = (SendChatRoomResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        try {
            SendChatRoomRequest request = (SendChatRoomRequest)aReqMsg;
            resChat.mCode = ResponseCode.SUCCESS;       
            
            ChatRoom chatRoom = Server.getChatRoomZone().findChatRoom(aSession.getChatRoom());
            if(chatRoom == null)
            {
                throw new BusinessException(Messages.INVALID_CHAT_ROOM);
            }
            //send to sender first
//            aSession.write(resChat);
            ChatEntity entity = new ChatEntity(request.content, aSession, new Date());
            chatRoom.addMessageHistory(entity);
            
            //broadcast message to another joiner
            SendChatRoomResponse broadcastMsg = (SendChatRoomResponse)resChat.clone(aSession);
            StringBuilder sb = new StringBuilder();
            sb.append(aSession.getUID()).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(aSession.getUserName()).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(request.content);
            broadcastMsg.value =  sb.toString();
            broadcastMsg.mCode = ResponseCode.SUCCESS;
            chatRoom.broadcastWithoutSender(broadcastMsg, aSession);
            
        } 
        catch(BusinessException ex)
        {
            mLog.warn(ex.getMessage());
            resChat.setFailure(ex.getMessage());
            
        }
//        catch (ServerException ex) {
//            mLog.error(ex.getMessage(), ex);
//                        
//        }
        
             
        return 1;
    }
}
