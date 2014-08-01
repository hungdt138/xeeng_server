/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;
import java.sql.CallableStatement;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tv.xeeng.game.data.CommentEntity;

/**
 *
 * @author tuanda
 */
public class CommentDB {
    private static final String  USER_ID_PARAM = "userId";
    
    
    private static final String  CONTENT_PARAM = "content";
    private static final String  SYSTEM_OBJECT_RECORD_ID_PARAM = "systemObjectRecordId";
    private static final String  SYSTEM_OBJECT_ID_PARAM = "systemObjectId";
    
    private static final String  COMMENT_ID_PARAM = "commentId";
    
    public void addComment(long userId, String comment, long objectId, int systemObjectId) throws SQLException
    {
        String query = "{call uspInsertComment(?,?, ?, ?)} ";
        Connection conn = DBPoolConnection.getConnection();

        try {

             CallableStatement cs = conn.prepareCall(query);
             
             cs.setLong(USER_ID_PARAM, userId);
             cs.setString(CONTENT_PARAM, comment);
             cs.setLong(SYSTEM_OBJECT_RECORD_ID_PARAM, objectId);
             cs.setInt(SYSTEM_OBJECT_ID_PARAM, systemObjectId);
             cs.execute();
        }
        finally
        {
            conn.close();
        }
        
    }
    
    
    public void updateComment(String comment, long commentId) throws SQLException
    {
        String query = "{call uspUpdateComment(?,?)} ";
        Connection conn = DBPoolConnection.getConnection();

        try {

             CallableStatement cs = conn.prepareCall(query);
             
             
             cs.setString(CONTENT_PARAM, comment);
             cs.setLong(COMMENT_ID_PARAM, commentId );
             
             cs.execute();
        }
        finally
        {
            conn.close();
        }
        
    }
    
    public List<CommentEntity> getComment(int systemObjectId, long systemObjectRecordId) throws SQLException
    {
        List<CommentEntity> lstComments = new ArrayList<CommentEntity>();
        Connection con = DBPoolConnection.getConnection();
        
        
        String query = "{ call uspGetComment(?,?) }";
        try
        {
            CallableStatement cs = con.prepareCall(query);
            
            cs.setInt(SYSTEM_OBJECT_ID_PARAM, systemObjectId);
            cs.setLong(SYSTEM_OBJECT_RECORD_ID_PARAM, systemObjectRecordId);
            
            ResultSet rs = cs.executeQuery();
            if(rs != null)
            {
                while(rs.next())
                {
                    
                    String content = rs.getString("content");
                    
                    long commentId = rs.getLong("commentId");
                    long userId = rs.getLong("userId");
                    String userName = rs.getString("name");
                    long avatarFileId = rs.getLong("avatarFileId");
                    
                            
                    
                    Date updatedDate = rs.getTimestamp("updatedDate");
                    Date createdDate = rs.getTimestamp("createdDate");
                    CommentEntity entity  = new CommentEntity(commentId, content, 
                            userId, userName, avatarFileId, createdDate, updatedDate, systemObjectId, systemObjectRecordId);
                    lstComments.add(entity);
                    
                }
                
                
                rs.close();
                cs.close();
            }
        }
        finally
        {
            con.close();
        }
        
        return lstComments;
    }
    
    
    public void addLike(long userId, long objectId, int systemObjectId) throws SQLException
    {
        String query = "{call uspInsertLike(?, ?, ?)} ";
        Connection conn = DBPoolConnection.getConnection();

        try {

             CallableStatement cs = conn.prepareCall(query);
             
             cs.setLong(USER_ID_PARAM, userId);
             
             cs.setLong(SYSTEM_OBJECT_RECORD_ID_PARAM, objectId);
             cs.setInt(SYSTEM_OBJECT_ID_PARAM, systemObjectId);
             cs.execute();
        }
        finally
        {
            conn.close();
        }
        
    }
}
