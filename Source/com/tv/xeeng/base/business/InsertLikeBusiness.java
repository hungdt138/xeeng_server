/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.InsertLikeRequest;
import com.tv.xeeng.base.protocol.messages.InsertLikeResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.LikeHistoryDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

/**
 *
 * @author tuanda
 */
public class InsertLikeBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(InsertLikeBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
//        mLog.debug("[BET] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory =  aSession.getMessageFactory();
        InsertLikeResponse resBet = (InsertLikeResponse) msgFactory
                        .getResponseMessage(aReqMsg.getID());
        
        try {
                InsertLikeRequest rqAddComment = (InsertLikeRequest) aReqMsg;
                LikeHistoryDB db = new LikeHistoryDB();
                int ret = db.insertLike(aSession.getUID(), rqAddComment.systemObjectRecordId, rqAddComment.systemObjectId);
                if(ret == -1)
                {
                    resBet.mCode = ResponseCode.FAILURE;
                    resBet.mErrorMsg = "Bạn đã like rồi";
                }
                else
                {
                    resBet.mCode = ResponseCode.SUCCESS;
                    
                    
                }
                
                aSession.write(resBet);
                
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
