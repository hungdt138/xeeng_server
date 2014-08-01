/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;











import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.DeleteMessageRequest;
import com.tv.xeeng.base.protocol.messages.DeleteMessageResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.MessageDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class DeleteMessageBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(DeleteMessageBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
//        mLog.debug("[BET] : Catch  ; " + aSession.getUserName());
        MessageFactory msgFactory =  aSession.getMessageFactory();
        DeleteMessageResponse resBet = (DeleteMessageResponse) msgFactory
                        .getResponseMessage(aReqMsg.getID());
        
        try {
                DeleteMessageRequest rqAddComment = (DeleteMessageRequest) aReqMsg;
                MessageDB db = new MessageDB();
                db.deleteMessage(rqAddComment.type, rqAddComment.messageId, aSession.getUID());
                
                resBet.mCode = ResponseCode.SUCCESS;
                aSession.write(resBet);
                CacheUserInfo cache = new CacheUserInfo();
                cache.deleteCacheMessage(aSession.getUID());
                
                
        }
        
        catch (Throwable t) {
                //resBet.setFailure(ResponseCode.FAILURE, t.getMessage());
                mLog.error("Process message " + aReqMsg.getID() + " error.", t);
                try {
                    resBet.setFailure(ResponseCode.FAILURE, "Co loi xay ra");
                        aSession.write(resBet);
                } catch (ServerException ex) {
                        // java.util.logging.Logger.getLogger(TurnBusiness.class.getName()).log(Level.SEVERE,
                        // null, ex);
                }

        } 
        
        return 1;
    }
}
