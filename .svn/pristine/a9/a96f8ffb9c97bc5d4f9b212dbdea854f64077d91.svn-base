package com.tv.xeeng.game.tournement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.tv.xeeng.base.business.BusinessException;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.data.UserEntity;



public class TourManager {
	ArrayList<TournementEntity> tours;

	public ArrayList<TournementEntity> getToursInfo() {
		return tours;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<TournementEntity> getToursInfo(long uid)
			throws SQLException, DBException {
		ArrayList<TournementEntity> res = (ArrayList<TournementEntity>) tours
				.clone();
		UserDB db = new UserDB();
		for (TournementEntity t : res) {
			t.isBook = (db.userTourIsExist(uid, t.id) > 0);
		}
		return res;
	}

	public String getTourDetail(int t) throws SQLException {
		String res = "Đây là giải đấu. Thích thì chiến";
		String query = "{ call GetTourInfo(?) }";
		Connection con = DBPoolConnection.getConnection();
		ResultSet rs = null;
		CallableStatement cs = null;
		try {

			cs = con.prepareCall(query);
			cs.clearParameters();
			cs.setInt(1, t);

			rs = cs.executeQuery();
			if (rs != null && rs.next()) {
				res = rs.getString("desc");

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (cs != null) {
				cs.clearParameters();
				cs.close();
			}
			if (con != null)
				con.close();
		}
		return res;
	}
	
	public TourManager() {
		tours = new ArrayList<TournementEntity>(10);
		try {
			initTour();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TournementEntity getTour(int id) {
		for (TournementEntity t : tours) {
			if (t.id == id)
				return t;
		}
		return null;
	}

	public void book(UserEntity u, int id) throws BusinessException,
			SQLException {
		TournementEntity t = getTour(id);
		//long res = 0;
		if (t != null) {
			String query = "{ call BookTour(?,?,?) }";
			Connection con = DBPoolConnection.getConnection();
			CallableStatement cs = null;
			try {
				cs = con.prepareCall(query);
				cs.clearParameters();
				cs.setLong(1, u.mUid);
				cs.setInt(2, id);
				cs.setLong(3, t.minBet * 100);

				cs.execute();

				cs.clearParameters();
				cs.close();
				//res = t.minBet * time;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				con.close();
			}
		} else {
			throw new BusinessException("Không tìm thấy giải đấu!");
		}
		//return res;
	}
	public void reload()  throws SQLException {
//		synchronized (tours) {
			tours.clear();
			initTour();
//		}
		
	}
	private void initTour() throws SQLException {
		String query = "{ call GetAllTourInfo() }";
		Connection con = DBPoolConnection.getConnection();
		ResultSet rs = null;
		CallableStatement cs = null;
		try {

			cs = con.prepareCall(query);
			cs.clearParameters();

			rs = cs.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					// System.out.println();
					String name = rs.getString("Name");
					// System.out.println("Tour: "+ name);
					long minBet = rs.getLong("moneyBet");
					int id = rs.getInt("id");
					int g = rs.getInt("Game");
					Date s = rs.getTimestamp("StartDate");
					Date e = rs.getTimestamp("EndDate");
					long c = rs.getLong("Creator");
					tours.add(new TournementEntity(id, s, e, name, minBet, g, c));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (cs != null) {
				cs.clearParameters();
				cs.close();
			}
			if (con != null)
				con.close();
		}
	}

}
