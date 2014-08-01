package com.tv.xeeng.game.trieuphu.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.databaseDriven.ImportData;
import com.tv.xeeng.databaseDriven.QuestionDB;


public class QuestionManager {
//	private static Hashtable<Integer, ArrayList< Question>> questions;

//	public static Question getQuestion(int id, int level) throws BusinessException {
//		try {
//			return questions.get(level).get(id);
//		} catch (Throwable e) {
//			throw new BusinessException("Khong tim thay cau hoi");
//		}
//	}
// 
    private static int questionSize[];
    
    static
    {
        initQuestionSize();
    }
    public static void reload()
    {
        initQuestionSize();
    }
    private static void initQuestionSize()
    {
        questionSize = new int[15];
        for(int i = 0; i< 15; i++)
        {
            questionSize[i] = QuestionDB.getQuestion().get(i+1).size();
        }
        
    }
    
    public  static ArrayList<Question> makeRandomList(boolean isSingleMode){
    	ArrayList<Question> res = new ArrayList<Question>();
        
        for(int i = 1; i< 16; i++)
        {
            Random rand = new Random(System.currentTimeMillis() * (i));
            int sizeRand = questionSize[i -1];
            if(isSingleMode)
                sizeRand /= 3;
            
            int t = (int) (Math.abs(rand.nextLong() % (sizeRand)));
            Question q = QuestionDB.getQuestion().get(i).get(t);
            res.add(q);
        }
        
    	return res;
    }
//	/*public QuestionManager() {
//		questions = new Hashtable<>(100000);
//	}*/
//	public static void initHash() {
//		questions = new Hashtable<Integer, ArrayList< Question>>(15);
//		for(int i = 1; i< 16; i++){
//			questions.put(i, new ArrayList< Question>(100000));
//		}
//	}
//	public static void init() {
//		ImportData imd = new ImportData();
//		ArrayList<ArrayList<String>> data = imd.readFolder("data", "data");
//		int i = 0;
//		for (ArrayList<String> d : data) {
//			if (d.size() == 7) {
//				i++;
//				Question q = new Question(i, d.get(1), new String[] { d.get(2),
//						d.get(3), d.get(4), d.get(5) }, d.get(6).trim(),
//						d.get(0), "Linh Tinh");
//				questions.get(Integer.parseInt(d.get(0))).add(q);
//			}
//		}
//		//System.out.println(questions.get(1).size());
//	}
//
//	public void init1() throws SQLException {
//		Connection con = DBPoolConnection.getConnection();
//		String query = "{ call GetQuestions() }";
//		try {
//			CallableStatement cs = con.prepareCall(query);
//			ResultSet rs = cs.executeQuery();
//			if (rs != null) {
//				while (rs.next()) {
//					int id = rs.getInt("id");
//					String detai = rs.getString("detail");
//					String domain = rs.getString("Domain");
//					String a = rs.getString("choix1");
//					String b = rs.getString("choix2");
//					String c = rs.getString("choix3");
//					String d = rs.getString("choix4");
//					String answer = rs.getString("answer");
//					String level = rs.getString("Level");
//					Question q = new Question(id, detai, new String[] { a, b,
//							c, d }, answer, level, domain);
//					questions.get(Integer.parseInt(level)).add(q);
//				}
//				rs.close();
//				cs.close();
//			}
//		} finally {
//			con.close();
//		}
//	}
}
