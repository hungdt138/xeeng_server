/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.gameshow;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.game.data.FileEntity;

/**
 *
 * @author tuanda
 */
public class GameVTVManager {

    private static ArrayList<VTVQuestion> qs = new ArrayList<VTVQuestion>();

    public static void init(){
        Connection con = DBPoolConnection.getConnection();
        String query = "{ call getVTVQuestion() }";
        try {
            CallableStatement cs = con.prepareCall(query);
            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("Id");
                    String detail = rs.getString("Info");
                    String answer = rs.getString("Answer");
                    VTVQuestion q = new VTVQuestion(detail, Integer.parseInt(answer), id);
                    qs.add(q);
                }
                System.out.println("VTVQuestion size:" + qs.size());
                rs.close();
                cs.close();
            }
        } catch (Throwable e) {
            
        } finally {
            try {
                con.close();
            } catch (Throwable e) {
            }
        }
    }

    public static String question(int id) throws BusinessException {
        VTVQuestion q = qs.get(id);
        if (q == null) {
            throw new BusinessException("Khong tim thay cau hoi!");
        }
        return q.detail;
    }

    public static boolean checkQuestion(int id, int an) {
        VTVQuestion q = qs.get(id);
        if (q != null && an == q.answer) {
            //TODO: update point for user
            return true;
        }
        return false;
    }
}
