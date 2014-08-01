package com.tv.xeeng.databaseDriven;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Calendar;
import java.util.Vector;

import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.data.MessageOfflineEntity;
import com.tv.xeeng.game.data.PostEntity;
import com.tv.xeeng.game.data.UserEntity;

public class DatabaseDriver {

	public static int phantram = 10;
	public static String url, db_password, db_username;
	public static boolean serverDebug = false;
    public static long matchIdIncrease;
        
    public static String getImage(String name) throws Exception{
		return ImageDB.getImageDetail(name);
	}
        
	public static void insertAvatar(long uid, String mobileAvatar,
			String flashAvatar) throws Exception {
		String query = "UPDATE userstt SET " + "avatarM='" + mobileAvatar
				+ "', avatarF='" + flashAvatar + "' WHERE UserID=" + uid + ";";
                
                Connection conn = DBPoolConnection.getConnection();
                try
                {
                    PreparedStatement st1 = conn.prepareStatement(query);
                    st1.executeUpdate(query);
                    st1.close();
                }
                finally
                {
                    conn.close();
                }
	}

	public static void sendMess(String detail, long sUID, long dUID,
			String title) throws Exception {
		String query = "insert into message (sourceID,destID,detail,title,status,time) values (?,?,?,?,0,getDate()); ";
                Connection conn = DBPoolConnection.getConnection();
                try
                {
                    CallableStatement cs = conn.prepareCall(query);
                    cs.clearParameters();

                    cs.setLong(1, sUID);
                    cs.setLong(2, dUID);
                    cs.setString(3, detail);
                    cs.setString(4, title);
                    cs.executeUpdate();
                    cs.clearParameters();
                    cs.close();
                }
                finally
                {
                    conn.close();
                }
	}

	public static void readMessage(long messID) throws Exception {
		String query = "UPDATE message SET status=? WHERE messID=?";
                Connection conn = DBPoolConnection.getConnection();
                
                try
                {
                    PreparedStatement st1 = conn.prepareStatement(query);
                    st1.setInt(1, 1);
                    st1.setInt(2, (int)messID);
                    st1.executeUpdate();
                    st1.close();
                }
                finally
                {
                    conn.close();
                }
	}

	// Fix
	public static long registerUser(String username, String password, int sex,
			int age, String mail, String phone, String key) throws Exception {

		// user MD5
		// password =
		// org.jboss.netty.handler.codec.base64.Base64.md5Hex(password);

		String query = "{?= call uspRegisterUserGC(?,?,?,?,?,?, ?) }";
                Connection conn = DBPoolConnection.getConnection();
                
		CallableStatement cs = conn.prepareCall(query);
		try {

			cs.clearParameters();
                        cs.registerOutParameter(1, java.sql.Types.BIGINT);
			cs.setString(2, username);
			cs.setString(3, password);
			cs.setInt(4, sex);
			cs.setInt(5, age);
			cs.setString(6, mail);
			cs.setString(7, phone);
                        cs.setString(8, key);
                        cs.execute();
                       
                        long uid = cs.getLong(1);
                        cs.close();
			return uid;
		} catch (Exception e) {
                    e.printStackTrace();
                }
                finally
                {
                    conn.close();
                }
                
		return -1;

	}

	
	public static int getUserLevel(long aUid) throws Exception {
		int res = 0;
		String query = "SELECT Level FROM userstt WHERE UserID=?;";
                Connection conn = DBPoolConnection.getConnection();
		try {
                    
                
                    PreparedStatement st = conn.prepareStatement(query);
                    st.setLong(1, aUid);

                    ResultSet rs = st.executeQuery();
                    if (rs != null && rs.next()) {
                            res = rs.getInt("Level");
                    }

                    st.clearParameters();
                    st.close();
                    rs.close();
                }
                finally
                {
                    conn.close();
                }

		return res;
	}

	// Fix

	public static long getUserMoney(long aUid) throws Exception {
		long res = 0;
		String query = "SELECT Cash FROM userstt WHERE UserID=?;";
                Connection conn = DBPoolConnection.getConnection();
		try {
                    
                
                    PreparedStatement st = conn.prepareStatement(query);
                    st.setLong(1, aUid);

                    ResultSet rs = st.executeQuery();
                    if (rs != null && rs.next()) {
                            res = rs.getLong("Cash");
                    }

                    st.clearParameters();
                    st.close();
                    rs.close();
                }
                finally
                {
                    conn.close();
                }
		return res;
	}

