/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.monitor;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.LogDB;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.room.ZoneManager;
import com.tv.xeeng.server.Server;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author tuanda
 */
public class NumberOnlineJob implements Job {

    private static Logger log = LoggerContext.getLoggerFactory()
            .getLogger(NumberOnlineJob.class);
    private static boolean isInProgress = false;
    private static ZoneManager zManager = Server.getWorker().getZoneManager();
    private byte[] zonesGame = {ZoneID.TIENLEN, ZoneID.PHOM, ZoneID.NEW_BA_CAY,
            ZoneID.BAU_CUA_TOM_CA, ZoneID.AILATRIEUPHU, ZoneID.PIKACHU};

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        if (!isInProgress) {
            isInProgress = true;
            try {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < zonesGame.length; i++) {
                    Zone zone = zManager.findZone(zonesGame[i]);
                    sb.append(zone.getZoneId()).append("@").append(zone.numberOnline()).append("#");
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                Server.numberOnline = sb.toString();

                try {
//                    LogDB.insertNumOfUserOnline(Server.getWorker().getmSessionMgr().getmSessions().size());

                    int totalNumberOnline = 0;
                    ConcurrentHashMap<String, ISession> sessions = Server.getWorker().getmSessionMgr().getmSessions();
                    for (Map.Entry<String, ISession> s : sessions.entrySet()) {
                        String userId = s.getKey();
                        ISession session = s.getValue();

                        if (session.isLoggedIn()) {
                            totalNumberOnline += 1;
                        }
                    }

                    int numTienLen = zManager.findZone(ZoneID.TIENLEN).getNumOfUser();
                    int numPhom = zManager.findZone(ZoneID.PHOM).getNumOfUser();
                    int numBaCay = zManager.findZone(ZoneID.BACAY).getNumOfUser();
                    int numBauCua = zManager.findZone(ZoneID.BAU_CUA_TOM_CA).getNumOfUser();
                    int numALTP = zManager.findZone(ZoneID.AILATRIEUPHU).getNumOfUser();
                    int numPikachu = zManager.findZone(ZoneID.PIKACHU).getNumOfUser();

                    int totalNumberOnlineByZone = numTienLen + numPhom + numBaCay + numBauCua + numALTP + numPikachu;
//                    if (totalNumberOnline < totalNumberOnlineByZone) {
//                        totalNumberOnline = totalNumberOnlineByZone; // tránh gây ra cảm giác không hợp lý
//                    }

                    LogDB.insertNumOfUserOnline(totalNumberOnline, numTienLen, numPhom, numBaCay, numBauCua, numALTP, numPikachu);
                } catch (NumberFormatException ex) {

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error(ex.getMessage(), ex);
            }

        }

        isInProgress = false;

    }
}
