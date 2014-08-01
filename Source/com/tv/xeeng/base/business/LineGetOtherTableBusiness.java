/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.LineGetOtherTableOutResponse;
import com.tv.xeeng.base.protocol.messages.LineGetOtherTableRequest;
import com.tv.xeeng.base.protocol.messages.LineGetOtherTableResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleTable;
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
public class LineGetOtherTableBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(LineGetOtherTableBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) {
        // process's status
        mLog.debug("[Get Other Table] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory = aSession.getMessageFactory();
        LineGetOtherTableResponse resMatchTurn = (LineGetOtherTableResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resMatchTurn.session = aSession;
        try {
            LineGetOtherTableRequest rq = (LineGetOtherTableRequest) aReqMsg;
            int zoneID = aSession.getCurrentZone();
            Zone zone = aSession.findZone(zoneID);
            Room room = zone.findRoom(rq.mMatchId);
            long currID = aSession.getUID();//rq.uid;
            if (room != null) {
                LineGetOtherTableOutResponse response = 
                        (LineGetOtherTableOutResponse) msgFactory.getResponseMessage(MessagesID.GetLineOtherTableOut);
                response.uid = currID;
                SimpleTable t = (SimpleTable) room.getAttactmentData();
                response.session = t.findPlayer(rq.uid).currentSession;
                response.session.write(response);
                /*if (zoneID == ZoneID.GEM_ONLINE) {
                    GemTable table = (GemTable) room.getAttactmentData();
                    table.getPlayer(rq.uid).currentSession.write(response);
                } else if (zoneID == ZoneID.LINE_ONLINE) {
                    LineTable table = (LineTable) room.getAttactmentData();
                    table.getPlayer(rq.uid).currentSession.write(response);
                }*/
                resMatchTurn.mCode = ResponseCode.SUCCESS;
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
        }catch (Exception ex1){
            resMatchTurn.setFailure(ResponseCode.FAILURE,
                        ex1.getMessage());
            aResPkg.addMessage(resMatchTurn);
        }

        return 1;
    }
}
