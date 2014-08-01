/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import java.io.Serializable;

/**
 *
 * @author tuanda
 */
public class UploadAvatarEntity implements Serializable{
    private long albumId;
    private StringBuilder content;
    private int imageId;
    private int maxParts;
    private byte[] rawData;
    
    public UploadAvatarEntity()
    {
        content = new StringBuilder();
        rawData = new byte[0];
    }
    
    

    /**
     * @return the albumId
     */
    public long getAlbumId() {
        return albumId;
    }

    /**
     * @param albumId the albumId to set
     */
    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    /**
     * @return the content
     */
    public StringBuilder getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(StringBuilder content) {
        this.content = content;
    }

    /**
     * @return the imageId
     */
    public int getImageId() {
        return imageId;
    }

    /**
     * @param imageId the imageId to set
     */
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    /**
     * @return the maxParts
     */
    public int getMaxParts() {
        return maxParts;
    }

    /**
     * @param maxParts the maxParts to set
     */
    public void setMaxParts(int maxParts) {
        this.maxParts = maxParts;
    }

    /**
     * @return the rawData
     */
    public byte[] getRawData() {
        return rawData;
    }
    
    public void appendRawData(byte[] arrPart)
    {
        int oldLength = rawData.length;
        int addLength = arrPart.length;
        byte[] newRawData = new byte[oldLength + addLength];
        System.arraycopy(rawData, 0, newRawData, 0, oldLength);
        System.arraycopy(arrPart, 0, newRawData, oldLength, addLength);
        
        rawData = newRawData;
                
    }
    
    
    
}
