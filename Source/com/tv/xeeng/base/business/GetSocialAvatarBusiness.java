/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetSocialAvatarResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;


/**
 *
 * @author tuanda
 */
public class GetSocialAvatarBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetSocialAvatarBusiness.class);
    
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("get social avatar");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetSocialAvatarResponse resFile = (GetSocialAvatarResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        /*
        try {
            
            GetSocialAvatarRequest rqAvatar = (GetSocialAvatarRequest) aReqMsg;
            CacheUserInfo cacheUser = new CacheUserInfo();
            long fileId = 0;
            if(rqAvatar.type == AIOConstants.NORMAL_FILE_TYPE) //for get file when just upload file
            {
                fileId = rqAvatar.uid;
            }
            else
            {
                UserEntity userEntity = cacheUser.getUserInfo(rqAvatar.uid);

                if(rqAvatar.type == AIOConstants.AVATAR_FILE_TYPE )
                {
                    fileId = userEntity.avFileId;
                }
                else if(rqAvatar.type == AIOConstants.BIA_FILE_TYPE )
                {
                    fileId = userEntity.biaFileId;
                }
            
            }
            CacheFileInfo cache  = new CacheFileInfo();
            FileEntity fileEntity = cache.getFileDetail(fileId);
            
//            AlbumDB db = new AlbumDB();
            
            
            UUID imgRequest = UUID.randomUUID();
            aSession.setImageRequest(imgRequest);
            long currentTime = System.currentTimeMillis();
            
            if(fileEntity != null)
            {
                if(rqAvatar.type == AIOConstants.NORMAL_FILE_TYPE)
                {
                    //dont response getSocialAvatar instead of SendFileICon. Its so stupid
                    
                    SendFileIconResponse queueAlbum = (SendFileIconResponse)msgFactory.getResponseMessage(MessagesID.SEND_IMAGE_ICON);
                    queueAlbum.mCode = ResponseCode.SUCCESS;
                   
    //                QueueImageEntity imgEntity = new QueueImageEntity(entity.getFileId(), false, aSession, queueAlbum, rqAlb.albumId);
                    QueueImageEntity imgEntity = new QueueImageEntity(fileEntity.getFileId(), true, aSession, queueAlbum);


                    imgEntity.setFileEntity(fileEntity);
                    imgEntity.setRequestImgId(imgRequest);
                    imgEntity.setRequestTime(currentTime);
                    ImageQueue imgQueue = new ImageQueue();
                    imgQueue.insertImage(imgEntity);
                    resFile = null;
                }
                else
                {
                    
                     resFile.mCode = ResponseCode.SUCCESS;   
                     StringBuilder sb = new StringBuilder();
                     sb.append(Long.toString(rqAvatar.uid)).append(AIOConstants.SEPERATOR_NEW_ELEMENT);
                     sb.append(Long.toString(fileId)).append(AIOConstants.SEPERATOR_NEW_ELEMENT);

                    if(rqAvatar.type == AIOConstants.AVATAR_FILE_TYPE)
                    {
                        if(fileEntity.getIconContent()== null )
                        {

                            String fileName = fileEntity.getLocation() + fileEntity.getFileName() +AIOConstants.DEFAUL_ICON_BASE64_SUFFIX;
                            String imgbase64=new Scanner(new File(fileName)).useDelimiter("\\Z").next();

                            fileEntity.setIconContent(imgbase64);

                            cache.updateCacheFileDetail(fileEntity);
                        }



                        sb.append(fileEntity.getIconContent());
                    }
                    else
                    {
                         if(fileEntity.getContent() == null)
                        {
                            String fileName = fileEntity.getLocation() + fileEntity.getFileName() +AIOConstants.DEFAUL_NORMAL_BASE64_FILE;
                            String imgbase64 = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
                            fileEntity.setContent(imgbase64);

                            cache.updateCacheFileDetail(fileEntity);
                        }
                        sb.append(fileEntity.getContent());


                    }
                    resFile.value = sb.toString();
                }
            }
            else
            {
                resFile.mErrorMsg ="File ảnh không tồn tại";
                resFile.mCode = ResponseCode.FAILURE;        
            }
            
            
            
            
            
        } 
                
        catch (IOException ex) {
            mLog.error(ex.getMessage(), ex);
            resFile.mCode = ResponseCode.FAILURE;    
//            resalbum.errMsg = "";
        }            
            

            
            
//        }
//        catch(BusinessException ex)
//        {
//            mLog.warn(ex.getMessage());
//            resalbum.setFailure(ex.getMessage());
//            
//        }
        finally
        {
            if (resFile!= null)
            {
                try {
                    aSession.write(resFile);
                } catch (ServerException ex) {
                    mLog.error(ex.getMessage(), ex);
                }
                        
            }
        }     */   
        return 1;
    }
}
