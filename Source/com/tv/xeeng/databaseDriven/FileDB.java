/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tv.xeeng.game.data.FileEntity;
import com.tv.xeeng.game.data.RateEntity;
import com.tv.xeeng.game.data.UserEntity;


/**
 *
 * @author tuanda
 */
public class FileDB {
    private static final String  FILE_ID_PARAM = "fileId";
    private static final String  FILE_NAME_PARAM = "fileName";
    private static final String  ALBUM_ID_PARAM = "albumId";
    private static final String  LOCATION_PARAM = "location";
    private static final String  USER_ID_PARAM = "userId";
    
    private Connection conn;
    public FileDB(Connection conn)
    {
        this.conn = conn;
    }
    public FileDB()
    {
        
    }
    
     public long insertFile(String fileName, long userId, 
            long albumId, String location) throws SQLException
    {
        String query = "{call uspInsertFile(?,?,?,?)} ";
        Connection conn = DBPoolConnection.getConnection();
        long fileId = 0;
        try {

             CallableStatement cs = conn.prepareCall(query);

             cs.setLong(USER_ID_PARAM, userId);
            
             cs.setString(FILE_NAME_PARAM, fileName);
             cs.setLong(ALBUM_ID_PARAM, albumId);
             cs.setString(LOCATION_PARAM, location);
             ResultSet rs =  cs.executeQuery();
             if(rs != null && rs.next())
              {
                 fileId =  rs.getLong("fileId");
                 rs.close();
              }
             cs.close();
        }
        finally
        {
            conn.close();
        }
        
        return fileId;
        
    }
     
    public FileEntity getFile(long fileId) throws SQLException
    {
        String query = "{call uspGetFile(?)} ";
        Connection con = null;
        if(conn == null)
            con= DBPoolConnection.getConnection(); //create new connection
        else
            con = conn;
        FileEntity entity = null;
        try {

             CallableStatement cs = con.prepareCall(query);

             cs.setLong(FILE_ID_PARAM, fileId);
             
             
             ResultSet rs =  cs.executeQuery();
             if(rs != null && rs.next())
             {
                 long albumId = rs.getLong("albumId");
                 String location = rs.getString("location");
                 String fileName = rs.getString("fileName");
                 entity = new FileEntity(fileId, fileName, albumId, location);
                 
                 
                 long viewCount = rs.getLong("viewCount");
                 long likeCount = rs.getLong("likeCount");
                 long commentCount = rs.getLong("commentCount");
                 RateEntity rateEntity = new RateEntity();
                 rateEntity.setCommentCount(commentCount);
                 rateEntity.setViewCount(viewCount);
                 rateEntity.setLikeCount(likeCount);
                 entity.setRateEntity(rateEntity);
                 
                 rs.close();;
             }
             cs.close();
        }
        finally
        {
            if(conn == null) 
                con.close(); //close connection 
        }
        
        return entity;
        
    }
    
    public List<FileEntity> getTopFile() throws SQLException
    {
        String query = "{call uspTopFile()} ";
        Connection con = null;
        List<FileEntity> lstResults = new ArrayList<FileEntity>();
        if(conn == null)
            con= DBPoolConnection.getConnection(); //create new connection
        else
            con = conn;
         
        try {

             CallableStatement cs = con.prepareCall(query);

             ResultSet rs =  cs.executeQuery();
             if(rs != null)
             {
                 while(rs.next())
                 {
                     long albumId = rs.getLong("albumId");
                     String location = rs.getString("location");
                     String fileName = rs.getString("fileName");
                     long fileId = rs.getLong("fileId");
                     FileEntity entity = new FileEntity(fileId, fileName, albumId, location);
                     

                     long viewCount = rs.getLong("viewCount");
                     long likeCount = rs.getLong("likeCount");
                     long commentCount = rs.getLong("commentCount");
                     RateEntity rateEntity = new RateEntity();
                     rateEntity.setCommentCount(commentCount);
                     rateEntity.setViewCount(viewCount);
                     rateEntity.setLikeCount(likeCount);
                     entity.setRateEntity(rateEntity);
                     
                     long userId = rs.getLong("userId");
                     String userName = rs.getString("name");
                     UserEntity usrEntity = new UserEntity();
                     usrEntity.mUid = userId;
                     usrEntity.mUsername = userName;
                     
                     entity.setUserEntity(usrEntity);
                     
                     lstResults.add(entity);
                 }
                 rs.close();;
             }
             cs.close();
        }
        finally
        {
            if(conn == null) 
                con.close(); //close connection 
        }
        
        return lstResults;
        
    }
    
    
    
    
    
    
    
}
