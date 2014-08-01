/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.data.ImageQueue;
import com.tv.xeeng.base.protocol.messages.OutChatRoomRequest;
import com.tv.xeeng.base.protocol.messages.OutChatRoomResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.chat.data.ChatRoom;
import com.tv.xeeng.game.data.AIOConstants;
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
public class OutChatRoomBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(OutChatRoomBusiness.class);
    
    
    
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
//        mLog.debug("get albums");
        MessageFactory msgFactory = aSession.getMessageFactory();
        OutChatRoomResponse resRooms = (OutChatRoomResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        try {
            OutChatRoomRequest request = (OutChatRoomRequest)aReqMsg;
            resRooms.mCode = ResponseCode.SUCCESS;       
            ChatRoom chatRoom = Server.getChatRoomZone().findChatRoom(request.chatRoomId);
            if(chatRoom != null)
            {
                chatRoom.out(aSession);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(Long.toString(aSession.getUID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(request.chatRoomId));
            
            resRooms.value = sb.toString();
//            aSession.write(resRooms);
            
            
//            //send online play in queue
//            GetOnlineMemberResponse onlineRes = (EnterChatRoomResponse) msgFactory.getResponseMessage(MessagesID.GET_ONLINE_MEMBER);
        } 
        catch (Exception ex) {
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
