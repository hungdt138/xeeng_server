/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.monitor;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.SimplePlayer;
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
public class TienLenJob implements Job {
    private static Logger log = LoggerContext.getLoggerFactory().getLogger(TienLenJob.class);
    private static boolean isInProgress = false;
    
    private static ZoneManager zManager = Server.getWorker().getZoneManager();
    private static final int SCHEDULE_PERIOD = 2 *1000;
    
    protected void doTimeout(Vector<RoomEntity> rooms)
    {
        long currentTime = System.currentTimeMillis()+ SCHEDULE_PERIOD;
        for (int i = 0; i < rooms.size(); i++) {
            long matchId = 0;
            try
            {
                SimpleTable table = (SimpleTable) rooms.get(i).mAttactmentData;
                matchId = table.matchID;
                long notActivatedTime = currentTime - table.getLastActivated();
                if (table.isPlaying &&(notActivatedTime > table.getCurrentTimeOut()) ) {
//                        log.warn("[Begin Auto process] by monitor matchId"+matchId + " notActivatedTime: " + notActivatedTime);
                        // over timeout don't wait until current player plays.
                        // Server will send automatically card
                    table.doTimeout();
//                        log.warn("[End Auto process] by monitor matchId"+matchId);
                }
            }catch(Exception ex)   
            {
                log.error("do timeout tienlen error matchId" + matchId, ex);
            }  
        }
    }

    public void execute(JobExecutionContext jec) throws JobExecutionException {
        if(!isInProgress)
        {
            isInProgress = true;
            
            try {
               Zone zone = zManager.findZone(ZoneID.TIENLEN);
               //log.warn("Begin run tienlen job timeout");
               //long beginTime = System.currentTimeMillis();
               Vector<RoomEntity> rooms = zone.dumpPlayingRooms(ZoneID.TIENLEN);
               doTimeout(rooms);
                
               //log.warn("End run tienlen job timeout with time" + (System.currentTimeMillis()- beginTime));    
            }
            catch (Exception ex) {
                ex.printStackTrace();
                log.error(ex.getMessage(), ex);
            }
        }
        
        isInProgress = false;
    }
    
}
