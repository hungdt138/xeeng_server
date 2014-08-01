/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.MD5;
import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.data.AlertUserEntity;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.data.UserInfoEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import org.slf4j.Logger;

/**
 *
 * @author
 */
@SuppressWarnings("unused")
public class UserDB {

    private static final Logger mLog = LoggerContext.getLoggerFactory()
            .getLogger(UserDB.class);
    private static final String LOGIN_NAME_PARAM = "loginName";
    private static final String USER_ID_PARAM = "userId";
    private static final String CITY_ID_PARAM = "cityId";
    private static final String ADDRESS_PARAM = "address";
    private static final String JOB_ID_PARAM = "jobId";
    private static final String BIRTHDAY_PARAM = "birthday";
    private static final String HOBBY_PARAM = "hobby";
    private static final String NICK_SKYPE_PARAM = "nickSkype";
    private static final String NICK_YAHOO_PARAM = "nickYahoo";
    private static final String PHONE_NUMBER_PARAM = "phoneNumber";
    private static final String AVATAR_FILE_ID_PARAM = "avatarFileId";
    private static final String CHARACTER_ID_PARAM = "characterId";
    private static final String SCREEN_PARAM = "screen";
    private static final String MOBILE_VERSION_PARAM = "mobileVersion";
    private static final String IP_PARAM = "ip";
    private static final String DEVICE_ID_PARAM = "deviceId";
    private static final String TOP_USER_PARAM = "topUser";
    private static final String FROM_DATE_PARAM = "fromDate";
    private static final String TO_DATE_PARAM = "toDate";
    private static final String LOG_TYPE_ID_PARAM = "logTypeId";
    private static final String PASSWORD_PARAM = "password";
    private static final String PHONG_ID_PARAM = "phongId";
    private static final String AGE_PARAM = "age";
    private static final String SEX_PARAM = "sex";
    private static final String CHK_PARTNER_SIDE_PARAM = "chkPartnerSide";
    private static final String INTRODUCE_ID_PARAM = "introduceId";
    private static final String IS_MXH_PARAM = "isMxh";
    private static final String SERIAL_PARAM = "serial";
    private static final String DEVICE_TYPE_PARAM = "deviceType";
    private static final String DEVICE_UID = "deviceUid";
    private static final String EMAIL_PARAM = "email";
    private static final String PHONE_PARAM = "phone";
    private static final String PARTNER_ID_PARAM = "partnerId";
    private static final String KEY_ID_PARAM = "keyId";
    private static final String COLLECT_INFO_PARAM = "collectInfo";
    private static final String MONEY_PARAM = "money";
    private static final String DT_PARAM = "dt";
    private static final String TYPE_PARAM = "type";
    private static final String FILE_ID_PARAM = "fileId";
    private static final String STATUS_PARAM = "stt";
    private static final String REF_CODE_PARAM = "refCode";
    private static final String REGIST_TIME = "registerTime";
    private static final String USERNAME_PARAM = "username";
    private static final String CMND_PARAM = "cmnd";
    private static final String XE_PHONE_NUMBER_PARAM = "xePhoneNumber";

    private static List<UserEntity> lstBotUser;
    private Connection conn;

    public UserDB() {
    }

    public UserDB(Connection con) {
        this.conn = con;
    }

