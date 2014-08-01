package com.tv.xeeng.memcached.data;

import com.tv.xeeng.base.common.FileHelper;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.level.XELevelItem;
import com.tv.xeeng.base.shop.XEShopItem;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.game.data.*;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.room.ZoneManager;
import com.tv.xeeng.memcached.IMemcacheClient;
import com.tv.xeeng.memcached.MemcacheClientPool;
import com.tv.xeeng.server.Server;
import org.slf4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XEDataUtils {

    public static final int XE_CACHE_TIMEOUT = 5 * 60; // 5 mins
    public static final int XE_CACHE_TIMEOUT_4LEVELS = 60 * 60; // 60 mins
    public static final int XE_CACHE_TIMEOUT_4TABLELIST = 30; // 30 secs
    public static String XE_CACHE_NAMESPACE = "XECache";
    private static boolean isUseCache = true;
    private static Logger mLog = LoggerContext.getLoggerFactory().getLogger(XEDataUtils.class);

    private static List<XEShopItem> getAllShopItemsFromDB() throws SQLException {
        String query = "{call uspXEGetAllShopItems() }";
        Connection con = DBPoolConnection.getConnection();
        List<XEShopItem> retList = null;
        CallableStatement cs = con.prepareCall(query);
        try {
            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                retList = new ArrayList<XEShopItem>();
                while (rs.next()) {
                    retList.add(new XEShopItem(
                            rs.getInt("id"),
                            rs.getInt("item_id"),
                            rs.getInt("cardinality"),
                            rs.getInt("mul_factor"),
                            rs.getInt("price"),
                            rs.getString("description")));
                }
                rs.close();
            }
        } finally {
            cs.close();
        }
        return retList;
    }

