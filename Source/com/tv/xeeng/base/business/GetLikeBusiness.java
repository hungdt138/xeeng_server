/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;


import java.sql.SQLException;
import java.util.List;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetLikeRequest;
import com.tv.xeeng.base.protocol.messages.GetLikeResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.LikeHistoryDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.LikeHistoryEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;

/**
 *
 * @author tuanda
 */
public class GetLikeBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetLikeBusiness.class);
    
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.debug("get albums");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetLikeResponse resLk = (GetLikeResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        try {
            
            GetLikeRequest rqComment = (GetLikeRequest) aReqMsg;
            
            LikeHistoryDB db = new LikeHistoryDB();
            List<LikeHistoryEntity> lstLks = db.getLike(rqComment.systemObjectId, rqComment.systemObjectRecordId);
            int size = lstLks.size();
            
            StringBuilder sb = new StringBuilder();
            
            for(int i = 0; i< size; i++)
            {
                LikeHistoryEntity entity = lstLks.get(i);
                
                
                sb.append(Long.toString(entity.getUserId())).append(AIOConstants.SEPERATOR_BYTE_1);
                
                sb.append(entity.getUserName()).append(AIOConstants.SEPERATOR_BYTE_2);
                
                
            }
            
            
            
            if(sb.length()>0)
            {
                
                sb.deleteCharAt(sb.length() -1);
            }
            
            resLk.mCode = ResponseCode.SUCCESS;        
            
            resLk.value = sb.toString();
            aSession.write(resLk);
            
            
        } 
        catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        }         
        catch (ServerException ex) {
            mLog.error(ex.getMessage(), ex);
        }
//        catch(BusinessException ex)
//        {
//            mLog.warn(ex.getMessage());
//            resalbum.setFailure(ex.getMessage());
//            
//        }
        finally
        {
            
        }        
        return 1;
    }
}
