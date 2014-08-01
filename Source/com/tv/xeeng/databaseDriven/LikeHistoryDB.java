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
import com.tv.xeeng.game.data.LikeHistoryEntity;

/**
 *
 * @author tuanda
 */
public class LikeHistoryDB {
    private static final String  USER_ID_PARAM = "userId";
    private static final String  BLOG_ID_PARAM = "blogId";
    
    private static final String  CONTENT_PARAM = "content";
    private static final String  SYSTEM_OBJECT_RECORD_ID_PARAM = "systemObjectRecordId";
    private static final String  SYSTEM_OBJECT_ID_PARAM = "systemObjectId";
    
    private static final int BLOG_SYSTEM_OBJECT_ID =1;
    private static final String  PAGE_INDEX_PARAM = "pageIndex";
    
    
    
    public int insertLike(long userId, long sysObjRecId, int systemObjectId) throws SQLException
    {
        String query = "{?= call uspInsertLike(?, ?, ?)} ";
        Connection conn = DBPoolConnection.getConnection();
        int ret = 0;
        try {

             CallableStatement cs = conn.prepareCall(query);
             cs.registerOutParameter(1, Types.INTEGER);
             cs.setLong(USER_ID_PARAM, userId);
             
             cs.setLong(SYSTEM_OBJECT_RECORD_ID_PARAM, sysObjRecId);
             cs.setInt(SYSTEM_OBJECT_ID_PARAM, systemObjectId);
             cs.execute();
             ret = cs.getInt(1);
        }
        finally
        {
            conn.close();
        }
        
        return ret;
        
    }
    
    public List<LikeHistoryEntity> getLike(int systemObjectId, long systemObjectRecordId) throws SQLException
    {
        List<LikeHistoryEntity> lstLks = new ArrayList<LikeHistoryEntity>();
        Connection con = DBPoolConnection.getConnection();
        
        
        String query = "{ call uspGetLike(?,?) }";
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
                    
                    
                    
                    long likeHistoryId = rs.getLong("likeHistoryId");
                    long userId = rs.getLong("userId");
                    String userName = rs.getString("name");
                    Date createdDate = rs.getTimestamp("createdDate");
                    LikeHistoryEntity entity  = new LikeHistoryEntity(likeHistoryId, userId, userName, 
                            systemObjectId, systemObjectRecordId, createdDate);
                    lstLks.add(entity);
                    
                }
                
                
                rs.close();
                cs.close();
            }
        }
        finally
        {
            con.close();
        }
        
        return lstLks;
    }
    
    
    
    
    
}