	// Fix

	public static void updateLevel(long uid) throws Exception {
		String query = "{ call UpdateLevel(?) }";
                Connection conn = DBPoolConnection.getConnection();
		try {
                    CallableStatement cs = conn.prepareCall(query);
                    cs.setLong(1, uid);
                    cs.executeUpdate();
                    
                    cs.close();
                }
                finally
                {
                    conn.close();
                }
	}

	// Fix

	public static int updateAvatar(long uid, int avatarID) throws Exception {
		String query = "{?= call UpdateAvatar(?,?) }";
		Connection conn = DBPoolConnection.getConnection();
                int res =-1;
		try {
                    
                
                    CallableStatement cs = conn.prepareCall(query);

                    cs.clearParameters();
                    cs.registerOutParameter(1, Types.INTEGER);
                    cs.setLong(2, uid);
                    cs.setLong(3, avatarID);
                    cs.execute();

                    res = cs.getInt(1);

                    cs.close();
                }
                finally
                {
                    conn.close();
                }
                return res;
                
	}

//	public static void removeFriend(long currUID, long friendUID)
//			throws Exception {
//		Vector<UserEntity> friends = getFrientList(currUID);
//		String text = "";
//
//		for (int i = 0; i < friends.size(); i++) {
//			UserEntity friend = friends.get(i);
//			if (friend.mUid != friendUID) {
//				text += friend.mUid + ",";
//			}
//		}
//		if (text.length() > 0) {
//			text = text.substring(0, text.length() - 1);
//		}
//
//		String query = "UPDATE users SET UserIDs='" + text
//				+ "' WHERE UserID=?;";
//                
//                Connection conn = DBConnection.getConnection();
//		try {
//                    
//                
//                    PreparedStatement st = conn.prepareStatement(query);
//                    st.setLong(1, currUID);
//                    st.executeUpdate();
//
//                    st.clearParameters();
//                    st.close();
//                }
//                finally
//                {
//                    conn.close();
//                }
//	}

	// Fix

//	public static void addFriend(long currUID, long friendUID) throws Exception {
//		String query = "SELECT * FROM users WHERE UserID=?";
//                Connection conn = DBConnection.getConnection();
//		try {
//                    
//                    PreparedStatement st = conn.prepareStatement(query);
//                    st.setLong(1, currUID);
//                    ResultSet rs = st.executeQuery();
//                    String text = "";
//                    if (rs != null) {
//                            if (rs.next()) {
//                                    text = rs.getString("UserIDs") + "," + friendUID;
//                                    query = "UPDATE users SET UserIDs='" + text
//                                                    + "' WHERE UserID=?;";
//                                    st = conn.prepareStatement(query);
//                                    st.clearParameters();
//                                    st.setLong(1, currUID);
//                                    st.executeUpdate();
//                            } else {
//                                    query = "INSERT INTO users values(?,?);";
//                                    text = "" + friendUID;
//                                    st = conn.prepareStatement(query);
//                                    st.clearParameters();
//                                    st.setLong(1, currUID);
//                                    st.setString(2, text);
//                                    st.executeUpdate();
//                            }
//
//                    } else {
//                            query = "INSERT INTO users values(?,?);";
//                            text = "" + friendUID;
//                            st = conn.prepareStatement(query);
//                            st.clearParameters();
//                            st.setLong(1, currUID);
//                            st.setString(2, text);
//                            st.executeUpdate();
//                    }
//
//                    st.clearParameters();
//                    st.close();
//                    rs.close();
//                }
//                finally
//                {
//                    conn.close();
//                }
//	}

