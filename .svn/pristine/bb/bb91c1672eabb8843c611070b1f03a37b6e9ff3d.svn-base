/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BetRequest;
import com.tv.xeeng.base.protocol.messages.BetResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaTable;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.lieng.data.LiengTable;
import com.tv.xeeng.game.newbacay.data.NewBaCayException;
import com.tv.xeeng.game.newbacay.data.NewBaCayTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

/**
 *
 * @author tuanda
 */
public class BetBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BetBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
//        mLog.debug("[BET] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory = null;// aSession.getMessageFactory();
        BetResponse resBet = null;

        try {
            BetRequest rqBet = (BetRequest) aReqMsg;
            rqBet.uid = aSession.getUID();
            Room room = aSession.getRoom();

            if (rqBet.matchID > 0) {
                MatchEntity matchEntity = CacheMatch.getMatch(rqBet.matchID);;
                room = null;
                if (matchEntity != null) {
                    room = matchEntity.getRoom();
                }
            }

            if (room == null) {
                mLog.error("Room is null ; matchID : " + rqBet.matchID
                        + " ; " + aSession.getUserName() + " ; zone = "
                        + aSession.getCurrentZone());
                throw new BusinessException("Bạn cần tham gia vào một trận trước khi chơi.");
            }

            msgFactory = room.getAttactmentData().getNotNullSession().getMessageFactory();

            resBet = (BetResponse) msgFactory.getResponseMessage(aReqMsg.getID());
            resBet.session = aSession;

            switch (aSession.getCurrentZone()) {
                case ZoneID.NEW_BA_CAY: {
                    NewBaCayTable table = (NewBaCayTable) room.getAttactmentData();
                    table.bet(rqBet);
                    if (table.isIsChiaBai()) //does all players bet for this match?
                    {
                        //generate poker
                        table.sendPokers();
                    }
                    break;
                }

                case ZoneID.BAU_CUA_TOM_CA: {
                    BauCuaTomCaTable table = (BauCuaTomCaTable) room.getAttactmentData();
                    table.bet(rqBet.uid, rqBet.holo, rqBet.tom, rqBet.cua, rqBet.ca, rqBet.ga, rqBet.huou);
                    break;
                }
            }
        } catch (Exception ex) {
//          mLog.debug(ex.getMessage());
            if (resBet != null) {
                resBet.setFailure(ResponseCode.FAILURE, ex.getMessage());
            }
            try {
                aSession.write(resBet);
            } catch (ServerException se) {

            }
        } catch (Throwable t) {
            //resBet.setFailure(ResponseCode.FAILURE, t.getMessage());
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
            try {
                aSession.write(resBet);
            } catch (ServerException ex) {
            }
        }
        return 1;
    }
}
