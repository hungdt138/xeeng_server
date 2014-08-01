/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.databaseDriven;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.game.data.InventoryItemEntity;
import com.tv.xeeng.memcached.data.XEDataUtils;
import com.tv.xeeng.memcached.data.XEGlobalCache;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

/**
 *
 * @author ThangTD
 */
public class InventoryDB {
    private static Logger mLog = LoggerContext.getLoggerFactory().getLogger(InventoryDB.class);
    
    private static final String MEMCACHED_NAME = "InvetoryItems";
    
    public static List<InventoryItemEntity> getUserInventory(long uid) {
        String key = MEMCACHED_NAME + "-" + uid;
        Object raw = XEGlobalCache.getCache(key);
        List<InventoryItemEntity> itemsList = null;
        
        if (raw != null) {
            itemsList = (List<InventoryItemEntity>) raw;
        }
        
        if (itemsList == null) {
            itemsList = getUserInventoryFromDB(uid);
            
            if (itemsList != null)
                XEGlobalCache.setCache(key, itemsList, XEGlobalCache.TIMEOUT_2_MIN);
        }
        
        return itemsList;
    }
    
    private static List<InventoryItemEntity> getUserInventoryFromDB(long uid)  {
        try {
            String query = "{ call uspXEGetInventoryOfUser(?) }";
            Connection con = DBPoolConnection.getConnection();
            List<InventoryItemEntity> retList = null;
            CallableStatement cs = con.prepareCall(query);
            cs.setLong(1, uid);
            try {
                ResultSet rs = cs.executeQuery();
                if (rs != null) {
                    retList = new ArrayList<InventoryItemEntity>();
                    while (rs.next()) {
                        retList.add(new InventoryItemEntity(
                                rs.getLong("userId"), 
                                rs.getString("itemCode"), 
                                rs.getInt("quantity"), 
                                rs.getString("name"), 
                                rs.getString("description"),
                                rs.getBoolean("isUsable")));
                    }
                    rs.close();

                    return retList;
                }
            } catch (SQLException ex) {
                mLog.error(ex.getMessage());
            } finally {
                cs.close();
            }
        } catch (SQLException ex) {
            mLog.error(ex.getMessage());
        }
        
        return null;
    }
    
    public static void deleteCacheUserInventory(long uid) {
        String key = MEMCACHED_NAME + "-" + uid;
        XEGlobalCache.deleteCacheUserInventory(key);
    }
}
