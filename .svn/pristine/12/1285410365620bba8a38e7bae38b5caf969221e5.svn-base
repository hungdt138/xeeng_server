/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;

import com.tv.xeeng.base.common.LoggerContext;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.slf4j.Logger;

/**
 *
 * @author thanhnvt
 */
public class GiftDB {

    private static final Logger logger = LoggerContext.getLoggerFactory().getLogger(GiftDB.class);

    public static int getNumOfReceivedFreeGold(long userId) {
        String query = "{ call uspXEGetNumOfReceivedFreeGold(?) }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.clearParameters();
            cs.setLong(1, userId);
            ResultSet rs = cs.executeQuery();
            if (rs != null && rs.next()) {
                return (rs.getInt(1));
            }

            return 0;
        } catch (SQLException ex) {
            logger.error("Error", ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    logger.error("Error", ex);
                }
            }
        }

        return -1;
    }

    /**
     * Nhận Gold miễn phí theo ngày.
     *
     * @param userId
     * @param numOfGold Số Gold được nhận
     * @param minGoldAllowed Số Gold tối thiểu của người chơi để được nhận Gold miễn phí
     * @return
     */
    public static boolean receiveFreeGoldByDay(long userId, int numOfGold, int minGoldAllowed) {
        String query = "{ ? = call uspXEReceiveFreeGoldByDay(?, ?, ?) }";
        Connection conn = DBPoolConnection.getConnection();
        try {

            CallableStatement cs = conn.prepareCall(query);
            cs.clearParameters();
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong("userId", userId);
            cs.setLong("numOfGold", numOfGold);
            cs.setLong("minGoldAllowed", minGoldAllowed);
            cs.execute();

            return cs.getBoolean(1);
        } catch (SQLException ex) {
            logger.error("Error", ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    logger.error("Error", ex);
                }
            }
        }

        return false;
    }
}
