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

import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.BlogEntity;


/**
 *
 * @author tuanda
 */
public class BlogDB {
    private static final String  USER_ID_PARAM = "userId";
    private static final String  REQUEST_USER_ID_PARAM = "requestUid";
    private static final String  BLOG_ID_PARAM = "blogId";
    private static final String  PAGE_INDEX_PARAM = "pageIndex";
    
    private static final String  TITLE_PARAM = "title";
    private static final String  SUBJECT_PARAM = "subject";
    private static final String  POST_PARAM = "post";
    private static final String  VIEWED_PARAM = "viewed";
    private Connection con = null;
    public BlogDB()
    {
        
    }
    
    public BlogDB(Connection con)
    {
        this.con = con;
    }
    
    public long newBlog(long userId, String title, String post) throws SQLException
    {
        String query = "{call uspCreateBlog(?,?,?)} ";
        Connection conn = null;
        
        conn = DBPoolConnection.getConnection();
        long blogId = 0;
        try {

             CallableStatement cs = conn.prepareCall(query);
             
             cs.setLong(USER_ID_PARAM, userId);
             cs.setString(TITLE_PARAM, title);
             cs.setString(POST_PARAM, post);
             ResultSet rs = cs.executeQuery();
            
             if(rs != null && rs.next())
             {
                 blogId = rs.getLong("blogId");
             }
             rs.close();
             cs.close();
        }
        finally
        {
            
                conn.close();
        }
        
        return blogId;
        
    }
    
    
    public void updateBlog(long blogId, String title, String subject, String post, int viewed) throws SQLException
    {
        String query = "{call uspUpdateBlog(?,?,?, ?, ?)} ";
        
        Connection conn = null;
        if(con != null)
            conn = con;
        else
            conn    = DBPoolConnection.getConnection();

        try {

             CallableStatement cs = conn.prepareCall(query);

             cs.setLong(BLOG_ID_PARAM, blogId);
             cs.setString(TITLE_PARAM, title);
             cs.setString(SUBJECT_PARAM, subject);
             cs.setString(POST_PARAM, post);
             cs.setInt(VIEWED_PARAM, viewed);
             cs.execute();
        }
        finally
        {
            if(con == null) //init new connection
            conn.close();
        }
        
    }
    
    
    
    
    public List<BlogEntity> getFriendsBlog(long userId, int pageIndex, int[] numBlogs) throws SQLException
    {
        List<BlogEntity> lstBlogs = new ArrayList<BlogEntity>();
        Connection con = DBPoolConnection.getConnection();
        
        
        String query = "{ call uspGetFriendBlogs(?,?,?) }";
        try
        {
            CallableStatement cs = con.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong(USER_ID_PARAM, userId);
            cs.setInt(PAGE_INDEX_PARAM, pageIndex);
            
            ResultSet rs = cs.executeQuery();
            if(rs != null)
            {
                while(rs.next())
                {
                    long blogId = rs.getLong("blogId");
                    long friendId = rs.getLong("userId");
                    String title = rs.getString("title");
                    String post = rs.getString("post");
                    String mainIdea = post.substring(0, AIOConstants.MAX_BLOG_MAIN_IDEA);
                    int viewCount = rs.getInt("viewCount");
                    int likeCount = rs.getInt("likeCount");
                    boolean isMore = post.length()>mainIdea.length();
                    int commentCount = rs.getInt("commentCount");
                    int socialRoleId = rs.getInt("socialRoleId");
                    Date dt = rs.getTimestamp("updatedDate");
                    Date dtCreated = rs.getTimestamp("createdDate");
                    
                    BlogEntity entity  = new BlogEntity(blogId, friendId, title, post, dt,
                                viewCount, likeCount, commentCount, mainIdea, isMore, dtCreated, socialRoleId);
                    
                    lstBlogs.add(entity);
                    
                }
                
                rs.close();
                cs.close();
            }
        }
        finally
        {
            con.close();
        }
        
        return lstBlogs;
    }
    
