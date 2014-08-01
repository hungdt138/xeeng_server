package com.tv.xeeng.base.business;

import java.util.Vector;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.OfflineMessageResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.MessageDB;
import com.tv.xeeng.game.data.Message;
import com.tv.xeeng.game.data.MessageEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class OfflineMessageBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(OfflineMessageBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[GET Wal]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        
        OfflineMessageResponse rp = (OfflineMessageResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        
        try {
            long uid = aSession.getUID();
            mLog.debug("[GET OFFLINE MESSAGE]: for" + uid);
            
//            CacheUserInfo cacheUser = new CacheUserInfo();
//            MessageEntity msgEntity = cacheUser.getMessage(aSession.getUID());
            
            MessageDB mdb = new MessageDB();
            Vector<Message> msList = mdb.receiveMessage(uid);
            
            rp.setSuccess(ResponseCode.SUCCESS, msList);
            
        } catch (Throwable t) {
        	rp.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu ");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((rp != null)) {
                aResPkg.addMessage(rp);
            }
        }
        return 1;
    }
}