	// Fix

//	public static Vector<UserEntity> getFrientList(long aid) throws Exception {
//		Vector<UserEntity> users = new Vector<UserEntity>();
//
//		String query = "SELECT UserIDs FROM users WHERE UserID=?";
//                Connection conn = DBConnection.getConnection();
//		try {
//                    PreparedStatement st = conn.prepareStatement(query);
//                    st.setLong(1, aid);
//
//                    ResultSet rs = st.executeQuery();
//                    while (rs.next()) {
//                            String text = rs.getString("UserIDs");
//                            ArrayList<Long> temp = friendList(text);
//                            for (long id : temp) {
//
//                                UserDB userDb = new UserDB();
//                                UserEntity user = userDb.getUserInfo(id);
//                                if (user != null) {
//                                        users.add(user);
//                                }
//
//                            }
//                    }
//
//                    st.clearParameters();
//                    st.close();
//                    rs.close();
//                }
//                finally
//                {
//                    conn.close();
//                }
//		return users;
//	}

//	private static ArrayList<Long> friendList(String input) {
//		ArrayList<Long> res = new ArrayList<Long>();
//		String[] temp = input.split(",");
//		for (String s : temp) {
//			try {
//				long l = Long.parseLong(s);
//				res.add(l);
//			} catch (Exception e) {
//			}
//		}
//		return res;
//	}

//	public static boolean isFriend(long source_uid, long uid) throws Exception {
//		boolean res = false;
//		Vector<UserEntity> friends = getFrientList(source_uid);
//		for (UserEntity u : friends) {
//			if (u.mUid == uid) {
//				return true;
//			}
//		}
//		return res;
//	}

	public  long logMatch(long ownerUID, int roomId,
			int tableNumber) throws Exception {
//		System.out.println("Log match : " + matchID);
		// added by tuanha
		// if (true) return 5;
                Connection conn = DBPoolConnection.getConnection();
                
		try {
			String query = "{ call uspCreateMatch(?,?,?) }";
                        
		
			CallableStatement cs = conn.prepareCall(query);
			cs.clearParameters();

			cs.setLong(1, ownerUID);
			cs.setInt(2, roomId);
			cs.setInt(3, tableNumber);
			

			ResultSet rs = cs.executeQuery();
			while (rs.next()) {
				try {
					long l = rs.getLong("MatchID");
					cs.clearParameters();
					rs.close();
                    cs.close();
					return l;
				} catch (Exception e) {
				}
			}
		} 
		finally
        {
            conn.close();
        }       

		return 0;
	}
        
        
        private static int staticCounter=0;
        private static final int nBits=4;
        private static long getUnique() {
            return (System.currentTimeMillis() << nBits) | (staticCounter++ & 2^nBits-1);
        }

	public static long logMatch() throws Exception {
             return getUnique();   
	}

	public static void logUserMatch(long uid, long matchID, String desc,
			long money, boolean isWin, long matchIDAuto) throws Exception {
//		System.out.println("logUserMatch : " + uid);
		int win = 0;
		if (isWin) {
			win = 1;
		}
		String query = "{ call LogMatchuser(?,?,?,?,?,?,?) }";
                Connection conn = DBPoolConnection.getConnection();
                try
                {
                    CallableStatement cs = conn.prepareCall(query);
                    cs.clearParameters();

                    cs.setLong(1, uid);
                    cs.setLong(2, matchID);
                    cs.setInt(3, win);
                    cs.setLong(4, money);
                    cs.setString(5, desc);
                    cs.setInt(6, phantram);
                    cs.setLong(7, matchIDAuto);
                    cs.executeUpdate();

                    cs.clearParameters();
                    cs.close();
                }
                finally
                {
                    conn.close();
                }
	}

	// Fix

	public static void logUserVASC(long uid, String desc, long money,
			int logType) throws Exception {
		String query = "{ call LogVASC(?,?,?,?) }";
                Connection conn = DBPoolConnection.getConnection();
                try
                {
                    
                
                    CallableStatement cs = conn.prepareCall(query);
                    cs.clearParameters();

                    cs.setLong(1, uid);
                    cs.setLong(2, money);
                    cs.setString(3, desc);
                    cs.setInt(4, logType);

                    cs.executeUpdate();

                    cs.clearParameters();
                    cs.close();
                }
                finally
                {
                    conn.close();
                }
	}


