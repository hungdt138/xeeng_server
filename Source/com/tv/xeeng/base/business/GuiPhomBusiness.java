package com.tv.xeeng.base.business;

import java.util.ArrayList;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.EndMatchResponse;
import com.tv.xeeng.base.protocol.messages.GuiPhomRequest;
import com.tv.xeeng.base.protocol.messages.GuiPhomResponse;
import com.tv.xeeng.base.session.ISession;
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

public class GuiPhomBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GuiPhomBusiness.class);
            public ArrayList<Integer> stringToList(String str){
                ArrayList<Integer> res = new ArrayList<Integer>();
                String[] cards1=str.split("#");
                for (int i=0;i<cards1.length;i++)
                    res.add(Integer.parseInt(cards1[i]));
                
                return res;
            }
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Gui Phom]: Catch");
        MessageFactory msgFactory = null;// aSession.getMessageFactory();
        GuiPhomResponse resGui = null;
               // (GuiPhomResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            GuiPhomRequest rqGui = (GuiPhomRequest) aReqMsg;
            long uid = aSession.getUID();
            long matchID = rqGui.matchID;
            ArrayList<Integer> cards = stringToList(rqGui.cards);
            
            MatchEntity matchEntity =  CacheMatch.getMatch(rqGui.matchID);;
            Room room = null;
            if(matchEntity != null)
                  room =  matchEntity.getRoom();
            
            if(room == null ||(room.getAttactmentData().matchID != matchID))
            {
                Zone zone = aSession.findZone(aSession.getCurrentZone());
                room = zone.findRoom(matchID);
            }
            
//            Zone zone = aSession.findZone(aSession.getCurrentZone());
//            Room room = zone.findRoom(matchID);
            
            PhomTable table = (PhomTable) room.getAttactmentData();
            
            msgFactory = table.getNotNullSession().getMessageFactory();
            resGui = (GuiPhomResponse) msgFactory.getResponseMessage(aReqMsg.getID());

            //Gui
            PhomPlayer player = table.findPlayer(uid);
            
            if (table.gui(uid, cards, rqGui.dUID, rqGui.phomID)){ // U gui
                resGui.cards = rqGui.cards;
            	resGui.setSuccess(ResponseCode.SUCCESS, rqGui.dUID, uid, rqGui.phomID);
//            	room.broadcastMessage(resGui, aSession, true);
            	
                table.broadcastMsg(resGui, table.getNewPlayings(), table.getNewWaitings(), player, true);
                
            	EndMatchResponse endMatchRes = (EndMatchResponse) 
            				msgFactory.getResponseMessage(MessagesID.MATCH_END);
                // set the result
                endMatchRes.setZoneID(ZoneID.PHOM);
                endMatchRes.session = aSession;
                
                if (table.owner.isOut || table.owner.notEnoughMoney()) {
                    PhomPlayer p1 = table.ownerQuit();
                    if (p1 != null) {
                        room.setOwnerName(p1.username);
                        table.owner = p1;
                        endMatchRes.newOwner = p1.id;
                        
                    }
                }
                endMatchRes.phomDuty = (table.isTakeDuty?table.getWinner().id:0);
                endMatchRes.setSuccess(ResponseCode.SUCCESS,
                         table.clonePlaying(), table.getWinner());
                endMatchRes.uType=3;
//                room.broadcastMessage(endMatchRes, aSession, true);
                table.broadcastMsg(endMatchRes, table.getNewPlayings(), table.getWaitings(), player, true);
                
                room.setPlaying(false);
                //table.removeNotEnoughMoney(room);
                table.resetPlayers();
                table.gameStop();                
            }else {
                resGui.cards = rqGui.cards;
            	resGui.setSuccess(ResponseCode.SUCCESS, rqGui.dUID, uid, rqGui.phomID);
            	//room.broadcastMessage(resGui, aSession, true);
                table.broadcastMsg(resGui, table.getNewPlayings(), table.getNewWaitings(), player, true);
            }

//            Thread.sleep(500);
            
        } catch (Throwable t) {
            t.printStackTrace();
            resGui.setFailure(ResponseCode.FAILURE, "Có lỗi xảy ra.");
        }
        return 1;
    }
}
