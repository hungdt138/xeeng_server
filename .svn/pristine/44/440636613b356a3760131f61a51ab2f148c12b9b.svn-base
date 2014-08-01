/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;

import com.tv.xeeng.game.data.GioiThieuEntity;
import com.tv.xeeng.game.data.SocialFriendEntity;
import com.tv.xeeng.game.data.UserEntity;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author tuanda
 */
public class FriendDB {

    private static final String USER_ID_PARAM = "userId";
    private static final String USER_NAME_PARAM = "userName";
    private static final String FRIEND_ID_PARAM = "friendId";
    private static final String ADD_PARAM = "add";
    private static final String ISMALE_PARAM = "isMale";
    private static final String CITY_ID_PARAM = "cityId";
    private static final String JOB_ID_PARAM = "jobId";
    private static final String FROM_YEAR_PARAM = "fromYear";
    private static final String TO_YEAR_PARAM = "toYear";
    private static final String CHARACTER_ID_PARAM = "characterId";
    private static final String HAS_AVATAR_PARAM = "hasAvatar";
    private static final String IS_ONLINE_PARAM = "isonline";
    private static final String FRIEND_NAME_PARAM = "friendName";
    private static final String PHONE_OR_EMAIL_PARAM = "phoneOrEmail";
    private static final String DEVICE_PARAM = "device";
    private static final String PARTNER_ID_PARAM = "partnerId";
    private static final String POSITION_GIOI_THIEU_PARAM = "positionGioiThieu";
    private static final String REF_GIOI_THIEU_PARAM = "refGioithieuId";

    private Connection conn;

    public FriendDB() {

    }

    public FriendDB(Connection conn) {
        this.conn = conn;
    }

    public int addFriend(long currUID, long friendUID, int bonusGold, int maxBonusAllowed) throws Exception {
        String query = "{?= call uspInsertFriend(?, ?, ?, ?)} ";
        Connection conn = DBPoolConnection.getConnection();
        int ret = -1;
        try {
            CallableStatement cs = conn.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong(USER_ID_PARAM, currUID);
            cs.setLong(FRIEND_ID_PARAM, friendUID);
            cs.setInt(3, bonusGold);
            cs.setInt(4, maxBonusAllowed);
            cs.execute();

            ret = cs.getInt(1);

            cs.close();
        } finally {
            conn.close();
        }

        return ret;
    }

    public int addSocialFriend(long currUID, long friendUID, int bonusGold, int maxBonusAllowed) throws Exception {
        String query = "{?= call uspAddSocialFriend(?,?,?,?)} ";
        Connection conn = DBPoolConnection.getConnection();
        int ret = -1;
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong(USER_ID_PARAM, currUID);
            cs.setLong(FRIEND_ID_PARAM, friendUID);
            cs.setInt("bonusGold", bonusGold);
            cs.setInt("maxGoldAllowed", maxBonusAllowed);
            cs.execute();

            ret = cs.getInt(1);

            cs.close();
        } finally {
            conn.close();
        }

