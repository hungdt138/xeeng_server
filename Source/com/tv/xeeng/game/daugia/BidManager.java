/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.daugia;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.game.data.AIOConstants;

/**
 *
 * @author tuanda
 */
public class BidManager {

    public static ArrayList<BidInfo> bidSessions;

    private static String dateToString(Date d) {
        SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("hh:mm dd/MM/yyyy");
        StringBuilder nowYYYYMMDD = new StringBuilder(
                dateformatYYYYMMDD.format(d));
        return nowYYYYMMDD.toString();
    }

    public static String getHistory(long uid, int bidID) throws Throwable {

        Connection con = DBPoolConnection.getConnection();
        String query = "{ call BidHistory(?,?) }";//TODO:
        CallableStatement cs = con.prepareCall(query);
        cs.clearParameters();
        cs.setLong(1, uid);
        cs.setLong(2, bidID);
        ResultSet res = cs.executeQuery();
        StringBuilder sb = new StringBuilder();
        while (res != null && res.next()) {
            long price = res.getLong("Price");
            Date d = res.getTimestamp("dateTime");
            sb.append(price).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(dateToString(d)).append(AIOConstants.SEPERATOR_BYTE_2);
        }
        if(sb.length()>0) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();                
    }

    public static void reload() {
        getQuestions();
    }

    public static void bid(long uid, int bidID, long money) throws Throwable {
        Connection con = DBPoolConnection.getConnection();
        String query = "{ call Bid(?,?,?) }";//TODO:
        CallableStatement cs = con.prepareCall(query);
        cs.clearParameters();
        cs.setLong(1, uid);
        cs.setLong(2, bidID);
        cs.setLong(3, money);
        cs.executeUpdate();
    }

    public static String bidSessionListInfo() {
        StringBuilder sb = new StringBuilder();
        for (BidInfo b : bidSessions) {
            /*sb.append(b.id).append(AIOConstants.SEPERATOR_BYTE_1);
             sb.append(b.name).append(AIOConstants.SEPERATOR_BYTE_1);
             sb.append(b.desc);(*/
            sb.append(b.toStringMessage()).append(AIOConstants.SEPERATOR_BYTE_3);
        }
        if (!bidSessions.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
    /*
     private static BidInfo findBid(int bidID) {
     for (BidInfo b : bidSessions) {
     if (b.id == bidID) {
     return b;
     }
     }
     return null;
     }
     */
    /*
     private static String bidSessionInfo(int bidID) {
     StringBuilder sb = new StringBuilder();
     BidInfo b = findBid(bidID);
     if (b != null) {
     sb.append(b.toStringMessage());
     }
     return sb.toString();
     }*/

    private static void getQuestions() {
        if (bidSessions == null) {
            bidSessions = new ArrayList<>();
        } else {
            bidSessions.clear();
        }

        try {
            Connection con = DBPoolConnection.getConnection();
            String query = "{ call uspGetBidSessions() }";//TODO:
            CallableStatement cs = con.prepareCall(query);
            ResultSet res = cs.executeQuery();
            while (res != null && res.next()) {
                int id = res.getInt("id");
                String productName = res.getString("pName");
                String productDesc = res.getString("pDesc");
                long productPrice = res.getLong("price");

                long max = res.getLong("max");
                long min = res.getLong("min");
                long step = res.getLong("step");
                String bidName = res.getString("bName");
                String bidDesc = res.getString("bDesc");
                BidType bType = intToBidType(res.getInt("type"));
                Date startD = res.getTimestamp("startDate");
                Date endD = res.getTimestamp("endDate");
                ArrayList<String> image = new ArrayList<>();
                image.add(res.getString("image"));
                ProductInfo product = new ProductInfo(image, productName, productDesc, productPrice);
                BidInfo q = new BidInfo(id, product, max, min, step, bidName, bidDesc, bType, startD, endD);
                bidSessions.add(q);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static BidType intToBidType(int input) {
        switch (input) {
            case 1:
                return BidType.NGUOC;
            case 2:
                return BidType.XUOI;
            case 3:
                return BidType.CAONHAT;
            default:
                return BidType.NGUOC;
        }
    }
}
