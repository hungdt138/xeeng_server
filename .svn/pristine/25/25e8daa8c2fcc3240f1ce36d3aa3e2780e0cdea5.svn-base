/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.newpika.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.SimpleException;
import com.tv.xeeng.game.data.ZoneID;

/**
 *
 * @author tuanda
 */
public class TopicManager {

    static ArrayList<Topic> topics = new ArrayList<>();
    static String topicsString = "";
    //static String baseURL = "http://123.30.187.51:8080/pikachu/icon/";

    public static void reload() {
        //TODO: load from Db
//        loadTopicFromDB();
        loadTopics();
    }

    public static List<NRoomEntity> rooms() {
        List<NRoomEntity> res = new ArrayList<>();
        for (Topic t : topics) {
            NRoomEntity entity = new NRoomEntity(t.roomID, (long) 0, 96, 24, "Level",
                    t.id, 1, (int) t.money, ZoneID.NEW_PIKA);
            res.add(entity);
            t.room = entity;
        }

        return res;
    }

    private static void loadTopicFromDB() {
        Connection con = DBPoolConnection.getConnection();
        String query = "select * from  PikaTopic;";
        try {
            PreparedStatement cs = con.prepareStatement(query);
            cs.clearParameters();
            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    int baseID = 1000;
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int numberIcon = rs.getInt("numberIcon");
                    long money = rs.getLong("money");
                    String baseURL = rs.getString("baseURL");
                    String icon = baseURL + name + "/icon.png";
                    String detail = baseURL + name + "/" + AIOConstants.SEPERATOR_BYTE_1 + numberIcon;
                    Topic t = new Topic(name, id, detail, icon, money, baseID + id);
                    topics.add(t);
                }
                cs.clearParameters();
                rs.close();
                cs.close();
            }
        } catch (Throwable e) {
        	e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (Throwable e) {
            }
        }
    }

    public static String getTopics() {
        return topicsString;
    }

    public static String getNewTopic(int id) {
        StringBuilder sb = new StringBuilder();
        for (Topic t : topics) {
            sb.append(t.room.getPhong().getPlaying()).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(t.id);
            if (t.id > id) {
                sb.append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(t.name).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(t.money).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(t.roomID).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(t.icon).append(AIOConstants.SEPERATOR_BYTE_2);
            } else {
                sb.append(AIOConstants.SEPERATOR_BYTE_2);
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private static void loadTopics() {
        StringBuilder sb = new StringBuilder();
        for (Topic t : topics) {
            sb.append(t.id).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(t.name).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(t.money).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(t.roomID).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(t.icon).append(AIOConstants.SEPERATOR_BYTE_2);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        topicsString = sb.toString();
    }

    private static Topic findTopic(int id) {
        int len = topics.size();
        for (int i = 0; i < len; i++) {
            Topic t = topics.get(i);
            if (t.id == id) {
                return t;
            }
        }
        return null;
    }

    public static String getDetailTopic(int id, long money) throws SimpleException {
        Topic t = findTopic(id);
        if (t != null) {
            if (money < t.money) {
                throw new SimpleException("Bạn không đủ tiền để chơi chủ đề này!");
            }
            return t.detail;
        } else {
            throw new SimpleException("Không tìm thấy chủ đề tương ứng!");
        }
    }
}