    public void clearLogin() throws SQLException {

        String query = "{ call uspClearLogin() }";
        Connection con = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = con.prepareCall(query);
            cs.execute();
            cs.close();
        } finally {
            con.close();
        }

    }

    public void updateGameEvent(long uid, int gameID) throws SQLException {
        String query = "{ call UpdateGameEventUser(?,?) }";
        Connection con = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = con.prepareCall(query);
            cs.clearParameters();
            cs.setLong(1, uid);
            cs.setInt(2, gameID);
            cs.executeUpdate();
            cs.close();
            mLog.debug("Update Event: " + uid + ":" + gameID);
        } finally {
            con.close();
        }

    }

    public String loginEvent(long userId, String version) throws DBException, SQLException {
        String query = "{call uspLoginEvent(?, ?) }";
        Connection con = DBPoolConnection.getConnection();
        String ret = "-1";
        try {
            CallableStatement cs = con.prepareCall(query);
            //cs.registerOutParameter(1, Types.NVARCHAR);

            cs.setLong(1, userId);
            cs.setString(2, version);
            ResultSet rs = cs.executeQuery();
            if (rs != null && rs.next()) {
                ret = rs.getString("result");
            }
            cs.close();
        } finally {
            con.close();
        }
        return ret;
    }

    public static void reload() {
        try {
            lstBotUser = getBotUser();
        } catch (Exception ex) {
            mLog.error(ex.getMessage());
        }
    }

    public boolean checkBotUser(long userId) {
        /*
         int botSize = lstBotUser.size();
         for (int i = 0; i < botSize; i++) {
         UserEntity entity = lstBotUser.get(i);
         if (entity.mUid == userId) {
         return true;
         }

         }*/

        return false;
    }

    public UserEntity flashLogin(String loginName, String password) throws SQLException {
        UserEntity res = null;
        String query = "{ call uspFlashLogin(?, ?) }";
        Connection con = DBPoolConnection.getConnection();
        ResultSet rs = null;
        CallableStatement cs = null;
        try {

            cs = con.prepareCall(query);
            cs.clearParameters();

            cs.setString(1, loginName);
            cs.setString(2, password);

            rs = cs.executeQuery();

            if (rs != null && rs.next()) {
                res = new UserEntity();
                //res.mPassword = rs.getString("Password");

                res.mUid = rs.getLong("UserID");

                res.mUsername = rs.getString("Name");
                res.mPassword = rs.getString("password");
//                res.mAge = rs.getInt("Age");

                //res.lastLogin = rs.getDatetime("LastTime");
                res.lastLogin = rs.getTimestamp("lastLogin");

//                res.mIsMale = rs.getInt("Sex")==1;
//                res.lastMatch=rs.getLong("lastMatch");
                //res.avatarID = rs.getInt("AvatarID");
                res.level = rs.getInt("Level");
                res.money = rs.getLong("Cash");
                res.playsNumber = rs.getInt("WonPlaysNumber");
//                res.avatarF = rs.getString("avatarF");
//                res.avatarM = rs.getString("avatarM");
                res.cellPhone = rs.getString("PhoneNumber");
                res.experience = rs.getInt("experience");
//                res.avatarVersion = rs.getString("avatarVersion");
                res.isOnline = rs.getBoolean("isOnline");
                res.vipId = rs.getInt("vipId");
                res.vipName = "Vip " + Integer.toString(res.vipId);
                rs.close();

            }

            cs.clearParameters();
            cs.close();

        } catch (Throwable ex) {
            con.close();
            con = DBPoolConnection.getConnection();
            cs = con.prepareCall(query);
//            cs.clearParameters();

            cs.setString(1, loginName);
            cs.setString(2, password);

            rs = cs.executeQuery();

            if (rs != null && rs.next()) {
                res = new UserEntity();
                //res.mPassword = rs.getString("Password");

                res.mUid = rs.getLong("UserID");

                res.mUsername = rs.getString("Name");
                res.mPassword = rs.getString("password");

//                res.mAge = rs.getInt("Age");
                //res.lastLogin = rs.getDatetime("LastTime");
                res.lastLogin = rs.getTimestamp("lastLogin");

//                res.mIsMale = rs.getInt("Sex")==1;
//                res.lastMatch=rs.getLong("lastMatch");
                //res.avatarID = rs.getInt("AvatarID");
                res.level = rs.getInt("Level");
                res.money = rs.getLong("Cash");
                res.playsNumber = rs.getInt("WonPlaysNumber");
//                res.avatarF = rs.getString("avatarF");
//                res.avatarM = rs.getString("avatarM");
                res.cellPhone = rs.getString("PhoneNumber");
                res.experience = rs.getInt("experience");
//                res.avatarVersion = rs.getString("avatarVersion");

                res.isOnline = rs.getBoolean("isOnline");
                res.vipId = rs.getInt("vipId");
                res.vipName = "Vip " + Integer.toString(res.vipId);
                try {
                    res.refCode = rs.getInt("refCode");
                } catch (Throwable e) {

                }
                rs.close();

            }
            cs.clearParameters();
            cs.close();

        } finally {

            con.close();
        }
        return res;

    }

    public UserEntity login(String loginName, String password, String device,
            String mobileVersion, String screen, String ip, int partnerId, boolean isMxh) throws SQLException {

        UserEntity res = null;
        String query = "{ call uspLogin(?, ?, ?, ?, ?,?, ?, ?) }";
        Connection con = DBPoolConnection.getConnection();
        ResultSet rs = null;
        CallableStatement cs = null;
        try {

            cs = con.prepareCall(query);
            cs.setString(1, loginName);
            cs.setString(2, password);
            cs.setString(3, device);
            cs.setString(4, mobileVersion);
            cs.setString(5, screen);
            cs.setString(6, ip);
            cs.setInt(7, partnerId);
            cs.setBoolean(8, isMxh);

            rs = cs.executeQuery();

            if (rs != null && rs.next()) {
                res = new UserEntity();
//                res.mPassword = rs.getString("Password");
                res.mUid = rs.getLong("UserID");
                //System.out.println("======================tuanda:" + res.mUid);
                res.mUsername = rs.getString("Name");
                res.loginName = rs.getString("LoginName");
                res.mPassword = password;
                //res.mAge = rs.getInt("Age");
                //res.lastLogin = rs.getDatetime("LastTime");
                res.lastLogin = rs.getTimestamp("lastLogin");
                res.mIsMale = rs.getBoolean("sex");
                //res.lastMatch=rs.getLong("lastMatch");
                res.avatarID = rs.getInt("AvatarID");
                res.level = rs.getInt("Level");
                res.money = rs.getLong("Cash");
                res.xeeng = rs.getLong("Xeeng");
                res.playsNumber = rs.getInt("WonPlaysNumber");
//                res.avatarF = rs.getString("avatarF");
//                res.avatarM = rs.getString("avatarM");
                res.cellPhone = rs.getString("PhoneNumber");
                res.isActive = rs.getBoolean("isActive");
                res.experience = rs.getInt("experience");
                //res.avatarVersion = rs.getString("avatarVersion");
                res.isOnline = rs.getBoolean("isOnline");
                res.vipId = rs.getInt("vipId");

                res.avFileId = rs.getLong("avatarFileId");
                res.biaFileId = rs.getLong("biaFileId");
                res.partnerId = rs.getInt("partnerId");
                res.stt = rs.getString("status");

                res.cmnd = rs.getString("cmnd");
                res.xePhoneNumber = rs.getString("xePhoneNumber");
                res.isLocked = rs.getBoolean("isLock");
                res.setLockExpired(rs.getDate("lockExpired"));
                res.setChatLockExpired(rs.getDate("chatLockExpired"));

                res.vipName = "Vip " + Integer.toString(res.vipId);
                try {
                    res.refCode = rs.getInt("refCode");
                } catch (Throwable e) {

                }
                rs.close();

            }
            cs.clearParameters();
            cs.close();

        } catch (Throwable ex) {
            con.close();
            mLog.error(ex.getMessage(), ex);

            con = DBPoolConnection.getConnection();
            cs = con.prepareCall(query);
//            cs.clearParameters();

            cs.setString(1, loginName);
            cs.setString(2, password);
            cs.setString(3, device);
            cs.setString(4, mobileVersion);
            cs.setString(5, screen);
            cs.setString(6, ip);
            cs.setInt(7, partnerId);

            rs = cs.executeQuery();

            if (rs != null && rs.next()) {
                res = new UserEntity();
//                res.mPassword = rs.getString("Password");
                res.mUid = rs.getLong("UserID");
                //System.out.println("======================tuanda:" + res.mUid);
                res.mUsername = rs.getString("Name");
                res.mPassword = password;
                //res.mAge = rs.getInt("Age");
                //res.lastLogin = rs.getDatetime("LastTime");
                res.lastLogin = rs.getTimestamp("lastLogin");
                res.mIsMale = rs.getBoolean("Sex");
                //res.lastMatch=rs.getLong("lastMatch");
                res.avatarID = rs.getInt("AvatarID");
                res.level = rs.getInt("Level");
                res.money = rs.getLong("Cash");
                res.xeeng = rs.getLong("Xeeng");
                res.playsNumber = rs.getInt("WonPlaysNumber");
//                res.avatarF = rs.getString("avatarF");
//                res.avatarM = rs.getString("avatarM");
                res.cellPhone = rs.getString("PhoneNumber");
                res.experience = rs.getInt("experience");
                //res.avatarVersion = rs.getString("avatarVersion");
                res.isOnline = rs.getBoolean("isOnline");
                res.vipId = rs.getInt("vipId");

                res.vipName = "Vip " + Integer.toString(res.vipId);
                try {
                    res.refCode = rs.getInt("refCode");
                } catch (Throwable e) {

                }
                rs.close();

            }
            cs.clearParameters();
            cs.close();

        } finally {

            con.close();
        }
        return res;
    }

    public UserEntity guestLogin(String deviceUid, int partnerId, int refCode, String mobileVersion) throws SQLException {
        UserEntity res = null;

        String query = "{ call uspGuestLogin(?, ?, ?, ?, ?) }";

        mLog.debug(query + deviceUid + partnerId + refCode + mobileVersion);

        Connection con = DBPoolConnection.getConnection();
        ResultSet rs = null;
        CallableStatement cs = null;
        try {
            cs = con.prepareCall(query);
            cs.setString(1, deviceUid);
            cs.setString(2, MD5.toMD5(deviceUid));
            cs.setInt(3, partnerId);
            cs.setInt(4, refCode);
            cs.setString(5, mobileVersion);

            rs = cs.executeQuery();

            if (rs != null && rs.next()) {
                res = new UserEntity();

                res.mUid = rs.getLong("UserID");
                res.mUsername = rs.getString("Name");
                res.loginName = rs.getString("Name");
                res.mPassword = rs.getString("password");
                res.lastLogin = rs.getTimestamp("lastLogin");
                res.mIsMale = rs.getBoolean("sex");
                res.avatarID = rs.getInt("AvatarID");
                res.level = rs.getInt("Level");
                res.money = rs.getLong("Cash");
                res.xeeng = rs.getLong("Xeeng");
                res.playsNumber = rs.getInt("WonPlaysNumber");
                res.cellPhone = rs.getString("PhoneNumber");
                res.isActive = rs.getBoolean("isActive");
                res.experience = rs.getInt("experience");
                res.isOnline = rs.getBoolean("isOnline");
                res.vipId = rs.getInt("vipId");
                res.avFileId = rs.getLong("avatarFileId");
                res.biaFileId = rs.getLong("biaFileId");
                res.partnerId = rs.getInt("partnerId");
                res.stt = rs.getString("status");
                res.vipName = "Vip " + Integer.toString(res.vipId);
                res.refCode = rs.getInt("refCode");
                res.isLocked = rs.getBoolean("isLock");
                res.setLockExpired(rs.getDate("lockExpired"));
                res.setChatLockExpired(rs.getDate("chatLockExpired"));

                rs.close();
            }
            cs.clearParameters();
            cs.close();

        } catch (Throwable ex) {
            con.close();
            mLog.error(ex.getMessage(), ex);
        } finally {
            con.close();
        }
        return res;
    }
    
    // Added by ThangTD
    public UserEntity newGuestLogin(String deviceUid, int partnerId, int refCode, String mobileVersion, int regTime) throws SQLException {
        UserEntity res = null;

        String query = "{call uspXEGuestLogin(?, ?, ?, ?, ?, ?)}";

        mLog.debug(query + deviceUid + partnerId + refCode + mobileVersion);

        Connection con = DBPoolConnection.getConnection();
        ResultSet rs = null;
        CallableStatement cs = null;
        try {
            cs = con.prepareCall(query);
            cs.setString(1, deviceUid);
            cs.setString(2, MD5.toMD5(deviceUid));
            cs.setInt(3, partnerId);
            cs.setInt(4, refCode);
            cs.setString(5, mobileVersion);
            cs.setInt(6, regTime);

            rs = cs.executeQuery();

            if (rs != null && rs.next()) {
                res = new UserEntity();

                res.mUid = rs.getLong("UserID");
                res.mUsername = rs.getString("Name");
                res.loginName = rs.getString("Name");
                res.mPassword = rs.getString("password");
                res.lastLogin = rs.getTimestamp("lastLogin");
                res.mIsMale = rs.getBoolean("sex");
                res.avatarID = rs.getInt("AvatarID");
                res.level = rs.getInt("Level");
                res.money = rs.getLong("Cash");
                res.xeeng = rs.getLong("Xeeng");
                res.playsNumber = rs.getInt("WonPlaysNumber");
                res.cellPhone = rs.getString("PhoneNumber");
                res.isActive = rs.getBoolean("isActive");
                res.experience = rs.getInt("experience");
                res.isOnline = rs.getBoolean("isOnline");
                res.vipId = rs.getInt("vipId");
                res.avFileId = rs.getLong("avatarFileId");
                res.biaFileId = rs.getLong("biaFileId");
                res.partnerId = rs.getInt("partnerId");
                res.stt = rs.getString("status");
                res.vipName = "Vip " + Integer.toString(res.vipId);
                res.refCode = rs.getInt("refCode");
                res.isLocked = rs.getBoolean("isLock");
                res.setLockExpired(rs.getDate("lockExpired"));
                res.setChatLockExpired(rs.getDate("chatLockExpired"));

                rs.close();
            }
            cs.clearParameters();
            cs.close();

        } catch (Throwable ex) {
            con.close();
            mLog.error(ex.getMessage(), ex);
        } finally {
            con.close();
        }
        return res;
    }

    public UserEntity faceLogin(String faceId, int partnerId, int refCode, String mobileVersion) throws SQLException {
        UserEntity res = null;
        String query = "{ call uspFaceLogin(?, ?, ?, ?, ?) }";

        mLog.debug(query + faceId + partnerId + refCode + mobileVersion);

        Connection con = DBPoolConnection.getConnection();
        ResultSet rs = null;
        CallableStatement cs = null;
        try {

            cs = con.prepareCall(query);
            cs.setString(1, faceId);
            cs.setString(2, MD5.toMD5("facebook" + faceId));
            cs.setInt(3, partnerId);
            cs.setInt(4, refCode);
            cs.setString(5, mobileVersion);

            rs = cs.executeQuery();

            if (rs != null && rs.next()) {
                res = new UserEntity();

                res.mUid = rs.getLong("UserID");
                res.mUsername = rs.getString("Name");
                res.mPassword = rs.getString("password");
                res.lastLogin = rs.getTimestamp("lastLogin");
                res.mIsMale = rs.getBoolean("sex");
                res.avatarID = rs.getInt("AvatarID");
                res.level = rs.getInt("Level");
                res.money = rs.getLong("Cash");
                res.playsNumber = rs.getInt("WonPlaysNumber");
                res.cellPhone = rs.getString("PhoneNumber");
                res.isActive = rs.getBoolean("isActive");
                res.experience = rs.getInt("experience");
                res.isOnline = rs.getBoolean("isOnline");
                res.vipId = rs.getInt("vipId");
                res.avFileId = rs.getLong("avatarFileId");
                res.biaFileId = rs.getLong("biaFileId");
                res.partnerId = rs.getInt("partnerId");
                res.stt = rs.getString("status");
                res.vipName = "Vip " + Integer.toString(res.vipId);
                res.refCode = rs.getInt("refCode");

                rs.close();

            }
            cs.clearParameters();
            cs.close();

        } catch (Throwable ex) {

            con.close();
            mLog.error(ex.getMessage(), ex);
        } finally {
            con.close();
        }
        return res;
    }

    public void logout(long userId, String collectInfo) throws SQLException {

        String query = "{ call uspLogout(?,?) }";
        Connection con = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = con.prepareCall(query);
            cs.clearParameters();
            cs.setLong(USER_ID_PARAM, userId);
            cs.setString(COLLECT_INFO_PARAM, collectInfo);
            cs.execute();
            cs.clearParameters();
            cs.close();
        } finally {
            con.close();
        }

    }

    public void insertDeadSession(long userId, int phongId) throws SQLException {
        String query = "{ call uspInsertDeadSession(?,?) }";
        Connection con = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = con.prepareCall(query);
            cs.clearParameters();
            cs.setLong(USER_ID_PARAM, userId);
            cs.setInt(PHONG_ID_PARAM, phongId);
            cs.execute();
            cs.clearParameters();
            cs.close();
        } finally {
            con.close();
        }

    }

    public UserEntity getUserInfo(String username) throws SQLException {
        String nameParame = "loginName";
        UserEntity res = new UserEntity();
        String query = "{ call uspGetUserInfoByName(?) }";
        Connection con = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = con.prepareCall(query);

            cs.setString(nameParame, username);

            ResultSet rs = cs.executeQuery();

            if (rs != null && rs.next()) {
                res.mPassword = rs.getString("Password");
                res.mUid = rs.getLong("UserID");
                res.loginName = rs.getString("loginName");
                res.lastLogin = rs.getDate("lastLogin");
                res.avatarID = rs.getInt("AvatarID");
                res.level = rs.getInt("Level");
                res.money = rs.getLong("Cash");
                res.xeeng = rs.getLong("Xeeng");

                res.playsNumber = rs.getInt("WonPlaysNumber");
                res.experience = rs.getInt("experience");
                res.avatarID = rs.getInt("avatarID");
                res.avFileId = rs.getLong("avatarFileId");
                res.biaFileId = rs.getLong("biaFileId");
                res.vipId = rs.getInt("vipId");
                res.isActive = rs.getBoolean("isActive");
                res.partnerId = rs.getInt("partnerId");
                res.cellPhone = rs.getString("phoneNumber");
                res.vipName = "Vip " + Integer.toString(res.vipId);//rs.getString("vipName");
                res.stt = rs.getString("status");

                try {
                    res.refCode = rs.getInt("refCode");
                } catch (Throwable e) {

                }

                try {
                    res.mUsername = rs.getString("Name");
                    res.cmnd = rs.getString("cmnd");
                    res.xePhoneNumber = rs.getString("xePhoneNumber");
                } catch (SQLException ex) {
                }

                rs.close();
            }

            cs.clearParameters();

            cs.close();
        } finally {
            con.close();
        }

        if (res.loginName == null) {
            return null;
        } else {
            return res;
        }
    }

    public UserEntity getUserInfoNoException(long uid) {
        try {
            return getUserInfo(uid);
        } catch (SQLException ex) {
            return null;
        }
    }

    public UserEntity getUserInfo(long uid) throws SQLException {
        String nameParame = "uid";
        UserEntity res = new UserEntity();
        String query = "{ call uspGetUserInfoByUid(?) }";
        Connection con;
        if (conn == null) {
            con = DBPoolConnection.getConnection();
        } else {
            con = conn;
        }
        try {
            CallableStatement cs = con.prepareCall(query);

            cs.setLong(nameParame, uid);

            ResultSet rs = cs.executeQuery();

            if (rs != null && rs.next()) {
                res.mPassword = rs.getString("Password");
                res.mUid = rs.getLong("UserID");
                res.loginName = rs.getString("loginName");
                res.lastLogin = rs.getDate("lastLogin");
                res.avatarID = rs.getInt("AvatarID");
                res.level = rs.getInt("Level");
                res.money = rs.getLong("Cash");
                res.xeeng = rs.getLong("Xeeng");
                res.mIsMale = rs.getInt("sex") == 1;
                res.playsNumber = rs.getInt("WonPlaysNumber");
                res.experience = rs.getInt("experience");
                res.avatarID = rs.getInt("avatarID");
                res.avFileId = rs.getLong("avatarFileId");
                res.biaFileId = rs.getLong("biaFileId");
                res.vipId = rs.getInt("vipId");
                res.isActive = rs.getBoolean("isActive");
                res.partnerId = rs.getInt("partnerId");
                res.cellPhone = rs.getString("phoneNumber");
                res.vipName = "Vip " + Integer.toString(res.vipId);//rs.getString("vipName");
                res.stt = rs.getString("status");

                try {
                    res.refCode = rs.getInt("refCode");
                } catch (Throwable e) {

                }

                try {
                    res.mUsername = rs.getString("Name");
                    res.cmnd = rs.getString("cmnd");
                    res.xePhoneNumber = rs.getString("xePhoneNumber");
                } catch (SQLException ex) {
                }

                rs.close();
            }

            cs.clearParameters();
            cs.close();
        } finally {
            if (conn == null) {
                con.close();// close init connection
            }
        }
        return res;
    }

    public boolean userIsExist(String loginName) throws DBException, SQLException {
        boolean result = false;
        String query = "SELECT Name FROM [user] WHERE Name = ?";
        Connection con = DBPoolConnection.getConnection();

        try {
            PreparedStatement st = con.prepareStatement(query);
            st.setString(1, loginName);
            ResultSet rs = st.executeQuery();
            if (rs != null && rs.next()) {
                result = true;
            }

            rs.close();
            st.close();

        } finally {

            con.close();
        }

        return result;
    }

    public long userTourIsExist(long uid, int tID) throws DBException, SQLException {
        long result = 0;
        String query = "SELECT uid FROM [UserTour] WHERE uid=? and tourID=?;";
        Connection con = DBPoolConnection.getConnection();

        try {
            PreparedStatement st = con.prepareStatement(query);
            st.setLong(1, uid);
            st.setInt(2, tID);
            ResultSet rs = st.executeQuery();
            if (rs != null && rs.next()) {
                result = rs.getLong("uid");
            }

            rs.close();
            st.close();

        } finally {

            con.close();
        }

        return result;
    }

    public long updateUserMoney(long money, boolean isWin, long uid, String desc, int experience, int logTypeId)
            throws DBException, SQLException {

        long cash = 0;
        String query = "{ call uspUpdateUserMoney(?,?,?,?, ?, ?) }";
        //SimpleConnnection conn = MasterPoolConnection.checkOut();
        Connection con = null;
        if (conn != null) {
            con = this.conn;//reuser connection
        } else {
            con = DBPoolConnection.getConnection();
        }

        try {
            CallableStatement cs = con.prepareCall(query);
            cs.setInt(1, (int) uid);
            cs.setInt(2, (int) (isWin ? money : -money));
            cs.setString(3, desc);
            cs.setInt(4, logTypeId);//Default log type id = 4;
            cs.setInt(6, experience);//Default log type id = 4;
            cs.setBoolean(5, isWin);

            ResultSet rs = cs.executeQuery();
            if (rs != null && rs.next()) {
                cash = rs.getLong("cash");
            }

            //conn.commit();
            cs.clearParameters();
            rs.close();
            cs.close();
        } finally {
            if (this.conn == null) {
                con.close(); // close connection which is opened from this function
            }
        }

        //update cash in cache. It shoudnt do this case in 
        CacheUserInfo.updateUserCashFromDB(uid, cash);

        return cash;
    }

    public long updateUserMoneyForTP(long money, boolean isWin, long uid, String desc, int experience, int logTypeId, int achievementQuestion)
            throws DBException, SQLException {

        long cash = 0;
        String query = "{ call uspUpdateUserMoneyForTP(?,?,?,?, ?, ?,?) }";
        //SimpleConnnection conn = MasterPoolConnection.checkOut();
        Connection con = null;
        if (conn != null) {
            con = this.conn;//reuser connection
        } else {
            con = DBPoolConnection.getConnection();
        }

        try {
            CallableStatement cs = con.prepareCall(query);
            cs.setLong(1, uid);
            cs.setInt(2, (int) (isWin ? money : -money));
            cs.setString(3, desc);
            cs.setInt(4, logTypeId);//Default log type id = 4;
            cs.setInt(6, experience);//Default log type id = 4;
            cs.setBoolean(5, isWin);
            cs.setInt(7, achievementQuestion);//

            ResultSet rs = cs.executeQuery();
            if (rs != null && rs.next()) {
                cash = rs.getLong("cash");
            }

            //conn.commit();
            cs.clearParameters();
            rs.close();
            cs.close();
        } finally {
            if (this.conn == null) {
                con.close(); // close connection which is opened from this function
            }
        }

        //update cash in cache. It shoudnt do this case in 
        CacheUserInfo.updateUserCashFromDB(uid, cash);

        return cash;
    }

    public void notMinus()
            throws SQLException {

        String query = "{call uspNotMinus()}";
        Connection con = null;
        if (this.conn != null) {
            con = conn;
        } else {
            con = DBPoolConnection.getConnection();
        }

        try {
            CallableStatement cs = con.prepareCall(query);
            cs.execute();
            cs.close();
        } finally {
            if (this.conn == null) {
                con.close();
            }
        }
    }

    public long transferMoney(long s_uid, String d_name, long money, int limit, int tax) throws SQLException, DBException {
        String query = "{?= call uspTransfer(?,?,?,?,?) }";

        Connection conn = DBPoolConnection.getConnection();
        long d_uid = -1;
        try {
            CallableStatement cs = conn.prepareCall(query);
            cs.registerOutParameter(1, Types.BIGINT);
            cs.setLong(3, s_uid);
            cs.setString(4, d_name);
            cs.setLong(2, money);
            cs.setInt(5, limit);
            cs.setInt(6, tax);
            cs.execute();

            //conn.commit();
            d_uid = cs.getLong(1);
            cs.close();
        } finally {
            conn.close();
        }

        return d_uid;

    }

    public int changePassword(long userId, String oldPassword, String newPassword) throws DBException, SQLException {
        String query = "{?= call uspUpdateUserInfo(?,?,?,?, ?, ?, ?) }";
        Connection con = DBPoolConnection.getConnection();
        int ret = -1;
        try {
            CallableStatement cs = con.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);

            cs.setLong(2, userId);

            cs.setString(3, null);

            cs.setString(4, null);

            cs.setString(5, oldPassword);

            cs.setString(6, newPassword);

            cs.setString(7, null);

            cs.setString(8, null);

            cs.execute();
            ret = cs.getInt(1);

            cs.close();
        } finally {
            con.close();
        }
        return ret;
    }

    public int updateUserInfo(long userId, String phoneNumber, String email, String oldPassword, String newPassword,
            int age, boolean sex) throws DBException, SQLException {
        String query = "{?= call uspUpdateUserInfo(?,?,?,?, ?, ?, ?) }";
        Connection con = DBPoolConnection.getConnection();
        int ret = -1;
        try {
            CallableStatement cs = con.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);

            cs.setLong(2, userId);

            cs.setString(3, null);

            if (email == null || email.equals("")) {
                cs.setString(4, null);
            } else {
                cs.setString(4, email);
            }

            if (oldPassword == null || oldPassword.equals("")) {
                cs.setString(5, null);
            } else {
                cs.setString(5, oldPassword);
            }
            if (newPassword == null || newPassword.equals("")) {
                cs.setString(6, null);
            } else {
                cs.setString(6, newPassword);
            }

            if (age < 0) {
                cs.setString(7, null);
            } else {
                cs.setInt(7, age);
            }

            cs.setBoolean(8, sex);

            cs.execute();
            ret = cs.getInt(1);

            cs.close();
        } finally {
            con.close();
        }
        return ret;
    }

    public boolean updateUserPassword(long userId, String passwordMD5) throws DBException, SQLException {
        String query = "{call uspUpdateUserPassword(?,?) }";
        Connection con = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = con.prepareCall(query);

            cs.setLong(1, userId);
            cs.setString(2, passwordMD5);

            cs.execute();
            cs.close();

            return true;
        } catch (SQLException ex) {
            mLog.debug("[+] Exception", ex);
            return false;
        } finally {
            con.close();
        }
    }

    public void updateUserMxhInfo(long userId, int cityId, String address, int jobId, Date birthday, String hobby,
            String nickSkype, String nickYahoo, String phoneNumber, boolean sex, long avatarFileId, String status, int characterId) throws SQLException {

        String query = "{call uspUpdateMxhUserInfo(?,?,?,?, ?, ?, ?, ?, ?, ?,?,?) }";
        Connection con = DBPoolConnection.getConnection();

        try {
            CallableStatement cs = con.prepareCall(query);

            cs.setLong(USER_ID_PARAM, userId);
//            cs.setBoolean(SEX_PARAM, sex);

            if (cityId == 0) {
                cs.setString(CITY_ID_PARAM, null);
            } else {
                cs.setInt(CITY_ID_PARAM, cityId);
            }

            if (address == null || address.equals("")) {
                cs.setString(ADDRESS_PARAM, null);
            } else {
                cs.setString(ADDRESS_PARAM, address);
            }

            if (jobId == 0) {
                cs.setString(JOB_ID_PARAM, null);
            } else {
                cs.setInt(JOB_ID_PARAM, jobId);
            }

            if (birthday == null || birthday.equals("")) {
                cs.setString(BIRTHDAY_PARAM, null);
            } else {
                cs.setTimestamp(BIRTHDAY_PARAM, new Timestamp(birthday.getTime()));
            }

            if (hobby == null || hobby.equals("")) {
                cs.setString(HOBBY_PARAM, null);
            } else {
                cs.setString(HOBBY_PARAM, hobby);
            }

            if (nickSkype == null || nickSkype.equals("")) {
                cs.setString(NICK_SKYPE_PARAM, null);
            } else {
                cs.setString(NICK_SKYPE_PARAM, nickSkype);
            }

            if (nickYahoo == null || nickYahoo.equals("")) {
                cs.setString(NICK_YAHOO_PARAM, null);
            } else {
                cs.setString(NICK_YAHOO_PARAM, nickSkype);
            }

            if (phoneNumber == null || phoneNumber.equals("")) {
                cs.setString(PHONE_NUMBER_PARAM, null);
            } else {
                cs.setString(PHONE_NUMBER_PARAM, phoneNumber);
            }

            if (avatarFileId == 0) {
                cs.setString(AVATAR_FILE_ID_PARAM, null);
            } else {
                cs.setLong(AVATAR_FILE_ID_PARAM, avatarFileId);
            }

            if (status == null || status.equals("")) {
                cs.setString(STATUS_PARAM, null);
            } else {
                cs.setString(STATUS_PARAM, status);
            }

            cs.setInt(CHARACTER_ID_PARAM, characterId);

            cs.execute();

            cs.close();
        } finally {
            con.close();
        }

    }

    public long registerUser(String loginName, String password, int sex, String phone, int partnerId,
            long introduceId, boolean isMxh, String refCode, int registerTime, String deviceUid, String username, String cmnd, String xePhoneNumber) throws Exception {
        String query = "{?= call uspRegUser(?,?,?,?,?,?,?,?,?,?,?,?,?) }";

        mLog.debug("Regist data " + loginName + password + deviceUid);

        Connection conn = DBPoolConnection.getConnection();

        CallableStatement cs = conn.prepareCall(query);
        try {

            cs.clearParameters();
            cs.registerOutParameter(1, java.sql.Types.BIGINT);
            cs.setString(LOGIN_NAME_PARAM, loginName);
            cs.setString(PASSWORD_PARAM, password);
            cs.setInt(SEX_PARAM, sex);
            cs.setLong(INTRODUCE_ID_PARAM, introduceId);
            cs.setString(PHONE_PARAM, phone);
            cs.setBoolean(IS_MXH_PARAM, isMxh);
            cs.setInt(PARTNER_ID_PARAM, partnerId);
            cs.setString(REF_CODE_PARAM, refCode);
            cs.setInt(REGIST_TIME, registerTime);
            cs.setString(DEVICE_UID, deviceUid);
            cs.setString(USERNAME_PARAM, username);
            cs.setString(CMND_PARAM, cmnd);
            cs.setString(XE_PHONE_NUMBER_PARAM, xePhoneNumber);

            cs.execute();

            long uid = cs.getLong(1);
            cs.close();
            return uid;
        } finally {
            conn.close();
        }

    }

    public List<UserEntity> getTopEvent(long userId) throws Exception {
        List<UserEntity> res = new ArrayList<UserEntity>();
        String query = "{ call uspGetTopEvent(?) }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.clearParameters();
            cs.setLong(USER_ID_PARAM, userId);
            ResultSet rs = cs.executeQuery();
            if (rs != null) {

                while (rs.next()) {

                    UserEntity user = new UserEntity();

                    user.mUid = rs.getLong("UserID");
                    //System.out.println("======================tuanda:" + res.mUid);
                    user.mUsername = rs.getString("Name");

                    user.money = rs.getLong("money");
                    user.avatarID = rs.getInt("avatarId");

                    res.add(user);

                }

                rs.close();
            }
        } finally {
            conn.close();
        }
        return res;
    }

    public int creditMoney(String userName, long money, Date dt) throws SQLException {
        String query = "{?= call uspCreditMoney(?,?,?) }";
        Connection conn = DBPoolConnection.getConnection();
        int result = -1;

        try {
            CallableStatement cs = conn.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(LOGIN_NAME_PARAM, userName);
            cs.setLong(MONEY_PARAM, money);
            cs.setTimestamp(DT_PARAM, new Timestamp(dt.getTime()));

            cs.execute();
            result = cs.getInt(1);
            return result;
        } finally {
            conn.close();
        }

    }

    public int freeTopup(long userId) throws SQLException {
        String query = "{?= call uspFreeTopup(?) }";
        Connection conn = DBPoolConnection.getConnection();
        int result = -1;

        try {
            CallableStatement cs = conn.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);

            cs.setLong(USER_ID_PARAM, userId);

            cs.execute();
            result = cs.getInt(1);
            return result;
        } finally {
            conn.close();
        }

    }

    public void setSocialAvatar(long uid, long fileId, int type) throws Exception {

        String query = "{call uspSetSocialAvatar(?,?,?) }";
        Connection conn = DBPoolConnection.getConnection();

        CallableStatement cs = conn.prepareCall(query);
        try {
            cs.clearParameters();
            cs.setLong(USER_ID_PARAM, uid);
            cs.setInt(TYPE_PARAM, type);
            cs.setLong(FILE_ID_PARAM, fileId);
            cs.execute();
            cs.close();
        } finally {
            conn.close();
        }

    }

    public void setStt(long uid, String stt) throws Exception {

        String query = "{call uspSetStt(?,?) }";
        Connection conn = DBPoolConnection.getConnection();

        CallableStatement cs = conn.prepareCall(query);
        try {
            cs.clearParameters();
            cs.setLong(USER_ID_PARAM, uid);
            cs.setString(STATUS_PARAM, stt);

            cs.execute();
            cs.close();
        } finally {
            conn.close();
        }

    }

    public List<UserEntity> getTopBlogger() throws Exception {
        List<UserEntity> res = new ArrayList<UserEntity>();
        String query = "{ call uspTopBlogger() }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.clearParameters();

            ResultSet rs = cs.executeQuery();
            if (rs != null) {

                while (rs.next()) {

                    UserEntity user = new UserEntity();

                    user.mUid = rs.getLong("UserID");
                    //System.out.println("======================tuanda:" + res.mUid);
                    user.mUsername = rs.getString("Name");

                    user.avFileId = rs.getLong("avatarFileId");
                    user.stt = rs.getString("status");

                    res.add(user);

                }

                rs.close();
            }
        } finally {
            conn.close();
        }
        return res;
    }

    public UserInfoEntity getUserMxhInfo(long userId) throws Exception {
        UserInfoEntity res = null;
        String query = "{ call uspGetUserMXHInfo(?) }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.clearParameters();
            cs.setLong(USER_ID_PARAM, userId);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {

                if (rs.next()) {
                    res = new UserInfoEntity();
                    res.address = rs.getString("address");
                    res.birthDay = rs.getDate("birthday");
                    res.characterId = rs.getInt("characterId");
                    res.cityId = rs.getInt("cityId");
                    res.hobby = rs.getString("hobby");
                    res.jobId = rs.getInt("jobId");
                    res.nickSkype = rs.getString("nickSkype");
                    res.nickYahoo = rs.getString("nickYahoo");

                }

                rs.close();
            }
        } finally {
            conn.close();
        }
        return res;
    }

    public List<UserEntity> getRichests(int partnerId) throws Exception {
        List<UserEntity> res = new Vector<UserEntity>(); //Track: udvUserInfo, TopRich storeprocedure
        String query = "{ call uspGetTopRich(?) }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.setInt(PARTNER_ID_PARAM, partnerId);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {

                while (rs.next()) {

                    UserEntity user = new UserEntity();
                    user.mPassword = rs.getString("Password");
                    user.mUid = rs.getLong("UserID");
                    //System.out.println("======================tuanda:" + res.mUid);
                    user.mUsername = rs.getString("Name");
                    user.mAge = rs.getInt("Age");
                    //res.lastLogin = rs.getDatetime("LastTime");
                    user.lastLogin = rs.getDate("LastTime");
                    user.mIsMale = rs.getInt("sex") == 1;
                    user.lastMatch = rs.getLong("lastMatch");
                    user.avatarID = rs.getInt("AvatarID");
                    user.level = rs.getInt("Level");
                    user.money = rs.getLong("Cash");
                    user.playsNumber = rs.getInt("WonPlaysNumber");
                    user.experience = rs.getInt("experience");

                    try {
                        user.refCode = rs.getInt("refCode");
                    } catch (Throwable e) {

                    }

                    try {
                        user.loginName = rs.getString("loginName");
                        user.cmnd = rs.getString("cmnd");
                        user.xePhoneNumber = rs.getString("xePhoneNumber");
                    } catch (SQLException ex) {
                    }

                    res.add(user);

                }

                rs.close();
            }
        } finally {
            conn.close();
        }

        return res;
    }

    public List<UserEntity> getBestPlayer(int partnerId) throws Exception {
        List<UserEntity> res = new Vector<UserEntity>();
        String query = "{ call uspGetTopLevel(?) }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.setInt(PARTNER_ID_PARAM, partnerId);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {

                while (rs.next()) {

                    UserEntity user = new UserEntity();
                    user.mPassword = rs.getString("Password");
                    user.mUid = rs.getLong("UserID");
                    //System.out.println("======================tuanda:" + res.mUid);

                    user.loginName = rs.getString("loginName");
                    user.mAge = rs.getInt("Age");
                    //res.lastLogin = rs.getDatetime("LastTime");
                    user.lastLogin = rs.getDate("LastTime");
                    user.mIsMale = rs.getInt("sex") == 1;
                    user.lastMatch = rs.getLong("lastMatch");
                    user.avatarID = rs.getInt("AvatarID");
                    user.level = rs.getInt("Level");
                    user.money = rs.getLong("Cash");
                    user.playsNumber = rs.getInt("WonPlaysNumber");
                    user.experience = rs.getInt("experience");

                    try {
                        user.refCode = rs.getInt("refCode");
                    } catch (Throwable e) {

                    }

                    try {
                        user.mUsername = rs.getString("Name");
                        user.cmnd = rs.getString("cmnd");
                        user.xePhoneNumber = rs.getString("xePhoneNumber");
                    } catch (SQLException ex) {
                    }

                    res.add(user);

                }

                rs.close();
            }
        } finally {
            conn.close();
        }
        return res;
    }

    public UserInfoEntity getUserMxhAccount(long userId) throws SQLException {
        UserInfoEntity res = null;
        String query = "{ call uspGetUserMXHInfo(?) }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.clearParameters();
            cs.setLong(USER_ID_PARAM, userId);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {

                if (rs.next()) {
                    res = new UserInfoEntity();
                    res.address = rs.getString("address");
                    res.birthDay = rs.getDate("birthday");
                    res.characterId = rs.getInt("characterId");
                    res.cityId = rs.getInt("cityId");
                    res.hobby = rs.getString("hobby");
                    res.jobId = rs.getInt("jobId");
                    res.nickSkype = rs.getString("nickSkype");
                    res.nickYahoo = rs.getString("nickYahoo");

                }

                rs.close();
            }
        } catch (Throwable e) {
            res = new UserInfoEntity();
            res.address = "Đang cập nhật";
            res.birthDay = null;
            res.characterId = 0;
            res.cityId = 0;
            res.hobby = "Đang cập nhật";
            res.jobId = 0;
            res.nickSkype = "Đang cập nhật";
            res.nickYahoo = "Đang cập nhật";
        } finally {
            conn.close();
        }
        return res;
    }

    public int useGiftcode(long userId, String serial, int type) throws SQLException {
        String query = "{?= call uspUseGiftCode(?,?,?) }";

        Connection conn = DBPoolConnection.getConnection();
        int ret = -1;
        try {
            CallableStatement cs = conn.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);

            cs.setLong(USER_ID_PARAM, userId);
            cs.setInt(DEVICE_TYPE_PARAM, type);
            cs.setString(SERIAL_PARAM, serial);

            cs.execute();

            ret = cs.getInt(1);

            cs.close();
        } finally {
            conn.close();
        }

        return ret;

    }

    public long bonusMoney(long userId, long money, int logTypeId) throws SQLException {
        String query = "{call uspBonusMoneyCC(?, ?, ?) }";
        Connection conn = DBPoolConnection.getConnection();
        long result = -1;

        try {
            CallableStatement cs = conn.prepareCall(query);
//                cs.registerOutParameter(1, Types.INTEGER );

            cs.setLong(USER_ID_PARAM, userId);
            cs.setLong(MONEY_PARAM, money);
            cs.setInt(LOG_TYPE_ID_PARAM, logTypeId);

            ResultSet rs = cs.executeQuery();
            if (rs != null && rs.next()) {
                result = rs.getLong("cash");
            }

            return result;
        } finally {
            conn.close();
        }

    }

    private static List<UserEntity> getBotUser() throws SQLException {
        List<UserEntity> res = new ArrayList<UserEntity>(); //Track: udvUserInfo, TopRich storeprocedure
        String query = "{ call uspGetAllBotUser() }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {

                while (rs.next()) {

                    UserEntity user = new UserEntity();
                    user.mPassword = rs.getString("Password");
                    user.mUid = rs.getLong("UserID");
                    //System.out.println("======================tuanda:" + res.mUid);
                    user.mUsername = rs.getString("Name");
//                            user.mAge = rs.getInt("Age");
//                            //res.lastLogin = rs.getDatetime("LastTime");
//                            user.lastLogin = rs.getDate("LastTime");
//                            user.mIsMale = rs.getInt("Sex")==1;
//                            user.lastMatch=rs.getLong("lastMatch");
//                            user.avatarID = rs.getInt("AvatarID");
//                            user.level = rs.getInt("Level");
//                            user.money = rs.getLong("Cash");
//                            user.playsNumber = rs.getInt("WonPlaysNumber");
//                            user.experience = rs.getInt("experience");

                    res.add(user);

                }

                rs.close();
            }
        } finally {
            conn.close();
        }

        return res;
    }

    public List<AlertUserEntity> getAlertUser(long userId) throws SQLException {
        List<AlertUserEntity> res = new ArrayList<AlertUserEntity>(); //Track: udvUserInfo, TopRich storeprocedure
        String query = "{ call uspGetAlertUser(?) }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.setLong(USER_ID_PARAM, userId);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {

                while (rs.next()) {

                    String content = rs.getString("content");
                    AlertUserEntity entity = new AlertUserEntity(content, userId);

                    res.add(entity);

                }

                rs.close();
            }
        } finally {
            conn.close();
        }

        return res;
    }

    public List<UserEntity> getTopGame(int partnerId, int gameId) throws Exception {
        List<UserEntity> res = new ArrayList<UserEntity>();
        String query = "{ call uspGetTopLevel(?) }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.setInt(PARTNER_ID_PARAM, partnerId);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {

                while (rs.next()) {

                    UserEntity user = new UserEntity();

                    user.mUid = rs.getLong("UserID");
                    //System.out.println("======================tuanda:" + res.mUid);
                    user.mUsername = rs.getString("Name");

                    user.playsNumber = rs.getInt("times"); //so lan tra loi cau hoi altp
                    user.point = rs.getInt("question"); //tra loi den cau so may

                    res.add(user);

                }

                rs.close();
            }
        } finally {
            conn.close();
        }
        return res;
    }

    public long auditUser(long userId,
            String mobileVersion, String screen, String ip, long deviceId) throws SQLException {

        long resultDevice = 0;
        String query = "{ call uspAuditUser(?, ?, ?, ?, ?) }";
        Connection con = DBPoolConnection.getConnection();
        ResultSet rs = null;
        CallableStatement cs = null;
        try {

            cs = con.prepareCall(query);
//            cs.clearParameters();

            cs.setLong(USER_ID_PARAM, userId);
            cs.setString(SCREEN_PARAM, null);
            cs.setString(MOBILE_VERSION_PARAM, mobileVersion);
            cs.setString(IP_PARAM, ip);
            cs.setLong(DEVICE_ID_PARAM, deviceId);

            rs = cs.executeQuery();

            if (rs != null && rs.next()) {
                resultDevice = rs.getLong("deviceId");
                rs.close();

            }
            cs.clearParameters();
            cs.close();

        } catch (Throwable ex) {

            cs.close();

        } finally {

            con.close();
        }
        return resultDevice;

    }
}
