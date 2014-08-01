/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.DeleteItemRequest;
import com.tv.xeeng.base.protocol.messages.DeleteItemResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.WallDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class DeleteItemBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(DeleteItemBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
//        mLog.debug("[BET] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory =  aSession.getMessageFactory();
        DeleteItemResponse resBet = (DeleteItemResponse) msgFactory
                        .getResponseMessage(aReqMsg.getID());
        
        try {
                DeleteItemRequest rqAddComment = (DeleteItemRequest) aReqMsg;
                WallDB db = new WallDB();
                long ret = db.deleteItem(rqAddComment.systemObjectId, 
                        rqAddComment.systemObjectRecordId, aSession.getUID());
                if(ret< 0)
                {
                    throw new BusinessException("Bạn không có quyền xóa đối tượng này");
                }
                resBet.mCode = ResponseCode.SUCCESS;
                aSession.write(resBet);
                
        }
        catch(BusinessException be)
        {
            resBet.setFailure(ResponseCode.FAILURE, be.getMessage());
            aResPkg.addMessage(resBet);
        }
                   
        
        catch (Throwable t) {
                //resBet.setFailure(ResponseCode.FAILURE, t.getMessage());
                mLog.error("Process message " + aReqMsg.getID() + " error.", t);
                try {
                        aSession.write(resBet);
                } catch (ServerException ex) {
                        // java.util.logging.Logger.getLogger(TurnBusiness.class.getName()).log(Level.SEVERE,
                        // null, ex);
                }

        } 
        
        return 1;
    }
}
