package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.LatBaiRequest;
import com.tv.xeeng.base.protocol.messages.LatBaiResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.lieng.data.LiengTable;
import com.tv.xeeng.game.newbacay.data.NewBaCayPlayer;
import com.tv.xeeng.game.newbacay.data.NewBaCayTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class LatBaiBusiness extends AbstractBusiness {
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(LatBaiBusiness.class);
    private static final String NONE_PLAYING_TABLE = "Bàn không chơi nữa rồi";
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        int rtn = PROCESS_FAILURE;
        
        MessageFactory msgFactory = aSession.getMessageFactory();
        LatBaiResponse resLatbai = (LatBaiResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resLatbai.session = aSession;
        
        mLog.debug("[LAT BAI]: Catch");
        try {
            // request message and its values
            LatBaiRequest rqLatbai = (LatBaiRequest) aReqMsg;
            
            int zoneId = aSession.getCurrentZone();
            // broadcast
           Room room = aSession.getRoom();
            if(room == null || room.getAttactmentData().matchID != rqLatbai.matchID)
            {
                Zone zone = aSession.findZone(aSession.getCurrentZone());
                room = zone.findRoom(rqLatbai.matchID);
            }
            
            long uid = aSession.getUID();
            if(room == null)
            {
                throw new BusinessException("Bàn chơi đã bị hủy !");
            }
            
            //modify this business for xoc dia game
            switch(zoneId)
            {
                case ZoneID.NEW_BA_CAY:
                {
                    NewBaCayTable table = (NewBaCayTable)room.getAttactmentData();
                    if(!table.isPlaying)
                    {
                        throw new BusinessException(NONE_PLAYING_TABLE);
                    }
                    
                    NewBaCayPlayer player = table.latbai(uid);
                    
                    if(table.isAllLatBai())
                    {
                        table.endMatch();
                    }
                    else
                    {
                        resLatbai.bcPlayer = player;
                        resLatbai.setSuccess(ResponseCode.SUCCESS);
                        resLatbai.uid = uid;
                        resLatbai.zoneId = ZoneID.NEW_BA_CAY;
                        //room.broadcastMessage(resLatbai, aSession, true);
                        table.broadcastMsg(resLatbai, table.getNewPlayings(), table.getNewWaitings(), player, false);
                    }
                    
                    return PROCESS_OK;
                }
                    
                case ZoneID.LIENG:
                {
                    LiengTable table = (LiengTable)room.getAttactmentData();
                    table.latbai(uid, rqLatbai.card);
                    return PROCESS_OK;
                }    
            }
            
            rtn = PROCESS_OK;
        }
        catch(BusinessException ex)
        {
            resLatbai.setFailure(ResponseCode.FAILURE, ex.getMessage());
            mLog.error("lat bai business " + ex.getMessage());
            aResPkg.addMessage(resLatbai);
            
            return PROCESS_OK;
        }
        
        catch (Throwable t) {
            // response failure
            resLatbai.setFailure(ResponseCode.FAILURE, "đang bị lỗi!");
            //aSession.setLoggedIn(false);
            rtn = PROCESS_OK;
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
            aResPkg.addMessage(resLatbai);
        }

        return rtn;
    }
}
