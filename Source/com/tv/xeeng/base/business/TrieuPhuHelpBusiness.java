package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.TrieuPhuHelpRequest;
import com.tv.xeeng.base.protocol.messages.TrieuPhuHelpResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.MatchEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuTable;
import com.tv.xeeng.memcached.data.CacheMatch;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class TrieuPhuHelpBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(TrieuPhuHelpBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) {
        // process's status
        mLog.debug("[Trieu Phu help] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory = aSession.getMessageFactory();
        TrieuPhuHelpResponse resMatchTurn = (TrieuPhuHelpResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resMatchTurn.session = aSession;
        try {
        	TrieuPhuHelpRequest rq = (TrieuPhuHelpRequest) aReqMsg;
                MatchEntity matchEntity =  CacheMatch.getMatch(rq.mMatchId);
                Room room = null;
                if(matchEntity != null)
                      room =  matchEntity.getRoom();

                if(room == null ||(room.getAttactmentData().matchID != rq.mMatchId))
                {
                    Zone zone = aSession.findZone(aSession.getCurrentZone());
                    room = zone.findRoom(rq.mMatchId);
                }
                        
            long currID = aSession.getUID();
            
            if (room != null) {
                TrieuPhuTable table = (TrieuPhuTable)room.getAttactmentData();
                table.help(currID, rq.type);
            } else {
                mLog.error("Room is null ; matchID : " + rq.mMatchId
                        + " ; " + aSession.getUserName() + " ; zone = "
                        + aSession.getCurrentZone());

                resMatchTurn.setFailure(ResponseCode.FAILURE,
                        "Bạn cần tham gia vào một trận trước khi chơi.");
                aSession.write(resMatchTurn);
            }
        }
        catch(BusinessException ex)
        {
            resMatchTurn.setFailure(ResponseCode.FAILURE,
                    ex.getMessage());
            aResPkg.addMessage(resMatchTurn);
        }
        catch (Throwable ex1) {
            resMatchTurn.setFailure(ResponseCode.FAILURE,
                    "Co loi xay ra");
            aResPkg.addMessage(resMatchTurn);
            mLog.error(ex1.getMessage(), ex1);
        }
        return 1;
    }
}
