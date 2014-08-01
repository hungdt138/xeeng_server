/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.data;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetSocialAvatarResponse;
import com.tv.xeeng.base.protocol.messages.SendFileIconResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.FileEntity;
import com.tv.xeeng.game.data.QueueImageEntity;
import com.tv.xeeng.memcached.data.CacheFileInfo;




/**
 *
 * @author tuanda
 */
public class ImageQueue implements Job{
//    private static Queue imgQueue = new ArrayDeque();
    private static ConcurrentHashMap<UUID, QueueImageEntity> imgQueue = new ConcurrentHashMap<UUID, QueueImageEntity>();
    
    private static Logger log = LoggerContext.getLoggerFactory()
			.getLogger(ImageQueue.class);
    
    private static boolean isInProgress = false;
    private static int TIME_BETWEEN_IMAGES = 40;
    
//    private static final Object lockObj = new Object();
    
    public  void insertImage(QueueImageEntity entity)
    {
        try
        {
//            imgQueue.add(entity);
            UUID uuid = UUID.randomUUID();
            
            imgQueue.put(uuid, entity);
        }
        catch(Exception ex)
        {
            log.error(ex.getMessage(), ex);
        }
    }
    
    
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        
        if(!isInProgress)
        {
//            log.debug("begin execute send image to client job");
            isInProgress = true;
            
            try
            {
                
                CacheFileInfo cacheFile = new CacheFileInfo();
                Enumeration<UUID> e = imgQueue.keys();
                
                while(e.hasMoreElements())
                {
                    try
                    {
                        QueueImageEntity queryEntity = null;
//                        synchronized(lockObj)
//                        {
//                           queryEntity =  (QueueImageEntity)imgQueue.remove();
//                        }
                        UUID key = e.nextElement();
                        
                        queryEntity = imgQueue.get(key);    
                        imgQueue.remove(key);
                        
                        if(queryEntity == null)
                                continue;
                        
                        ISession aSession = queryEntity.getSession();
                        
                        if( aSession == null  || aSession.isExpired())
                            continue;
                        
                        long currentTime = System.currentTimeMillis();
                        if((currentTime- aSession.getLastSendImage() < TIME_BETWEEN_IMAGES && aSession.getDeviceType() != AIOConstants.ANDROID_DEVICE) || currentTime - queryEntity.getRequestTime() < TIME_BETWEEN_IMAGES)
                        {
                            ImageQueue imgQueue = new ImageQueue();
                            imgQueue.insertImage(queryEntity);
                            continue; 
                        }
                        
                        if(aSession.getImageRequest() != queryEntity.getRequestImgId())
                            continue; //move to another action. The action is old
                        
                        
                        FileEntity fileEntity= queryEntity.getFileEntity();
                        if(fileEntity == null)
                        {
                            fileEntity=  cacheFile.getFileDetail(queryEntity.getFileId());
                        }
                        
                        if(fileEntity == null)
                        {
                            log.warn("khong ton tai file");
                            continue;
                        }
                        
                        String imgbase64 ="";
                        String fileName = fileEntity.getLocation() + fileEntity.getFileName();
                        if(queryEntity.isIsIcon())
                        {
                            String fName = fileName;
                            if(aSession.getDeviceType() == AIOConstants.ANDROID_DEVICE)
                            {
                                if(fileEntity.getIcon80Content() == null)
                                {
                                    boolean is80Icon = true;
                                    File f = new File(fName + AIOConstants.DEFAUL_ICON80_BASE64_SUFFIX);
//                                    log.debug("icon80 " + f.getAbsoluteFile());
                                    if(!f.exists())
                                    {
//                                        log.debug("khong phai icon80 1 ");
                                        f = new File(fName + AIOConstants.DEFAUL_ICON_BASE64_SUFFIX);
                                        is80Icon = false;
                                    }
                                    
                                    imgbase64 = new Scanner(f).useDelimiter("\\Z").next();
                                    
                                    fileEntity.setIcon80Content(imgbase64);
                                    cacheFile.updateCacheFileDetail(fileEntity);
                                    if(!is80Icon)
                                    {
//                                        log.debug("khong phai icon 80");
                                        fileEntity.setIconContent(imgbase64);
                                    }
                                    
                                }
                                
                                imgbase64 = fileEntity.getIcon80Content();
                                
                            }
                            else
                            {
                                fileName += AIOConstants.DEFAUL_ICON_BASE64_SUFFIX;



                                if(fileEntity.getIconContent() == null)
                                {
                                   imgbase64 = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
                                   fileEntity.setIconContent(imgbase64);

                                    cacheFile.updateCacheFileDetail(fileEntity);
                                }

                                imgbase64 = fileEntity.getIconContent();
                            }
                            
                            
                        }
                        else
                        {
                            fileName += AIOConstants.DEFAUL_NORMAL_BASE64_FILE;
                            if(fileEntity.getContent() == null)
                            {
                               imgbase64 = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
                               fileEntity.setContent(imgbase64);
                    
                                cacheFile.updateCacheFileDetail(fileEntity);
                            }
                            
                            imgbase64 = fileEntity.getContent();
                            
                        }
                        
                       
                        StringBuilder sb = new StringBuilder();
                        
                        if(queryEntity.getResponse() instanceof SendFileIconResponse)
                        {
                            SendFileIconResponse fileIconRes = ((SendFileIconResponse)queryEntity.getResponse());
                            //check position of this session(don't send redundant file
                            
                            
                             sb.append(fileEntity.getAlbumId()).append(AIOConstants.SEPERATOR_BYTE_1);
                             
                             sb.append(fileEntity.getFileId()).append(AIOConstants.SEPERATOR_BYTE_1);
                             sb.append(imgbase64);
                             fileIconRes.value = sb.toString();
                        } else if(queryEntity.getResponse() instanceof GetSocialAvatarResponse)
                        {
                            sb.append(queryEntity.getUserId()).append(AIOConstants.SEPERATOR_BYTE_1);
                             sb.append(fileEntity.getFileId()).append(AIOConstants.SEPERATOR_BYTE_1);
                             sb.append(imgbase64);
                             ((GetSocialAvatarResponse)queryEntity.getResponse()).value = sb.toString();
                        }
                        
                        aSession.write(queryEntity.getResponse() );
                        aSession.setLastSendImage(System.currentTimeMillis());
                        
                    }
                    catch (ServerException ex) {
                        log.error(ex.getMessage(), ex);
                    }                    
                    catch (IOException ex)
                    {
                        log.error(ex.getMessage(), ex);
                    }
                    catch(Exception ex)
                    {
                        log.error(ex.getMessage(), ex);
                    }
                        
                }
            }
            catch(Exception ex)
            {
                log.error(ex.getMessage(), ex);
            }
//             log.debug("end execute send image to client job");
        }
        isInProgress = false;
    }
}
