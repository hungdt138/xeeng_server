/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.business;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.DBCache;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.Utils;
import com.tv.xeeng.protocol.AbstractBusiness;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.server.Server;

/**
 *
 * @author tuanda
 */
public class ReloadCacheBusiness extends AbstractBusiness {

    private static final org.slf4j.Logger mLog = LoggerContext.getLoggerFactory().getLogger(ReloadCacheBusiness.class);

    public int handleMessage(ISession aSession, IRequestMessage aReqMsg, IResponsePackage aResPkg) {
        mLog.warn("Reload cache");
        //UserDB db = new UserDB();

        if (Utils.isSuperUser(aSession.getUID())) {
            DBCache.reload();
            try {
                Server.getWorker().getTourMgr().reload();
                Server.changeCachedIP();
            } catch (Throwable e) {
                mLog.error(e.getMessage());
            }
        }
        return 1;
    }
}
