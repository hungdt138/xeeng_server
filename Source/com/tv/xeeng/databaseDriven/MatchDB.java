/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

/**
 *
 * @author tuanda
 */
public class MatchDB {
    private static final int phantram = 10;
    private Connection conn = null;
    
    public MatchDB(Connection conn)
    {
        this.conn  = conn;
    }
    
    public  long logUserMatch(long uid, long matchID, String desc,
			long money, boolean isWin, long matchIDAuto) throws Exception {
                
        long userCash = 0;
        int win = 0;
        if (isWin) {
            win = 1;
        }
        String query = "{ call LogMatchuser(?, ?,?,?,?,?,?,?) }";
        
        CallableStatement cs = conn.prepareCall(query);
        cs.clearParameters();
        cs.registerOutParameter(8, Types.BIGINT);
        cs.setLong(1, uid);
        cs.setLong(2, matchID);
        cs.setInt(3, win);
        
        cs.setLong(4, money);
        cs.setString(5, desc);
        cs.setInt(6, phantram);
        cs.setLong(7, matchIDAuto);
        cs.execute();
        userCash = cs.getLong(8);

        cs.clearParameters();
        cs.close();
            
        
        return userCash;
        
    }

    
}
