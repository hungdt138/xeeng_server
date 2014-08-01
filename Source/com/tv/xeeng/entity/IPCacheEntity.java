package com.tv.xeeng.entity;

import java.util.ArrayList;
import java.util.List;

public class IPCacheEntity extends CacheEntity{
    private List<CacheEntity> lstInfoConnect = new ArrayList<CacheEntity>();
    public IPCacheEntity()
    {
        super();
    }

    /**
     * @return the lstIPConnect
     */
    public List<CacheEntity> getLstInfoConnect() {
        return lstInfoConnect;
    }
    
    public void addNewConnect(CacheEntity entity)
    {
        lstInfoConnect.add(entity);
    }
}
