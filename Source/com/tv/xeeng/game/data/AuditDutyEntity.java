/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tuandavrus
 */
public class AuditDutyEntity implements Serializable{
    private UserEntity usrEntity; // nguoi follow
//    private String userName;
    private Date auditDate;
    private int bonusMoney; // nguoi bi follow
    
    public AuditDutyEntity()
    {
        
    }
    
    public AuditDutyEntity(UserEntity entity, Date auditDate, int bonusMoney)
    {
        this.usrEntity = entity;
        this.auditDate = auditDate;
        this.bonusMoney = bonusMoney;
        
    }

    
    /**
     * @return the auditDate
     */
    public Date getAuditDate() {
        return auditDate;
    }

    /**
     * @param auditDate the auditDate to set
     */
    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    /**
     * @return the bonusMoney
     */
    public int getBonusMoney() {
        return bonusMoney;
    }

    /**
     * @param bonusMoney the bonusMoney to set
     */
    public void setBonusMoney(int bonusMoney) {
        this.bonusMoney = bonusMoney;
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

    

    

}
