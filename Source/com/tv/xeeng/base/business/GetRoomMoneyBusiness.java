package com.tv.xeeng.base.business;

import java.util.Hashtable;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.GetRoomMoneyResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class GetRoomMoneyBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetRoomMoneyBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
    	
        mLog.debug("[GET ROOM MONEY LIST]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetRoomMoneyResponse resGetRoomMoneyList = (GetRoomMoneyResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            
            long uid = aSession.getUID();
            mLog.debug("[GET ROOM MONEY LIST]:" + uid);
            //TODO  why we need room money list
            Hashtable<Integer, Long> list = new Hashtable<Integer, Long>();//DatabaseDriver.getRoomMoneyList();
            resGetRoomMoneyList.setSuccess(ResponseCode.SUCCESS, list);

        } catch (Throwable t) {
            resGetRoomMoneyList.setFailure(ResponseCode.FAILURE, "Không thể kết nối đến cơ sở dữ liệu ");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resGetRoomMoneyList != null) ) {
                aResPkg.addMessage(resGetRoomMoneyList);
            }
        }

        return 1;
    }
}
