/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.EnterChatRoomRequest;
import com.tv.xeeng.base.protocol.messages.EnterChatRoomResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.chat.data.ChatRoom;
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
public class EnterChatRoomBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(EnterChatRoomBusiness.class);
    
    
    
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
//        mLog.debug("get albums");
        MessageFactory msgFactory = aSession.getMessageFactory();
        EnterChatRoomResponse resRooms = (EnterChatRoomResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        try {
            EnterChatRoomRequest request = (EnterChatRoomRequest)aReqMsg;
            resRooms.mCode = ResponseCode.SUCCESS;       
            ChatRoom chatRoom = Server.getChatRoomZone().findChatRoom(request.chatRoomId);
            chatRoom.enter(aSession);
//            aSession.setCurrentZone(0);
            aSession.setCurrPosition(null);
            
            resRooms.value = chatRoom.getChatRoomHistory();
            aSession.write(resRooms);
            
            
//            //send online play in queue
//            GetOnlineMemberResponse onlineRes = (EnterChatRoomResponse) msgFactory.getResponseMessage(MessagesID.GET_ONLINE_MEMBER);
        } 
        catch (ServerException ex) {
            mLog.error(ex.getMessage(), ex);
        }                
                   
            

            
            
//        }
//        catch(BusinessException ex)
//        {
//            mLog.warn(ex.getMessage());
//            resalbum.setFailure(ex.getMessage());
//            
//        }
             
        return 1;
    }
}
