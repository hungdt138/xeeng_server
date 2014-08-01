/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

/**
 *
 * @author tuanda
 */
public class CacheEntity {
    private String namespace;
    private long value;
    private long key;
    
    public CacheEntity(String namespace, long value, long key)
    {
        this.namespace = namespace;
        this.value = value;
        this.key = key;
    }
    
    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
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
     * @return the key
     */
    public long getKey() {
        return key;
    }
    
    
    
}
