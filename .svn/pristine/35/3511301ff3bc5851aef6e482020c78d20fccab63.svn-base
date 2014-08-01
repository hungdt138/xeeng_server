package com.tv.xeeng.base.business;

import java.util.Vector;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetPlayingListRequest;
import com.tv.xeeng.base.protocol.messages.GetPlayingListResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.room.RoomEntity;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class GetPlayingListBusiness extends AbstractBusiness
{

    private static final Logger mLog = 
    	LoggerContext.getLoggerFactory().getLogger(GetPlayingListBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg)
    {
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetPlayingListResponse resGetPlayingList = (GetPlayingListResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        mLog.debug("[GET PLAYING ROOM LIST]: Catch");
        try
        {
            GetPlayingListRequest rqGetWaitingList = (GetPlayingListRequest) aReqMsg;
            Zone zone = aSession.findZone(aSession.getCurrentZone());
            int numPlayingRoom = zone.getNumPlaylingRoom();
            Vector<RoomEntity> playingRooms = zone.dumpPlayingRooms(rqGetWaitingList.mOffset, rqGetWaitingList.mLength, aSession.getCurrentZone());
            resGetPlayingList.setSuccess(ResponseCode.SUCCESS, numPlayingRoom, playingRooms);
        } catch (Throwable t) {
            resGetPlayingList.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu ");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resGetPlayingList != null)) {
                aResPkg.addMessage(resGetPlayingList);
            }
        }

        return 1;
    }

}
