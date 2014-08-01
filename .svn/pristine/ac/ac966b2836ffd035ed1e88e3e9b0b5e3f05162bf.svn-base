/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.LineEndMatchRequest;
import com.tv.xeeng.base.protocol.messages.LineEndMatchResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.line.data.LineTable;
import com.tv.xeeng.game.newpika.data.NewPikaTable;
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
public class LineEndMatchBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(LineEndMatchBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) {
        // process's status
        mLog.debug("[LINE - End] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory = aSession.getMessageFactory();
        LineEndMatchResponse resMatchTurn = (LineEndMatchResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resMatchTurn.session = aSession;
        try {
            LineEndMatchRequest rq = (LineEndMatchRequest) aReqMsg;
            int zoneID = aSession.getCurrentZone();
            Zone zone = aSession.findZone(zoneID);
            Room room = zone.findRoom(rq.mMatchId);
            long currID = aSession.getUID();//rq.uid;
            if (room != null) {
                boolean isWin = rq.isWin;
                switch (zoneID) {                                     
                    case ZoneID.PIKACHU: {
                        PikachuTable pTable = (PikachuTable) room.getAttactmentData();
                        if (pTable.isPlaying) {
                            if (isWin) {
                                pTable.endMatch(currID, rq.type);
                            } else {
                                pTable.stop(currID);
                            }
                        } else {
                            resMatchTurn.setFailure(ResponseCode.FAILURE,
                                    "Bàn chơi đã kết thúc rồi.");
                            aSession.write(resMatchTurn);
                        }
                        break;
                    }
                    case ZoneID.NEW_PIKA:
                        NewPikaTable pTable = (NewPikaTable) room.getAttactmentData();
                        if (pTable.isPlaying) {

                            pTable.endMatch(currID, 1);

                        } else {
                            resMatchTurn.setFailure(ResponseCode.FAILURE,
                                    "Bàn chơi đã kết thúc rồi.");
                            aSession.write(resMatchTurn);
                        }
                        break;
                    default:
                        break;
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
            ex1.printStackTrace();
            resMatchTurn.setFailure(ResponseCode.FAILURE,
                    ex1.getMessage());
            aResPkg.addMessage(resMatchTurn);
        }

        return 1;
    }
}
