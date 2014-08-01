package com.tv.xeeng.base.business;

import java.util.logging.Level;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.AnPhomRequest;
import com.tv.xeeng.base.protocol.messages.AnPhomResponse;
import com.tv.xeeng.base.protocol.messages.EndMatchResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleException;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.phom.data.PhomException;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class AnPhomBusiness extends AbstractBusiness {
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BocPhomBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[An Phom]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        AnPhomResponse resAn = null;
                //(AnPhomResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        PhomTable table = null;
        long uid = aSession.getUID();
        Room room = null;
        try {
            AnPhomRequest rqAn = (AnPhomRequest) aReqMsg;
            
            long matchID = rqAn.matchID;
//            Zone zone = aSession.findZone(aSession.getCurrentZone());
//            Room room = zone.findRoom(matchID);
            MatchEntity matchEntity =  CacheMatch.getMatch(rqAn.matchID);;
            
            if(matchEntity != null)
                  room =  matchEntity.getRoom();
            
            if(room == null ||(room.getAttactmentData().matchID != matchID))
            {
                Zone zone = aSession.findZone(aSession.getCurrentZone());
                room = zone.findRoom(matchID);
            }
                        
            table = (PhomTable) room.getAttactmentData();
            
            msgFactory = table.getNotNullSession().getMessageFactory();
            
            resAn = (AnPhomResponse) msgFactory.getResponseMessage(aReqMsg.getID());
            //An
            resAn.session = aSession;
            long money = table.eat(uid);
            
            resAn.setSuccess(ResponseCode.SUCCESS, money, uid, table.getPrePlayerID(uid));
            resAn.chot = table.getChot();
            resAn.isHaBai = table.isHaBaiTurn();
            resAn.session = aSession;
            
            if (resAn!=null)
            {
                PhomPlayer player = table.findPlayer(uid);
                table.broadcastMsg(resAn, table.getNewPlayings(), table.getNewWaitings(), player, true);
//                room.broadcastMessage(resAn, aSession, true);
            }
        }
        catch (BusinessException ex) {
            if (table != null) {
                table.processU(1);
                EndMatchResponse endMatchRes = (EndMatchResponse) msgFactory.getResponseMessage(MessagesID.MATCH_END);
                // set the result
                endMatchRes.setZoneID(ZoneID.PHOM);
                endMatchRes.phomDuty = (table.isTakeDuty ? table.getWinner().id : 0);
                endMatchRes.uType = 1;
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

//                room.broadcastMessage(endMatchRes, aSession,
//                        true);
                try {
                table.broadcastMsg(endMatchRes, table.getPlayings(), table.getWaitings(), table.findPlayer(uid), true);
                } catch (Throwable e11){
                    
                }
                //table.supRemOldVer(endMatchRes.newOwner, AIOConstants.PROTOCOL_MODIFY_MID);

                room.setPlaying(false);
                //table.removeNotEnoughMoney(room);
                table.resetPlayers();
                table.gameStop();
            }  
        }
        catch (PhomException ex) {
            resAn.setFailure(ResponseCode.FAILURE, ex.getMessage());
            aResPkg.addMessage(resAn);
        }catch (SimpleException ex) {
            resAn.setFailure(ResponseCode.FAILURE, ex.getMessage());
            aResPkg.addMessage(resAn);
        } 
        finally {
            
        }
        return 1;
    }
}
