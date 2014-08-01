package com.tv.xeeng.base.business;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.TimeOutRequest;
import com.tv.xeeng.base.protocol.messages.TimeOutResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class TimeOutBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(TimeOutBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        boolean isSuccess = false;
        MessageFactory msgFactory = aSession.getMessageFactory();
        TimeOutResponse resTimeOut = (TimeOutResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            mLog.debug("[ SUGGEST ]: Catch");
            TimeOutRequest rqTimeOut = (TimeOutRequest) aReqMsg;
            Zone bacayZone = aSession.findZone(aSession.getCurrentZone());
            Room currentRoom = bacayZone.findRoom(rqTimeOut.mMatchId);

            UserDB userDb = new UserDB();
            UserEntity newUser = userDb.getUserInfo(rqTimeOut.player_friend_id);
            if (newUser != null) {
                if (currentRoom == null) {
                    resTimeOut.setFailure(ResponseCode.FAILURE,"Bạn đã thoát khỏi room");
                }
            } else {
                resTimeOut.setFailure(ResponseCode.FAILURE, "Không tìm thấy bạn chơi nữa!");
            }
        } catch (Throwable t) {
            resTimeOut.setFailure(ResponseCode.FAILURE, "bị lỗi!");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if (resTimeOut != null && !isSuccess) {
                aResPkg.addMessage(resTimeOut);
            }
        }
        return 1;
    }
}
