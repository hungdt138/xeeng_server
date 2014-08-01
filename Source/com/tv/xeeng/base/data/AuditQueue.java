/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.databaseDriven.BlogDB;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.databaseDriven.WallDB;
import com.tv.xeeng.game.data.AuditFollowEntity;
import com.tv.xeeng.game.data.BlogEntity;

/**
 *
 * @author tuanda
 */
public class AuditQueue implements Job{
//    private static Queue imgQueue = new ArrayDeque();
    private static ConcurrentHashMap<UUID, Object> imgQueue = new ConcurrentHashMap<UUID, Object>();
    
    private static Logger log = LoggerContext.getLoggerFactory()
			.getLogger(AuditQueue.class);
    
    private static boolean isInProgress = false;
//    private static final Object lockObj = new Object();
    
    
    public static void insert(Object entity)
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
    
    public static void insertBlog(BlogEntity entity)
    {
        try
        {
//            imgQueue.add(entity);
            Enumeration<UUID> e = imgQueue.keys();
                 
            boolean found = false;     
            while(e.hasMoreElements())
            {
                Object queryEntity = null;
                UUID key = e.nextElement();
                queryEntity = imgQueue.get(key);
                if(queryEntity instanceof BlogEntity)
                {
                    BlogEntity blogEntity = (BlogEntity)queryEntity;
                    if(blogEntity.getBlogId() == entity.getBlogId())
                    {
                        found = true;
                        blogEntity.setViewing(blogEntity.getViewing() + 1);
                        break;
                    }
                }

            }
                        
            if(!found)
            {
                UUID uuid = UUID.randomUUID();
                imgQueue.put(uuid, entity);
            }
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
            Connection conn = null;
            
            try
            {
                
                
                 Enumeration<UUID> e = imgQueue.keys();
                 
                 
                while(e.hasMoreElements())
                {
                    if(conn == null)
                    {
                        conn = DBPoolConnection.getConnection();
                        
                    }
                    
                    try
                    {
                        Object queryEntity = null;
                        UUID key = e.nextElement();
                        
                        queryEntity = imgQueue.get(key);    
                        imgQueue.remove(key);
                        
                       
                        
                        
                        if(queryEntity instanceof AuditFollowEntity)
                        {
                            AuditFollowEntity followEntity = (AuditFollowEntity)queryEntity;
                            WallDB db = new WallDB(conn);
                            db.auditFollow(followEntity.getUserId(), followEntity.getFollowId());
                        } 
                        else if(queryEntity instanceof BlogEntity)
                        {
                            BlogEntity blogEntity = (BlogEntity)queryEntity;
                            BlogDB db = new BlogDB(conn);
                            db.updateBlog(blogEntity.getBlogId(), blogEntity.getTitle(),
                                    blogEntity.getSubject(), blogEntity.getPost(), blogEntity.getViewing());
                            
                        }
                            
                        
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
            finally
            {
                if(conn!= null)
                {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        log.error(ex.getMessage(), ex);
                    }
                }
            }
//             log.debug("end execute send image to client job");
        }
        isInProgress = false;

        
        
            
       
            
    }
    
}
