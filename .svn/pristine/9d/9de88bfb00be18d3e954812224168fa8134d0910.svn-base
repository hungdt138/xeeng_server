package com.tv.xeeng.base.business;

import java.util.ArrayList;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.EndMatchResponse;
import com.tv.xeeng.base.protocol.messages.HaPhomRequest;
import com.tv.xeeng.base.protocol.messages.HaPhomResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class HaPhomBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(HaPhomBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Ha Phom]: Catch");
        MessageFactory msgFactory = null;//aSession.getMessageFactory();
        HaPhomResponse resHa = null;
                //(HaPhomResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            HaPhomRequest rqHa = (HaPhomRequest) aReqMsg;
            long uid = aSession.getUID();

            if (rqHa.uid > 0)
                uid = rqHa.uid;

            long matchID = rqHa.matchID;
            ArrayList<ArrayList<Integer>> cards = rqHa.cards;
            
            MatchEntity matchEntity = CacheMatch.getMatch(matchID);
            Room room = null;
            if(matchEntity != null)
                room =  matchEntity.getRoom();
            
            if (room == null ||(room.getAttactmentData().matchID != matchID))
            {
                int zoneId = rqHa.zoneId;
                if (zoneId == 0)
                {
                    zoneId = aSession.getCurrentZone();
                }
                Zone zone = aSession.findZone(zoneId);
                room = zone.findRoom(matchID);
            }
            
            if (room == null)
                return 0;
            
            PhomTable table = (PhomTable) room.getAttactmentData();
            
            msgFactory = table.getNotNullSession().getMessageFactory();
            resHa =(HaPhomResponse) msgFactory.getResponseMessage(aReqMsg.getID());
            resHa.session = aSession;
            resHa.cards = rqHa.cards1;
            resHa.uid = uid;
            
            if (!table.isPlaying) {
                mLog.error("Error Haphom. Khi da ket thuc van! " + table.turnInfo());
                return 1;
            }
            
            //Ha
            table.haPhom(uid, cards, rqHa.u, rqHa.card);
            
            resHa.setSuccess(ResponseCode.SUCCESS, rqHa.u, rqHa.card);
            
            PhomPlayer player = table.findPlayer(uid);
            table.broadcastMsg(resHa, table.getNewPlayings(), table.getNewWaitings(), player, true);
//            room.broadcastMessage(resHa, aSession, true);
            
            if(rqHa.u == 1 || table.currentPlayer.uType > 0 ) { // U
//                Thread.sleep(500);
            	EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory.getResponseMessage(MessagesID.MATCH_END);
                // set the result
                endMatchRes.setZoneID(ZoneID.PHOM);
                endMatchRes.phomDuty = (table.isTakeDuty ? table.getWinner().id : 0);
                endMatchRes.uType = rqHa.u;
                endMatchRes.session = aSession;
                if (table.owner.isOut || table.owner.notEnoughMoney()) {
                    PhomPlayer p1 = table.ownerQuit();
                    if (p1 != null) {
                        room.setOwnerName(p1.username);
                        table.owner = p1;
                        
                        endMatchRes.newOwner = p1.id;
                    }
                }
                //TODO fix dead session 
//                table.resetPlayers();
                endMatchRes.setSuccess(ResponseCode.SUCCESS, table.clonePlaying(), table.getWinner());
//                room.broadcastMessage(endMatchRes, aSession, true);
                table.broadcastMsg(endMatchRes, table.getPlayings(), table.getWaitings(), player, true);
                table.supRemOldVer(endMatchRes.newOwner, AIOConstants.PROTOCOL_MODIFY_MID);
                
                room.setPlaying(false);
                //table.removeNotEnoughMoney(room);
                table.resetPlayers();
                table.gameStop();
            }
        } catch (Throwable t) {
            /*HaPhomRequest rqHa = (HaPhomRequest) aReqMsg;
            resHa.setSuccess(ResponseCode.SUCCESS, rqHa.u, rqHa.card);
            Zone zone = aSession.findZone(aSession.getCurrentZone());
            long matchID = rqHa.matchID;
            Room room = zone.findRoom(matchID);
            room.broadcastMessage(resHa, aSession, true);
            
//            t.printStackTrace()*/
            resHa.setFailure(ResponseCode.FAILURE, "Có lỗi xảy ra.");
            aResPkg.addMessage(resHa);
        }
        return 1;
    }
}
