/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;

//import allinone.room.NRoomEntity;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.chat.data.ChatRoomEntity;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.AdvertisingEntity;
import com.tv.xeeng.game.data.CharacterEntity;
import com.tv.xeeng.game.data.ChargingInfo;
import com.tv.xeeng.game.data.CityEntity;
import com.tv.xeeng.game.data.JobEntity;
import com.tv.xeeng.game.data.Message;
import com.tv.xeeng.game.data.UpdateLevelEntity;
import com.tv.xeeng.game.data.Utils;
import com.tv.xeeng.game.data.VersionEntity;
import com.tv.xeeng.server.Server;

/**
 *
 * @author 
 */
public class InfoDB {

//    private static List<VersionEntity> versions = null;
    private static List<VersionEntity> lstVersion;
//    private static Vector<AvatarEntity> lstAvatars;
    private static ArrayList<ChargingInfo> lstChargings;
    private static List<ChargingInfo> lstCards;
    private static List<AdvertisingEntity> lstAdvert;
    private static List<CityEntity> lstCities;
    private static List<JobEntity> lstJobs;
    private static List<CharacterEntity> lstCharacters;
    private static ChargingInfo getMobileCharging = null;
    private static final String SYSTEM_OBJECT_ID_PARAM = "systemObjectId";
    private static final String SYSTEM_OBJECT_RECORD_PARAM = "systemObjectRecordId";
    private static final String SOCIAL_ROLE_ID_PARAM = "socialRoleId";
    private static String personCommonInfo;
    static String lstTuocVi = null;
    final int dbFlag = 3;
    private Connection con = null;
    static List<UpdateLevelEntity> lstUpdateMoney = null;

    public InfoDB() {
    }

    public InfoDB(Connection con) {
        this.con = con;
    }

    /*
     *  singleton
     */
    public static void reload() {
        try {
            lstVersion = getVersionInfos();
            Utils.reloadSuperUser();
            lstTuocVi = Tuocvi();
//                lstAvatars = getAvatarList();
            lstChargings = getCharingInfo();
            reloadAdv();
            //lstAdvert = getAdvert();
            lstCards = getCardInfo();
            if (lstCharacters == null) {
                lstCities = getLstCity();
                lstCharacters = getLstCharacter();
                lstJobs = getLstJob();
                StringBuilder sb = new StringBuilder();
                int citySize = lstCities.size();
                for (int i = 0; i < citySize; i++) {
                    CityEntity entity = lstCities.get(i);
                    sb.append(entity.getCityId()).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(entity.getName()).append(AIOConstants.SEPERATOR_BYTE_2);
                }

                if (citySize > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append(AIOConstants.SEPERATOR_BYTE_3);
                }


                int jobSize = lstJobs.size();

                for (int i = 0; i < jobSize; i++) {
                    JobEntity entity = lstJobs.get(i);
                    sb.append(entity.getJobId()).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(entity.getName()).append(AIOConstants.SEPERATOR_BYTE_2);
                }

                if (jobSize > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append(AIOConstants.SEPERATOR_BYTE_3);
                }

                int characterSize = lstCharacters.size();

                for (int i = 0; i < characterSize; i++) {
                    CharacterEntity entity = lstCharacters.get(i);
                    sb.append(entity.getCharacterId()).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(entity.getName()).append(AIOConstants.SEPERATOR_BYTE_2);
                }

                if (characterSize > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }

                personCommonInfo = sb.toString();
            }

        } catch (DBException ex) {
            Logger.getLogger(InfoDB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static long giftForUser(long uid, String objectID, int type) throws SQLException {
        String query = "{call uspGiftForUser(?,?,?)}";
        Connection conn = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = conn.prepareCall(query);
            cs.clearParameters();
            cs.setLong(1, uid);
            cs.setString(2, objectID);
            cs.setInt(3, type);

            ResultSet rs = cs.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getLong("result");
            }
            rs.close();

            cs.clearParameters();

            cs.close();

        } finally {
            conn.close();
        }
        return 0;
    }

    public static String getGiftType() throws SQLException {
        StringBuilder res = new StringBuilder();
        String query = "{call uspGetGiftUser()}";
        Connection conn = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = conn.prepareCall(query);
            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("userGiftTypeID");
                    String detail = rs.getString("description");
                    res.append(id).append(AIOConstants.SEPERATOR_BYTE_1).
                            append(detail).append(AIOConstants.SEPERATOR_BYTE_2);
                }
            }
            if (res.length() > 0) {
                res.deleteCharAt(res.length() - 1);
            }
            rs.close();

