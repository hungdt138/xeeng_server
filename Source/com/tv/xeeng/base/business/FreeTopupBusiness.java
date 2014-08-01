package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.FreeTopupResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

public class FreeTopupBusiness extends AbstractBusiness {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(FreeTopupBusiness.class);
   
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg,
            IResponsePackage aResPkg) throws ServerException {
        mLog.debug("[Free topup] : Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        FreeTopupResponse resFree = (FreeTopupResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        
        try {
            UserDB db = new UserDB();
            int result = db.freeTopup(aSession.getUID());
            if(result== -1)
            {
                resFree.times = 0;
                throw new BusinessException("Bạn đã hết số lần nạp free");
                
            }
            else if(result== -2)
            {
                resFree.times = 3;
                throw new BusinessException("Số tiền bạn có nhiều hơn so với qui định để nạp Gold miễn phí");
            }
            
            resFree.times = result;
            resFree.mCode = ResponseCode.SUCCESS;
            CacheUserInfo.updateUserCashFromDB(aSession.getUID(), 1000);
            

        } 
        catch(BusinessException ex)
        {
            resFree.setFailure(ResponseCode.FAILURE, ex.getMessage());
        }
        catch (Throwable t) {
            resFree.setFailure(ResponseCode.FAILURE, "Bị lỗi kick out");
            mLog.error("Process message " + aReqMsg.getID() + " error.", t);
        } finally {
            if ((resFree != null)) {
                aResPkg.addMessage(resFree);
            }
        }
        return 1;
    }
}
