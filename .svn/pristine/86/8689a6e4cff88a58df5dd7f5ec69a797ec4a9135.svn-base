/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.memcached.data;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.databaseDriven.EventDB;
import com.tv.xeeng.game.data.EventPlayerEntity;
import com.tv.xeeng.memcached.IMemcacheClient;

/**
 *
 * @author tuanda
 */
public class CacheEventInfo extends CacheUserInfo {
    private static final String EVENT_NAME_SPACE = "event";
    private static final int EVENT_TIME_CACHE = 600;
    private static final Logger mLog = LoggerContext.getLoggerFactory()
			.getLogger(CacheEventInfo.class);
    
     private  List<EventPlayerEntity> loadEventPlayerFromDB(int eventId) throws SQLException
    {
        EventDB db = new EventDB();
        return db.getTopPlayer(eventId);
    }
     
    public List<EventPlayerEntity> getEventPlayer(int eventId)
    {
        try
        {
            if(isUseCache)
            {
                IMemcacheClient client = cachedPool.borrowClient();
                List<EventPlayerEntity>  enity = null;
                try
                {
                        
                String key = EVENT_NAME_SPACE + Integer.toString(eventId);
                enity = (List<EventPlayerEntity> )client.get(key);
                if(enity == null)
                {
//                    loadFromDatabase++;
                    enity = loadEventPlayerFromDB(eventId);
                    client.set(key, EVENT_TIME_CACHE, enity);
                }
                }
                finally
                {

                    cachedPool.returnClient(client);
                }
                return enity;

            }
        }
         catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
            return null;
        }
        catch(Exception ex)
        {
            mLog.error(ex.getMessage(), ex);
                    
        }
        
        try {
            return loadEventPlayerFromDB(eventId);
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        }
        
        
        return null;
    }
    
}
