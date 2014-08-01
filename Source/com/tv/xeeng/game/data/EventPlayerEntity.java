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
public class EventPlayerEntity implements Serializable {
    private UserEntity usrEntity;
    
    private String description;
    public long point;
    
    public EventPlayerEntity(UserEntity usrEntity, String description)
    {
        
    
        this.usrEntity = usrEntity;
        this.description = description;
    }

    /**
     * @return the usrEntity
     */
    public UserEntity getUsrEntity() {
        return usrEntity;
    }

    /**
     * @param usrEntity the usrEntity to set
     */
    public void setUsrEntity(UserEntity usrEntity) {
        this.usrEntity = usrEntity;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    
    
    

   
    
    
    
}
