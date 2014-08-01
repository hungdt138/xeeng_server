package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.ReadyRequest;
import com.tv.xeeng.base.protocol.messages.ReadyResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.Messages;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class ReadyBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ReadyBusiness.class);
    
    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[READY]: Catch ");
        MessageFactory msgFactory = aSession.getMessageFactory();
        ReadyResponse resReady = (ReadyResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resReady.setSession(aSession);
        try {
            ReadyRequest rqMatchNew = (ReadyRequest) aReqMsg;

            if (rqMatchNew.uid == 0) {
                rqMatchNew.uid = aSession.getUID();
            }

            MatchEntity matchEntity = CacheMatch.getMatch(rqMatchNew.matchID);
            Room room = null;
            if (matchEntity != null) {
                room = matchEntity.getRoom();
            }

            if (room == null || (room.getAttactmentData().matchID != rqMatchNew.matchID)) {
                Zone zone = aSession.findZone(aSession.getCurrentZone());
                room = zone.findRoom(rqMatchNew.matchID);
            }

            if (room != null) {
                resReady.zone = aSession.getCurrentZone();

                switch (aSession.getCurrentZone()) {
/*
                case ZoneID.PHOM:
                case ZoneID.TIENLEN: 
				case ZoneID.NEW_BA_CAY:
				case ZoneID.BAU_CUA_TOM_CA:
                case ZoneID.AILATRIEUPHU:
                case ZoneID.PIKACHU: 
*/
                    default: {
                        SimpleTable newTable = room.getAttactmentData();

                        if (newTable.isPlaying) {
                            throw new BusinessException(Messages.READY_PLAYING_TABLE);
                        }
                        
                        newTable.playerReadyWithBroadcast(rqMatchNew.uid, true);
                        
                        resReady = null;
                        break;
                    }
                }
            } else {
                resReady.setFailure(ResponseCode.FAILURE, "Bàn chơi đã bị huỷ!");
            }

        } catch (BusinessException be) {
            resReady.setFailure(ResponseCode.FAILURE, be.getMessage());
//			mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } catch (Throwable t) {
            resReady.setFailure(ResponseCode.FAILURE, "Bị lỗi " + t.toString());
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resReady != null)) {
                aResPkg.addMessage(resReady);
            }
        }

        return 1;
    }
}
