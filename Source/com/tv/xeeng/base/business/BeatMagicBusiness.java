/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.BaoSamRequest;
import com.tv.xeeng.base.protocol.messages.BaoSamResponse;
import com.tv.xeeng.base.protocol.messages.BeatMagicRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.newbacay.data.NewBaCayTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.xam.data.SamPlayer;
import com.tv.xeeng.game.xam.data.SamTable;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class BeatMagicBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BeatMagicBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[BeatMagic]: Catch");
        try {
            BeatMagicRequest rqAn = (BeatMagicRequest) aReqMsg;
            long uid = aSession.getUID();
            long matchID = rqAn.matchID;
            MatchEntity matchEntity = CacheMatch.getMatch(rqAn.matchID);
            Room room = null;
            if (matchEntity != null) {
                room = matchEntity.getRoom();
            }
            if (room == null || (room.getAttactmentData().matchID != matchID)) {
                Zone zone = aSession.findZone(aSession.getCurrentZone());
                room = zone.findRoom(matchID);
            }
            int zoneId = rqAn.zoneID;
            switch (zoneId) {
                case ZoneID.NEW_BA_CAY: {
                    NewBaCayTable table = (NewBaCayTable) room.getAttactmentData();
                    table.beat(uid, 1,rqAn.code);
                    break;
                }
                default:
                    break;
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return 1;
    }
}
