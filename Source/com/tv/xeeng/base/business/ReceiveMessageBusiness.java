package com.tv.xeeng.base.business;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.ReceiveMessageRequest;
import com.tv.xeeng.base.protocol.messages.ReceiveMessageResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.InfoDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.Message;
import com.tv.xeeng.game.data.MessageEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class ReceiveMessageBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(ReceiveMessageBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, 
    		IResponsePackage aResPkg) {
    	
        mLog.debug("[Get Mess]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        ReceiveMessageResponse resHa =
                (ReceiveMessageResponse) msgFactory.getResponseMessage(aReqMsg.getID());
       
        try {
        	ReceiveMessageRequest rqMess = (ReceiveMessageRequest) aReqMsg;
            long s_uid = aSession.getUID();
            ArrayList<Message> temp  = null;
            if(aSession.isMXHDevice())
            {
                CacheUserInfo cache = new CacheUserInfo();
                
                MessageEntity mesEntity = cache.getMessage(s_uid);
                StringBuilder sb = new StringBuilder();
                
                if(mesEntity != null)
                {
                    List<Message> lstMessages = mesEntity.getLstMessage();
                    sb.append(rqMess.pageIndex).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(mesEntity.getNumPage()).append(AIOConstants.SEPERATOR_BYTE_3);
                    int fromIndex = rqMess.pageIndex * AIOConstants.PAGE_10_DEFAULT_SIZE;
                    int toIndex = (rqMess.pageIndex +1) * AIOConstants.PAGE_10_DEFAULT_SIZE;
                    
                    if(toIndex> lstMessages.size())
                        toIndex = lstMessages.size();
                    
                    List<UserEntity> diffUsers = new ArrayList<UserEntity>();
                    int userSize;   
                    for(int i = fromIndex; i< toIndex; i++)
                    {
                        Message m = lstMessages.get(i);
                        userSize = diffUsers.size();
                        int index = userSize;
                        UserEntity usrEntity = new UserEntity();
                        usrEntity.mUid = m.sID;
                        usrEntity.mUsername = m.sName;
                        usrEntity.avFileId = m.avatarFileId;
                        
                        for(int j = 0; j< userSize; j++)
                        {
                            if(diffUsers.get(j).mUid == usrEntity.mUid)
                            {
                                index = j; 
                                break;
                            }
                        }

                        if(index == userSize || userSize == 0)
                        {
                            diffUsers.add(usrEntity);
                        }
                        
                        sb.append(index).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(m.id).append(AIOConstants.SEPERATOR_BYTE_1);
                        String title = "";
                        
                        if(m.title != null)
                            title = m.title;
                        
                        sb.append(title).append(AIOConstants.SEPERATOR_BYTE_1);
                        
                        sb.append(Long.toString(m.date.getTime())).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(m.isRead?"1":"0").append(AIOConstants.SEPERATOR_BYTE_2);
                        
                    }
                    
                    userSize = diffUsers.size();
                    
                    UUID imgRequest = UUID.randomUUID();
                    aSession.setImageRequest(imgRequest);
            
                    if(toIndex> fromIndex)
                    {
                        sb.deleteCharAt(sb.length() -1);
                        sb.append(AIOConstants.SEPERATOR_BYTE_3);
                        for(int i = 0; i< userSize; i++)
                        {
                            UserEntity usrEntity = diffUsers.get(i);
                            sb.append(usrEntity.mUid).append(AIOConstants.SEPERATOR_BYTE_1);
                            sb.append(usrEntity.mUsername).append(AIOConstants.SEPERATOR_BYTE_1);
                            sb.append(usrEntity.avFileId).append(AIOConstants.SEPERATOR_BYTE_2);
                        }
                        sb.deleteCharAt(sb.length() -1);
                    }
                    
                     resHa.value = sb.toString();
                        resHa.session = aSession;
                        resHa.mCode = ResponseCode.SUCCESS;
                        aSession.write(resHa);
                    
                    return 1;
                    
                }
            }
            else
            {
                InfoDB db = new InfoDB();
                temp = db.receiveMessage(s_uid, aSession.getUserName());
            }
            
            resHa.setSuccess(ResponseCode.SUCCESS,temp);
            resHa.session = aSession;
            aSession.write(resHa);
            
        } catch (Throwable t) {
            t.printStackTrace();
            resHa.setFailure(ResponseCode.FAILURE, "Có lỗi xảy ra.");
            aResPkg.addPackage(aResPkg);
        }finally {
        	
        }
        return 1;
    }
}
