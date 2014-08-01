package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.SendMessageRequest;
import com.tv.xeeng.base.protocol.messages.SendMessageResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.databaseDriven.MessageDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class SendMessageBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(SendMessageBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, 
    		IResponsePackage aResPkg) {
    	
        mLog.debug("[Send Mess]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        SendMessageResponse resHa =
                (SendMessageResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        resHa.session = aSession;
        try {
            SendMessageRequest rqHa = (SendMessageRequest) aReqMsg;
            long uid = aSession.getUID();
            String mess = rqHa.message;
            long d_uid = rqHa.dUID;
            long s_uid = uid;
            String title = rqHa.title;
            if(aSession.isMXHDevice())
            {
                MessageDB db = new MessageDB();
                db.insertMessage(s_uid, rqHa.message, d_uid, rqHa.title);
                CacheUserInfo cache = new CacheUserInfo();
                cache.deleteCacheMessage(d_uid);
            }
            else
            {
                DatabaseDriver.sendMess(mess, s_uid, d_uid,title);
            }
                resHa.session = aSession;
            
            resHa.setSuccess(ResponseCode.SUCCESS);
        } catch (Throwable t) {
            t.printStackTrace();
            resHa.setFailure(ResponseCode.FAILURE, "Có lỗi xảy ra.");
        }finally {
        	if(resHa != null){
        		aResPkg.addMessage(resHa);
        	}
        }
        return 1;
    }
}
