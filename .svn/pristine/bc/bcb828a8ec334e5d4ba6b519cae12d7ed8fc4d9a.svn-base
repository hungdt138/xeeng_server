package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.PrivateChatRequest;
import com.tv.xeeng.base.protocol.messages.PrivateChatResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.databaseDriven.MessageDB;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class PrivateChatBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(PrivateChatBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        int rtn = PROCESS_FAILURE;
        MessageFactory msgFactory = aSession.getMessageFactory();
        PrivateChatResponse resChat =
                (PrivateChatResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        mLog.debug("[PRIVATE CHAT]: Catch");
        try {
            PrivateChatRequest rqChat = (PrivateChatRequest) aReqMsg;
            String message = rqChat.mMessage;
            long sourceID = aSession.getUID();
            long destID = rqChat.destUid;

//            ISession buddySession = aSession.getManager().findSession(destID);
            ISession buddySession = aSession.getManager().findPrvChatSession(destID);
//            ISession sourceSesstion = aSession.getManager().findSession(sourceID);
//            UserEntity usrEntity = CacheUserInfo.getCacheUserInfo(sourceID);
            if(aSession.isMXHDevice())
            {
//                if (buddySession != null && buddySession.getChatRoom()>0) {
                if (buddySession != null) {
                    if(buddySession.getChatRoom()>0 || buddySession.getByteProtocol()> AIOConstants.PROTOCOL_MXH)
                    {
                        if(buddySession.getRoom()!= null && buddySession.getRoom().isPlaying() 
                                && buddySession.getDeviceType() != AIOConstants.IPHONE_DEVICE)
                        {
                            resChat.setFailure(ResponseCode.FAILURE, buddySession.getUserName() + " đang chơi game rồi");
                            // Send message to buddy
                            aSession.write(resChat);
                        
                        }
                        else
                        {
                            resChat.setSuccess(ResponseCode.SUCCESS, sourceID, message, aSession.getUserName());
                            // Send message to buddy
                            buddySession.write(resChat);
                        }
                    }
                } else {
                    if(aSession.getByteProtocol()> AIOConstants.PROTOCOL_MXH)
                    {
                        CacheUserInfo cache = new CacheUserInfo();
                        String destName = cache.getUserInfo(destID).mUsername;
                        resChat.setFailure(ResponseCode.FAILURE, destName + " đã offline rồi");
                      // Send message to buddy
                        aSession.write(resChat);
                    }
                    
                    
                    
                        MessageDB db = new MessageDB();
                        db.insertMessage(sourceID, message, destID, "");
                    

                }
            }
            else
            {
                if (buddySession != null && buddySession.getChatRoom()>0) {
                    resChat.setSuccess(ResponseCode.SUCCESS, sourceID, message, aSession.getUserName());
                    // Send message to buddy
                    buddySession.write(resChat);
                } 
            }
            

            rtn = PROCESS_OK;

        } catch (Throwable t) {

            resChat.setFailure(ResponseCode.FAILURE, "Process Error!");
            //aSession.setLoggedIn(false);
            rtn = PROCESS_OK;
            // log this error
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
        }

        return rtn;
    }
}
