/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.protocol.messages.BotRequest;
import com.tv.xeeng.base.protocol.messages.GetDutyResponse;
import com.tv.xeeng.base.protocol.messages.GetWapGameResponse;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DutyDB;
import com.tv.xeeng.databaseDriven.UserDB;
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
public class GetDutyBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog =
            LoggerContext.getLoggerFactory().getLogger(GetDutyBusiness.class);
    
    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        MessageFactory msgFactory = aSession.getMessageFactory();
        GetDutyResponse resWapGame = (GetDutyResponse) msgFactory.getResponseMessage(aReqMsg.getID());
        DutyDB db = new DutyDB();
        resWapGame.setSuccess(db.getListDuty());
        aResPkg.addMessage(resWapGame);
        return 1;
    }
}
