/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.BaoSamRequest;
import com.tv.xeeng.base.protocol.messages.BaoSamResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MatchEntity;
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
public class BaoSamBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BaoSamBusiness.class);

    @Override
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[Bao Sam]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        BaoSamResponse resAn = (BaoSamResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            BaoSamRequest rqAn = (BaoSamRequest) aReqMsg;
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

            SamTable table = (SamTable) room.getAttactmentData();
            if (resAn == null) {
                msgFactory = table.getNotNullSession().getMessageFactory();
                resAn = (BaoSamResponse) msgFactory.getResponseMessage(aReqMsg.getID());
            }
            if (rqAn.isBao) {
                if (table.baoSam(uid)) {
                    resAn.setSuccess(uid, true, false);
                    
                    SamPlayer player = table.findPlayer(uid);
                    table.broadcastMsg(resAn, table.getNewPlayings(), table.getNewWaitings(), player, true);
                } else {
                    mLog.debug("---THANGTD DEBUG SAM---BAO SAM KHONG THANH CONG: " + uid);
                }
            } else {
                table.huyBaoSam(uid);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            resAn.setFailure(ex.getMessage());
            aResPkg.addMessage(resAn);
        }
        
        return 1;
    }
}