        return ret;
    }

    public boolean removeSocialFriend(long currUID, long friendUID)
            throws Exception {

        String query = "{call uspDeleteSocialFriend(?,?)}";

        Connection conn = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = conn.prepareCall(query);
            cs.setLong(USER_ID_PARAM, currUID);
            cs.setLong(FRIEND_ID_PARAM, friendUID);
            cs.execute();

            return true;
        } catch (SQLException ex) {
            return false;
        } finally {
            conn.close();
        }
    }

    public int addFriendByName(long currUID, String friendName) throws Exception {
        String query = "{?= call uspInsertFriendByName(?,?)} ";
        Connection conn = DBPoolConnection.getConnection();
        int ret = -1;
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong(USER_ID_PARAM, currUID);
            cs.setString(FRIEND_ID_PARAM, friendName);
            cs.execute();

            ret = cs.getInt(1);

            cs.close();
        } finally {
            conn.close();
        }

        return ret;
    }

    public void removeFriend(long currUID, long friendUID)
            throws Exception {

        String query = "{call uspDeleteFriend(?,?)}";

        Connection conn = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = conn.prepareCall(query);
            cs.setLong(USER_ID_PARAM, currUID);
            cs.setLong(FRIEND_ID_PARAM, friendUID);
            cs.execute();
        } finally {
            conn.close();
        }
    }

    public boolean isFriend(long source_uid, long uid) throws Exception {

        String query = "{?= call uspIsFriend(?,?)} ";
        Connection conn = DBPoolConnection.getConnection();
        int ret = -1;
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong(USER_ID_PARAM, source_uid);
            cs.setLong(FRIEND_ID_PARAM, uid);
            cs.execute();

            ret = cs.getInt(1);

            cs.close();
        } finally {
            conn.close();
        }

        return ret == 1;
    }

    public Vector<UserEntity> getFrientList(long userId, boolean isOnline) throws Exception {
        Vector<UserEntity> users = new Vector<UserEntity>();

        String query = "{call uspGetFriends(?, ?)}";
        Connection conn = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = conn.prepareCall(query);
            cs.setLong(USER_ID_PARAM, userId);
            cs.setBoolean(IS_ONLINE_PARAM, isOnline);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    UserEntity res = new UserEntity();
                    res.mUid = rs.getLong("friendId");
                    res.mUsername = rs.getString("Name");
                    res.avatarID = rs.getInt("AvatarID");
                    res.level = rs.getInt("Level");
                    res.money = rs.getLong("Cash");
                    res.playsNumber = rs.getInt("WonPlaysNumber");
                    res.isLogin = rs.getBoolean("isOnline");
                    users.add(res);
                }

                rs.close();
            }

            cs.close();
        } finally {
            conn.close();
        }
        return users;
    }

    public List<UserEntity> getSocialFriends(long userId) throws SQLException {
        List<UserEntity> res = new ArrayList<UserEntity>();
        String query = "{ call uspGetSocialFriends(?) }";

        Connection con = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = con.prepareCall(query);
            cs.setLong(USER_ID_PARAM, userId);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                long friendId = rs.getLong("friendId");
                String fname = rs.getString("fName");
                long fcash = rs.getLong("fCash");
                boolean isOnline = rs.getBoolean("fOnline");
                long avFileId = rs.getLong("avatarFileId");
                UserEntity entity = new UserEntity();
                entity.mUsername = fname;
                entity.mUid = friendId;
                entity.money = fcash;
                entity.isOnline = isOnline;
                entity.avFileId = avFileId;
                res.add(entity);
            }

            rs.close();
            cs.close();
        } finally {
            con.close();
        }
        return res;
    }

    public List<SocialFriendEntity> getRequestFriends(long userId) throws SQLException {
        List<SocialFriendEntity> res = new ArrayList<SocialFriendEntity>();
        String query = "{ call uspGetRequestFriends(?) }";

        Connection con = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = con.prepareCall(query);
            cs.setLong(USER_ID_PARAM, userId);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                long friendId = rs.getLong("friendId");
                String fName = rs.getString("name");
                long avFileId = rs.getLong("avatarfileId");
                SocialFriendEntity entity = new SocialFriendEntity(friendId, fName, avFileId);
                res.add(entity);
            }
            rs.close();
            cs.close();
        } finally {
            con.close();
        }
        return res;
    }

    public int getNumRequestFriends(long userId) throws Exception {
        int ret = 0;
        String query = "{ call uspGetNumRequestFriends(?) }";

        Connection con = null;

        if (this.conn == null) {
            con = DBPoolConnection.getConnection();
        } else {
            con = this.conn;
        }

        try {
            CallableStatement cs = con.prepareCall(query);
            cs.setLong(USER_ID_PARAM, userId);
            ResultSet rs = cs.executeQuery();
            if (rs != null && rs.next()) {
                ret = rs.getInt("count");
                rs.close();
            }

            cs.clearParameters();

            cs.close();
        } finally {
            if (this.conn == null) {
                con.close();
            }
        }

        return ret;

    }

    public List<UserEntity> findFriends(boolean isMale, int cityId, int jobId, int fromYear, int toYear,
            int characterId, boolean hasAvatar, String name) throws SQLException {
        List<UserEntity> lstUsers = new ArrayList<UserEntity>();
        String query = "{ call uspFindFriends(?,?,?,?,?,?,?, ?) }";

        Connection con = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = con.prepareCall(query);
            cs.setBoolean(ISMALE_PARAM, isMale);
            cs.setInt(CITY_ID_PARAM, cityId);
            cs.setInt(JOB_ID_PARAM, jobId);
            cs.setInt(FROM_YEAR_PARAM, fromYear);
            cs.setInt(TO_YEAR_PARAM, toYear);
            cs.setInt(CHARACTER_ID_PARAM, characterId);
            cs.setBoolean(HAS_AVATAR_PARAM, hasAvatar);
            cs.setString(FRIEND_NAME_PARAM, name);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    UserEntity res = new UserEntity();
                    res.mUid = rs.getLong("UserID");
                    res.mUsername = rs.getString("Name");
                    res.mIsMale = rs.getBoolean("sex");
                    res.avFileId = rs.getInt("avatarFileId");
                    lstUsers.add(res);

                }

                rs.close();
            }

            cs.close();

        } finally {
            con.close();
        }
        return lstUsers;
    }

    public long guiTang(String phoneOrEmail, long userId, int deviceId, int positionGioiThieu, String userName, int partnerId) throws SQLException {
        String query = "{call uspInsertRefGioiThieu(?,?, ?, ?,?, ?)} ";
        Connection con = null;

        if (this.conn == null) {
            con = DBPoolConnection.getConnection();
        } else {
            con = this.conn;
        }
        long refGioiThieuId = -1;
//            Connection conn = DBPoolConnection.getConnection();

        try {

            CallableStatement cs = con.prepareCall(query);

            cs.setLong(USER_ID_PARAM, userId);
            cs.setString(PHONE_OR_EMAIL_PARAM, phoneOrEmail);
            cs.setInt(DEVICE_PARAM, deviceId);
            cs.setInt(POSITION_GIOI_THIEU_PARAM, positionGioiThieu);
            cs.setString(USER_NAME_PARAM, userName);
            cs.setInt(PARTNER_ID_PARAM, partnerId);
            cs.execute();

            ResultSet rs = cs.executeQuery();
            if (rs != null && rs.next()) {
                refGioiThieuId = rs.getLong("RefGioiThieuId");
            }

            cs.close();
        } finally {
            if (conn == null) {
                con.close();
            }
        }

        return refGioiThieuId;

    }

    public GioiThieuEntity getRefGioithieu(long refGioiThieuId) throws SQLException {

        String query = "{call uspGetRefGioithieu(?)} ";
        Connection con = null;

        if (this.conn == null) {
            con = DBPoolConnection.getConnection();
        } else {
            con = this.conn;
        }

//            Connection conn = DBPoolConnection.getConnection();
        GioiThieuEntity entity = null;

        try {

            CallableStatement cs = con.prepareCall(query);

            cs.setLong(REF_GIOI_THIEU_PARAM, refGioiThieuId);

            cs.execute();

            ResultSet rs = cs.executeQuery();
            if (rs != null && rs.next()) {

                long userId = rs.getLong("userId");
                String phoneOrEmail = rs.getString("phoneOrMail");
                int partnerId = rs.getInt("partnerId");
                entity = new GioiThieuEntity(userId, phoneOrEmail, partnerId);

            }

            cs.close();
        } finally {
            if (conn == null) {
                con.close();
            }
        }

        return entity;

    }

}
