package com.tv.xeeng.databaseDriven;

import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;

/**
 * Created by yeuchimse on 17/06/2014.
 */
public class LogDB {
    static Logger logger = Logger.getLogger(LogDB.class);

    public static void insertNumOfUserOnline(int numOfUserOnline, int numTienLen, int numPhom, int numBaCay,
int numBauCua, int numALTP, int numPikachu) {

        try {
            String query = "{ call uspXEInsertNumOfUserOnline(?, ?, ?, ?, ?, ?, ?) }";
            Connection conn = DBPoolConnection.getConnection();
            try {
                CallableStatement cs = conn.prepareCall(query);
                cs.setInt(1, numOfUserOnline);
                cs.setInt(2, numTienLen);
                cs.setInt(3, numPhom);
                cs.setInt(4, numBaCay);
                cs.setInt(5, numBauCua);
                cs.setInt(6, numALTP);
                cs.setInt(7, numPikachu);

                cs.execute();
            } finally {
                conn.close();
            }
        } catch (Exception ex) {

        }
    }

    /**
     * Cập nhật log thiết bị của user theo lần đăng nhập cuối.
     * @param userId
     * @param osName Tên OS (android, ios, java)
     * @param osVersion Version của OS (4.2.2, 4.4)
     * @param osMAC MAC của thiết bị
     */
    public static void updateUserDevice(long userId, String osName, String osVersion, String osMAC) {
        try {
            String query = "{ call uspXEUpdateUserDevice(?, ?, ?, ?) }";
            Connection conn = DBPoolConnection.getConnection();
            try {
                CallableStatement cs = conn.prepareCall(query);
                cs.setLong(1, userId);
                cs.setString(2, osName);
                cs.setString(3, osVersion);
                cs.setString(4, osMAC);

                cs.execute();
            } finally {
                conn.close();
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }
}
