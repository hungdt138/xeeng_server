/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.LineEatRequest;
import com.tv.xeeng.base.protocol.messages.LineEatResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.pikachu.datta.PikachuPlayer;
import com.tv.xeeng.game.pikachu.datta.PikachuTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

/**
 *
 * @author tuanda
 */
public class LineEatBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(LineEatBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) {
        // process's status
        mLog.debug("[LINE - EAT] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory = aSession.getMessageFactory();
        LineEatResponse resMatchTurn = (LineEatResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resMatchTurn.session = aSession;
        try {
            LineEatRequest rq = (LineEatRequest) aReqMsg;
            int zoneID = aSession.getCurrentZone();
            Zone zone = aSession.findZone(zoneID);
            Room room = zone.findRoom(rq.mMatchId);
            long currID = aSession.getUID();//rq.uid;
            if (room != null) {
               if (zoneID == ZoneID.PIKACHU) {
                    PikachuTable table = (PikachuTable) room.getAttactmentData();
                    int number = rq.number;
                    if (table.isPlaying) { // Stop
                    	PikachuPlayer pl = table.findPlayer(currID);
                        pl.point = number;
                        resMatchTurn.setSuccess(currID, number);
                        table.broadcastMsg(resMatchTurn, table.playings, table.waitings, table.owner, true);
                    }
                }
            } else {
                mLog.error("Room is null ; matchID : " + rq.mMatchId
                        + " ; " + aSession.getUserName() + " ; zone = "
                        + aSession.getCurrentZone());

                resMatchTurn.setFailure(ResponseCode.FAILURE,
                        "Bạn cần tham gia vào một trận trước khi chơi.");
                aSession.write(resMatchTurn);
            }
        } catch (ServerException ex) {
            resMatchTurn.setFailure(ResponseCode.FAILURE,
                    "Không thể gửi được.");
            aResPkg.addMessage(resMatchTurn);
        } catch (Exception ex1) {
            //ex1.printStackTrace();
            resMatchTurn.setFailure(ResponseCode.FAILURE,
                    ex1.getMessage());
            aResPkg.addMessage(resMatchTurn);
        }

        return 1;
    }
}
