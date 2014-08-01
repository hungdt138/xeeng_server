/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetChatRoomRequest;
import com.tv.xeeng.base.protocol.messages.GetChatRoomResponse;
import com.tv.xeeng.base.session.ISession;
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
public class GetChatRoomBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetChatRoomBusiness.class);
    
    private static final int CACHE_VERSION = 3;
    
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
//        mLog.debug("get albums");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetChatRoomResponse resRooms = (GetChatRoomResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        try {
             GetChatRoomRequest rqChatRoom = (GetChatRoomRequest) aReqMsg;
             
             resRooms.mCode = ResponseCode.SUCCESS;
             
             if(rqChatRoom.cacheVersion != CACHE_VERSION)
             {     
                resRooms.value = Server.getChatRoomZone().getAllRooms(CACHE_VERSION);
             }
             else
             {
                 resRooms.value = Server.getChatRoomZone().getOnlyPlaying();
                 
             }
             
             
                
             aSession.write(resRooms);
            
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
