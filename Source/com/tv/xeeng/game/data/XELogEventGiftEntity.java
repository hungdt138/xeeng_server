/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.game.data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author ThangTD
 */
public class XELogEventGiftEntity implements Serializable {
    private long userId;
    private String evgfCode;
    private long value;
    private String type;
    private String message;
    private Timestamp useDate;
    private long fromValue;
    private long toValue;

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
     * @return the evgfCode
     */
    public String getEvgfCode() {
        return evgfCode;
    }

    /**
     * @param evgfCode the evgfCode to set
     */
    public void setEvgfCode(String evgfCode) {
        this.evgfCode = evgfCode;
    }

    /**
     * @return the value
     */
    public long getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(long value) {
        this.value = value;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the fromValueBefore
     */
    public long getFromValueBefore() {
        return fromValue;
    }

    /**
     * @param fromValueBefore the fromValueBefore to set
     */
    public void setFromValueBefore(long fromValueBefore) {
        this.fromValue = fromValueBefore;
    }

    /**
     * @return the toValueBefore
     */
    public long getToValueBefore() {
        return toValue;
    }

    /**
     * @param toValueBefore the toValueBefore to set
     */
    public void setToValueBefore(long toValueBefore) {
        this.toValue = toValueBefore;
    }

    /**
     * @return the useDate
     */
    public Timestamp getUseDate() {
        return useDate;
    }

    /**
     * @param useDate the useDate to set
     */
    public void setUseDate(Timestamp useDate) {
        this.useDate = useDate;
    }
}
