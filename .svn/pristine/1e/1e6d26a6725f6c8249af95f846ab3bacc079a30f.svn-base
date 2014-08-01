package com.tv.xeeng.base.business;
import java.util.List;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetSocialFriendRequest;
import com.tv.xeeng.base.protocol.messages.GetSocialFriendResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheFriendsInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class GetSocialFriendBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetSocialFriendBusiness.class);
    
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("get social friend");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetSocialFriendResponse resalbum = (GetSocialFriendResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        try {
            GetSocialFriendRequest rqSocial = (GetSocialFriendRequest) aReqMsg;
            CacheFriendsInfo cache  = new CacheFriendsInfo();
            List<UserEntity> friends = cache.getFriends(aSession.getUID());
            
            int friendSize = friends.size();
//            int fromIndex = rqSocial.pageIndex * AIOConstants.PAGE_10_DEFAULT_SIZE;
            int fromIndex = rqSocial.pageIndex * AIOConstants.PAGE_50_DEFAULT_SIZE;
//            int toIndex = fromIndex + AIOConstants.PAGE_10_DEFAULT_SIZE;
            int toIndex = fromIndex + AIOConstants.PAGE_50_DEFAULT_SIZE;
            
            mLog.warn(" Friend size va from index " + friendSize + "  " + fromIndex);
            
            if(toIndex>friendSize)
            {
                toIndex = friendSize;
            }
            
            StringBuilder sb = new StringBuilder();
            
            
            for(int i = fromIndex; i < toIndex; i++)
            {
                UserEntity entity = friends.get(i);
                sb.append(Long.toString(entity.mUid)).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(entity.mUsername).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Long.toString(entity.money)).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Long.toString(entity.avFileId)).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(entity.isOnline?"1":"0").append(AIOConstants.SEPERATOR_BYTE_2);
            }
            
            if(sb.length() > 0)
            {
                sb.deleteCharAt(sb.length() -1 );
                sb.append(AIOConstants.SEPERATOR_BYTE_3);
                sb.append(rqSocial.pageIndex).append(AIOConstants.SEPERATOR_BYTE_1);
//                sb.append(Math.round(friendSize/AIOConstants.PAGE_10_DEFAULT_SIZE));
                sb.append(Math.round(friendSize/AIOConstants.PAGE_50_DEFAULT_SIZE));
            }
            
            mLog.warn(" Fiend list " + sb.toString());

            
//            UUID imgRequest = UUID.randomUUID();
//            aSession.setImageRequest(imgRequest);
//            
//            long currentTime = System.currentTimeMillis();
            
//            for(int i = fromIndex; i< toIndex; i++)
//            {
//                UserEntity entity = friends.get(i);
//                if(entity.avFileId>0)
//                {
//                    //put into queue
//                    GetSocialAvatarResponse queueAvar = (GetSocialAvatarResponse)msgFactory.getResponseMessage(MessagesID.GET_SOCIAL_AVATAR);
//                    queueAvar.mCode = ResponseCode.SUCCESS;
//                    QueueImageEntity imgEntity = new QueueImageEntity(entity.avFileId, true,
//                            aSession, queueAvar);
//                    
//                    imgEntity.setUserId(entity.mUid);
//                    imgEntity.setRequestImgId(imgRequest);
//                    imgEntity.setRequestTime(currentTime);
//                    
//                    ImageQueue imgQueue = new ImageQueue();
//                    imgQueue.insertImage(imgEntity);
//                
//                }
//            }
            
            resalbum.mCode = ResponseCode.SUCCESS; 
            resalbum.value = sb.toString();

        } 
//        {
//            mLog.warn(ex.getMessage());
//            resalbum.setFailure(ex.getMessage());
//            
//        }
        finally
        {
            if (resalbum!= null)
            {
                try {
                    aSession.write(resalbum);
                } catch (ServerException ex) {
                    mLog.error(ex.getMessage(), ex);
                }
                        
            }
        }        
        return 1;
    }
}
