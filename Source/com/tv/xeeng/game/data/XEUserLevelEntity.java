/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.game.data;

import com.tv.xeeng.memcached.data.XEDataUtils;

/**
 *
 * @author thanhnvt
 */
public class XEUserLevelEntity {
    private int id;
    private String name;
    private long minGold;
    private long maxGold;
    
        @Override
    public String toString() {
            return XEDataUtils.serializeParams(id, name, minGold, maxGold);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMinGold() {
        return minGold;
    }

    public void setMinGold(long minGold) {
        this.minGold = minGold;
    }

    public long getMaxGold() {
        return maxGold;
    }

    public void setMaxGold(long maxGold) {
        this.maxGold = maxGold;
    }
}
