package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.ReadMessageRequest;
import com.tv.xeeng.base.protocol.messages.ReadMessageResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.MessageDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class ReadMessageBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(ReadMessageBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[ReadMessage] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        ReadMessageResponse resRead =
                (ReadMessageResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resRead.session = aSession;
        
        try {
            ReadMessageRequest req = (ReadMessageRequest) aReqMsg;
            long messID = req.messID;
            
            StringBuilder sb = new StringBuilder();
            
            MessageDB db = new MessageDB();
            String detail = db.readMessage(messID);                       
            
            CacheUserInfo cache = new CacheUserInfo();
            cache.deleteCacheMessage(aSession.getUID());
                        
            sb.append(messID).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(detail);
                
            resRead.value = sb.toString();
            
            resRead.setSuccess(ResponseCode.SUCCESS);
            
        } catch (Exception e) {
            mLog.error(e.getMessage(), e);
            resRead.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu");
        } finally {
            if ((resRead != null)) {
                aResPkg.addMessage(resRead);
            }
        }
        return 1;
    }
}