	public static Vector<UserEntity> getRichests() throws Exception {
		Vector<UserEntity> res = new Vector<UserEntity>(); //Track: udvUserInfo, TopRich storeprocedure
		String query = "{ call TopRich() }";
                Connection conn = DBPoolConnection.getConnection();
                try
                {
                    CallableStatement cs = conn.prepareCall(query);
                    cs.clearParameters();
                    ResultSet rs = cs.executeQuery();
                    if(rs!= null)
                    {
                        
                        while (rs.next()) {

                            UserEntity user = new UserEntity();
                            user.mPassword = rs.getString("Password");
                            user.mUid = rs.getLong("UserID");
                            //System.out.println("======================tuanda:" + res.mUid);
                            user.mUsername = rs.getString("Name");
//                            user.mAge = rs.getInt("Age");
                            //res.lastLogin = rs.getDatetime("LastTime");
//                            user.lastLogin = rs.getDate("LastTime");
//                            user.mIsMale = rs.getInt("Sex")==1;
                            user.lastMatch=rs.getLong("lastMatch");
                            user.avatarID = rs.getInt("AvatarID");
                            user.level = rs.getInt("Level");
                            user.money = rs.getLong("Cash");
                            user.playsNumber = rs.getInt("WonPlaysNumber");
                            user.experience = rs.getInt("experience");

                            res.add(user);


                        }

                        rs.close();
                    }
                }
                finally
                {
                    conn.close();
                }

		return res;
	}

	public static Vector<UserEntity> getBestPlayer() throws Exception {
		Vector<UserEntity> res = new Vector<UserEntity>();
		String query = "{ call TopLevel() }";
                Connection conn = DBPoolConnection.getConnection();
                try
                {
                    

                    CallableStatement cs = conn.prepareCall(query);
                    cs.clearParameters();
                    ResultSet rs = cs.executeQuery();
                    if(rs!= null)
                    {
                        
                        while (rs.next()) {

                            UserEntity user = new UserEntity();
                            user.mPassword = rs.getString("Password");
                            user.mUid = rs.getLong("UserID");
                            //System.out.println("======================tuanda:" + res.mUid);
                            user.mUsername = rs.getString("Name");
                            user.mAge = rs.getInt("Age");
                            //res.lastLogin = rs.getDatetime("LastTime");
                            user.lastLogin = rs.getDate("LastTime");
                            user.mIsMale = rs.getInt("Sex")==1;
                            user.lastMatch=rs.getLong("lastMatch");
                            user.avatarID = rs.getInt("AvatarID");
                            user.level = rs.getInt("Level");
                            user.money = rs.getLong("Cash");
                            user.playsNumber = rs.getInt("WonPlaysNumber");
                            user.experience = rs.getInt("experience");

                            res.add(user);


                        }

                        rs.close();
                    }
                }
                finally
                {
                    conn.close();
                }
		return res;
	}

	public static Vector<UserEntity> getMostPlaying() throws Exception {
		Vector<UserEntity> res = new Vector<UserEntity>();
		String query = "{ call TopPlaysNumber() }";
                Connection conn = DBPoolConnection.getConnection();
                try
                {
                    
                
                    CallableStatement cs = conn.prepareCall(query);
                    cs.clearParameters();
                    ResultSet rs = cs.executeQuery();
                    if(rs!= null)
                    {
                        
                        while (rs.next()) {

                            UserEntity user = new UserEntity();
                            user.mPassword = rs.getString("Password");
                            user.mUid = rs.getLong("UserID");
                            //System.out.println("======================tuanda:" + res.mUid);
                            user.mUsername = rs.getString("Name");
                            user.mAge = rs.getInt("Age");
                            //res.lastLogin = rs.getDatetime("LastTime");
                            user.lastLogin = rs.getDate("LastTime");
                            user.mIsMale = rs.getInt("Sex")==1;
                            user.lastMatch=rs.getLong("lastMatch");
                            user.avatarID = rs.getInt("AvatarID");
                            user.level = rs.getInt("Level");
                            user.money = rs.getLong("Cash");
                            user.playsNumber = rs.getInt("WonPlaysNumber");
                            user.experience = rs.getInt("experience");

                            res.add(user);


                        }

                        rs.close();
                    }
                }
                finally
                {
                    conn.close();
                }
                
		return res;
	}

	public static boolean matchIsExist(long matchID) throws Exception {
		String query = "{ call FindMatch(?) }";
                Connection conn = DBPoolConnection.getConnection();
                try
                {
                    CallableStatement cs = conn.prepareCall(query);
                    cs.clearParameters();
                    cs.setLong(1, matchID);
                    ResultSet rs = cs.executeQuery();
                    if (rs != null && rs.next()) {
                            return true;
                    }

                    rs.close();
                }
                finally
                {
                    conn.close();
                }
		return false;
	}

//	public static Hashtable<Integer, Long> getRoomMoneyList() throws Exception {
//		Hashtable<Integer, Long> res = new Hashtable<Integer, Long>();
//		String query = "SELECT * FROM roomtype;";
//                Connection conn = DBConnection.getConnection();
//                try
//                {
//                    PreparedStatement st = conn.prepareStatement(query);
//                    ResultSet rs = st.executeQuery();
//                    while (rs.next()) {
//                            int roomType = rs.getInt("RoomTypeID");
//                            long money = rs.getLong("MinBook");
//                            res.put(roomType, money);
//                    }
//
//                    st.clearParameters();
//                    st.close();
//                    rs.close();
//                }
//                finally
//                {
//                    conn.close();
//                }
//		return res;
//	}

