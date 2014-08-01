/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.game.data.ImageEntity;



/**
 *
 * @author tuanda
 */
public class ImageDB {
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ImageDB.class);
    
    private static List<ImageEntity> lstImages;
    
    public static void reload()
    {
        try {
            allImages();
            
        } catch (SQLException ex) {
            mLog.error(ex.getMessage());
        }
    }
    private static void allImages() throws SQLException
    {
        Connection con = DBPoolConnection.getConnection();
        try
        {
           lstImages = new ArrayList<> ();
            String query = "{ call uspGetAllImage() }";
            //SimpleConnnection conn = MasterPoolConnection.checkOut();
            
            //CallableStatement cs =  conn.getConnection().prepareCall(query);
            CallableStatement cs = con.prepareCall(query);
            cs.clearParameters();
            ResultSet rs = cs.executeQuery();
            if(rs != null)
            {
                while(rs.next())
                {
                    ImageEntity entity = new ImageEntity(rs.getInt("idimage"), rs.getString("name"), rs.getString("detail"));
                    lstImages.add(entity);
                }
                rs.close();
            }
            cs.close();
            
        }
        finally
        {
            con.close();
        }
        
    }
    
    public static String getImageDetail(String name)
    {
        if(lstImages.isEmpty())
        {
            mLog.error("get image error");
        }
        
        
        for(int i = 0; i< lstImages.size(); i++)
        {
            if(lstImages.get(i).getName().equals(name))
            {
                return lstImages.get(i).getDetail();
            }
        }
        return null;
    }
    
}
