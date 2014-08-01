/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.monitor;

import java.util.Vector;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.room.RoomEntity;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.room.ZoneManager;
import com.tv.xeeng.server.Server;

/**
 *
 * @author tuanda
 */

public class GameJob implements Job {
    private static Logger log = LoggerContext.getLoggerFactory().getLogger(GameJob.class);
    private static boolean isInProgress = false;
    private static ZoneManager zManager = Server.getWorker().getZoneManager();
    private byte[] zonesGame = {ZoneID.SAM, ZoneID.TIENLEN, ZoneID.PHOM, ZoneID.NEW_BA_CAY, ZoneID.BAU_CUA_TOM_CA, ZoneID.PIKACHU, ZoneID.AILATRIEUPHU};
    private static final int SCHEDULE_PERIOD = 2 * 1000;

    protected void doTimeout(Vector<RoomEntity> rooms) {
        long currentTime = System.currentTimeMillis() + SCHEDULE_PERIOD;
        for (int i = 0; i < rooms.size(); i++) {
            long matchId = 0;
            try {
                SimpleTable table = (SimpleTable) rooms.get(i).mAttactmentData;
                matchId = table.matchID;
                //log.debug("isPlaying " + table.isPlaying);
                if (table.isPlaying && (currentTime - table.getLastActivated() > table.getCurrentTimeOut())) {
                    log.debug("[Auto process] by monitor match id: " + table.matchID + " name: " + table.name);
                    // over timeout don't wait until current player plays.
                    // Server will send automatically card
                    table.doTimeout();
                }
            } catch (Exception ex) {
                log.error("do timeout error matchId" + matchId, ex);
            }
        }
    }

    protected void doKickout(Vector<RoomEntity> rooms) {
        long currentTime = System.currentTimeMillis() + SCHEDULE_PERIOD;
        for (int i = 0; i < rooms.size(); i++) {
            long matchId = 0;
            try {
                SimpleTable table = (SimpleTable) rooms.get(i).mAttactmentData;
                if (currentTime - table.lastaActive > table.kickOutTime) {
                    table.doAutoKickOut();
                }
            } catch (Exception ex) {
                log.error("do timeout error matchId" + matchId, ex);
            }
        }
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        if (!isInProgress) {
            isInProgress = true;
            try {
                for (int i = 0; i < zonesGame.length; i++) {
                    try {
                        Zone zone = zManager.findZone(zonesGame[i]);
                        Vector<RoomEntity> rooms = zone.dumpPlayingRooms(zonesGame[i]);
                        doTimeout(rooms);
                        
                        //Vector<RoomEntity> rooms1 = zone.dumpRooms(zonesGame[i]);
                        //doKickout(rooms1);
                    } catch (Exception ex) {
                        log.error(ex.getMessage(), ex);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error(ex.getMessage(), ex);
            }
        }

        isInProgress = false;
    }
}
