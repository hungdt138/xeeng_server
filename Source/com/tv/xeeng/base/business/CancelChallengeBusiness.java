/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelChallengeRequest;
import com.tv.xeeng.base.protocol.messages.CancelChallengeResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaException;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaTable;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.newbacay.data.NewBaCayException;
import com.tv.xeeng.game.newbacay.data.NewBaCayTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

/**
 *
 * @author tuanda
 */
public class CancelChallengeBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(CancelChallengeBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[BET] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory = aSession.getMessageFactory();
        CancelChallengeResponse resCancel = (CancelChallengeResponse) msgFactory
                        .getResponseMessage(aReqMsg.getID());
        // boolean isFinish = false;
        try {
                CancelChallengeRequest rqCancel = (CancelChallengeRequest) aReqMsg;
                Room room = aSession.getRoom();

                // Thread.sleep(10000);
                if(room == null)
                {
                    throw new BusinessException("Bạn cần tham gia vào một trận trước khi chơi.");
                }
                
                switch(aSession.getCurrentZone())
                {
                    case ZoneID.NEW_BA_CAY:{
                        NewBaCayTable table = (NewBaCayTable) room.getAttactmentData();

                        table.cancelChallenge(rqCancel, aSession.getUID());

                        break;
                    }    
                }
        }
        
        catch(NewBaCayException ex)
        {
            
            resCancel.setFailure( ex.getMessage());
            
            mLog.debug("Invalid " + ex.getMessage());
            
            try {
                        aSession.write(resCancel);
                } catch (ServerException se) {
                       
                }
            
            
        }
        catch(BusinessException ex)
        {
            mLog.error("Process message " + aReqMsg.getID() + " error." + ex.getMessage());
            resCancel.setFailure( ex.getMessage());
            
            mLog.error("Invalid " + ex.getMessage());
            
            try {
                        aSession.write(resCancel);
                } catch (ServerException se) {
                       
                }
            
            
        }
        catch (Throwable t) {
                //resBet.setFailure(ResponseCode.FAILURE, t.getMessage());
                mLog.error("Process message " + aReqMsg.getID() + " error.", t);
                try {
                        aSession.write(resCancel);
                } catch (ServerException ex) {
                        // java.util.logging.Logger.getLogger(TurnBusiness.class.getName()).log(Level.SEVERE,
                        // null, ex);
                }

        } 
        
        return 1;
    }
}
