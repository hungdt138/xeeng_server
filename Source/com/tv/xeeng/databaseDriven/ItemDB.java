/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;
import java.sql.CallableStatement;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tv.xeeng.game.data.CommentEntity;
import com.tv.xeeng.game.data.ItemEntity;
import com.tv.xeeng.game.data.UserEntity;

/**
 *
 * @author tuanda
 */
public class ItemDB {
    private static final String USER_ID_PARAM = "userId";
    private static final String ITEM_ID_PARAM = "itemId";
    private static final String BONUS_MONEY_PARAM = "bonusMoney";
    private static final String BONUS_TIMES_QUAY_PARAM = "bonusTimesQuay";
    
    private static List<ItemEntity> items;
    
    public static void reload()
    {
        if(items == null)
        {
            try {
                items = loadItems();
            } catch (SQLException ex) {
                Logger.getLogger(ItemDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static List<ItemEntity> getItems()
    {
        return items;
    }
    
    private static  List<ItemEntity> loadItems() throws SQLException
    {
        List<ItemEntity> lstItems = new ArrayList<ItemEntity>();
        Connection con = DBPoolConnection.getConnection();
        
        
        String query = "{ call uspGetItem() }";
        try
        {
            CallableStatement cs = con.prepareCall(query);
            
            ResultSet rs = cs.executeQuery();
            if(rs != null)
            {
                while(rs.next())
                {
                    int itemId = rs.getInt("itemId");
                    
                    
                    String name = rs.getString("name");
                    int type = rs.getInt("type");
                    int price = rs.getInt("price");
                    int categoryItem = rs.getInt("categoryItem");
                    
                    ItemEntity entity = new ItemEntity(itemId, name, price, categoryItem, type);
                    lstItems.add(entity);
                    
                }
                
                
                rs.close();
                cs.close();
            }
        }
        finally
        {
            con.close();
        }
        
        return lstItems;
    }
    
    public int buyItem(long userId, int itemId) throws SQLException
    {
        Connection con = DBPoolConnection.getConnection();
        int ret = -1;
        
        String query = "{?=  call uspBuyItem(?, ?) }";
        try
        {
            CallableStatement cs = con.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong(USER_ID_PARAM, userId);
            cs.setInt(ITEM_ID_PARAM, itemId);
            cs.execute();
            
            ret = cs.getInt(1);
        }
        finally
        {
            con.close();
        }
                    
        return ret;    
    }
    
    
    public UserEntity quayCNKD(long userId, int bonusMoney, int bonusTimes) throws SQLException
    {
        Connection con = DBPoolConnection.getConnection();
        UserEntity entity = new UserEntity();
        entity.timesQuay = -1;
        
        
        String query = "{call uspQuayAudit(?, ?, ?) }";
        try
        {
            CallableStatement cs = con.prepareCall(query);
            
            cs.setLong(USER_ID_PARAM, userId);
            cs.setInt(BONUS_MONEY_PARAM, bonusMoney);
            cs.setInt(BONUS_TIMES_QUAY_PARAM, bonusTimes);
            ResultSet rs = cs.executeQuery();
            if(rs != null)
            {
                if(rs.next())
                {
                    int timesQuay = rs.getInt("timesQuay");
                    
                    
                    long cash = rs.getLong("cash");
                    
                    entity.timesQuay = timesQuay;
                    entity.mUid  = userId;
                    entity.money = cash;
                    
                    
                }
                
                
                rs.close();
                cs.close();
            }
            
            
        }
        finally
        {
            con.close();
        }
                    
        return entity;    
    }
    
    
}
