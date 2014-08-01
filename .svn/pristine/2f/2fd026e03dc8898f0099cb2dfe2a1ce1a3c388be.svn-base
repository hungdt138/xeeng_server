/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.memcached.data;

import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.databaseDriven.FileDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.FileEntity;
import com.tv.xeeng.game.data.TopFileEntity;
import com.tv.xeeng.memcached.IMemcacheClient;

/**
 *
 * @author tuanda
 */
public class CacheFileInfo extends CacheUserInfo {
//    private static final String FILE_NAME_SPACE = "file";
    private static final String FILE_DETAIL_NAME_SPACE = "fDetail";
    private static final int FILE_TIME_CACHE = 300;
    private static final int TOP_FILE_CACHE = 6000;
    private static final String TOP_FILE_NAME_SPACE = "topF";
    
    private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(CacheFileInfo.class);
    
     private  FileEntity loadFileFromDB(long fileId) throws SQLException
    {
        FileDB db = new FileDB();
        return db.getFile(fileId);
    }
    
    private  TopFileEntity getTopFileFromDB() throws SQLException
    {
        FileDB db = new FileDB();
        List<FileEntity> lstFiles = db.getTopFile();
        int size = lstFiles.size();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i< size; i++)
        {
            FileEntity entity = lstFiles.get(i);
            sb.append(Long.toString(entity.getUserEntity().mUid)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(entity.getUserEntity().mUsername).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Long.toString(entity.getFileId())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(entity.getRateEntity().getLikeCount()).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(entity.getRateEntity().getCommentCount()).append(AIOConstants.SEPERATOR_BYTE_2);
        }
        
        if(size>0)
        {
            sb.deleteCharAt(sb.length() -1);
        }
        
        TopFileEntity entity = new TopFileEntity(lstFiles, sb.toString());
        
        return entity;
    }
    
    
    public FileEntity getFileDetail(long fileId)
    {
        try
        {
            if(isUseCache)
            {
                IMemcacheClient client = cachedPool.borrowClient();
                FileEntity enity =  null;
                try
                {
                    String key = FILE_DETAIL_NAME_SPACE + Long.toString(fileId);
                    enity = (FileEntity)client.get(key);
                    if(enity == null || enity.getRateEntity() == null)
                    {
    //                    loadFromDatabase++;
                        enity = loadFileFromDB(fileId);
                        
                        
                        client.set(key, FILE_TIME_CACHE, enity);
                    }
//                    else
//                    {
//    //                    loadFromCache++;
//                        mLog.debug("use cache ");
//                    }
                }
                catch (SQLException ex) {
                    mLog.error(ex.getMessage(), ex);
                    
                }
                catch(Exception ex)
                {
                     mLog.error(ex.getMessage(), ex);
                }
                cachedPool.returnClient(client);
                return enity;

            }
        }
         
        catch(Exception ex)
        {
            mLog.error(ex.getMessage(), ex);
                    
        }
        
        try {
            return loadFileFromDB(fileId);
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        }
        
        
        return null;
    }
    
    public void deleteFileDetailCache(long fileId)
    {
        if(isUseCache)
        {
            IMemcacheClient client = cachedPool.borrowClient();
            try {
               
                
                String key = FILE_DETAIL_NAME_SPACE + Long.toString(fileId);
                
                client.delete(key);
                
            }
            finally
            {
                 cachedPool.returnClient(client);
            }
            
        }
    }
    
    
    
    public TopFileEntity getTopFile()
    {
        try
        {
            if(isUseCache)
            {
                IMemcacheClient client = cachedPool.borrowClient();
                TopFileEntity enity = null;
                try
                {
                    String key = TOP_FILE_NAME_SPACE;
                    enity = (TopFileEntity)client.get(key);
                    
                    if(enity == null)
                    {
    //                    loadFromDatabase++;
                        
                        enity = getTopFileFromDB();
                        client.set(key, TOP_FILE_CACHE, enity);
                        
                    }
                    
                    
                }
                catch (SQLException ex) {
                    mLog.error(ex.getMessage(), ex);
                }
                
                catch(Exception ex)
                {
                    mLog.error(ex.getMessage(), ex);
                }
                cachedPool.returnClient(client);
                return enity;

            }
        }
         
        catch(Exception ex)
        {
            mLog.error(ex.getMessage(), ex);
                    
        }
        
        try {
            return getTopFileFromDB();
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        }
        
        
        return null;
    }
    
    
    
    public void updateCacheFileDetail(FileEntity entity)
    {
        try
        {
            if(isUseCache)
            {
                String key = FILE_DETAIL_NAME_SPACE + Long.toString(entity.getFileId());
                IMemcacheClient client = cachedPool.borrowClient();
                client.set(key, FILE_TIME_CACHE, entity);
                cachedPool.returnClient(client);
            }
        }
         
        catch(Exception ex)
        {
            mLog.error(ex.getMessage(), ex);
                    
        }
        
        
    }
    
    
    
    
    
}
