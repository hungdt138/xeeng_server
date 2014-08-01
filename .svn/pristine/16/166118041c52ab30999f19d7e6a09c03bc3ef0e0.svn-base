package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.data.CommonQueue;
import com.tv.xeeng.base.protocol.messages.EnterZoneRequest;
import com.tv.xeeng.base.protocol.messages.EnterZoneResponse;
import com.tv.xeeng.base.protocol.messages.ZoneCacheResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.RoomDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.QueueEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.memcached.data.CacheGameInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class EnterZoneBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ChatBusiness.class);
    private static final int CURRENT_ZONE_CACHE_VERSION = 1;
    private static final int NOT_USED_CACHE_VERSION = -1;
    
    private void sendZoneCacheInfo(ISession aSession, MessageFactory msgFactory, int zoneId, int timeout)
    {
        StringBuilder sb = new StringBuilder();
        ZoneCacheResponse cacheRes = (ZoneCacheResponse) msgFactory.getResponseMessage(MessagesID.ZONE_CACHE);
        cacheRes.mCode = ResponseCode.SUCCESS;
        cacheRes.gameInfo = RoomDB.getGameInfo(zoneId);
        sb.append(Integer.toString(CURRENT_ZONE_CACHE_VERSION)).append(AIOConstants.SEPERATOR_BYTE_3).append(cacheRes.gameInfo);
        sb.append(AIOConstants.SEPERATOR_BYTE_3).append(timeout);
        cacheRes.gameInfo = sb.toString();
        
        QueueEntity gameInfoEntity = new QueueEntity(aSession, cacheRes);
        CommonQueue queue = new CommonQueue();
        queue.insertQueue(gameInfoEntity);
        
        
    }
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        MessageFactory msgFactory = aSession.getMessageFactory();
        EnterZoneResponse resEnter = (EnterZoneResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        
        mLog.debug("[ENTER ZONE]: Catch");
        String zoneName = "#";
        try {
            EnterZoneRequest rqEnter = (EnterZoneRequest) aReqMsg;
            int zoneID = rqEnter.zoneID;
            zoneName = ZoneID.getZoneName(zoneID);
            mLog.debug("Zone name = " + zoneName);
            aSession.setCurrentZone(zoneID);
            resEnter.timeout = ZoneID.getTimeout(zoneID);
            resEnter.session = aSession;
            aSession.setCurrPosition(null);
//            aSession.getCollectInfo().append("->EnterZone: ").append(zoneName);
            if(aSession.getByteProtocol()> AIOConstants.PROTOCOL_PRIMITIVE)
            {
                aSession.setChatRoom(0);
                CacheGameInfo cache = new CacheGameInfo();
               
                if( rqEnter.cacheVersion > NOT_USED_CACHE_VERSION)
                {
                    if(rqEnter.cacheVersion != CURRENT_ZONE_CACHE_VERSION)
                    {
                        sendZoneCacheInfo(aSession, msgFactory, zoneID, resEnter.timeout);
                    }
                }
                
                if(aSession.getByteProtocol() < AIOConstants.PROTOCOL_MODIFY_MID)
                {
                    resEnter.value = cache.getPhongInfo(zoneID, aSession.isMobileDevice());
                }
                else
                {
                    resEnter.value = cache.getPhongInfo(zoneID, false);
                }
                
            }
            else
            {
                RoomDB db = new RoomDB();
                resEnter.lstRooms = db.getRooms(zoneID);
            }
            
            
            resEnter.setSuccess(ResponseCode.SUCCESS);
//            DatabaseDriver.updateUserZone(aSession.getUID(),zoneID);
            aSession.setPhongID(0);
            aResPkg.addMessage(resEnter);
            
        } catch (Throwable t) {
            resEnter.setFailure(ResponseCode.FAILURE, "Hiện tại không vào được game " + zoneName + " này!");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
            aResPkg.addMessage(resEnter);
        }
        return 1;
    }
}
