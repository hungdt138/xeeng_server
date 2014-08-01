/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;

import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.newpika.data.TopicManager;
import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class RoomDB {
    
    static Map<Integer, List<NRoomEntity>> lstRooms = new HashMap<>();
    private static final String PLAYING_PARAM = "playing";
    private static final String ROOM_ID_PARAM = "roomId";
    private static Map<Integer, String> lstGameCache = new HashMap<>();
    
    public RoomDB() {
    }
    
    public static void reload() {
        try {
            initAllRooms();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
    public static String getGameInfo(int zoneId) {
        return lstGameCache.get(zoneId);
    }
    
    private static void initAllRooms() throws SQLException, IllegalArgumentException, IllegalAccessException {
        String query = "{ call uspGetAllRooms() }";
        Connection con = DBPoolConnection.getConnection();
        try {
            
            CallableStatement cs = con.prepareCall(query);
            
            ResultSet rs = cs.executeQuery();
            
            List<NRoomEntity> res = new ArrayList<>();
            
            while (rs.next()) {
                NRoomEntity entity = new NRoomEntity(rs.getInt("id"),
                        rs.getInt("playing"), rs.getInt("availables"),
                        rs.getInt("numTable"), rs.getString("level"),
                        rs.getInt("number"), rs.getInt("levelId"),
                        rs.getInt("minCash"), rs.getInt("zoneId"));
                entity.setIsVip(rs.getInt("isVip"));
                
                res.add(entity);
            }
            
            rs.close();
            cs.close();
            
            res.addAll(TopicManager.rooms()); // add newRoom for New_PIKA

            Field[] zonesId = ZoneID.class.getFields();
            for (Field f : zonesId) {
                int zoneID = (Integer) f.get(new ZoneID());
                StringBuilder sb = new StringBuilder();
                List<NRoomEntity> rooms = new ArrayList<>();
                int size = res.size();
                boolean flagAddZone = false;
                for (int j = 0; j < size; j++) {
                    if (res.get(j).getZoneId() == zoneID) {
                        flagAddZone = true;
                        NRoomEntity entity = res.get(j);
                        rooms.add(res.get(j));
                        sb.append(entity.getId()).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(entity.getLv()).append(AIOConstants.SEPERATOR_BYTE_2);
                    }
                }
                
                if (flagAddZone) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                
                lstGameCache.put(zoneID, sb.toString());
                lstRooms.put(zoneID, rooms);
            }
            
        } finally {
            con.close();
        }
    }
    
    public List<NRoomEntity> getRooms(int zoneId) {
        return lstRooms.get(zoneId);
        
    }
    
    public NRoomEntity getRoomEntity(int zoneId, int phongId) {
        NRoomEntity entity = null;
        List<NRoomEntity> rooms = getRooms(zoneId);
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getId() == phongId) {
                entity = rooms.get(i);
                break;
            }
        }
        return entity;
    }
    
    public int validateMoneySetting(int roomId, int money, long userId) throws SQLException {
        int res;
        
        String query = "{?= call uspValidateChangeSetting(?,?,?) }";
        Connection conn = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = conn.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            
            cs.setInt(2, roomId);
            cs.setInt(3, money);
            cs.setLong(4, userId);
            
            cs.execute();
            res = cs.getInt(1);
            
            cs.close();
        } finally {
            conn.close();
        }
        
        return res;
        
    }
    
    public void setPhongStatus(int roomId, int playing) throws SQLException {
        String query = "{ call uspSetPhongStatus(?,?) }";
        Connection con = DBPoolConnection.getConnection();
        try {
            CallableStatement cs = con.prepareCall(query);
            cs.setInt(ROOM_ID_PARAM, roomId);
            cs.setInt(PLAYING_PARAM, playing);
            
            cs.execute();
            cs.clearParameters();
            cs.close();
            
        } finally {
            con.close();
        }
        
    }
}