	public static void insertSuggestion(long uid, String note) throws Exception {
		String query = "INSERT INTO suggestion VALUES(?,GetDate(),?);";
                Connection conn = DBPoolConnection.getConnection();
                try
                {
                    PreparedStatement st = conn.prepareStatement(query);
                    st.setLong(1, uid);
                    st.setString(2, note);
                    st.executeUpdate();

                    st.clearParameters();
                    st.close();
                }
                finally
                {
                    conn.close();
                }
	}

	public static Vector<PostEntity> getPostList() throws Exception {
		Vector<PostEntity> res = new Vector<PostEntity>();
//		String query = "SELECT * FROM posts ORDER BY IDPosts DESC;";
//                Connection conn = DBConnection.getConnection();
//                try
//                {
//                    PreparedStatement st = conn.prepareStatement(query);
//                    ResultSet rs = st.executeQuery();
//                    while (rs.next()) {
//                            PostEntity v = new PostEntity();
//                            v.avatarID = 12;
//                            v.postID = rs.getInt("IDPosts");
//                            v.content = rs.getString("Content");
//                            v.postDate = rs.getLong("Date");
//                            v.title = rs.getString("Username");
//                            v.isNewComment = rs.getInt("isNewComment");
//                            res.add(v);
//                    }
//
//                    st.clearParameters();
//                    st.close();
//                    rs.close();
//                }
//                finally
//                {
//                    conn.close();
//                }
		return res;
	}

	public static Vector<PostEntity> getCommentList(long uid, int postID)
			throws Exception, DBException {
		System.out.println("IDPOST  " + postID);
		Vector<PostEntity> res = new Vector<PostEntity>();
//		String query = "SELECT * FROM posts WHERE IDPosts =?;";
//                Connection conn = DBConnection.getConnection();
//                try
//                {
//                    PreparedStatement st = conn.prepareStatement(query);
//                    st.setInt(1, postID);
//                    ResultSet rs = st.executeQuery();
//
//                    UserDB userDb = new UserDB();
//                    if (rs != null && rs.next()) {
//                            PostEntity v = new PostEntity();
//                            v.avatarID = userDb.getUserInfo(rs.getString("Username")).avatarID;
//                            v.postID = rs.getInt("IDPosts");
//                            v.content = rs.getString("Content");
//                            v.postDate = rs.getLong("Date");
//                            v.title = rs.getString("Username");
//                            res.add(v);
//                    }
//
//                    st.clearParameters();
//                    st.close();
//                    rs.close();
//
//                    query = "SELECT * FROM comments WHERE IDPosts =?;";
//                    st = conn.prepareStatement(query);
//                    st.setInt(1, postID);
//                    rs = st.executeQuery();
//
//                    while (rs.next()) {
//                            PostEntity v = new PostEntity();
//                            v.avatarID = userDb.getUserInfo(rs.getString("Username")).avatarID;
//                            v.postID = rs.getInt("IDPosts");
//                            v.content = rs.getString("Comment");
//                            v.postDate = rs.getLong("Date");
//                            v.title = rs.getString("Username");
//                            res.add(v);
//                    }
//
//                    st.clearParameters();
//                    st.close();
//                    rs.close();
//                }
//                finally
//                {
//                    conn.close();
//                }
		return res;
	}

	public static void insertPost(String username, String note)
			throws Exception {
		Calendar cal = Calendar.getInstance();
		long time = cal.getTimeInMillis();
		String query = "INSERT INTO posts(Content,Date,Username) VALUES(?,?,?);";
                Connection conn = DBPoolConnection.getConnection();
                try
                {
                    PreparedStatement st = conn.prepareStatement(query);
                    st.setString(1, note);
                    st.setLong(2, time);
                    st.setString(3, username);
                    st.executeUpdate();

                    st.clearParameters();
                    st.close();
                }
                finally
                {
                    conn.close();
                }
	}

