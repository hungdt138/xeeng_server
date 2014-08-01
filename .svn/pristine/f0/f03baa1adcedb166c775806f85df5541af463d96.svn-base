/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tuanda
 */
public class BlogEntity implements Serializable{
    private long blogId;
    private long userId;
    private String userName;
    private long avFileId;//it should be userEntity
    
    private String title;
    private String post;
    private Date updatedDate;
    private Date createdDate;
    private int viewCount;
    private int likeCount;
    private String subject;
    private int commentCount;
    private boolean more;
    private int socialRoleId;
    private int viewing = 0;
    
    public BlogEntity(long blogId, String title, String post)
    {
        this.blogId = blogId;
        this.title = title;
        this.post = post;
                
    }
    
    public BlogEntity(long blogId, long userId, String title, String post, Date updatedDate,
            int viewCount, int likeCount, int commentCount, String subject, boolean more, Date createdDate,
            int socialRoleId)
    {
        this.blogId = blogId;
        this.userId = userId;
        this.title = title;
        this.post = post;
        this.updatedDate = updatedDate;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.subject = subject;
        this.commentCount = commentCount;
        this.more = more;
        this.createdDate = createdDate;
        this.socialRoleId = socialRoleId;
    }
    public BlogEntity(long blogId, long userId, String title, String post, Date updatedDate,
            int viewCount, int likeCount, int commentCount, String subject, boolean more, Date createdDate)
    {
        this.blogId = blogId;
        this.userId = userId;
        this.title = title;
        this.post = post;
        this.updatedDate = updatedDate;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.subject = subject;
        this.commentCount = commentCount;
        this.more = more;
        this.createdDate = createdDate;
        
    
    }
    
    public BlogEntity()
    {
        
    }

    /**
     * @return the blogId
     */
    public long getBlogId() {
        return blogId;
    }

    /**
     * @param blogId the blogId to set
     */
    public void setBlogId(long blogId) {
        this.blogId = blogId;
    }

    /**
     * @return the userId
     */
    public long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the post
     */
    public String getPost() {
        return post;
    }

    /**
     * @param post the post to set
     */
    public void setPost(String post) {
        this.post = post;
    }

    /**
     * @return the updatedDate
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    /**
     * @param updatedDate the updatedDate to set
     */
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * @return the viewCount
     */
    public int getViewCount() {
        return viewCount;
    }

    /**
     * @param viewCount the viewCount to set
     */
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    /**
     * @return the likeCount
     */
    public int getLikeCount() {
        return likeCount;
    }

    /**
     * @param likeCount the likeCount to set
     */
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    

    /**
     * @return the commentCount
     */
    public int getCommentCount() {
        return commentCount;
    }

    /**
     * @param commentCount the commentCount to set
     */
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the more
     */
    public boolean isMore() {
        return more;
    }

    /**
     * @param more the more to set
     */
    public void setMore(boolean more) {
        this.more = more;
    }

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the socialRoleId
     */
    public int getSocialRoleId() {
        return socialRoleId;
    }

    /**
     * @param socialRoleId the socialRoleId to set
     */
    public void setSocialRoleId(int socialRoleId) {
        this.socialRoleId = socialRoleId;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the avFileId
     */
    public long getAvFileId() {
        return avFileId;
    }

    /**
     * @param avFileId the avFileId to set
     */
    public void setAvFileId(long avFileId) {
        this.avFileId = avFileId;
    }

    /**
     * @return the viewing
     */
    public int getViewing() {
        return viewing;
    }

    /**
     * @param viewing the viewing to set
     */
    public void setViewing(int viewing) {
        this.viewing = viewing;
    }
    
}
