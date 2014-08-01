package com.tv.xeeng.base.business;

import java.lang.reflect.Field;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.AnnounceRequest;
import com.tv.xeeng.base.protocol.messages.AnnounceResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.room.Phong;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



public class AnnounceBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(AnnounceBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[Announce]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        AnnounceResponse resAnno = (AnnounceResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try {
            AnnounceRequest rq = (AnnounceRequest) aReqMsg;
            String msg = rq.message;
            resAnno.setSuccess(ResponseCode.SUCCESS, msg);
            Field[] temp = ZoneID.class.getFields();
			for (Field f : temp) {
				int zoneID = (Integer) f.get(new ZoneID());
				if(zoneID != 0){
					Zone zone = aSession.findZone(zoneID);
					Iterable<Phong> phongs = zone.phongValues();
					for(Phong p : phongs){
						p.broadcastZone(resAnno, aSession, false);
					}
				}
			}
        } catch (Throwable t) {
            resAnno.setFailure(ResponseCode.FAILURE, "Lỗi ");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        }
        return 1;
    }
}
