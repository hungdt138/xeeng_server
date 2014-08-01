/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.UpdateCommentRequest;
import com.tv.xeeng.base.protocol.messages.UpdateCommentResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.CommentDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

/**
 *
 * @author tuanda
 */
public class UpdateCommentBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(UpdateCommentBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
//        mLog.debug("[BET] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory =  aSession.getMessageFactory();
        UpdateCommentResponse resBet = (UpdateCommentResponse) msgFactory
                        .getResponseMessage(aReqMsg.getID());
        
        try {
                UpdateCommentRequest rqAddComment = (UpdateCommentRequest) aReqMsg;
                CommentDB db = new CommentDB();
                db.updateComment( rqAddComment.comment, rqAddComment.commentId);
                resBet.mCode = ResponseCode.SUCCESS;
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
