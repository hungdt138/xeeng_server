package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.TrieuPhuAnswerRequest;
import com.tv.xeeng.base.protocol.messages.TrieuPhuAnswerResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuTable;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


public class TrieuPhuAnswerBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(TrieuPhuAnswerBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        // process's status
        mLog.debug("[Trieu Phu answer] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory = aSession.getMessageFactory();
        TrieuPhuAnswerResponse resMatchTurn = (TrieuPhuAnswerResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        try {
            TrieuPhuAnswerRequest rq = (TrieuPhuAnswerRequest) aReqMsg;
            MatchEntity matchEntity = CacheMatch.getMatch(rq.mMatchId);
            Room room = null;
            int zoneID = aSession.getCurrentZone();
            if (matchEntity != null) {
                room = matchEntity.getRoom();
                zoneID = matchEntity.getZoneId();
            }

            if (room == null || (room.getAttactmentData().matchID != rq.mMatchId)) {
                Zone zone = aSession.findZone(aSession.getCurrentZone());
                room = zone.findRoom(rq.mMatchId);
            }

            if (room != null) {
                switch (zoneID) {
                    case ZoneID.AILATRIEUPHU: {
                        TrieuPhuTable table = (TrieuPhuTable) room.getAttactmentData();
                        table.answer(aSession.getUID(), rq.answer);
                        if (table.isSingleMode) {
                            table.doTimeout();
                        } else {
                            if (table.isAllAnswer()) {
                                table.doTimeout();
                            } else {
                                resMatchTurn.setSuccess(Long.toString(aSession.getUID()));
                                table.broadcastMsg(resMatchTurn, table.getNewPlayings(), table.getNewWaitings(), table.findPlayer(aSession.getUID()), true);
                                table.checkFinish();
                            }
                        }
                        
                        break;
                    }
                    
                    default:
                        break;
                }
            } else {
                mLog.error("Room is null trieu phu answer");
                resMatchTurn.setFailure("Bạn cần tham gia vào một trận trước khi chơi.");
                aSession.write(resMatchTurn);
            }
        } catch (Throwable ex1) {
            mLog.error(ex1.getMessage(), ex1);
            resMatchTurn.setFailure(ex1.getMessage());
            aResPkg.addMessage(resMatchTurn);
        }
        
        return 1;
    }
}
