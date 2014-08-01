package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.TrieuPhuMultiModeRequest;
import com.tv.xeeng.base.protocol.messages.TrieuPhuMultiModeResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.pikachu.datta.PikachuTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuTable;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class TrieuPhuMultiModeBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(TrieuPhuMultiModeBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[Change Mode]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        TrieuPhuMultiModeResponse resPostListResponse = (TrieuPhuMultiModeResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            TrieuPhuMultiModeRequest rq = (TrieuPhuMultiModeRequest) aReqMsg;
            long uid = aSession.getUID();
            MatchEntity matchEntity = CacheMatch
                    .getMatch(rq.mMatchId);
            Room room = null;
            int zoneId = 0;
            if (matchEntity != null) {
                room = matchEntity.getRoom();
                zoneId = matchEntity.getZoneId();
            }
            if (room == null) {
                zoneId = aSession.getCurrentZone();
                room = aSession.getRoom();
            }
            if (room == null
                    || (room.getAttactmentData().matchID != rq.mMatchId)) {
                Zone zone = aSession.findZone(zoneId);
                room = zone.findRoom(rq.mMatchId);
            }
            if (room != null) {
                switch (zoneId) {
                    case ZoneID.PIKACHU: {
                        PikachuTable pTable = (PikachuTable) room.getAttactmentData();
                        pTable.changeMode(uid);
                        break;
                    }
                    case ZoneID.AILATRIEUPHU: {
                        TrieuPhuTable table = (TrieuPhuTable) room.getAttactmentData();
                        table.changeMode(uid);
                    }
                }
                resPostListResponse.setSuccess();
            } else {
                resPostListResponse.setFailure("Không tìm thấy bàn của bạn");
            }

        } catch (Throwable t) {
            resPostListResponse.setFailure(t.getMessage());
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resPostListResponse != null)) {
                aResPkg.addMessage(resPostListResponse);
            }
        }
        return 1;
    }
}
