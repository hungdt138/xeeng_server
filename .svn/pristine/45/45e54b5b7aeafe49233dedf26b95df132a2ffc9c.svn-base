/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import java.util.UUID;

import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class QueueImageEntity {
    private long fileId;
    private boolean isIcon;
    private long userId;
    
    private UUID requestImgId;
    private long requestTime;
    
    private ISession session;
    private IResponseMessage response;
//    private int albumIndex;
    private FileEntity fileEntity;
    
    public QueueImageEntity(long fileId, boolean isIcon, ISession session, 
            IResponseMessage response)
    {
        this.fileId = fileId;
        this.isIcon = isIcon;
        this.session = session;
        this.response = response;
        
//        this.albumIndex = albumIndex;
                
    }
    
    public QueueImageEntity()
    {
        
    }

    /**
     * @return the fileId
     */
    public long getFileId() {
        return fileId;
    }

    /**
     * @param fileId the fileId to set
     */
    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    /**
     * @return the isIcon
     */
    public boolean isIsIcon() {
        return isIcon;
    }

    /**
     * @param isIcon the isIcon to set
     */
    public void setIsIcon(boolean isIcon) {
        this.isIcon = isIcon;
    }

    /**
     * @return the session
     */
    public ISession getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(ISession session) {
        this.session = session;
    }

    /**
     * @return the response
     */
    public IResponseMessage getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(IResponseMessage response) {
        this.response = response;
    }

//    /**
//     * @return the albumIndex
//     */
//    public int getAlbumIndex() {
//        return albumIndex;
//    }
//
//    /**
//     * @param albumIndex the albumIndex to set
//     */
//    public void setAlbumIndex(int albumIndex) {
//        this.albumIndex = albumIndex;
//    }

    /**
     * @return the fileEntity
     */
    public FileEntity getFileEntity() {
        return fileEntity;
    }

    /**
     * @param fileEntity the fileEntity to set
     */
    public void setFileEntity(FileEntity fileEntity) {
        this.fileEntity = fileEntity;
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
     * @return the requestImgId
     */
    public UUID getRequestImgId() {
        return requestImgId;
    }

    /**
     * @param requestImgId the requestImgId to set
     */
    public void setRequestImgId(UUID requestImgId) {
        this.requestImgId = requestImgId;
    }

    /**
     * @return the requestTime
     */
    public long getRequestTime() {
        return requestTime;
    }

    /**
     * @param requestTime the requestTime to set
     */
    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }


    
    
}