            cs.clearParameters();

            cs.close();

        } finally {
            conn.close();
        }
        return res.toString();
    }

    private List<UpdateLevelEntity> getLstupdateMoney() throws DBException, SQLException {
        if (lstUpdateMoney == null) {
            lstUpdateMoney = moneyForUpdateLevel();
        }

        return lstUpdateMoney;
    }

    public static String getPersonCommonInfo() {
        return personCommonInfo;
    }

    public String getTuocvi() throws DBException, SQLException {

        return lstTuocVi;
    }

    public List<AdvertisingEntity> getAdvertising() {
        return lstAdvert;
    }

    public static void reloadAdv() throws SQLException {
        //for(int i = 0; i< 100; i++) {    
            lstAdvert = getAdvert();
        //}
        
    }
//    public static Vector<AvatarEntity> getLstAvatars()
//    {
//        return lstAvatars;
//    }

    public static List<CityEntity> getCities() {
        return lstCities;
    }

    public static List<JobEntity> getJobs() {
        return lstJobs;
    }

    public static List<CharacterEntity> getCharacters() {
        return lstCharacters;
    }

    public ArrayList<ChargingInfo> getPartnerChargings(int partnerId, String refCode) {
        ArrayList<ChargingInfo> partnerChargins = new ArrayList<ChargingInfo>();
        for (int i = 0; i < lstChargings.size(); i++) {
            ChargingInfo charginInfo = lstChargings.get(i);
            if (charginInfo.partnerId == partnerId && charginInfo.refCode.equals(refCode)) {
                partnerChargins.add(charginInfo);
            }
        }
        return partnerChargins;
    }

    public static ArrayList<ChargingInfo> getLstChargings(int partnerId) {
        ArrayList<ChargingInfo> partnerChargins = new ArrayList<ChargingInfo>();
        for (int i = 0; i < lstChargings.size(); i++) {
            ChargingInfo charginInfo = lstChargings.get(i);
            if (charginInfo.partnerId == partnerId) {
                partnerChargins.add(charginInfo);
            }
        }
        return partnerChargins;
    }

    public static ArrayList<ChargingInfo> getLstChargings(int partnerId, boolean isActive) {
        ArrayList<ChargingInfo> partnerChargins = new ArrayList<ChargingInfo>();
        for (int i = 0; i < lstChargings.size(); i++) {
            ChargingInfo charginInfo = lstChargings.get(i);
            if (charginInfo.partnerId == partnerId && charginInfo.isActive == isActive) {
                partnerChargins.add(charginInfo);
            }
        }
        return partnerChargins;
    }

    public List<ChargingInfo> getLstCardInfo() {
        return lstCards;
    }

    /*
     *  Get data from database
     */
    private List<UpdateLevelEntity> moneyForUpdateLevel() throws SQLException, DBException {

        String query = "SELECT * FROM updatelevelmoney";

        Connection conn = DBPoolConnection.getConnection();
        List<UpdateLevelEntity> res = new ArrayList<UpdateLevelEntity>();

        try {
            PreparedStatement st = conn.prepareStatement(query);
            ResultSet rs = st.executeQuery();



            while (rs.next()) {
                UpdateLevelEntity entity = new UpdateLevelEntity(rs.getInt("Level"), rs.getInt("money"));

                res.add(entity);
            }

            rs.close();
            st.close();

        } finally {
            conn.close();
        }

        return res;
    }

    private static String Tuocvi() throws DBException, SQLException {
        //SimpleConnnection conn = MasterPoolConnection.checkOut();
        StringBuilder res = new StringBuilder();

        String query = "SELECT * FROM level;";

        Connection conn = DBPoolConnection.getConnection();
        try {
            PreparedStatement st = conn.prepareStatement(query);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                res.append("+");
                res.append(rs.getInt("LevelID"));
                res.append("_");
                res.append(rs.getString("Description"));
            }
            res.delete(0, 1);

            rs.close();
            st.close();


        } finally {
            conn.close();
        }

        return res.toString();

    }

    public ChargingInfo getActive(List<ChargingInfo> chargingInfo,
            String phoneNumber) {

        if (getMobileCharging != null && phoneNumber == null) {
            return getMobileCharging;
        }
        String telcoProvider = "";

        if (getMobileCharging != null && phoneNumber != null) {
            if (phoneNumber.matches(AIOConstants.MOBIFONE_PREFIX)) {
                telcoProvider = "mobifone";
            } else if (phoneNumber.matches(AIOConstants.VINAPHONE_PREFIX)) {
                telcoProvider = "vinaphone";
            } else {
                telcoProvider = "viettel";
            }
        }

        ChargingInfo activeInfo = null;
        int chargingSize = chargingInfo.size();
        for (int i = 0; i < chargingSize; i++) {
            ChargingInfo info = chargingInfo.get(i);
            if (info.isActive) {
                activeInfo = info;
                if (info.desc.contains(telcoProvider) || telcoProvider.equals("")) {
                    break;
                }
            }
        }

        return activeInfo;
    }

    public ArrayList<ChargingInfo> getChargings(int partnerId, String phoneNumber) {
        int telcoProvider = 0;
        if (getMobileCharging != null && phoneNumber != null) {
            if (phoneNumber.matches(AIOConstants.MOBIFONE_PREFIX)) {
                telcoProvider = 1;
            }
        }

        ArrayList<ChargingInfo> partnerChargins = new ArrayList<>();
        int chargingSize = lstChargings.size();
        for (int i = 0; i < chargingSize; i++) {
            ChargingInfo charginInfo = lstChargings.get(i);
            if (charginInfo.partnerId == partnerId && ((charginInfo.telcoProvider == telcoProvider
                    && !charginInfo.isActive) || charginInfo.isActive)) {
                partnerChargins.add(charginInfo);
            }
        }
        return partnerChargins;
    }

    public int getMoneyForUpdateLevel(int level) throws DBException, SQLException {
        int res = 0;

        List<UpdateLevelEntity> updateLevelInfo = getLstupdateMoney();
        for (int i = 0; i < updateLevelInfo.size(); i++) {
            if (level == updateLevelInfo.get(i).getLevel()) {
                res = updateLevelInfo.get(i).getMoney();
                break;
            }
        }


        return res;
    }

    public static VersionEntity getLatestVersion(int partnerId) {
        for (int i = 0; i < lstVersion.size(); i++) {
            if (lstVersion.get(i).getPartnerId() == partnerId) {
                return lstVersion.get(i);
            }
        }
        return null;
    }

