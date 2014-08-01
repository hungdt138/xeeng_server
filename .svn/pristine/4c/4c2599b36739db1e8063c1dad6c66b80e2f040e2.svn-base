/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.LineGetOtherTableOutRequest;
import com.tv.xeeng.base.protocol.messages.LineGetOtherTableResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.line.data.LineTable;
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
public class LineGetOtherTableOutBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(LineGetOtherTableOutBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) {
        // process's status
        mLog.debug("[LINE - Get Matrix Out] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory = aSession.getMessageFactory();
        LineGetOtherTableResponse resMatchTurn = (LineGetOtherTableResponse) msgFactory.getResponseMessage(MessagesID.GetLineOtherTable);
        resMatchTurn.session = aSession;
        try {
            LineGetOtherTableOutRequest rq = (LineGetOtherTableOutRequest) aReqMsg;
            int zoneID = aSession.getCurrentZone();
            Zone zone = aSession.findZone(zoneID);
            Room room = zone.findRoom(rq.mMatchId);
            if (room != null) {
                LineTable table = (LineTable) room.getAttactmentData();
                String str = rq.matrix;
                resMatchTurn.setSuccess(str);
                resMatchTurn.uid = rq.uid;
                if (table.isPlaying) { // Stop
                    ISession otherSession = table.getPlayer(rq.uid).currentSession;
                    resMatchTurn.session = otherSession;
                    otherSession.write(resMatchTurn);
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
            mLog.error("Không thể gửi được.");
        }catch (Exception ex1){
            mLog.error(ex1.getMessage());
            resMatchTurn.setFailure(ResponseCode.FAILURE,
                        ex1.getMessage());
            aResPkg.addMessage(resMatchTurn);
        }

        return 1;
    }
}
