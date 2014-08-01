/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.AuditDutyEntity;
import com.tv.xeeng.game.data.DutyEntity;
import com.tv.xeeng.game.data.UserEntity;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;

/**
 *
 * @author tuanda
 */
public class DutyDB {

    private static final Logger mLog = LoggerContext.getLoggerFactory()
            .getLogger(DutyDB.class);
    private static final String DUTY_ID_PARAM = "dutyId";
    private static final String USER_ID_PARAM = "userId";
    private static List<DutyEntity> lstDuties;

    private static String dutyValue = "";

    public static void reload() {
        try {
            lstDuties = getDuty();
            int dutySize = lstDuties.size();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dutySize; i++) {
                DutyEntity entity = lstDuties.get(i);
                sb.append(Integer.toString(entity.getDutyId())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(entity.getTittle()).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(entity.getContent()).append(AIOConstants.SEPERATOR_BYTE_2);

            }

            if (dutySize > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }

            dutyValue = sb.toString();

        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        }

    }

    public List<DutyEntity> getDuties() {
        return lstDuties;
    }

    public String getListDuty() {
        return dutyValue;
    }

    private static List<DutyEntity> getDuty() throws SQLException {
        List<DutyEntity> res = new ArrayList<DutyEntity>();
        String query = "{ call uspGetDuty() }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
                    //cs.clearParameters();
            //cs.setInt(EVENT_ID_PARAM, eventId);
            ResultSet rs = cs.executeQuery();
            if (rs != null) {

                while (rs.next()) {

                    int dutyId = rs.getInt("dutyId");
                    String title = rs.getString("title");
                    String content = rs.getString("content");
                    String receiveDutyDesc = rs.getString("receiveDutyDesc");
                    boolean hasReceiveDuty = rs.getBoolean("hasReceiveDuty");
                    String doneDutyDesc = rs.getString("doneDutyDesc");
                    boolean hasDoneDuty = rs.getBoolean("hasDoneDuty");
                    String resultDutyDesc = rs.getString("resultDutyDesc");
                    boolean hasResultDuty = rs.getBoolean("hasResultDuty");

                    String dtDuty = rs.getString("dtDuty");
                    String dtFormat = rs.getString("dtFormat");
                    StringBuilder sb = new StringBuilder();
                    sb.append(Integer.toString(dutyId)).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(title).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(content).append(AIOConstants.SEPERATOR_BYTE_3);

                    sb.append(receiveDutyDesc).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(hasReceiveDuty ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_3);

                    sb.append(doneDutyDesc).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(hasDoneDuty ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_3);

                    sb.append(resultDutyDesc).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(hasResultDuty ? "1" : "0");

                    DutyEntity entity = new DutyEntity(dutyId, title, content, receiveDutyDesc, hasReceiveDuty, doneDutyDesc,
                            hasDoneDuty, resultDutyDesc, hasResultDuty, dtFormat, dtDuty);

                    entity.setDutyDetail(sb.toString());

                    res.add(entity);

                }

                rs.close();
            }
        } finally {
            conn.close();
        }
        return res;
    }

    public int doneDuty(int dutyId, long userId) throws SQLException {
        int ret = 0;
        String query = "{ call uspDoneDuty(?,?) }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
            //cs.clearParameters();
            cs.setInt(DUTY_ID_PARAM, dutyId);
            cs.setLong(USER_ID_PARAM, userId);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    ret = rs.getInt("ret");
                }

                rs.close();
            }
        } finally {
            conn.close();
        }
        return ret;
    }

    public List<AuditDutyEntity> getAuditDuty(int dutyId) throws SQLException {
        List<AuditDutyEntity> res = new ArrayList<AuditDutyEntity>();
        String query = "{ call uspGetResultDuty(?) }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
            //cs.clearParameters();
            cs.setInt(DUTY_ID_PARAM, dutyId);

            ResultSet rs = cs.executeQuery();
            if (rs != null) {

                while (rs.next()) {

                    long userId = rs.getLong("userId");
                    String name = rs.getString("name");
                    long avatarFileId = rs.getLong("avatarFileId");
                    Date auditDate = rs.getTimestamp("auditDate");
                    int bonusMoney = rs.getInt("bonusMoney");

                    UserEntity usrEntity = new UserEntity();
                    usrEntity.mUid = userId;
                    usrEntity.mUsername = name;
                    usrEntity.avFileId = avatarFileId;

                    AuditDutyEntity entity = new AuditDutyEntity(usrEntity, auditDate, bonusMoney);
                    res.add(entity);

                }

                rs.close();
            }
        } finally {
            conn.close();
        }
        return res;
    }

}
