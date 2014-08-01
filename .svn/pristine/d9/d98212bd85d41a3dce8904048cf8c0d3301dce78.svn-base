/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import java.util.List;
import java.util.UUID;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.data.ImageQueue;
import com.tv.xeeng.base.protocol.messages.GetSocialAvatarResponse;
import com.tv.xeeng.base.protocol.messages.RequestFriendRequest;
import com.tv.xeeng.base.protocol.messages.RequestFriendResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.QueueImageEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SocialFriendEntity;
import com.tv.xeeng.memcached.data.CacheFriendsInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

/**
 *
 * @author tuanda
 */
public class RequestFriendBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(RequestFriendBusiness.class);
    
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("get request friends");
        MessageFactory msgFactory = aSession.getMessageFactory();
        RequestFriendResponse resComment = (RequestFriendResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        try {
            RequestFriendRequest rqSocial = (RequestFriendRequest) aReqMsg;
            
            CacheFriendsInfo cacheFriend = new CacheFriendsInfo();
            
            List<SocialFriendEntity> lstFriends = cacheFriend.getSocialFriendRequests(aSession.getUID());
            int friendSize = lstFriends.size();
            int fromIndex = rqSocial.pageIndex * AIOConstants.PAGE_10_DEFAULT_SIZE;
            int toIndex = fromIndex + AIOConstants.PAGE_10_DEFAULT_SIZE;
            if(toIndex>friendSize)
            {
                toIndex = friendSize;
            }
            
            
            StringBuilder sb = new StringBuilder();
            
            for(int i = fromIndex; i< toIndex; i++)
            {
                SocialFriendEntity entity = lstFriends.get(i);
                sb.append(Long.toString(entity.getfId())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(entity.getfName()).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Long.toString(entity.getfAvatarFileId())).append(AIOConstants.SEPERATOR_BYTE_2);   
            }
            
            if(sb.length()>0)
            {   
                sb.deleteCharAt(sb.length() -1);
            }
            
            sb.append(AIOConstants.SEPERATOR_BYTE_3);
            sb.append(rqSocial.pageIndex).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Math.round(friendSize/AIOConstants.PAGE_10_DEFAULT_SIZE));
            
            
            
            resComment.mCode = ResponseCode.SUCCESS;        
            
            resComment.value = sb.toString();
            aSession.write(resComment);
            UUID imgRequest = UUID.randomUUID();
            aSession.setImageRequest(imgRequest);
            long currentTime = System.currentTimeMillis();
            
            //send icon to client    
//            CacheFileInfo cacheFile  = new CacheFileInfo();
            for(int i = fromIndex; i< toIndex; i++)
            {
                SocialFriendEntity entity = lstFriends.get(i);
                if(entity.getfAvatarFileId()>0)
                {
                    //put into queue
                    GetSocialAvatarResponse queueAvar = (GetSocialAvatarResponse)msgFactory.getResponseMessage(MessagesID.GET_SOCIAL_AVATAR);
                    queueAvar.mCode = ResponseCode.SUCCESS;
    //                QueueImageEntity imgEntity = new QueueImageEntity(entity.getFileId(), false, aSession, queueAlbum, rqAlb.albumId);
                    QueueImageEntity imgEntity = new QueueImageEntity(entity.getfAvatarFileId(), true,
                            aSession, queueAvar);
                    
                    
                    imgEntity.setUserId(entity.getUserId());
                    imgEntity.setRequestImgId(imgRequest);
                    imgEntity.setRequestTime(currentTime);
                    
                    ImageQueue imgQueue = new ImageQueue();
                    imgQueue.insertImage(imgEntity);
                
                }
            }
            
            
        } 
                
        catch (ServerException ex) {
            mLog.error(ex.getMessage(), ex);
        }

        finally
        {
            
        }        
        return 1;
    }
}
