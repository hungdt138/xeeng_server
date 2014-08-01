/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.BotRequest;
import com.tv.xeeng.base.protocol.messages.DoneDutyRequest;
import com.tv.xeeng.base.protocol.messages.DoneDutyResponse;
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
public class DoneDutyBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(DoneDutyBusiness.class);
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {

        DoneDutyRequest rqBot = (DoneDutyRequest) aReqMsg;
        MessageFactory msgFactory = aSession.getMessageFactory();
        DoneDutyResponse resDoneDuty = (DoneDutyResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        try
        {
        
            DutyDB db = new DutyDB();
            List<DutyEntity> lstDuties = db.getDuties();
            DutyEntity dutyEntity = null;
            int size = lstDuties.size();
            for(int i = 0; i< size; i++)
            {
                DutyEntity entity = lstDuties.get(i);
                if(entity.getDutyId() == rqBot.dutyId)
                {
                    dutyEntity = entity;
                    break;
                }
            }
            
            if(dutyEntity==  null)
                throw new BusinessException("Không tồn tại nhiệm vụ này");
            
            SimpleDateFormat sdf = new SimpleDateFormat(dutyEntity.getDateFomat());
            Date dtNow = new Date();
            String dtNowString = sdf.format(dtNow);
            if(!dtNowString.matches(dutyEntity.getDtDuty()))
            {
                if(rqBot.dutyId == 1)
                {
                    throw new BusinessException("Đã hết hoặc chưa đến giờ báo danh");
                }
                else
                {
                    throw new BusinessException("Đã hết giờ làm nhiệm vụ hoặc chưa đến giờ");
                }
            }
            
                int ret = db.doneDuty(rqBot.dutyId, aSession.getUID());

                if(ret == -2)
                    throw new BusinessException("Bạn đã báo danh rồi");
            
                if(ret == -3)
                    throw new BusinessException("Đã đủ số người báo danh nhận giải. Lần sau bạn hãy đến báo danh sớm hơn");
                
                resDoneDuty.setSuccess("Bạn báo danh thành công và được thưởng 3.000 Gold vào tài khoản");
            
        }
        catch(BusinessException be)
        {
            resDoneDuty.setFailure(be.getMessage());
        }
        
        catch(Exception ex )
        {
            resDoneDuty.setFailure("Co loi xay ra");
            mLog.error(ex.getMessage(), ex);
            
        }
        
        aResPkg.addMessage(resDoneDuty);
        return 1;
    }
}
