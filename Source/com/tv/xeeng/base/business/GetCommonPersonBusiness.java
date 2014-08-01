/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;


import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetCommonPersonRequest;
import com.tv.xeeng.base.protocol.messages.GetCommonPersonResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.InfoDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.CharacterEntity;
import com.tv.xeeng.game.data.CityEntity;
import com.tv.xeeng.game.data.JobEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;









/**
 *
 * @author tuanda
 */
public class GetCommonPersonBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetCommonPersonBusiness.class);
    
    
    
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
//        mLog.debug("get albums");
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetCommonPersonResponse resChat = (GetCommonPersonResponse) msgFactory.getResponseMessage(aReqMsg.getID());

        try {
            GetCommonPersonRequest request = (GetCommonPersonRequest)aReqMsg;
            resChat.mCode = ResponseCode.SUCCESS;      
            
            
            
            resChat.value = InfoDB.getPersonCommonInfo();
            
            //send to sender first
            aSession.write(resChat);
           
            
        } 
        
        catch (ServerException ex) {
            mLog.error(ex.getMessage(), ex);
                        
        }
        
             
        return 1;
    }
}
