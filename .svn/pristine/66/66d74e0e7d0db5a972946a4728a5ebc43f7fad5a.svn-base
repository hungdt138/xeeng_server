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
public class BCTCJob implements Job {
    private static Logger log = LoggerContext.getLoggerFactory().getLogger(BCTCJob.class);
    private static boolean isInProgress = false;
    
    private static ZoneManager zManager = Server.getWorker().getZoneManager();
    
    private static final int SCHEDULE_PERIOD = 2 *1000;
    
    protected void doTimeout(Vector<RoomEntity> rooms)
    {
        long currentTime = System.currentTimeMillis() + SCHEDULE_PERIOD;
        for (int i = 0; i < rooms.size(); i++) {
            try
            {
                SimpleTable table = (SimpleTable) rooms.get(i).mAttactmentData;
               
                if (table.isPlaying &&(currentTime - table.getLastActivated() > table.getCurrentTimeOut()) ) {
                        log.debug("[Auto process] by monitor " + table.name);
                        // over timeout don't wait until current player plays.
                        // Server will send automatically card
                        table.doTimeout();
                }
            } catch(Exception ex)   
            {
                log.error(ex.getMessage(), ex);
            }   
        }
    }

    public void execute(JobExecutionContext jec) throws JobExecutionException {
        if(!isInProgress)
        {
            isInProgress = true;
            
            try {
                Zone zone = zManager.findZone(ZoneID.BAU_CUA_TOM_CA);
                Vector<RoomEntity> rooms = zone.dumpPlayingRooms(ZoneID.BAU_CUA_TOM_CA);
                doTimeout(rooms);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                log.error(ex.getMessage(), ex);
            }  
           
        }
        isInProgress = false;
    }
}
