package com.tv.xeeng.base.business;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetTopPlayerTourRequest;
import com.tv.xeeng.base.protocol.messages.GetTopPlayerTourResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class GetTopPlayerTourBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(BocPhomBusiness.class);

	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {

		mLog.debug("[Get Top Player Tour]: Catch");
		MessageFactory msgFactory = aSession.getMessageFactory();
		GetTopPlayerTourResponse resBoc = (GetTopPlayerTourResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		try {
			GetTopPlayerTourRequest rqBoc = (GetTopPlayerTourRequest) aReqMsg;
			int tID = rqBoc.tourID;
			resBoc.setSuccess(getTop10(tID));
		} catch (Throwable t) {
			resBoc.setFailure(ResponseCode.FAILURE,
					"Có lỗi xảy ra." + t.getMessage());
			t.printStackTrace();
		} finally {
			aResPkg.addMessage(resBoc);
		}
		return 1;
	}

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
