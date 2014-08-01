package com.tv.xeeng.base.monitor;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.data.CommonQueue;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.base.session.SessionManager;
import com.tv.xeeng.databaseDriven.DBCache;
import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.CacheEntity;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.room.Phong;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.game.room.ZoneManager;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.server.Server;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author tuanda
 */
public class CommonMonitorJob implements Job {

    private static final ZoneManager zManager = Server.getWorker().getZoneManager();
    private static final int MB = 1024 * 1024;
    private static int count = 0;
    private static final String USER_NAMESPACE = "user";
    private static final String USER_ACTIVE_NAMESPACE = "userActive";
    private static Logger log = LoggerContext.getLoggerFactory().getLogger(CommonMonitorJob.class);

    private void closeTimeoutSession() {
        try {
            SessionManager sm = Server.getWorker().getmSessionMgr();
            Enumeration<ISession> values;
            values = sm.getmSessions().elements();

            while (values.hasMoreElements()) {
                ISession session = values.nextElement();
//                log.warn("validate session : " + session.userInfo() + session.isExpired() + session.isSpam() + session.getCreatedTime() + session.getLastAccessTime());
                try {
                    if (session.isExpired() || session.isSpam() || session.isExpiredNew()) {
                        try {
                            if (session.getRoom() != null) {
                                log.warn("Idle session: " + session.userInfo() + " but still stuck in game " + session.getRoom().getAttactmentData().getMatchID() + " last access time: " + session.getLastAccessTime());
//                                return;
                                session.cancelTable();
                            }
                            
                            log.warn("Close idle session: " + session.userInfo() + " last access time: " + session.getLastAccessTime());
                            if (session.isExpiredNew()) {
                                log.warn("Because of isExpiredNew() method --- last time stamp: " + new Date(session.getLastTimestamp()));
                            }
                        } catch (Exception ex) {
                            log.warn("close idle connection", ex);
                        }
                        session.sessionClosed();
                    }
                } catch (Exception ex) {
                    log.error("close idle connection", ex);
                }
            }
        } catch (Exception ex) {
            log.error("monitor session timeout", ex);
        }
    }

    private void countPlayers() {
        Enumeration<Zone> zones = zManager.getZones();
        while (zones.hasMoreElements()) {
            Zone zone = zones.nextElement();
            //find phong for this zone and correct number playing in this zone

            Collection<Phong> phongs = zone.phongValues();

            for (Phong phong : phongs) {
                //find rooms of this phong
                try {
                    int playing = 0;
                    List<Room> rooms = new ArrayList<>(phong.getRooms());

                    int roomSize = rooms.size();

                    for (int i = 0; i < roomSize; i++) {
                        Room room = rooms.get(i);
                        boolean hasPlayers = true;
                        Enumeration<ISession> ses = room.getEnteringSession().elements();
                        int numSession = 0;
                        boolean flagDel = false;

                        while (ses.hasMoreElements()) {
                            numSession++;
                            ISession s = ses.nextElement();
                            String userName = s.getUserName();
//                                    hasPlayers = true;  
                            if (userName == null || userName.equals("") || userName.trim().equals("")) {
                                flagDel = true;
                            }
//                                    playing++;
                        }

                        if (flagDel && numSession == 1)//delete invalid room only one session
                        {
                            log.warn("***Delete invalid room***");
                            room.allLeft();
                        }

                        SimpleTable table = room.getAttactmentData();
                        if (table != null && table.getTableSize() == 0) {
                            room.allLeft();
                            log.warn("***Delete invalid room***" + table.tableIndex);
                            hasPlayers = false;
                        }

                        if (hasPlayers) {

                            if (table != null) {
                                playing += table.getTableSize();
                                if (playing > 96) {
                                    playing = 96;
                                }
                            }
                        }

                    }

                    phong.setPlaying(playing);
                } catch (Exception ex) {
                    log.error("Common monitor", ex.getStackTrace());
                }
            }

        }
    }

    private void releaseMemory() {
        Runtime r = Runtime.getRuntime();
        // Get current size of heap in bytes
        long heapSize = r.totalMemory() / MB;
        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
        long heapMaxSize = r.maxMemory() / MB;

        // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
        long heapFreeSize = r.freeMemory() / MB;
        log.debug("[heapSize before release] " + heapSize + " [max heap size] " + heapMaxSize + " [heapFreeSize] " + heapFreeSize);
        log.debug("release gc");
        r.gc();
        log.debug("[heapsize after release] " + heapSize + " [max heap size] " + heapMaxSize + " [heapFreeSize] " + heapFreeSize);
    }

    private void minusUser(Connection conn) {
        UserDB userDb = new UserDB(conn);
        try {
            userDb.notMinus();
        } catch (SQLException ex) {
            log.error("sql error", ex);
        }
    }

    private void updateListRefreshCache(Connection conn) {
        try {
            List<CacheEntity> lstCaches = DBCache.getRefreshCaches(conn);
            int sizeCache = lstCaches.size();
            for (int i = 0; i < sizeCache; i++) {
                CacheEntity entity = lstCaches.get(i);
                if (entity.getNamespace().equals(USER_NAMESPACE)) {
//                    long updateCash = entity.getValue();
                    long userId = entity.getKey();
//                    CacheUserInfo.updateUserCashFromDB(userId, updateCash);
//                    
                    /* sửa bởi thanhnvt: thay đổi cơ chế refresh cache để tránh nhập nhằng giữa Gold và Cash. */
                    log.debug("[+] Update cache: UserId = " + userId);
                    CacheUserInfo.deleteCacheUserById(userId);
                } else if (entity.getNamespace().equals(USER_ACTIVE_NAMESPACE)) {
//                    long updateCash = entity.getValue();
                    long userId = entity.getKey();
//                    CacheUserInfo.updateUserActiveCashFromDB(userId, updateCash);

                    /* sửa bởi thanhnvt: thay đổi cơ chế refresh cache để tránh nhập nhằng giữa Gold và Cash. */
                    log.debug("[+] Update cache: UserId = " + userId);
                    CacheUserInfo.deleteCacheUserById(userId);
                }
            }
        } catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
//        log.debug("begin execute common monitor job");
        count++;
        Connection con = DBPoolConnection.getConnection();

        try {
            CommonQueue queue = new CommonQueue();

            queue.sendQueueMessage();

            if (count % 60 == 0) {

                closeTimeoutSession();

                if (count % 600 == 0) {
                    countPlayers();
                }

                if (count % 1200 == 0) {
                    releaseMemory();
                    count = 0;
                    minusUser(con);
                }
            }

            if (count % 3600 == 0)//1h new advertions
            {
                queue.changeAdv();
            }

//            try {
//                if (!con.isValid(3)) {
//                    con = DBPoolConnection.getConnection();
//                }
//            } catch (SQLException ex) {
//                con = DBPoolConnection.getConnection();
//            }
            
            updateListRefreshCache(con);

        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }
}
