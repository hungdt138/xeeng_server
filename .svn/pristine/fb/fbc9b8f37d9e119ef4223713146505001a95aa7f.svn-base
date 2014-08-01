package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.PikachuHelpRequest;
import com.tv.xeeng.base.protocol.messages.PikachuHelpResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.pikachu.datta.PikachuPlayer;
import com.tv.xeeng.game.pikachu.datta.PikachuTable;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class PikachuHelpBusiness extends AbstractBusiness {

	private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(PikachuHelpBusiness.class);

	public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
			IResponsePackage aResPkg) {
		mLog.debug("[Pikachu - Help] : Catch  ; " + aSession.getUserName());
		MessageFactory msgFactory = aSession.getMessageFactory();
		PikachuHelpResponse resMatchTurn = (PikachuHelpResponse) msgFactory
				.getResponseMessage(aReqMsg.getID());
		try {
			PikachuHelpRequest rq = (PikachuHelpRequest) aReqMsg;
			int zoneID = aSession.getCurrentZone();
			Zone zone = aSession.findZone(zoneID);
			Room room = zone.findRoom(rq.mMatchId);
			long currID = aSession.getUID();// rq.uid;
			if (room != null) {
				PikachuTable table = (PikachuTable) room.getAttactmentData();
				boolean isHelp = rq.isHelp;
				if (table.isPlaying) { // Stop
					PikachuPlayer pl = table.findPlayer(currID);
					if(isHelp) pl.hint++;
					else pl.revert++;
					resMatchTurn.setSuccess(isHelp);
					table.broadcastMsg(resMatchTurn, table.getNewPlayings(), table.getNewWaitings(),
                                                pl, true);
				}
			} else {
				mLog.error("Room is null ; matchID : " + rq.mMatchId + " ; "
						+ aSession.getUserName() + " ; zone = "
						+ aSession.getCurrentZone());

				resMatchTurn.setFailure(ResponseCode.FAILURE,
						"Bạn cần tham gia vào một trận trước khi chơi.");
				aSession.write(resMatchTurn);
			}
		} catch (ServerException ex) {
			resMatchTurn
					.setFailure(ResponseCode.FAILURE, "Không thể gửi được.");
			aResPkg.addMessage(resMatchTurn);
		} catch (Exception ex1) {
			// ex1.printStackTrace();
			resMatchTurn.setFailure(ResponseCode.FAILURE, ex1.getMessage());
			aResPkg.addMessage(resMatchTurn);
		}

		return 1;
	}
}
