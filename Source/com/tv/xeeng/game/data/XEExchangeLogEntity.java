/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.game.data;

/**
 *
 * @author yeuchimse
 */
public class XEExchangeLogEntity {
    private long userId;
    private long fromValue;
    private String fromType;
    private long toValue;
    private String toType;
    private String message;
    private long fromValueBefore;
    private long fromValueAfter;
    private long toValueBefore;
    private long toValueAfter;
    private int shopId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getFromValue() {
        return fromValue;
    }

    public void setFromValue(long fromValue) {
        this.fromValue = fromValue;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public long getToValue() {
        return toValue;
    }

    public void setToValue(long toValue) {
        this.toValue = toValue;
    }

    public String getToType() {
        return toType;
    }

    public void setToType(String toType) {
        this.toType = toType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getFromValueBefore() {
        return fromValueBefore;
    }

    public void setFromValueBefore(long fromValueBefore) {
        this.fromValueBefore = fromValueBefore;
    }

    public long getFromValueAfter() {
        return fromValueAfter;
    }

    public void setFromValueAfter(long fromValueAfter) {
        this.fromValueAfter = fromValueAfter;
    }

    public long getToValueBefore() {
        return toValueBefore;
    }

    public void setToValueBefore(long toValueBefore) {
        this.toValueBefore = toValueBefore;
    }

    public long getToValueAfter() {
        return toValueAfter;
    }

    public void setToValueAfter(long toValueAfter) {
        this.toValueAfter = toValueAfter;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }
}