	public static void insertComment(int idPost, String userName, String comment)
			throws Exception {
		String query = "INSERT INTO comments(IDPosts,Username,Comment,Date) VALUES(?,?,?,?);";
                Connection conn = DBPoolConnection.getConnection();
                try
                {
                    PreparedStatement st = conn.prepareStatement(query);
                    Calendar cal = Calendar.getInstance();
                    long time = cal.getTimeInMillis();
                    st.setInt(1, idPost);
                    st.setString(2, userName);
                    st.setString(3, comment);
                    st.setLong(4, time);
                    st.executeUpdate();

                    st.clearParameters();
                    st.close();
                }
                finally
                {
                    conn.close();
                }

	}

	

	

	public static void updateNewComment(int idPost, int status)
			throws Exception {
		String query = "UPDATE posts SET isNewComment=? WHERE IDPosts=?;";
                Connection conn = DBPoolConnection.getConnection();
                try
                {
                    PreparedStatement st = conn.prepareStatement(query);
                    st.setInt(1, status);
                    st.setInt(2, idPost);
                    st.executeUpdate();

                    st.clearParameters();
                    st.close();
                }
                finally
                {
                    conn.close();
                }
	}

	public static String getUserNameByPost(int idPost) throws Exception {
//		String query = "SELECT Username FROM posts  WHERE IDPosts=?;";
//                Connection conn = DBConnection.getConnection();
//                String res = "undefined";
//                try
//                {
//                    PreparedStatement st = conn.prepareStatement(query);
//                    st.setInt(1, idPost);
//                    
//                    ResultSet rs = st.executeQuery();
//                    if (rs != null && rs.next()) {
//                            res = rs.getString("Username");
//                    }
//
//                    st.clearParameters();
//                    st.close();
//                    rs.close();
//                }
//                finally
//                {
//                    conn.close();
//                }
		return null;
	}

	public static void insertOfflineMess(long uid, long desID, String mess)
			throws Exception {
//		String query = "insert into offlinemessage (userIDSend,userIDReceive,mes,datetimeSend) values (?,?,?,GetDate());";
//                Connection conn = DBConnection.getConnection();
//                try
//                {
//                    CallableStatement cs = conn.prepareCall(query);
//                    cs.clearParameters();
//                    cs.setLong(1, uid);
//                    cs.setLong(2, desID);
//                    cs.setString(3, mess);
//                    cs.executeUpdate();
//
//                    cs.clearParameters();
//                    cs.close();
//                }
//                finally
//                {
//                    conn.close();
//                }
	}

//	public static void deleteAllMessByDesID(long desID) throws Exception {
//		String query = "DELETE FROM offlinemessage WHERE userIDReceive=?;";
//                Connection conn = DBConnection.getConnection();
//                try
//                {
//                    CallableStatement cs = conn.prepareCall(query);
//                    cs.clearParameters();
//                    cs.setLong(1, desID);
//                    cs.executeUpdate();
//
//                    cs.clearParameters();
//                    cs.close();
//                }
//                finally
//                {
//                    conn.close();
//                }
//	}

	public static Vector<MessageOfflineEntity> getMessageOffline(long desID)
			throws Exception {
		Vector<MessageOfflineEntity> res = new Vector<MessageOfflineEntity>();
//		String query = "SELECT * FROM offlinemessage WHERE userIDReceive =?;";
//                Connection conn = DBConnection.getConnection();
//                try
//                {
//                    PreparedStatement st = conn.prepareStatement(query);
//                    st.setLong(1, desID);
//                    ResultSet rs = st.executeQuery();
//                    while (rs.next()) {
//                            MessageOfflineEntity v = new MessageOfflineEntity();
//                            v.sendID = rs.getLong("userIDSend");
//                            v.datetime = rs.getDate("datetimeSend");
//                            v.mess = rs.getString("mes");
//                            UserDB userDb = new UserDB();
//
//                            v.sendName = userDb.getUserInfo(v.sendID).mUsername;
//
//                            res.add(v);
//                    }
//                    deleteAllMessByDesID(desID);
//
//                    st.clearParameters();
//                    st.close();
//                    rs.close();
//                }
//                finally
//                {
//                    conn.close();
//                }
		return res;
	}
}
