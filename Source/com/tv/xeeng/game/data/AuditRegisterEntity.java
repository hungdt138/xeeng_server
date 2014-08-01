/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import java.util.Date;

/**
 *
 * @author tuandavrus
 */
public class AuditRegisterEntity {
    private int count;
    private long lastDateTime;
    
    public AuditRegisterEntity(int count, long lastDate)
    {
        this.count = count;
        this.lastDateTime = lastDate;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the lastDateTime
     */
    public long getLastDateTime() {
        return lastDateTime;
    }

    /**
     * @param lastDateTime the lastDateTime to set
     */
    public void setLastDateTime(long lastDateTime) {
        this.lastDateTime = lastDateTime;
    }

    
    
}
