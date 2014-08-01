/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;



import java.util.List;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.BotRequest;
import com.tv.xeeng.base.protocol.messages.GetDutyDetailRequest;
import com.tv.xeeng.base.protocol.messages.GetDutyDetailResponse;
import com.tv.xeeng.base.protocol.messages.GetDutyResponse;
import com.tv.xeeng.base.protocol.messages.GetWapGameResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DutyDB;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.DutyEntity;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



/**
 *
 * @author tuanda
 */
public class GetDutyDetailBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetDutyDetailBusiness.class);
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {

        GetDutyDetailRequest rqBot = (GetDutyDetailRequest) aReqMsg;
        
        
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetDutyDetailResponse resWapGame = (GetDutyDetailResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try
        {
        
            DutyDB db = new DutyDB();
            List<DutyEntity> lstDuties = db.getDuties();
            boolean hasFound = false;
            int size = lstDuties.size();
            for(int i = 0; i< size; i++)
            {
                DutyEntity entity = lstDuties.get(i);
                if(entity.getDutyId() == rqBot.dutyId)
                {
                    hasFound = true;
                    resWapGame.setSuccess(entity.getDutyDetail());
                    break;
                }
            }
            
            if(!hasFound)
                resWapGame.setFailure("Không tồn tại nhiệm vụ này");
            
        }
        catch(Exception ex )
        {
            mLog.error(ex.getMessage(), ex);
            
        }
        aResPkg.addMessage(resWapGame);
        return 1;
    }
}
