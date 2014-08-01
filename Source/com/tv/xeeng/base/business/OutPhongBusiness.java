package com.tv.xeeng.base.business;


import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.room.Phong;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;



public class OutPhongBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(OutPhongBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        try {
            int zoneID = aSession.getCurrentZone();
            Zone zone = aSession.findZone(zoneID);
            mLog.debug("Phong:" + aSession.getPhongID());
            if(aSession.getPhongID() != 0){
                Phong currentPhong = zone.getPhong(aSession.getPhongID());
                if(currentPhong == null)
                {
                    mLog.warn("OutPhongBusiness currentPHong= null [PhongID]" + aSession.getPhongID());
                }
                currentPhong.outPhong(aSession);
            }
            
        } catch (Throwable t) {
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        }
        return 1;
    }
}
