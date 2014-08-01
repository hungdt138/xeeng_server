/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.game.data.EventGiftEntity;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;

/**
 *
 * @author ThangTD
 */
public class EventGiftsDB {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(EventGiftsDB.class);

    private static List<EventGiftEntity> itemsList;

    public static void reload() {
        try {
            itemsList = getEventGiftsFromDB();
            mLog.debug("[EVENT GIFTS] : " + itemsList.size() + " items");
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        }
    }

    private static List<EventGiftEntity> getEventGiftsFromDB() throws SQLException {
        List<EventGiftEntity> res = new ArrayList<EventGiftEntity>();
        String query = "{ call uspXEGetAllEventGifts() }";
        Connection conn = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = conn.prepareCall(query);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    String evitCode = rs.getString("evitCode");
                    String evgfCode = rs.getString("evgfCode");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    float rate = rs.getFloat("rate");
                    long value = rs.getLong("value");
                    String type = rs.getString("type");
                    int quantity = rs.getInt("quantity");
                    boolean isLimitUsage = rs.getBoolean("isLimitUsage");

                    EventGiftEntity entity = new EventGiftEntity(evitCode, evgfCode, name, description, rate, value, type, quantity, isLimitUsage);
                    res.add(entity);
                }

                rs.close();
            }
        } finally {
            conn.close();
        }
        return res;
    }

    /**
     * @return the itemsList
     */
    public static List<EventGiftEntity> getItemsList() {
        return itemsList;
    }
}
