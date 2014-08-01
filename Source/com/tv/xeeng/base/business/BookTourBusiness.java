package com.tv.xeeng.base.business;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.BookTourRequest;
import com.tv.xeeng.base.protocol.messages.BookTourResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.tournement.TourManager;
import com.tv.xeeng.game.tournement.TournementEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class BookTourBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(BookTourBusiness.class);

	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {

		mLog.debug("[Book Tour]: Catch");
		MessageFactory msgFactory = aSession.getMessageFactory();
		BookTourResponse resBoc = (BookTourResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		try {
			BookTourRequest rqBoc = (BookTourRequest) aReqMsg;
			long uid = aSession.getUID();
			int tID = rqBoc.tourID;
			TourManager mng = aSession.getTourMgr();
			TournementEntity tour = mng.getTour(tID);
			UserDB uDB = new UserDB();
                        CacheUserInfo cacheUser = new CacheUserInfo();
			UserEntity user = cacheUser.getUserInfo(uid);
			long m = tour.minBet * 2;
			if (user.money < m) {
				resBoc.setFailure(ResponseCode.FAILURE,
						"Bạn không đủ tiền đăng ký giải đấu.");
			} else {
				long res = uDB.userTourIsExist(uid, tID);
				if (res > 0) {
					resBoc.setFailure(ResponseCode.FAILURE,
							"Bạn đã đăng ký giải đấu này rồi.");
				} else {
					mng.book(user, tID);
					uDB.updateUserMoney(m, false, uid,
							"Đăng ký tham gia giải đấu " + tID, 0, 0);
					resBoc.setSuccess(tID);
				}
			}
		} catch (Throwable t) {
			resBoc.setFailure(ResponseCode.FAILURE, t.getMessage());
			// t.printStackTrace();
		} finally {
			aResPkg.addMessage(resBoc);
		}
		return 1;
	}

	@SuppressWarnings("unused")
	private ArrayList<UserEntity> getTop10(int id) throws SQLException {
		ArrayList<UserEntity> res = new ArrayList<UserEntity>();
		String query = "{ call getTop10(?) }";
		Connection con = DBPoolConnection.getConnection();
		CallableStatement cs = null;
		try {
			ResultSet rs = null;
			cs = con.prepareCall(query);
			cs.clearParameters();
			cs.setInt(1, id);

			rs = cs.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					UserEntity user = new UserEntity();
					user.mUsername = rs.getString("Name");
					user.money = rs.getLong("cash");
					res.add(user);
				}
			}
			rs.close();
			cs.clearParameters();
			cs.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null)
				con.close();
		}
		return res;
	}
}
