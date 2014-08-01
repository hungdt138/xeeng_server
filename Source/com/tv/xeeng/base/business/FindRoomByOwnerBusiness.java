package com.tv.xeeng.base.business;


import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.FindRoomByOwnerRequest;
import com.tv.xeeng.base.protocol.messages.FindRoomByOwnerResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class FindRoomByOwnerBusiness extends AbstractBusiness
{

    private static final Logger mLog = 
    	LoggerContext.getLoggerFactory().getLogger(FindRoomByOwnerBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg)
    {
        mLog.debug("[FIND ROOM]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        FindRoomByOwnerResponse resFindRoom = (FindRoomByOwnerResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try
        {
            FindRoomByOwnerRequest rqFind = (FindRoomByOwnerRequest) aReqMsg;
            String username = rqFind.roomOwner;
            Zone zone = aSession.findZone(aSession.getCurrentZone());
            Room room = zone.findRoomByOwner(username);
            if(room != null){
            	resFindRoom.setSuccess(ResponseCode.SUCCESS, room.dumpRoom());
            } else {
            	resFindRoom.setFailure(ResponseCode.FAILURE, "Không tìm được bàn");
            }
        } catch (Throwable t){
            resFindRoom.setFailure(ResponseCode.FAILURE, "Không tìm được bàn");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally{
            if ((resFindRoom != null) ){
                aResPkg.addMessage(resFindRoom);
            }
        }

        return 1;
    }
}
