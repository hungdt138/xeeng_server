/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.monitor;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.room.ZoneManager;
import com.tv.xeeng.server.Server;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 * @author tuanda
 */
public class OnlineOvernightJob implements Job {

    private static Logger log = LoggerContext.getLoggerFactory()
            .getLogger(OnlineOvernightJob.class);
    private static boolean isInProgress = false;
    private static ZoneManager zManager = Server.getWorker().getZoneManager();
    private byte[] zonesGame = {ZoneID.TIENLEN, ZoneID.PHOM, ZoneID.NEW_BA_CAY,
        ZoneID.BAU_CUA_TOM_CA, ZoneID.AILATRIEUPHU, ZoneID.PIKACHU};

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        if (!isInProgress) {
            isInProgress = true;
            try {
                ConcurrentHashMap<String, ISession> sessions = Server.getWorker().getmSessionMgr().getmSessions();
                for (Map.Entry<String, ISession> s : sessions.entrySet()) {
                    String userId = s.getKey();
                    ISession session = s.getValue();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error(ex.getMessage(), ex);
            }

        }

        isInProgress = false;
    }
}
