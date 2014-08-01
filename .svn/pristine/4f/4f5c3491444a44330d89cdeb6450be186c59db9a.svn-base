/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.BotRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;





/**
 *
 * @author tuanda
 */
public class BotBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(BotBusiness.class);
    
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
//        mLog.warn("Reload cache");
//        DBCache.reload();
//           try{
//                    Server.getWorker().getTourMgr().reload();
//            } catch (Throwable e) {
//                    mLog.error(e.getMessage());
//                    }
        BotRequest rqBot = (BotRequest) aReqMsg;
        mLog.debug("bot session");
        UserDB db = new UserDB();
        if(db.checkBotUser(aSession.getUID()))
        {
            aSession.setBotType(rqBot.botType);
        }
        
        return 1;
    }
}
