/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.memcached.data;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.databaseDriven.FriendDB;
import com.tv.xeeng.game.data.SocialFriendEntity;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.IMemcacheClient;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import org.slf4j.Logger;

/**
 *
 * @author tuanda
 */
public class CacheFriendsInfo extends CacheUserInfo {

    private static final String FRIENDS_NAME_SPACE = "friends";
    private static final String FRIENDS_REQUEST_SOCIAL_NAME_SPACE = "fRSocial";
    private static final int FRIENDS_TIME_CACHE = 20;
    private static final Logger mLog = LoggerContext.getLoggerFactory()
            .getLogger(CacheFriendsInfo.class);

    private List<UserEntity> loadFriendsFromDB(long userId) throws SQLException {
        FriendDB db = new FriendDB();
        return db.getSocialFriends(userId);
    }

    private List<SocialFriendEntity> loadRequestFriendFromDB(long userId) throws SQLException {
        FriendDB db = new FriendDB();
        return db.getRequestFriends(userId);
    }

    /**
     * Cập nhật lại danh sách bạn bè của user sau khi có thay đổi.
     *
     * @param userId
     */
    public void refreshCache(long userId) {
        try {
            IMemcacheClient client = cachedPool.borrowClient();
            String key = FRIENDS_NAME_SPACE + Long.toString(userId);
            List<UserEntity> enity = loadFriendsFromDB(userId);
            client.set(key, FRIENDS_TIME_CACHE, enity);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(CacheFriendsInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<UserEntity> getFriends(long userId) {
        try {
            if (isUseCache) {
                IMemcacheClient client = cachedPool.borrowClient();
                List<UserEntity> enity = null;
                try {
                    String key = FRIENDS_NAME_SPACE + Long.toString(userId);
                    enity = (List<UserEntity>) client.get(key);
                    if (enity == null) {
                        //                    loadFromDatabase++;
                        enity = loadFriendsFromDB(userId);
                        client.set(key, FRIENDS_TIME_CACHE, enity);
                    }

                } catch (SQLException ex) {
                    mLog.error(ex.getMessage(), ex);
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
            return loadFriendsFromDB(userId);
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        }

        return null;
    }

    public List<SocialFriendEntity> getSocialFriendRequests(long userId) {
        try {
            if (isUseCache) {
                IMemcacheClient client = cachedPool.borrowClient();
                List<SocialFriendEntity> enity = null;
                try {
                    String key = FRIENDS_REQUEST_SOCIAL_NAME_SPACE + Long.toString(userId);
                    enity = (List<SocialFriendEntity>) client.get(key);
                    if (enity == null) {
                        //                    loadFromDatabase++;
                        enity = loadRequestFriendFromDB(userId);
                        client.set(key, FRIENDS_TIME_CACHE, enity);
                    }

                } catch (SQLException ex) {
                    mLog.error(ex.getMessage(), ex);
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
            return loadRequestFriendFromDB(userId);
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        }

        return null;
    }

//    public void updateCacheFileDetail(FileEntity entity)
//    {
//        try
//        {
//            if(isUseCache)
//            {
//                String key = FRIENDS_NAME_SPACE + Long.toString(entity.getFileId());
//                IMemcacheClient client = cachedPool.borrowClient();
//                client.set(key, FRIENDS_TIME_CACHE, entity);
//                cachedPool.returnClient(client);
//            }
//        }
//         
//        catch(Exception ex)
//        {
//            mLog.error(ex.getMessage(), ex);
//                    
//        }
//        
//        
//    }
//    
}
