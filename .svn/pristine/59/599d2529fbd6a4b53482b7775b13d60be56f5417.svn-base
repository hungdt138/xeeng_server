/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.memcached.data;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.game.data.MatchEntity;

/**
 *
 * @author tuanda
 */
public class CacheMatch {
    private  static ConcurrentHashMap<Long, MatchEntity> matches = new ConcurrentHashMap<Long, MatchEntity>();
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(CacheMatch.class);
    
    public static void add(MatchEntity entity)
    {
        try
        {
            Long key = Long.valueOf(entity.getMatchId());
            if(matches.containsKey(key))
            {
                //warning
                mLog.warn("exist cache matchId " + entity.getMatchId());
//                System.out.println("exits");
            }
            else
            {
                matches.put(key, entity);
            }
        
        }
        catch(Exception ex)
        {
            mLog.error(ex.getMessage(), ex);
        }
        
    }
    
    public static void delete(long matchId)
    {
        try
        {
            Long key = Long.valueOf(matchId);
            if(matches.containsKey(key))
            {
                matches.remove(key);
            }
        }
        catch(Exception ex)
        {
            mLog.error(ex.getMessage(), ex);
        }
    }
    
    public static MatchEntity getMatch(long matchId)
    {
        try
        {
            Long key = Long.valueOf(matchId);
            MatchEntity entity = matches.get(key);

            if(entity == null)
            {
//                System.out.println("not exits");
                mLog.warn("not exist cache matchId " + matchId);
            }
            
            return entity;
        }
        catch(Exception ex)
        {
            mLog.error(ex.getMessage(), ex);
        }
        
        return null;
    }
}
