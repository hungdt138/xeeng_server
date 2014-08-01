/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;


import com.tv.xeeng.game.trieuphu.data.Question;
import org.apache.axis.encoding.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tuandavrus
 */
public class QuestionDB {
    private static Hashtable<Integer, ArrayList< Question>> questions;
    
    public static void reload()
    {
        if(questions == null)
        {
            try {
                questions = loadQuestions();
            } catch (SQLException ex) {
                Logger.getLogger(QuestionDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static Hashtable<Integer, ArrayList< Question>> getQuestion()
    {
    	reload();
        return questions;
    }
    
    
    private static Hashtable<Integer, ArrayList<Question>> loadQuestions() throws SQLException
    {
        Hashtable<Integer, ArrayList<Question>> results = new Hashtable<Integer, ArrayList<Question>>();
        for(int i = 1; i< 16; i++)
        {
            results.put(i, new ArrayList<Question>());
        }
        
        
        Connection con = DBPoolConnection.getConnection();
        String query = "{ call uspGetQuestion() }";
        try {
                CallableStatement cs = con.prepareCall(query);
                ResultSet rs = cs.executeQuery();
                if (rs != null) {
                        while (rs.next()) {
                                int id = rs.getInt("questionId");
                                String detai = rs.getString("detail");
                                try
                                {
                                    byte[] dataByte = Hex.decode(detai);

                                    detai = new String(Base64.decode(new String(dataByte)));
                                }
                                catch(Exception ex)
                                {
                                    
                                }
                                
                                String a = rs.getString("choix1");
                                String b = rs.getString("choix2");
                                String c = rs.getString("choix3");
                                String d = rs.getString("choix4");
                                int answer = rs.getInt("answer");
                                int level = rs.getInt("levelId");
                                String[] variants = new String[] { a, b,c, d };
                                Question q = new Question(id, detai, variants, answer, level);
                                
                                results.get(level).add(q);
                        }
                        rs.close();
                        cs.close();
                }
                
        }  catch(Throwable e) {
            e.printStackTrace();
        }finally {
                con.close();
        }
        
        System.out.println("ALTP size:" + results.size());
        return results;
        
        
    }
    
}
