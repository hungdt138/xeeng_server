package com.tv.xeeng.base.business;


import java.util.Vector;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetWaitingListRequest;
import com.tv.xeeng.base.protocol.messages.GetWaitingListResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.room.RoomEntity;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class GetWaitingListBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(GetWaitingListBusiness.class);

	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {
		mLog.debug("[GET WAITING ROOM LIST]: Catch : "
				+ aSession.getCurrentZone());
                
                aSession.getCollectInfo().append("->GetWaitingRoomList: ");
                
		MessageFactory msgFactory = aSession.getMessageFactory();
		GetWaitingListResponse resGetWaitingList = (GetWaitingListResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		try {
			GetWaitingListRequest rqGetWaitingList = (GetWaitingListRequest) aReqMsg;
			Zone zone = aSession.findZone(aSession.getCurrentZone());

			if (zone == null) {
				mLog.error("Error : Cant get zone ! "
						+ aSession.getCurrentZone() + " : ["
						+ aSession.getUserName() + "]");
				if (aSession.getCurrentZone() == -1) {
					aSession.setCurrentZone(ZoneID.BACAY);
//					DatabaseDriver.updateUserZone(aSession.getUID(),
//							ZoneID.BACAY);
					zone = aSession.findZone(aSession.getCurrentZone());
				}
			}
			Vector<RoomEntity> waitingRooms = zone.dumpWaitingRooms(
					rqGetWaitingList.mOffset, rqGetWaitingList.mLength,
					rqGetWaitingList.level, rqGetWaitingList.minLevel,
					aSession.getCurrentZone());
			mLog.debug("[GET WAITING ROOM LIST]: size - " + waitingRooms.size());
			resGetWaitingList.setSuccess(ResponseCode.SUCCESS,
					waitingRooms.size(), waitingRooms,
					aSession.getCurrentZone());

		} catch (Throwable t) {
			resGetWaitingList.setFailure(ResponseCode.FAILURE,
					"Không thể kết nối đến cơ sở dữ liệu ");
			mLog.error("Process message " + aReqMsg.getID() + " error.", t);
		} finally {
			if ((resGetWaitingList != null)) {
				aResPkg.addMessage(resGetWaitingList);
			}
		}

		return 1;
	}
}
