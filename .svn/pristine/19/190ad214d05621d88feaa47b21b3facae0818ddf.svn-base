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
import com.tv.xeeng.base.protocol.messages.GetTopFileRequest;
import com.tv.xeeng.base.protocol.messages.GetTopFileResponse;
import com.tv.xeeng.base.protocol.messages.SendFileIconResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.FileEntity;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.QueueImageEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.TopFileEntity;
import com.tv.xeeng.memcached.data.CacheFileInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;





/**
 *
 * @author tuanda
 */
public class GetTopFileBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetTopFileBusiness.class);
    
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("get albums");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetTopFileResponse resalbum = (GetTopFileResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        try {
            
            GetTopFileRequest rqAlb = (GetTopFileRequest) aReqMsg;
            
//            AlbumDB db = new AlbumDB();
            CacheFileInfo cache  = new CacheFileInfo();
            
            
            TopFileEntity topEntity = cache.getTopFile();
            List<FileEntity> filesEntity = topEntity.getLstFiles();
  
            resalbum.mCode = ResponseCode.SUCCESS;  
            
            if(rqAlb.page == 0)
            {
                resalbum.value = topEntity.getTopFileStr();
            }
            
            aSession.write(resalbum);
            int fromIndex = (rqAlb.page) * rqAlb.size;
            int toIndex = fromIndex+ rqAlb.size;
            int fileSize = filesEntity.size();
            toIndex = toIndex< fileSize? toIndex: fileSize;
            
            UUID imgRequest = UUID.randomUUID();
            aSession.setImageRequest(imgRequest);
            long currentTime = System.currentTimeMillis();
            
            for(int i = fromIndex; i< toIndex; i++)
            {
                FileEntity entity = filesEntity.get(i);
                entity.setFileIndex(i);
                SendFileIconResponse queueAlbum = (SendFileIconResponse)msgFactory.getResponseMessage(MessagesID.SEND_IMAGE_ICON);
                queueAlbum.mCode = ResponseCode.SUCCESS;
//                QueueImageEntity imgEntity = new QueueImageEntity(entity.getFileId(), false, aSession, queueAlbum, rqAlb.albumId);
                QueueImageEntity imgEntity = new QueueImageEntity(entity.getFileId(), true, aSession, queueAlbum);
                
                imgEntity.setRequestImgId(imgRequest);
                imgEntity.setRequestTime(currentTime);
                    
                imgEntity.setFileEntity(entity);
                ImageQueue imgQueue = new ImageQueue();
                                    imgQueue.insertImage(imgEntity);
                
            }
            
            
            
            
            
        } 
        catch (ServerException ex) {
            mLog.error(ex.getMessage(), ex);
            resalbum.setFailure("co loi xay ra");
            aResPkg.addMessage(resalbum);
        }//        catch(BusinessException ex)
//        {
//            mLog.warn(ex.getMessage());
//            resalbum.setFailure(ex.getMessage());
//            
//        }
        finally
        {
//            if (resalbum!= null)
//            {
//                try {
//                    aSession.write(resalbum);
//                } catch (ServerException ex) {
//                    mLog.error(ex.getMessage(), ex);
//                }
//                        
//            }
        }        
        return 1;
    }
}
