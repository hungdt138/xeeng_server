/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author tuanda
 */
public class CardOfflineTxnDB {
    private static String CARD_ID_PARAM = "cardId";
    private static String CARD_CODE_PARAM = "cardCode";
    private static String USER_NAME_PARAM = "userName";
    private static String USER_ID_PARAM = "userID";
    private static String SERVICE_ID_PARAM = "serviceId";
    private static String REF_CODE_PARAM = "refCode";
    
    public CardOfflineTxnDB()
    {
        
    }
    
    public void insertCard(String userName, String cardId, String cardCode, String serviceId, long userId, String refCode) throws SQLException
    {
        String query = "{ call uspInsertCardOfflineTxn(?, ?, ?, ?, ?,?) }";
        Connection con = DBPoolConnection.getConnection();
        try
        {
            CallableStatement cs = null;
            cs = con.prepareCall(query);

            cs.setString(USER_NAME_PARAM, userName);
            cs.setLong(USER_ID_PARAM, userId);
            cs.setString(CARD_ID_PARAM, cardId);
            cs.setString(CARD_CODE_PARAM, cardCode);
            cs.setString(SERVICE_ID_PARAM, serviceId);
            cs.setString(REF_CODE_PARAM, refCode);
            cs.execute();
            cs.close();
                   
        }
        finally
        {
            con.close();
        }
                    
    }
}
