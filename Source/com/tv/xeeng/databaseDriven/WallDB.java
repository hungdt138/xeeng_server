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
import java.util.Date;
import java.util.List;

import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.AlbumEntity;
import com.tv.xeeng.game.data.AlertEntity;
import com.tv.xeeng.game.data.AuditFollowEntity;
import com.tv.xeeng.game.data.FileEntity;
import com.tv.xeeng.game.data.RateEntity;
import com.tv.xeeng.game.data.UserEntity;


/**
 *
 * @author tuanda
 */
public class WallDB {

    private static final String  MESSEAGE_PARAM = "message";
    private static final String  USER_ID_PARAM = "userId";
    private static final String  SOURCE_ID_PARAM = "sourceId";
    private static final String  DEST_ID_PARAM = "destId";
    private static final String  FILE_ID_PARAM = "fileId";
    private static final String  SYSTEM_OBJECT_RECORD_ID_PARAM = "systemObjectRecordId";
    private static final String  SYSTEM_OBJECT_ID_PARAM = "systemObjectId";
    private static final String  FOLLOW_ID_PARAM = "followId";
    
    private Connection conn;
    public WallDB(Connection conn)
    {
        this.conn = conn;
    }
    public WallDB()
    {
        
    }
    
     public void postWall( long sourceId, long destId,  String mind, long fileId ) throws SQLException
    {
        String query = "{call uspPostWall(?,?,?,?)} ";
        Connection conn = DBPoolConnection.getConnection();

        try {

             CallableStatement cs = conn.prepareCall(query);

             cs.setLong(SOURCE_ID_PARAM, sourceId);
             cs.setLong(DEST_ID_PARAM, destId);
             cs.setLong(FILE_ID_PARAM, fileId);
             cs.setString(MESSEAGE_PARAM, mind);
             
             cs.execute();
        }
        finally
        {
            conn.close();
        }
        
    }
     
    public List<AlertEntity> getWall(long userId) throws SQLException
    {
        List<AlertEntity> lstAlerts = new ArrayList<AlertEntity>();
        Connection con = DBPoolConnection.getConnection();
        
        
        String query = "{ call uspGetWall(?) }";
        try
        {
            CallableStatement cs = con.prepareCall(query);
            cs.setLong(USER_ID_PARAM, userId);
            
            
            ResultSet rs = cs.executeQuery();
            if(rs != null)
            {
                while(rs.next())
                {
                    long alertId = rs.getLong("alertId");
                    Date dtCreated = rs.getTimestamp("createdDate");
                    int alertTypeId = rs.getInt("alertTypeId");
                    String message = rs.getString("message");
                    String userName = rs.getString("name");
                    int systemObjectId = rs.getInt("systemObjectId");
                    long systemObjectRecordId = rs.getLong("systemObjectRecordId");
                    long actionUserId = rs.getLong("actionUserId");
                    long avatarFileId = rs.getLong("avatarFileId");
                    long commentCount = rs.getLong("commentCount");
                    long likeCount = rs.getLong("likeCount");
                    
                    UserEntity usrEntity = new UserEntity();
                    usrEntity.mUid = actionUserId;
                    usrEntity.mUsername = userName;
                    usrEntity.avFileId = avatarFileId;
                    
                    RateEntity rateEntity = new RateEntity();
                    rateEntity.setCommentCount(commentCount);
                    rateEntity.setLikeCount(likeCount);
                    
                    if(systemObjectId == AIOConstants.BLOG_SYSTEM_OBJECT_ID)
                    {
                        if(alertTypeId == AIOConstants.CREATE_BLOG_SYSTEM_OBJECT_ID)
                        {
                            message = "đăng bài viết '" + message + "'";
                        }
                        else if(alertTypeId == AIOConstants.MODIFY_BLOG_SYSTEM_OBJECT_ID )
                        {
                            message = "chỉnh sửa bài viết '" + message + "'";
                        }
                        
                    }
                    
                    
                    AlertEntity entity  = new AlertEntity(alertId, usrEntity, dtCreated, alertTypeId,
                            message, systemObjectId, systemObjectRecordId);
                    entity.setRateEntity(rateEntity);
                    lstAlerts.add(entity);
                    
                }
                
                rs.close();
                cs.close();
            }
        }
        finally
        {
            con.close();
        }
        
        
        
        return lstAlerts;
        
    }
    
    public long deleteItem(int systemObjectId, long systemObjectRecordId, long userId) throws SQLException
    {
        String query = "{call uspDeleteItem(?,?, ?)} ";
        Connection conn = DBPoolConnection.getConnection();
        long ret = -1;
        try {

             CallableStatement cs = conn.prepareCall(query);

             cs.setInt(SYSTEM_OBJECT_ID_PARAM, systemObjectId);
            
             cs.setLong(SYSTEM_OBJECT_RECORD_ID_PARAM, systemObjectRecordId);
             cs.setLong(USER_ID_PARAM, userId);
             
              ResultSet rs = cs.executeQuery();
              if(rs!= null && rs.next())
              {
                  ret = rs.getLong("UserId");
                  rs.close();
              }
              cs.close();
        }
        finally
        {
            conn.close();
        }
        
        return ret;
    }
    
    
    public void auditFollow( long userId, long followId) throws SQLException
    {
        String query = "{call uspAuditFollowUSer(?,?)} ";
        

        
             CallableStatement cs = conn.prepareCall(query);

             cs.setLong(USER_ID_PARAM, userId);
             cs.setLong(FOLLOW_ID_PARAM, followId);
             
             cs.execute();
        
    }
    
    
    public List<AuditFollowEntity> getfollow(long followId) throws SQLException
    {
        List<AuditFollowEntity> lstFollows = new ArrayList<AuditFollowEntity>();
        Connection con = DBPoolConnection.getConnection();
        
        
        String query = "{ call uspGetFollowUser(?) }";
        try
        {
            CallableStatement cs = con.prepareCall(query);
            cs.setLong(USER_ID_PARAM, followId);
            
            
            ResultSet rs = cs.executeQuery();
            if(rs != null)
            {
                while(rs.next())
                {
                    long userId = rs.getLong("userId");
                    String name = rs.getString("name");
                    Date dateFollow = rs.getTimestamp("dateFollow");
                    boolean sex = rs.getBoolean("sex");
                    long avatarFileId = rs.getLong("avatarFileId");
                   
                    AuditFollowEntity entity  = new AuditFollowEntity(userId, followId);
                    UserEntity userEntity = new UserEntity();
                    userEntity.mUid = userId;
                    userEntity.mUsername = name;
                    userEntity.avFileId = avatarFileId;
                    userEntity.mIsMale = sex;
                    entity.setFollowDate(dateFollow);
                    entity.setUser(userEntity);
                    
                    lstFollows.add(entity);
                }
                
                rs.close();
                cs.close();
            }
        }
        finally
        {
            con.close();
        }
        
        
        
        return lstFollows;
        
    }
    
    
    
    
    
    
}
