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
import com.tv.xeeng.game.data.CacheEntity;
import com.tv.xeeng.game.trieuphu.data.QuestionManager;
import com.tv.xeeng.memcached.data.CacheUserInfo;


/**
 *
 * @author tuanda
 */
public class DBCache {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(DBCache.class);
    private static int count = 0;
    public static boolean isUseCache = true;
    public static boolean isUsePhom = true;
    public static boolean isLoadQuestion = true;
    public static boolean isLoadMonthlyEvent = false;
    
    public static void reload() {
    InfoDB.reload();
    
//    ImageDB.reload();
//    ItemDB.reload();
//    UserDB.reload();
//    DutyDB.reload();

    if (count == 0) {
        RoomDB.reload();
        resetCacheInfo();
        if (isLoadQuestion) {
            QuestionDB.reload();
            QuestionManager.reload();
        }
    }
    
    count++;

    EventDB.reload();
    
    // Added by ThangTD
    if (isLoadMonthlyEvent) {
        EventItemsDB.reload();
        EventGiftsDB.reload();
    }
    // End added
    
    if (!isUseCache) {
        CacheUserInfo.finishCache();
    }
}

    private static void resetCacheInfo() {
        Connection con = DBPoolConnection.getConnection();
        try {
            String query = "{ call uspResetCacheInfo() }";

            CallableStatement cs = con.prepareCall(query);
            cs.execute();
            cs.close();
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                mLog.error(ex.getMessage(), ex);
            }
        }

    }

    public static List<CacheEntity> getRefreshCaches(Connection conn) throws SQLException {
        List<CacheEntity> res = new ArrayList<CacheEntity>();
        String query = "{ call uspGetUserToRefresh() }";
        CallableStatement cs = conn.prepareCall(query);
        ResultSet rs = cs.executeQuery();
        if (rs != null) {
            while (rs.next()) {

                String namespace = rs.getString("namespace");
                long value = rs.getLong("value");
                long key = rs.getLong("keyCache");

                CacheEntity entity = new CacheEntity(namespace, value, key);

                res.add(entity);
            }
            rs.close();
            cs.close();
        }
        return res;
    }
}
