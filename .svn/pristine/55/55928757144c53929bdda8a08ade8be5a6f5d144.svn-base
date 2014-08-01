/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.NewsCategoryEntity;


/**
 *
 * @author tuanda
 */
public class NewsCategoryDB {

    
    
    private Connection conn;
    private static String categories;
    private static List<NewsCategoryEntity> lstCat;
    
    public NewsCategoryDB()
    {
        
    }
    
    public static void reload()
    {
        try {
            lstCat = getAllCategories1();
            StringBuilder sb = new StringBuilder();
            
            int catSize = lstCat.size();
            for(int i = 0; i< catSize; i++)
            {
                NewsCategoryEntity entity = lstCat.get(i);
                sb.append(Integer.toString(entity.getCategoryId())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(entity.getName()).append(AIOConstants.SEPERATOR_BYTE_1);
                int maxPage = entity.getPage();
                sb.append(Integer.toString(maxPage)).append(AIOConstants.SEPERATOR_BYTE_2);
            }
            
            if(catSize>0)
            {
                sb.deleteCharAt(sb.length() -1);
            }
            
            categories = sb.toString();
            
        } catch (SQLException ex) {
            Logger.getLogger(NewsCategoryDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static String getStrCategories()
    {
        return categories;
    }
    
    public static List<NewsCategoryEntity> getNewsCategory()
    {
        return lstCat;
    }
    private static List<NewsCategoryEntity> getAllCategories1()  throws SQLException{
        List<NewsCategoryEntity> res = new ArrayList<NewsCategoryEntity>();
        res.add(new NewsCategoryEntity(1, "Tin Nong", 1, 1));
        return res;
    }
    private static List<NewsCategoryEntity> getAllCategories() throws SQLException
    {
        List<NewsCategoryEntity> res = new ArrayList<NewsCategoryEntity>();
        String query = "{call uspGetNewsCategory()}";
        Connection conn = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = conn.prepareCall(query);
            ResultSet rs = cs.executeQuery();
            if(rs != null)
            {
                while (rs.next()) {
                        int categorId = rs.getInt("newsCategoryId");
                        String name = rs.getString("name");
                        int page = rs.getInt("page");
                        int partnerCategoryId = rs.getInt("siteCategoryId");
                        NewsCategoryEntity entity = new NewsCategoryEntity(categorId, name, page, partnerCategoryId);
                        res.add(entity);
                }

                rs.close();
            }
            cs.close();
        }
        finally
        {
            conn.close();
        }
        
        return res;
        
    }
    
    
    
    
    
    
    

    
    
    
    
    
    
    
}