//    private static Vector<AvatarEntity> getAvatarList() throws SQLException {
//		Vector<AvatarEntity> res = new Vector<AvatarEntity>();
//                String query = "{call uspGetAllAvatar()}";
//                Connection conn = DBPoolConnection.getConnection();
//		try {
//                    CallableStatement cs = conn.prepareCall(query);
//                    ResultSet rs = cs.executeQuery();
//                    while (rs.next()) {
//                            int id = rs.getInt("AvatarID");
//                            String desc = rs.getString("Description");
//                            int money = rs.getInt("money");
//                            AvatarEntity av = new AvatarEntity(id, desc, money);
//                            res.add(av);
//                    }
//
//                    rs.close();
//                    cs.close();
//                }
//                finally
//                {
//                    conn.close();
//                }
//		return res;
//	}
    private static List<VersionEntity> getVersionInfos() throws SQLException {
        List<VersionEntity> res = new ArrayList<VersionEntity>();
        String query = "SELECT * FROM version;";

        Connection con = DBPoolConnection.getConnection();
        try {

            PreparedStatement st = con.prepareStatement(query);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                VersionEntity v = new VersionEntity();
                v.desc = rs.getString("Description");
                v.id = rs.getInt("VersionID");
                v.link = rs.getString("linkDown");
                //v.createDate = rs.getDatetime("CreateDate");
                v.createDate = rs.getDate("CreateDate");
                v.isNeedUpdate = rs.getBoolean("isNeedUpdate");
                v.newsUpdate = rs.getString("newsUpdate");
                v.setPartnerId(rs.getInt("partnerId"));
                res.add(v);
            }

            rs.close();
            st.close();

            //MasterPoolConnection.checkIn(conn);
        } finally {
            con.close();
        }
        return res;
    }

    private static ArrayList<ChargingInfo> getCharingInfo() throws SQLException {
        ArrayList<ChargingInfo> res = new ArrayList<ChargingInfo>();
        String query = "{call uspCreateChargingInfo()}";
        Connection conn = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = conn.prepareCall(query);
            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int number = rs.getInt("number");
                    String value = rs.getString("value");
                    String detail = rs.getString("Description");
                    int partnerId = rs.getInt("partnerId");
                    String refCode = "";//rs.getString("refCode");
                    boolean isNeedActive = rs.getBoolean("isNeedActive");
                    boolean isActive = rs.getBoolean("isActive");
                    boolean isGetMobile = false;
                    int telcoProvider = 0;

                    ChargingInfo info = new ChargingInfo(id, Integer.toString(number),
                            value, detail, partnerId, refCode, isNeedActive);
                    info.isActive = isActive;
                    info.isSMS = true;
                    info.position = 0;
                    info.telcoProvider = telcoProvider;

                    if (isGetMobile) {
                        getMobileCharging = info;
                    } else {
                        res.add(info);
                    }
                }
            }

            rs.close();

            cs.clearParameters();

            cs.close();

        } finally {
            conn.close();
        }
        return res;
    }

    public ArrayList<Message> receiveMessage(long id, String destName) throws Exception {
        ArrayList<Message> res = new ArrayList<Message>();
        String query = "{ call uspReceiveMessage(?) }";
        String UID_PARAM = "uid";

        Connection conn = DBPoolConnection.getConnection();

        try {
            CallableStatement cs = conn.prepareCall(query);
            cs.setLong(UID_PARAM, id);
            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    long messID = rs.getLong("messID");
                    long dID = rs.getLong("destID");
                    long sID = rs.getLong("sourceID");
                    String detail = rs.getString("detail");
                    String title = rs.getString("title");
                    String sourceName = rs.getString("SourceName");
                    //String destName = rs.getString("DestName");
                    Date date = rs.getDate("time");
                    res.add(new Message(messID, sID, dID, detail, title, date,
                            sourceName, destName));

                }

                rs.close();
            }



            cs.clearParameters();

            cs.close();
        } finally {
            conn.close();
        }

        return res;

    }

    private static List<AdvertisingEntity> getAdvert() throws SQLException {
        List<AdvertisingEntity> results = new ArrayList<AdvertisingEntity>();

        String query = "{ call uspGetAdvertising() }";
        Connection conn = null;

        conn = DBPoolConnection.getConnection();

        try {
            CallableStatement cs = conn.prepareCall(query);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    long advId = rs.getLong("advertisingId");
                    String content = rs.getString("content");
                    Date dtCreated = rs.getDate("createdDate");
                    int partnerId = rs.getInt("partnerId");
                    AdvertisingEntity entity = new AdvertisingEntity(advId, content, dtCreated);
                    entity.setPartnerId(partnerId);
                    results.add(entity);
                }
                rs.close();

            }

            cs.clearParameters();

            cs.close();
        } finally {

            conn.close();
        }


        return results;
    }

    public List<ChatRoomEntity> getChatRooms() throws SQLException {
        List<ChatRoomEntity> res = new ArrayList<ChatRoomEntity>();
        String query = "{ call uspGetChatRoom() }";

        Connection con = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = con.prepareCall(query);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                int chatRoomId = rs.getInt("chatRoomId");
                String name = rs.getString("name");
                String stt = rs.getString("stt");
                int maxPlayers = rs.getInt("maxPlayer");
                boolean isFanC = (rs.getInt("fanClub") == 1);
                ChatRoomEntity entity = new ChatRoomEntity(chatRoomId, name, stt, maxPlayers, isFanC);
                res.add(entity);

            }


            rs.close();
            cs.close();


        } finally {
            con.close();
        }
        return res;
    }

    public void setRole(int systemObjectID, int socialRoleId, long systemObjectRecordId) throws SQLException {
        String query = "{call uspSetSocialRole(?,?,?)} ";
        Connection conn = DBPoolConnection.getConnection();

        try {

            CallableStatement cs = conn.prepareCall(query);

            cs.setLong(SYSTEM_OBJECT_RECORD_PARAM, systemObjectRecordId);
            cs.setInt(SOCIAL_ROLE_ID_PARAM, socialRoleId);
            cs.setInt(SYSTEM_OBJECT_ID_PARAM, systemObjectID);

            cs.execute();
        } finally {
            conn.close();
        }

    }

    private static List<CityEntity> getLstCity() throws SQLException {
        List<CityEntity> res = new ArrayList<CityEntity>();
        String query = "{ call uspGetCity() }";

        Connection con = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = con.prepareCall(query);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                int cityId = rs.getInt("cityId");
                String name = rs.getString("name");

                CityEntity entity = new CityEntity(cityId, name);
                res.add(entity);

            }

            rs.close();
            cs.close();

        } finally {
            con.close();
        }
        return res;


    }

    private static List<JobEntity> getLstJob() throws SQLException {
        List<JobEntity> res = new ArrayList<JobEntity>();
        String query = "{ call uspGetJob() }";

        Connection con = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = con.prepareCall(query);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                int cityId = rs.getInt("jobId");
                String name = rs.getString("name");

                JobEntity entity = new JobEntity(cityId, name);
                res.add(entity);

            }

            rs.close();
            cs.close();

        } finally {
            con.close();
        }
        return res;


    }

    private static List<CharacterEntity> getLstCharacter() throws SQLException {
        List<CharacterEntity> res = new ArrayList<CharacterEntity>();
        String query = "{ call uspGetCharacter() }";

        Connection con = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = con.prepareCall(query);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                int cityId = rs.getInt("characterId");
                String name = rs.getString("name");

                CharacterEntity entity = new CharacterEntity(cityId, name);
                res.add(entity);

            }

            rs.close();
            cs.close();

        } finally {
            con.close();
        }
        return res;


    }

    private static List<ChargingInfo> getCardInfo() throws SQLException {
        List<ChargingInfo> res = new ArrayList<ChargingInfo>();
        String query = "{call uspGetCardInfo()}";
        Connection conn = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = conn.prepareCall(query);
            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("logTypeId");
                    String service = rs.getString("serviceCode");
                    String value = "";
                    String detail = rs.getString("CommandCode");

//                            int partnerId = rs.getInt("partnerId");
                    String refCode = "";//rs.getString("refCode");

                    ChargingInfo info = new ChargingInfo(id, service, value, detail, -1, refCode, false);

                    res.add(info);
                }
            }

            rs.close();

            cs.clearParameters();

            cs.close();

        } finally {
            conn.close();
        }
        return res;
    }
}