//    public static 
    public static List<XEShopItem> getAllShopItems() {
        try {
            return getAllShopItemsFromDB();
        } catch (SQLException ex) {
            return null;
        }
    }

    public static List<XELogEventGiftEntity> getEventGiftLogOfUser(long uid) {
        try {
            String query = "{call uspXEGetEventGiftLogOfUser(?) }";
            Connection con = DBPoolConnection.getConnection();
            List<XELogEventGiftEntity> retList = null;
            CallableStatement cs = con.prepareCall(query);
            cs.setLong(1, uid);
            try {
                ResultSet rs = cs.executeQuery();
                if (rs != null) {
                    retList = new ArrayList<XELogEventGiftEntity>();
                    while (rs.next()) {
                        XELogEventGiftEntity entity = new XELogEventGiftEntity();
                        entity.setMessage(rs.getString("message"));
                        entity.setUseDate(rs.getTimestamp("useDate"));

                        retList.add(entity);
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

    public static int useInventoryItem(long uid, String itemCode, String giftCode, int itemLimit) {
        int res = -1;
        try {
            String query = "{ call uspXEUseInventoryItem(?, ?, ?, ?) }";
            Connection con = DBPoolConnection.getConnection();
            CallableStatement cs = con.prepareCall(query);

            try {
                cs.setLong(1, uid);
                cs.setString(2, itemCode);
                cs.setString(3, giftCode);
                cs.setInt(4, itemLimit);

                ResultSet rs = cs.executeQuery();
                if (rs != null && rs.next()) {
                    res = rs.getInt("result");
                }
                rs.close();
            } catch (SQLException ex) {
                mLog.error(ex.getMessage());
            } finally {
                cs.close();
            }
        } catch (SQLException ex) {
            mLog.error(ex.getMessage());
        }

        return res;
    }

    public static boolean joinEventItems(long uid, String code, String name, String description, long fee) {
        int result = -1;
        try {
            String query = "{ call uspXEJoinEventItems(?, ?, ?, ?, ?) }";
            Connection con = DBPoolConnection.getConnection();
            CallableStatement cs = con.prepareCall(query);

            try {
                cs.setLong(1, uid);
                cs.setString(2, code);
                cs.setString(3, name);
                cs.setString(4, description);
                cs.setLong(5, fee);

                ResultSet rs = cs.executeQuery();
                if (rs != null && rs.next()) {
                    result = rs.getInt("result");
                }
                rs.close();
            } catch (SQLException ex) {
                mLog.error(ex.getMessage());
            } finally {
                cs.close();
            }
        } catch (SQLException ex) {
            mLog.error(ex.getMessage());
        }

        return result == 1;
    }
    
    public static boolean insertInventoryOfUser(long uid, String code) {
        int result = -1;
        try {
            String query = "{ call uspXEInsertInventoryOfUser(?, ?) }";
            Connection con = DBPoolConnection.getConnection();
            CallableStatement cs = con.prepareCall(query);

            try {
                cs.setLong(1, uid);
                cs.setString(2, code);

                ResultSet rs = cs.executeQuery();
                if (rs != null && rs.next()) {
                    result = rs.getInt("result");
                }
                rs.close();
            } catch (SQLException ex) {
                mLog.error(ex.getMessage());
            } finally {
                cs.close();
            }
        } catch (SQLException ex) {
            mLog.error(ex.getMessage());
        }

        return result == 1;
    }

    public static boolean updateUserMoney(long uid, long numOfXeeng, long numOfGold) {
        try {
            String query = "{call uspXEUpdateUserMoney(?, ?, ?) }";
            Connection con = DBPoolConnection.getConnection();
            CallableStatement cs = con.prepareCall(query);

            try {
                cs.setLong(1, uid);
                cs.setLong(2, numOfXeeng);
                cs.setLong(3, numOfGold);

                cs.execute();

                return true;
            } catch (SQLException ex) {
                mLog.error(ex.getMessage());
            } finally {
                cs.close();
            }

            return false;
        } catch (SQLException ex) {
            mLog.error(ex.getMessage());
        }

        return false;
    }

    public static boolean insertExchangeLog(XEExchangeLogEntity exchangeLog) {
        try {
            String query = "{call uspXEInsertExchangeLog(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
            Connection con = DBPoolConnection.getConnection();
            CallableStatement cs = con.prepareCall(query);

            try {
                cs.setLong(1, exchangeLog.getUserId());

                cs.setLong(2, exchangeLog.getFromValue());
                cs.setObject(3, exchangeLog.getFromType());
                cs.setLong(4, exchangeLog.getToValue());
                cs.setObject(5, exchangeLog.getToType());

                cs.setObject(6, exchangeLog.getMessage());

                cs.setLong(7, exchangeLog.getFromValueBefore());
                cs.setLong(8, exchangeLog.getFromValueAfter());
                cs.setLong(9, exchangeLog.getToValueBefore());
                cs.setLong(10, exchangeLog.getToValueAfter());

                cs.execute();

                return true;
            } catch (SQLException ex) {
                mLog.error(ex.getMessage());
            } finally {
                cs.close();
            }

            return false;
        } catch (SQLException ex) {
            mLog.error(ex.getMessage());
        }

        return false;
    }

    public static boolean insertPrivateMessage(XEPrivateMessageEntity pm) {
        try {
            String query = "{call uspXEInsertPrivateMessage(?, ?, ?, ?) }";
            Connection con = DBPoolConnection.getConnection();
            CallableStatement cs = con.prepareCall(query);

            try {
                cs.setLong(1, pm.getFromUserId());
                cs.setLong(2, pm.getToUserId());
                cs.setObject(3, pm.getTitle());
                cs.setObject(4, pm.getContent());

                cs.execute();

                return true;
            } catch (SQLException ex) {
                mLog.error(ex.getMessage());
            } finally {
                cs.close();
            }
        } catch (SQLException ex) {
            mLog.error(ex.getMessage());
        }

        return false;
    }

    public static List<XEPrivateMessageEntity> getAllPrivateMessages(long userId, int numOfDay) {
        try {
            List<XEPrivateMessageEntity> pms = new ArrayList<XEPrivateMessageEntity>();

            String query = "{call uspXEGetAllPrivateMessages(?, ?) }";
            Connection con = DBPoolConnection.getConnection();
            CallableStatement cs = con.prepareCall(query);

            cs.setLong(1, userId);
            cs.setInt(2, numOfDay);

            try {
                ResultSet rs = cs.executeQuery();
                while (rs.next()) {
                    XEPrivateMessageEntity pm = new XEPrivateMessageEntity();

                    pm.setId(rs.getInt("id"));
                    pm.setFromUserId(rs.getLong("fromUserId"));
                    pm.setToUserId(rs.getLong("toUserId"));
                    pm.setTitle(rs.getObject("title").toString());
                    pm.setContent(rs.getObject("content").toString());
                    pm.setDateSent(rs.getTimestamp("dateSent"));

                    pms.add(pm);
                }
                rs.close();

                return pms;
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

    public static XEPrivateMessageEntity getPrivateMessage(long id) {
        try {
            String query = "{call uspXEGetPrivateMessage(?) }";
            Connection con = DBPoolConnection.getConnection();
            CallableStatement cs = con.prepareCall(query);

            cs.setLong(1, id);

            try {
                ResultSet rs = cs.executeQuery();
                if (rs.next()) {
                    XEPrivateMessageEntity pm = new XEPrivateMessageEntity();

                    pm.setId(rs.getInt("id"));
                    pm.setFromUserId(rs.getLong("fromUserId"));
                    pm.setToUserId(rs.getLong("toUserId"));
                    pm.setTitle(rs.getObject("title").toString());
                    pm.setContent(rs.getObject("content").toString());
                    pm.setDateSent(rs.getTimestamp("dateSent"));

                    return pm;
                }
                rs.close();
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

    /**
     * Encode một tập các param.
     *
     * @param params
     * @return
     */
    public static String serializeParams(Object... params) {
        StringBuilder sBuilder = new StringBuilder();
        if (params.length > 0) {
            int i;
            for (i = 0; i < params.length - 1; i++) {
                sBuilder.append(String.valueOf(params[i]));
                sBuilder.append(AIOConstants.SEPERATOR_BYTE_1);
            }
            sBuilder.append(String.valueOf(params[i]));
        }
        return sBuilder.toString();
    }

    public static String serializeList(List objs) {
        return serializeList(objs, AIOConstants.SEPERATOR_BYTE_2);
    }

    public static String serializeList(List objs, final String seperator) {
        StringBuilder sBuilder = new StringBuilder();
        if (objs.size() > 0) {
            int i;
            for (i = 0; i < objs.size() - 1; i++) {
                sBuilder.append(objs.get(i).toString());
                sBuilder.append(seperator);
            }
            sBuilder.append(objs.get(i).toString());
        }
        return sBuilder.toString();
    }

    public static String getAndEncodeAllShopItems() {
        try {
            String entity;
            if (isUseCache) {
                MemcacheClientPool pool = CacheUserInfo.getCachedPool();
                IMemcacheClient client = pool.borrowClient();
                String key = XE_CACHE_NAMESPACE + "_ShopItems";
                entity = (String) client.get(key);
                mLog.debug("Get from cache: " + entity);
                if (entity == null) {
                    entity = serializeList(getAllShopItemsFromDB(), AIOConstants.SEPERATOR_BYTE_2);
                    mLog.debug("Get from DB: " + entity + "_");
                    client.set(key, XE_CACHE_TIMEOUT, entity);
                }
                pool.returnClient(client);
            } else {
                entity = serializeList(getAllShopItemsFromDB(), AIOConstants.SEPERATOR_BYTE_2);
            }
            return entity;
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static ArrayList<String> loadPredefinedTableNames() {
        String query = "{call uspXEGetPredefinedTableNames() }";
        Connection con = DBPoolConnection.getConnection();
        ArrayList<String> retList = null;
        try {
            CallableStatement cs = con.prepareCall(query);
            try {
                ResultSet rs = cs.executeQuery();
                if (rs != null) {
                    retList = new ArrayList<String>();
                    while (rs.next()) {
                        retList.add(rs.getString("table_name"));
                    }
                    rs.close();
                }
            } finally {
                cs.close();
            }
            return retList;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<XELevelItem> getAllLevelItemsFromDB(int zoneId) throws SQLException {
        String query = "{call uspXEGetAllLevels(?) }";
        Connection con = DBPoolConnection.getConnection();
        List<XELevelItem> retList = null;
        CallableStatement cs = con.prepareCall(query);
        cs.setInt(1, zoneId);

        try {
            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                retList = new ArrayList<XELevelItem>();
                while (rs.next()) {
                    retList.add(new XELevelItem(rs.getInt("levelId"), rs.getString("level"), rs.getInt("minCash"), rs.getString("cashList"), rs.getInt("isVip")));
                }
                rs.close();
            }
        } finally {
            cs.close();
        }
        return retList;
    }

    public static String getAndEncodeAllLevelItems(int zoneId) {
        try {
            String entity = null;
            if (isUseCache) {
                MemcacheClientPool pool = CacheUserInfo.getCachedPool();
                IMemcacheClient client = pool.borrowClient();
                String key = XE_CACHE_NAMESPACE + "_LevelItems" + zoneId;
                entity = (String) client.get(key);
                mLog.debug("Get from cache: " + entity);
                if (entity == null) {
                    entity = serializeList(getAllLevelItemsFromDB(zoneId), AIOConstants.SEPERATOR_BYTE_2);
                    mLog.debug("Get from DB: " + entity);
                    client.set(key, XE_CACHE_TIMEOUT_4LEVELS, entity);
                }
                pool.returnClient(client);
            } else {
                entity = serializeList(getAllLevelItemsFromDB(zoneId), AIOConstants.SEPERATOR_BYTE_2);
            }
            return entity;
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static List<Room> getTablesByLevelFromZone(Zone zone, int levelID) {
        return zone.xeGetTablesByLevel(levelID);
    }

    public static String getAndEncodeTablesInZone(Zone zone, int levelID) {
        try {
            /* Note (thanhnvt): 
             * Em bỏ cache do gặp lỗi người chơi khác không thấy những bàn vừa được tạo (API FastPlay).
             * Danh sách bàn chơi available em nghĩ thay đổi liên tục nên có thể tạm bỏ cache cũng được 
             */
            String entity = null;
//            if (isUseCache) {
//                MemcacheClientPool pool = CacheUserInfo.getCachedPool();
//                IMemcacheClient client = pool.borrowClient();
//                String key = XE_CACHE_NAMESPACE + "_Zone" + zone.getZoneId() + "Level" + levelID + "Tables";
//                entity = (String) client.get(key);
//                mLog.debug("Get from cache: " + entity);
//                if (entity == null) {
//                    entity = serializeList(getTablesByLevelFromZone(zone, levelID), AIOConstants.SEPERATOR_BYTE_2);
//                    mLog.debug("Get from DB: " + entity);
//                    client.set(key, XE_CACHE_TIMEOUT_4TABLELIST, entity);
//                }
//                pool.returnClient(client);
//            } else {
            entity = serializeList(getTablesByLevelFromZone(zone, levelID), AIOConstants.SEPERATOR_BYTE_2);
//            }
            return entity;
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static List<Room> getTablesByLevel(int levelID) {
        ZoneManager zManager = Server.getWorker().getZoneManager();
        ArrayList<Room> tableList = new ArrayList<Room>();
        for (Zone zone : zManager.getZoneList()) {
            tableList.addAll(getTablesByLevelFromZone(zone, levelID));
        }
        return tableList;
    }

    public static String getAndEncodeTables(int levelID) {
        try {
            /* Note (thanhnvt): 
             * Em bỏ cache do gặp lỗi người chơi khác không thấy những bàn vừa được tạo (API FastPlay).
             * Danh sách bàn chơi available em nghĩ thay đổi liên tục nên có thể tạm bỏ cache cũng được 
             */

            String entity = null;
//            if (isUseCache) {
//                MemcacheClientPool pool = CacheUserInfo.getCachedPool();
//                IMemcacheClient client = pool.borrowClient();
//                String key = XE_CACHE_NAMESPACE + "_Level" + levelID + "Tables";
//                entity = (String) client.get(key);
//                mLog.debug("Get from cache: " + entity);
//                if (entity == null) {
//                    entity = serializeList(getTablesByLevel(levelID), AIOConstants.SEPERATOR_BYTE_2);
//                    mLog.debug("Get from DB: " + entity + "_");
//                    client.set(key, XE_CACHE_TIMEOUT_4TABLELIST, entity);
//                }
//                pool.returnClient(client);
//            } else {
            entity = serializeList(getTablesByLevel(levelID), AIOConstants.SEPERATOR_BYTE_2);
//            }
            return entity;
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Lấy toàn bộ tin tức.
     */
    public static List<XENewsEntity> getAllNews() {
        try {
            List<XENewsEntity> allNews = new ArrayList<XENewsEntity>();

            String query = "{call uspXEGetAllNews() }";
            Connection con = DBPoolConnection.getConnection();
            CallableStatement cs = con.prepareCall(query);

            try {
                ResultSet rs = cs.executeQuery();
                if (rs != null) {
                    while (rs.next()) {
                        XENewsEntity news = new XENewsEntity();

                        news.setId(rs.getInt("id"));
                        news.setTitle(rs.getObject("title").toString());
                        news.setHtmlContent(rs.getObject("htmlContent").toString());
                        news.setDateCreated(rs.getDate("dateCreated"));
                        news.setDateModified(rs.getDate("dateModified"));
                        news.setUserCreated(rs.getLong("userCreated"));
                        news.setUserModified(rs.getLong("userModified"));

                        allNews.add(news);
                    }
                    rs.close();
                }

                return allNews;
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

    /**
     * Lấy toàn bộ sự kiện.
     */
    public static List<EventEntity> getEventFromDB() throws SQLException {
        List<EventEntity> res = new ArrayList<EventEntity>();
        String query = "{ call uspGetEvent() }";
        Connection conn = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = conn.prepareCall(query);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {

                while (rs.next()) {
                    String title = rs.getString("Name");
                    String content = rs.getString("content");
                    int eventId = rs.getInt("GameEventId");
                    int partnerId = rs.getInt("partnerId");
                    int gameId = rs.getInt("gameID");
                    boolean isConcurrent = rs.getBoolean("isConcurrent");

                    EventEntity entity = new EventEntity(title, content, null, eventId, gameId);
                    entity.setPartnerId(partnerId);
                    entity.setConcurrent(isConcurrent);
                    //entity.setEventType(rs.getInt("EventType"));
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
     * Lấy toàn bộ cấp độ người chơi.
     */
    public static List<XEUserLevelEntity> getAllUserLevels() {
        try {
            List<XEUserLevelEntity> levels = new ArrayList<XEUserLevelEntity>();

            String query = "{call uspXEGetUserLevels() }";
            Connection con = DBPoolConnection.getConnection();
            CallableStatement cs = con.prepareCall(query);

            try {
                ResultSet rs = cs.executeQuery();
                if (rs != null) {
                    while (rs.next()) {
                        XEUserLevelEntity level = new XEUserLevelEntity();

                        level.setId(rs.getInt("id"));
                        level.setName(rs.getObject("name").toString());
                        level.setMinGold(rs.getLong("minGold"));
                        level.setMaxGold(rs.getLong("maxGold"));

                        levels.add(level);
                    }
                    rs.close();
                }

                return levels;
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

    /**
     * Lấy tin tức theo id.
     */
    public static XENewsEntity getNewsById(int id) {
        try {
            String query = "{call uspXEGetNewsById(?) }";
            Connection con = DBPoolConnection.getConnection();
            CallableStatement cs = con.prepareCall(query);

            try {
                cs.setInt(1, id);

                ResultSet rs = cs.executeQuery();
                if (rs != null && rs.next()) {
                    XENewsEntity news = new XENewsEntity();

                    news.setId(rs.getInt("id"));
                    news.setTitle(rs.getObject("title").toString());
                    news.setHtmlContent(rs.getObject("htmlContent").toString());
                    news.setDateCreated(rs.getDate("dateCreated"));
                    news.setDateModified(rs.getDate("dateModified"));
                    news.setUserCreated(rs.getLong("userCreated"));
                    news.setUserModified(rs.getLong("userModified"));

                    rs.close();

                    return news;
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

    public static Room getTable(int matchID) {
        ZoneManager zManager = Server.getWorker().getZoneManager();
        for (Zone zone : zManager.getZoneList()) {
            Room r = zone.findRoom(matchID);
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    public static String getAndEncodeTable(int matchID) {
        Room r = getTable(matchID);
        if (r != null) {
            return r.toString();
        }
        return null;
    }

    public static List<String> loadBlacklistWords() {
        List<String> blacklist = new ArrayList<String>();

        String blacklistContent = FileHelper.readFileUTF8("conf/blacklist_words.txt");
        for (String word : blacklistContent.split("\n")) {
            word = word.trim();
            if (!word.isEmpty()) {
                blacklist.add(word);
            }
        }

        System.out.println("[+] Loading blacklist words...");
        System.out.println(String.format("[+] Done (%d words).", blacklist.size()));

        return blacklist;
    }
}
