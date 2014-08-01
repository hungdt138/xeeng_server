package com.tv.xeeng.base.business;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.QuayCNKDResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.ItemDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;



import java.util.Random;

public class QuayCNKDBusiness extends AbstractBusiness {

    private static final Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(QuayCNKDBusiness.class);
    
    private static final int[] arrBonusMoney = {0, 0, 0, 0, 2500, 2500, 5000, 5000, 10000, 10000, 25000, 50000};
    private static final int[] arrBonusTimes = {0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final int[] arrClient = {0, 2, 1, 3, 20, 22, 11, 13, 10, 12, 21, 23};

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("[BUY_ITEM]: Catch");
        MessageFactory msgFactory = aSession.getMessageFactory();
        QuayCNKDResponse resBuyAvatar =
                (QuayCNKDResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        
        try {
//            QuayCNKDRequest rqBuyAvatar = (QuayCNKDRequest) aReqMsg;
//            ItemDB db = new ItemDB();
            
            Random rand = new Random(System.currentTimeMillis());
            int ret = (int)(Math.abs(rand.nextLong() % 12));
            
            if(ret> 9)
            {
                //it means win 5 or 10 times
                int nextRet = (int)(Math.abs(rand.nextLong() % 5));
                if(nextRet>0)
                {
                    ret = (int)(Math.abs(rand.nextLong() % 4)) +2; // it fails
                }
                
            }
            
            ItemDB db = new ItemDB();
            UserEntity entity = db.quayCNKD(aSession.getUID(), arrBonusMoney[ret], arrBonusTimes[ret]);
            
            if(entity.timesQuay < -1)
            {
                throw new BusinessException("Bạn không đủ 5.000 Gold để quay vòng");
            }
            
            //update cash
            StringBuilder sb = new StringBuilder();
            sb.append(arrClient[ret]).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(entity.timesQuay);
            
            
           resBuyAvatar.mCode = ResponseCode.SUCCESS;
           resBuyAvatar.value = sb.toString();
                    
        }
        catch(BusinessException be)
        {
            resBuyAvatar.setFailure(be.getMessage());
        }
        catch (Throwable t) {
            mLog.error(t.getMessage(), t);
            resBuyAvatar.setFailure("Có lỗi xảy ra");
        } finally {
            if ((resBuyAvatar != null)) {
                aResPkg.addMessage(resBuyAvatar);
            }
        }
        return 1;
    }
}
