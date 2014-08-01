package com.tv.xeeng.entity;

/**
 *
 * @author tuanda
 */
public class CacheEntity {
    private long dateCreated;
    
    public CacheEntity()
    {
       dateCreated = System.currentTimeMillis(); 
    }

    /**
     * @return the dateCreated
     */
    public long getDateCreated() {
        return dateCreated;
    }

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
