package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.BocPhomRequest;
import com.tv.xeeng.base.protocol.messages.BocPhomResponse;
import com.tv.xeeng.base.protocol.messages.GetOtherPokerResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.phom.data.Poker;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;




import java.util.ArrayList;

public class BocPhomBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(BocPhomBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,  IResponsePackage aResPkg) {
        mLog.debug("[Boc Phom]: Catch");
        MessageFactory msgFactory = null;//aSession.getMessageFactory();
        BocPhomResponse resBoc = null;
//        if(msgFactory != null)
//                resBoc = (BocPhomResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            BocPhomRequest rqBoc = (BocPhomRequest) aReqMsg;

            long uid = aSession.getUID();

//            mLog.debug("uid = "+uid+" ;  rqBoc.uid : "+rqBoc.uid);
            //System.out.println("uid = "+uid+" ;  rqBoc.uid : "+rqBoc.uid);

//            if (rqBoc.uid>-1)
//                uid=rqBoc.uid;
            
            long matchID = rqBoc.matchID;
            
            MatchEntity matchEntity =  CacheMatch.getMatch(matchID);
            Room room = null;
            if(matchEntity != null)
                  room =  matchEntity.getRoom();
            if(room == null || (room.getAttactmentData().matchID != matchID))
            {
                int zoneId = rqBoc.zoneId;
                if(zoneId == 0)
                {
                    zoneId = aSession.getCurrentZone();
                }
                
                Zone zone = aSession.findZone(zoneId);
                room = zone.findRoom(matchID);
            }
            
            if(room == null)
                return 0;
            
            PhomTable table = (PhomTable) room.getAttactmentData();
           
            msgFactory = table.getNotNullSession().getMessageFactory();
            resBoc = (BocPhomResponse) msgFactory.getResponseMessage(aReqMsg.getID());
            resBoc.session = aSession;
            
            if (table.restCards.size() <= 0 || table.isPlaying == false)
            {
                //System.out.println("Error! Khong con card de boc!");
                mLog.debug("Error! Khong con card de boc!");
                return 1;
            }
            
//            long realUid = 0;
            
            //Boc
            Poker p;
            if(rqBoc.uid == -1 || rqBoc.uid == uid){
                p = table.getCard(uid);
//                realUid = uid;
            }else {
                p = table.getCard(rqBoc.uid);
//                realUid = rqBoc.uid;
            }
            
            resBoc.setSuccess(ResponseCode.SUCCESS, p);
            resBoc.isHabai = table.isHaBaiTurn();
            BocPhomResponse broadcast = (BocPhomResponse) msgFactory.getResponseMessage(aReqMsg.getID());
            broadcast.setSuccess(ResponseCode.SUCCESS);
            
            if (rqBoc.uid >- 1){// Auto BocPhom from Server
                if (!table.currentPlayer.isAutoPlay && rqBoc.uid == uid){ //Player do not quit
//                    resBoc.session = aSession;
                    aSession.write(resBoc);
                    table.broadcastMsg(broadcast, table.getNewPlayings(), table.getNewWaitings(), table.currentPlayer, false);
//                    room.broadcastMessage(broadcast, aSession, false);
                } else { // Player quitt
//                    room.broadcastMessage(broadcast, aSession, true);
                    table.broadcastMsg(broadcast, table.getNewPlayings(), table.getNewWaitings(), table.currentPlayer, false);
                }
            }
            else {// Request BocPhom from Client
//                resBoc.session = aSession;
                aSession.write(resBoc);
                table.broadcastMsg(broadcast, table.getNewPlayings(), table.getNewWaitings(), table.currentPlayer, false);
//                room.broadcastMessage(broadcast, aSession, false);
            }
            
//            if (!table.superUsers.isEmpty()) {
//                try
//                {
//                    GetOtherPokerResponse getOtherPoker = (GetOtherPokerResponse) msgFactory
//                                    .getResponseMessage(MessagesID.GET_OTHER_POKER);
//                    getOtherPoker.setSuccess(ResponseCode.SUCCESS, realUid, true);
//                    ArrayList<Poker> cards = new ArrayList<Poker>(); cards.add(p);
//                    getOtherPoker.setPhomCards(cards);
//                    for(PhomPlayer player : table.superUsers){
//                            player.write(getOtherPoker);
//                    }
//                }catch(Exception ex)
//                {
//                    mLog.error(ex.getMessage(), ex);
//                }
//            }
        } catch (Throwable t) {
//            t.printStackTrace();
            resBoc.setFailure(ResponseCode.FAILURE, "Có lỗi xảy ra." + t.getMessage());
            aResPkg.addMessage(resBoc);
        }
        return 1;
    }
}