    public BlogEntity getBlogDetail(long blogId) throws SQLException
    {
        
        BlogEntity entity = new BlogEntity();
        Connection con = DBPoolConnection.getConnection();
        
        String query = "{ call uspGetBlog(?) }";
        try
        {
            CallableStatement cs = con.prepareCall(query);
            
            cs.setLong(BLOG_ID_PARAM, blogId);
            
            
            ResultSet rs = cs.executeQuery();
            if(rs != null)
            {
                if(rs.next())
                {
                    
                    String title = rs.getString("title");
                    String post = rs.getString("post");
                    
                    entity.setBlogId(blogId);
                    entity.setTitle(title);
                    entity.setPost(post);
                    
                    
                }
                
                
                rs.close();
                cs.close();
            }
        }
        finally
        {
            con.close();
        }
        
        return entity;
    }
    
    
    public List<BlogEntity> getBlogAccount(long userId) throws SQLException
    {
        List<BlogEntity> lstBlogs = new ArrayList<BlogEntity>();
        Connection con = DBPoolConnection.getConnection();
        
        
        String query = "{ call uspGetBlogAccount(?) }";
        try
        {
            CallableStatement cs = con.prepareCall(query);
            
            cs.setLong(USER_ID_PARAM, userId);
            
            
            ResultSet rs = cs.executeQuery();
            if(rs != null)
            {
                while(rs.next())
                {
                    long blogId = rs.getLong("blogId");
                    long friendId = rs.getLong("userId");
                    String title = rs.getString("title");
                    String post = rs.getString("post");
                    int subjectLen = post.length();
                    if(subjectLen> AIOConstants.MAX_BLOG_MAIN_IDEA)
                    {
                        subjectLen =AIOConstants.MAX_BLOG_MAIN_IDEA;
                    } 
                    String mainIdea = post.substring(0, subjectLen);
                    int viewCount = rs.getInt("viewCount");
                    int likeCount = rs.getInt("likeCount");
                    boolean isMore = post.length()>mainIdea.length();
                    int commentCount = rs.getInt("commentCount");
                    int socialRoleId = rs.getInt("socialRoleId");
                    Date dt = rs.getTimestamp("updatedDate");
                    Date dtCreated = rs.getTimestamp("createdDate");
                    
                    BlogEntity entity  = new BlogEntity(blogId, friendId, title, post, dt,
                                viewCount, likeCount, commentCount, mainIdea, isMore, dtCreated, socialRoleId);
                    
                    lstBlogs.add(entity);
                    
                }
                
                
                rs.close();
                cs.close();
            }
        }
        finally
        {
            con.close();
        }
        
        return lstBlogs;
    }
    
    
    public List<BlogEntity> getTopBlog() throws SQLException
    {
        List<BlogEntity> lstBlogs = new ArrayList<BlogEntity>();
        Connection con = DBPoolConnection.getConnection();
        
        
        String query = "{ call uspTopBlog() }";
        try
        {
            CallableStatement cs = con.prepareCall(query);
            
            ResultSet rs = cs.executeQuery();
            if(rs != null)
            {
                while(rs.next())
                {
                    long blogId = rs.getLong("blogId");
                    
                    long userId = rs.getLong("userId");
                    String userName = rs.getString("name");
                    long avFileId = rs.getLong("avatarFileId");
                    
                    String title = rs.getString("title");
                    String post = rs.getString("post");
                    int subjectLen = post.length();
                    if(subjectLen> AIOConstants.MAX_BLOG_MAIN_IDEA)
                    {
                        subjectLen =AIOConstants.MAX_BLOG_MAIN_IDEA;
                    } 
                    String mainIdea = post.substring(0, subjectLen);
                    int viewCount = rs.getInt("viewCount");
                    int likeCount = rs.getInt("likeCount");
                    boolean isMore = post.length()>mainIdea.length();
                    int commentCount = rs.getInt("commentCount");
//                    int socialRoleId = rs.getInt("socialRoleId");
                    Date dt = rs.getTimestamp("updatedDate");
                    Date dtCreated = rs.getTimestamp("createdDate");
                    
                    BlogEntity entity  = new BlogEntity(blogId, userId, title, post, dt,
                                viewCount, likeCount, commentCount, mainIdea, isMore, dtCreated);
                    
                    entity.setUserName(userName);
                    entity.setAvFileId(avFileId);
                    lstBlogs.add(entity);
                    
                }
                
                
                rs.close();
                cs.close();
            }
        }
        finally
        {
            con.close();
        }
        
        return lstBlogs;
    }
    
    
    public List<BlogEntity> getNewestBlog() throws SQLException
    {
        List<BlogEntity> lstBlogs = new ArrayList<BlogEntity>();
        Connection con = DBPoolConnection.getConnection();
        
        
        String query = "{ call uspGetNewestBlog() }";
        try
        {
            CallableStatement cs = con.prepareCall(query);
            
            ResultSet rs = cs.executeQuery();
            if(rs != null)
            {
                while(rs.next())
                {
                    long blogId = rs.getLong("blogId");
                    
                    long userId = rs.getLong("userId");
                    String userName = rs.getString("name");
                    long avFileId = rs.getLong("avatarFileId");
                    
                    String title = rs.getString("title");
                    String post = rs.getString("post");
                    int subjectLen = post.length();
                    if(subjectLen> AIOConstants.MAX_BLOG_MAIN_IDEA)
                    {
                        subjectLen =AIOConstants.MAX_BLOG_MAIN_IDEA;
                    } 
                    String mainIdea = post.substring(0, subjectLen);
                    int viewCount = rs.getInt("viewCount");
                    int likeCount = rs.getInt("likeCount");
                    boolean isMore = post.length()>mainIdea.length();
                    int commentCount = rs.getInt("commentCount");
//                    int socialRoleId = rs.getInt("socialRoleId");
                    Date dt = rs.getTimestamp("updatedDate");
                    Date dtCreated = rs.getTimestamp("createdDate");
                    
                    BlogEntity entity  = new BlogEntity(blogId, userId, title, post, dt,
                                viewCount, likeCount, commentCount, mainIdea, isMore, dtCreated);
                    
                    entity.setUserName(userName);
                    entity.setAvFileId(avFileId);
                    lstBlogs.add(entity);
                    
                }
                
                
                rs.close();
                cs.close();
            }
        }
        finally
        {
            con.close();
        }
        
        return lstBlogs;
    }
    
}
