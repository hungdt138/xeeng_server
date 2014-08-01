/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelShowHandRequest;
import com.tv.xeeng.base.protocol.messages.CancelShowHandResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaException;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaTable;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

/**
 *
 * @author tuanda
 */
public class CancelShowHandBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(CancelShowHandBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[BET] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory = aSession.getMessageFactory();
        CancelShowHandResponse resCancel = (CancelShowHandResponse) msgFactory
                        .getResponseMessage(aReqMsg.getID());

        try {
                CancelShowHandRequest rqCancel = (CancelShowHandRequest) aReqMsg;
                Room room = aSession.getRoom();

                if(room == null)
                {
                    throw new BusinessException("Bạn cần tham gia vào một trận trước khi chơi.");
                }
                
                switch(aSession.getCurrentZone())
                {
                    case ZoneID.BAU_CUA_TOM_CA:{
                        BauCuaTomCaTable table = (BauCuaTomCaTable) room.getAttactmentData();
                        table.cancelShowHand(rqCancel.players);
                        break;
                    } 
                }
        }
        catch(BauCuaTomCaException ex)
        {
            
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
                mLog.error("Process message " + aReqMsg.getID() + " error.", t);
                try {
                        aSession.write(resCancel);
                } catch (ServerException ex) {
                }
        } 
        return 1;
    }
}
