/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.game.data;

import java.io.Serializable;

/**
 *
 * @author thangtd
 */
public class EventItemEntity implements Serializable {
    private String code;
    private String name;
    private String description;
    private float rate;
    private int type;
    private int quantity;
    private boolean isUsable;
    private String components;
    private long fee;
    private int limit;
    
    public EventItemEntity(String code, String name, String description, float rate, int type, int quantity, boolean isUsable, String components, long fee, int limit) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.rate = rate;
        this.type = type;
        this.quantity = quantity;
        this.isUsable = isUsable;
        this.components = components;
        this.fee = fee;
        this.limit = limit;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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

    /**
     * @return the rate
     */
    public float getRate() {
        return rate;
    }

    /**
     * @param rate the rate to set
     */
    public void setRate(float rate) {
        this.rate = rate;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the isUsable
     */
    public boolean isIsUsable() {
        return isUsable;
    }

    /**
     * @param isUsable the isUsable to set
     */
    public void setIsUsable(boolean isUsable) {
        this.isUsable = isUsable;
    }

    /**
     * @return the components
     */
    public String getComponents() {
        return components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents(String components) {
        this.components = components;
    }

    /**
     * @return the fee
     */
    public long getFee() {
        return fee;
    }

    /**
     * @param fee the fee to set
     */
    public void setFee(long fee) {
        this.fee = fee;
    }

    /**
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }
}