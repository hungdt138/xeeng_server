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

import java.util.logging.Level;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.AchievementEntity;
import com.tv.xeeng.game.data.EventEntity;
import com.tv.xeeng.game.data.EventPlayerEntity;
import com.tv.xeeng.game.data.UserEntity;

/**
 * 
 * @author tuanda
 */
public class EventDB {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(EventDB.class);
    private static final String EVENT_ID_PARAM = "eventId";
    private static List<EventEntity> lstEvents;
    private static List<AchievementEntity> lstAchievements;
    private static String achievementValue;

    // private static String image =
    // "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";
    // private static String imageDetail =
    // "/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDE8FeDfD+reGLa8v7Dzrh2cM/nSLnDEDgMB2rpF+HXhLOTpX/kzL/8VVD4eSbfB1oM/wAT/wDoRrrBNjnNfP1q1RVJJSe/c9ylQg6cXyrZdDAk+H/g+OSFTo4zK+0f6RL/AHSf73tUn/CufCIH/IJ/8mZf/i6XVNRzJgNjaflwenvWjp+o/bLNZG++DtbHc1EcRVa5lJ/eVLDQi7WX3GUfh34SH/MJxj0uZf8A4qitt7j3oqlWqfzP72L2FP8AlX3HC+DZ2i8I2ezg+aQe3V/pW5eahKkU6KpBAAQ8/MT1HHSuU8KXUQ8OWsPmKZA+4orjcBuHJGc4/wAa1tTVJEkLM3RMgDPc+9VXS9o/UvDu9JehBK4nx5kSeZgbskZz/nNW7G9exDx+XtjRDjHR2PPb8q87g1mK58QtcvCIhOQgfdyuOMnP4enSt7UNVSz165trmaNLcWwIwCW3ZBwcd8Z/DFavDuLUfmZLExmnLzsd59rd2iYfdZCTz9KKzFmx9mx3Q/yFFZ8przHldhqEdjLBLksyjJCsV5z0zitpvF+5ChiTOQCzsxGPpj1zRRXrToQm7yPFp15wVos5h5f38zKY8OW+6ny4J7AjgelNmnkuJzNKQXbGSO+Bj+lFFbKK3Mm29DpLfxaIoIo3hLNHGoLeYcs3GSeO/NFFFYfVqb6HR9brLqf/2Q==";
    public static void reload() {
        try {
            lstEvents = getEventFromDB();
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        }
    }

    public static List<EventEntity> getEvents(int partnerId) {
        // boolean onlyPartner = false;
        int size = lstEvents.size();
        // List<EventEntity> eventPartners = new ArrayList<EventEntity>();
        List<EventEntity> allPartners = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            EventEntity entity = lstEvents.get(i);
            if (entity.getPartnerId() == 0 || entity.getPartnerId() == partnerId) {
                allPartners.add(entity);
            }
            /*
             * if (entity.getPartnerId() == partnerId) {
             * eventPartners.add(entity); onlyPartner = true; }
             */
        }
        List<EventEntity> retEvents;
        /*
         * if (onlyPartner) { retEvents = eventPartners; } else {
         */
        retEvents = allPartners;
        // }

        return retEvents;
    }

    public static String getAchivements(int partnerId) {
        // boolean onlyPartner = false;
        int size = lstEvents.size();
        // List<EventEntity> achievePartners = new ArrayList<>();
        List<EventEntity> allAchievement = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            EventEntity entity = lstEvents.get(i);
            // if (entity.isConcurrent()) {
            if (entity.getPartnerId() == 0 || entity.getPartnerId() == partnerId) {
                allAchievement.add(entity);
            }

            // if (entity.getPartnerId() == partnerId) {
            // achievePartners.add(entity);
            // onlyPartner = true;
            // }

            // }
        }

        List<EventEntity> retEvents;
        // if (onlyPartner) {
        // retEvents = achievePartners;
        // } else {
        retEvents = allAchievement;
        // }

        int retSize = retEvents.size();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < retSize; i++) {
            EventEntity achiveEntity = retEvents.get(i);
            if (achiveEntity.getGameId() >= 0) {
                sb.append(achiveEntity.getEventId()).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(achiveEntity.getTittle()).append(AIOConstants.SEPERATOR_BYTE_2);
            }
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public List<EventPlayerEntity> getTopPlayer(int eventId) throws SQLException {
        List<EventPlayerEntity> res = new ArrayList<EventPlayerEntity>();
        String query = "{ call uspGetTopEventPlayer(?) }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.clearParameters();
            cs.setInt(EVENT_ID_PARAM, eventId);
            ResultSet rs = cs.executeQuery();
            if (rs != null) {

                while (rs.next()) {

                    UserEntity user = new UserEntity();

                    user.mUid = rs.getLong("UserID");
                    // System.out.println("======================tuanda:" +
                    // res.mUid);
                    user.mUsername = rs.getString("Name");

                    // user.money = rs.getLong("money");
                    user.avFileId = rs.getLong("avatarFileId");

                    String description = rs.getString("description");

                    EventPlayerEntity eventEntity = new EventPlayerEntity(user, description);
                    eventEntity.point = rs.getLong("point");
                    res.add(eventEntity);

                }

                rs.close();
            }
        } finally {
            conn.close();
        }
        return res;
    }

    private static List<EventEntity> getEventFromDB() throws SQLException {
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
}
