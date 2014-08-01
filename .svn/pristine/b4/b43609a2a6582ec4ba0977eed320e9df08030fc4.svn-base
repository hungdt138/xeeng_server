/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.base.business;

import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetAllRoomRequest;
import com.tv.xeeng.base.protocol.messages.GetAllRoomResponse;
import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.RoomDB;
import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.memcached.data.CacheGameInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author Administrator
 */
public class GetAllRoomBusiness extends AbstractBusiness {
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GetWaitingListBusiness.class);
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[GET  ALL ROOM LIST]: Catch : "+aSession.getCurrentZone());

        MessageFactory msgFactory = aSession.getMessageFactory();
        GetAllRoomResponse resGetAllRoom =
        	(GetAllRoomResponse)msgFactory.getResponseMessage(aReqMsg.getID());
        resGetAllRoom.session = aSession;
        try{
//            GetAllRoomRequest req = (GetAllRoomRequest) aReqMsg;
            if(aSession.getByteProtocol()> AIOConstants.PROTOCOL_PRIMITIVE)
            {
                CacheGameInfo cache = new CacheGameInfo();
                
                if(aSession.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
                {
                    resGetAllRoom.value = cache.getPhongInfo(aSession.getCurrentZone(), false);
                }
                else
                {
                    resGetAllRoom.value = cache.getPhongInfo(aSession.getCurrentZone(), aSession.isMobileDevice());
                }
                resGetAllRoom.mCode = ResponseCode.SUCCESS;
            }
            else
            {
                RoomDB db = new RoomDB();

                List<NRoomEntity> rooms = db.getRooms(aSession.getCurrentZone());
    //            aSession.setRoomLevel(req.level);

                resGetAllRoom.setSuccess(ResponseCode.SUCCESS, rooms);
            }
        } catch (Exception e) {
        	mLog.debug("Get all rooms list error:"+e.getCause());
			resGetAllRoom.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu");
        }
        finally{
            if ((resGetAllRoom != null)){
                aResPkg.addMessage(resGetAllRoom);
            }
        }

        return 1;
    }

}
