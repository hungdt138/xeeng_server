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
public class KickOutJob implements Job {
    private static Logger log = LoggerContext.getLoggerFactory().getLogger(KickOutJob.class);
    private static boolean isInProgress = false;
    
    private static ZoneManager zManager = Server.getWorker().getZoneManager();
    private byte[] zonesGame = {ZoneID.BACAY, ZoneID.PHOM, ZoneID.TIENLEN, ZoneID.NEW_BA_CAY};
    
    protected void kickoutTimeout(Zone zone, Vector<RoomEntity> rooms)
    {
        //long currentTime = System.currentTimeMillis()+SCHEDULE_PERIOD;
        for (int i = 0; i < rooms.size(); i++) {
            try
            {
                SimpleTable table = (SimpleTable) rooms.get(i).mAttactmentData;
                table.kickTimeout(zone.findRoom(rooms.get(i).mRoomId));
            }
            catch(Exception ex)
            {
                log.error("kickout " + ex.getMessage() + ex.getStackTrace());
            }
        }
    }

    public void execute(JobExecutionContext jec) throws JobExecutionException {
        if(!isInProgress)
        {
            isInProgress = true;
            
            try {
                for(int i = 0; i< zonesGame.length; i++)
                {
                    Zone zone = zManager.findZone(zonesGame[i]);
                    Vector<RoomEntity> rooms = zone.dumpWaitingRooms();
                    kickoutTimeout(zone, rooms);
                    
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                log.error(ex.getMessage(), ex);
            }
        }
        
        isInProgress = false;
    }
}