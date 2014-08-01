package com.tv.xeeng.memcached.data;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.databaseDriven.MessageDB;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.Message;
import com.tv.xeeng.game.data.MessageEntity;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.data.UserInfoEntity;
import com.tv.xeeng.memcached.IMemcacheClient;
import com.tv.xeeng.memcached.MemcacheClientPool;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.slf4j.Logger;

/**
 *
 * @author tuanda
 */
public class CacheUserInfo {

    public static boolean isUseCache = true;
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(CacheUserInfo.class);

    private static final String USER_NAMESPACE = "user";
    private static final String USER_ADD_INFO_NAMESPACE = "usrAI";
    private static final String SERVER_LIST = "192.168.50.107:9501";
    private static final String GAME_NAMESPACE = "lsstart1!";

    private static final int TIME_CACHE = 600; //5' 

    private static final String GET_MESSAGE_NAMESPACE = "gMessage";

    protected static MemcacheClientPool cachedPool;

    public static MemcacheClientPool getCachedPool() {
        return cachedPool;
    }

    public static void finishCache() {
        isUseCache = false;
        cachedPool.shutdown();
    }

    public static void initCache() {
        try {
            Properties appConfig = new Properties();
            appConfig.load(new FileInputStream("conf/c3p0.properties"));
            String host = appConfig.getProperty("memcachedServer");
            String server = host + ":9501";
            cachedPool = new MemcacheClientPool();
            cachedPool.start(server, GAME_NAMESPACE);
        } catch (IOException ex) {
            cachedPool = new MemcacheClientPool();
            cachedPool.start(SERVER_LIST, GAME_NAMESPACE);

            java.util.logging.Logger.getLogger(CacheUserInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static UserEntity loadUserInfoFromDB(long userId) throws SQLException {
        UserDB db = new UserDB();
        return db.getUserInfo(userId);
    }

    private UserInfoEntity loadUserAddInfoFromDB(long userId) throws SQLException {
        UserDB db = new UserDB();
        return db.getUserMxhAccount(userId);
    }

    private MessageEntity getMessageFromDB(long userId) throws Exception {
        MessageDB db = new MessageDB();
        List<Message> lstMessage = db.receiveMessage(userId);
        MessageEntity entity = new MessageEntity(lstMessage);

        return entity;
    }

    public UserEntity getUserInfo(long userId) {
        try {
            if (isUseCache) {
                IMemcacheClient client = cachedPool.borrowClient();
                UserEntity entity = null;
                try {
                    String key = USER_NAMESPACE + Long.toString(userId);
                    entity = (UserEntity) client.get(key);
                    if (entity == null) {
                        entity = loadUserInfoFromDB(userId);
                        client.set(key, TIME_CACHE, entity);
                    }
                } catch (SQLException ex) {
                    mLog.error(ex.getMessage(), ex);
                } catch (Exception ex) {
                    mLog.error(ex.getMessage(), ex);
                }
                cachedPool.returnClient(client);
                return entity;

            }
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
        }

        try {
            return loadUserInfoFromDB(userId);
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        }

        return null;
    }

    public UserEntity getFullUserInfo(long userId) {
        try {
            if (isUseCache) {
                IMemcacheClient client = cachedPool.borrowClient();
                UserEntity entity = null;
                try {

                    String key = USER_NAMESPACE + Long.toString(userId);
                    entity = (UserEntity) client.get(key);
                    if (entity == null) {
                        entity = loadUserInfoFromDB(userId);
                        client.set(key, TIME_CACHE, entity);
                    }

                    String keyAdd = USER_ADD_INFO_NAMESPACE + Long.toString(userId);
                    UserInfoEntity addInfo = (UserInfoEntity) client.get(keyAdd);
                    if (addInfo == null) {
                        addInfo = loadUserAddInfoFromDB(userId);
                        client.set(keyAdd, TIME_CACHE, addInfo);

                    }
                    entity.usrInfoEntity = addInfo;
                } catch (SQLException ex) {
                    mLog.error(ex.getMessage(), ex);
                } catch (Exception ex) {
                    mLog.error(ex.getMessage(), ex);
                }
                cachedPool.returnClient(client);
                return entity;

            }
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);

        }

        try {
            UserEntity entity = loadUserInfoFromDB(userId);
            if (entity != null) {
                UserInfoEntity addInfo = loadUserAddInfoFromDB(userId);
                entity.usrInfoEntity = addInfo;
            }
            return entity;
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        }

        return null;
    }

    public static void updateUserCashFromDB(long uid, long cash) {
        try {
            if (isUseCache) {

                IMemcacheClient client = cachedPool.borrowClient();
                try {

                    String key = USER_NAMESPACE + Long.toString(uid);
                    UserEntity entity = (UserEntity) client.get(key);
                    if (entity != null) {
                        entity.money = cash;
                        client.set(key, TIME_CACHE, entity);
                    }
                } catch (Exception ex) {
                    //put isUserCa
                    mLog.error(ex.getMessage(), ex);
                }

                cachedPool.returnClient(client);

            }
        } catch (Exception ex) {
            //put isUserCa
            mLog.error(ex.getMessage(), ex);
        }
    }

    public void updateCacheUserInfo(UserEntity entity) {
        try {
            if (isUseCache) {

                IMemcacheClient client = cachedPool.borrowClient();
                try {
                    String key = USER_NAMESPACE + Long.toString(entity.mUid);
                    client.set(key, TIME_CACHE, entity);
                } finally {
                    cachedPool.returnClient(client);
                }
            }
        } catch (Exception ex) {
            //put isUserCa
            mLog.error(ex.getMessage(), ex);
        }
    }

    public static void updateUserActiveCashFromDB(long uid, long cash) {
        try {
            if (isUseCache) {

                IMemcacheClient client = cachedPool.borrowClient();
                try {
                    String key = USER_NAMESPACE + Long.toString(uid);

                    UserEntity entity = (UserEntity) client.get(key);
                    if (entity != null) {
                        entity.money = cash;
                        entity.isActive = true;
                        client.set(key, TIME_CACHE, entity);
                    }
                } finally {
                    cachedPool.returnClient(client);
                }

            }
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
        }
    }

    public static void deleteCacheUserById(long uId) {
        try {
            if (isUseCache) {

                IMemcacheClient client = cachedPool.borrowClient();
                try {
                    String key = USER_NAMESPACE + Long.toString(uId);
                    client.delete(key);
                } finally {
                    cachedPool.returnClient(client);
                }
            }
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
        }
    }

    public void deleteCacheUser(UserEntity usrEntity) {
        try {
            if (isUseCache) {
                IMemcacheClient client = cachedPool.borrowClient();
                try {
                    String key = USER_NAMESPACE + Long.toString(usrEntity.mUid);
                    client.delete(key);
                } finally {
                    cachedPool.returnClient(client);
                }
            }
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
        }
    }

    public void deleteFullCacheUser(UserEntity usrEntity) {
        try {
            if (isUseCache) {

                IMemcacheClient client = cachedPool.borrowClient();
                try {
                    String key = USER_NAMESPACE + Long.toString(usrEntity.mUid);
                    String keyADD = USER_ADD_INFO_NAMESPACE + Long.toString(usrEntity.mUid);
                    client.delete(key);
                    client.delete(keyADD);

                } finally {
                    cachedPool.returnClient(client);
                }

            }
        } catch (Exception ex) {
            //put isUserCa
            mLog.error(ex.getMessage(), ex);
        }
    }

    public MessageEntity getMessage(long userId) {
        try {
            if (isUseCache) {
                IMemcacheClient client = cachedPool.borrowClient();
                MessageEntity enity = null;
                try {
                    String key = GET_MESSAGE_NAMESPACE + Long.toString(userId);
                    enity = (MessageEntity) client.get(key);
                    if (enity == null) {
                        //                    loadFromDatabase++;
                        enity = getMessageFromDB(userId);
                        client.set(key, TIME_CACHE, enity);
                    }
                } catch (Exception ex) {
                    mLog.error(ex.getMessage(), ex);
                }

                cachedPool.returnClient(client);
                return enity;

            }
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
        }

        try {
            return getMessageFromDB(userId);
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
        }

        return null;
    }

    public void deleteCacheMessage(long userId) {
        try {
            if (isUseCache) {
                IMemcacheClient client = cachedPool.borrowClient();
                try {
                    String key = GET_MESSAGE_NAMESPACE + Long.toString(userId);
                    client.delete(key);
                } finally {
                    cachedPool.returnClient(client);
                }
            }
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
        }
    }

}
