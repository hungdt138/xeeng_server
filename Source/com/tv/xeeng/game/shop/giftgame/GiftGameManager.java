/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.shop.giftgame;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.Couple;
import com.tv.xeeng.game.data.SimpleException;

/**
 *
 * @author tuanda
 */
public class GiftGameManager {

    private static Hashtable<Integer, GiftGameEntity> giftGames = new Hashtable<>();
    private static Hashtable<Integer, String> giftType = new Hashtable<>();

    public static void reload() throws SQLException {
        String query = "{call GetGift()}";
        try (Connection conn = DBPoolConnection.getConnection();
                CallableStatement cs = conn.prepareCall(query);
                ResultSet rs = cs.executeQuery()) {
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("GiftGameID");
                    String name = rs.getString("name");
                    String category = rs.getString("typeName");
                    String icon = rs.getString("icon");
                    String bigIcon = rs.getString("bigIcon");
                    long price = rs.getLong("price");
                    int giftTypeID = rs.getInt("giftTypeID");
                    String activeChat = rs.getString("activeChat");
                    String passiveChat = rs.getString("passiveChat");
                    GiftGameEntity ent = new GiftGameEntity(id, name, category,
                            giftTypeID, icon, bigIcon, price, activeChat, passiveChat);

                    giftGames.put(ent.id, ent);
                    if (!giftType.containsKey(giftTypeID)) {
                        giftType.put(giftTypeID, category);
                    }
                }
            }
        }
    }

    public static String getGiftGameByType(int categoryID, boolean isJava) {
        StringBuilder sb = new StringBuilder();
        Enumeration<GiftGameEntity> enums = giftGames.elements();
        while (enums.hasMoreElements()) {
            GiftGameEntity at = enums.nextElement();
            if (at.categoryID == categoryID) {
                sb.append(at.toString(isJava)).append(AIOConstants.SEPERATOR_BYTE_2);
            }
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();

    }

    public static String getGiftGameType() {
        StringBuilder sb = new StringBuilder();
        Enumeration<Integer> enums = giftType.keys();

        while (enums.hasMoreElements()) {
            int at = enums.nextElement();
            String v = giftType.get(at);
            sb.append(at).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(v).append(AIOConstants.SEPERATOR_BYTE_2);
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();

    }
    
    public static Couple<String, String> gift(int giftGameID, long matchID, long uid,
            String uids, int number) throws SQLException, SimpleException {
        String query = "{call MakeGiftGame(?,?,?,?,?)}";
        int res = 0;
        try (Connection conn = DBPoolConnection.getConnection()) {
            CallableStatement cs = conn.prepareCall(query);
            cs.clearParameters();
            cs.setLong(1, uid);
            cs.setLong(2, matchID);
            cs.setInt(3, number);
            cs.setString(4, uids);
            cs.setInt(5, giftGameID);

            ResultSet rs = cs.executeQuery();
            if (rs != null && rs.next()) {
                res = rs.getInt("result");
            }

        }
        if (res == 1) {
            return giftGameChat(giftGameID);
        } else {
            throw new SimpleException("Không tặng được");
        }

    }
    private static Couple<String, String> giftGameChat(int giftID) {
        GiftGameEntity g = giftGames.get(giftID);
        return new Couple<>(g.activeChat , g.passiveChat);
    }
}
