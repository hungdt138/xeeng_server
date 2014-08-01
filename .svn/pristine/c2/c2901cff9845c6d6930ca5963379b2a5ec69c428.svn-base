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
public class DeadSessionJob implements Job {
    private static Logger log = LoggerFactory.getLogger(DeadSessionJob.class);
    private static boolean isInProgress = false;
    private byte[] zonesGame = {ZoneID.BACAY, ZoneID.PHOM, ZoneID.TIENLEN};
    private static ZoneManager zManager = Server.getWorker().getZoneManager();
    
    
    

    public void execute(JobExecutionContext jec) throws JobExecutionException {
//        if(!isInProgress)
//        {
//            isInProgress = true;
//            log.info("------- Begin execute dead session job----------------------");
//            try {
//               UserDB db = new UserDB();
//               List<Long> users =  db.getUserToCheck();
//               
//               //dump waiting room and remove dead session of this user
//               for(int i = 0; i< zonesGame.length; i++) {
//					
//					
//                        Zone zone = zManager.findZone(zonesGame[i]);
//                        Vector<RoomEntity> rooms = zone.dumpWaitingRooms();
//                        
//                        for(int j = 0;j < users.size(); j++)
//                        {
//                            for(int k= 0; k< rooms.size(); k++)
//                            {
//                                SimpleTable table = (SimpleTable)rooms.get(k).mAttactmentData;
//                                SimplePlayer deadPlayer = table.findPlayer(i);
//                                if(deadPlayer!= null)
//                                {
//                                    // this is a dead session. please kill it
//                                    log.info("Remove dead session of player" + deadPlayer.username);
//                                    table.removePlayer(deadPlayer);
//                                }
//                            }
//                            
//                            //save to database
//                        }
//                        
//                        
//                }
//               
//               
//            }catch (SQLException ex) {
//                ex.printStackTrace();
//                log.error(ex.getMessage());
//            } 
//            catch (Exception ex) {
//                ex.printStackTrace();
//                log.error(ex.getMessage());
//            }  
//            log.info("------- End execute dead session job----------------------");
//        }
//        
//        isInProgress = false;
        
    }
    
}
