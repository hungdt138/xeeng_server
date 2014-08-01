package com.tv.xeeng.game.room;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.protocol.IResponseMessage;
import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class Phong {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
            Phong.class);
    private final ConcurrentHashMap<Long, Room> mTables = new ConcurrentHashMap<Long, Room>();
    private final ConcurrentHashMap<Long, ISession> mSessions = new ConcurrentHashMap<Long, ISession>();
    public int id;
    public int zoneID;
    public int level;
    public Zone zone;
    private int playing;

    public Phong(int i, int z, int l, Zone z1) {
        this.id = i;
        this.zoneID = z;
        this.level = l;
        this.zone = z1;
        playing = 0;
    }

    public void enterPhong(ISession s) {
        this.mSessions.put(s.getUID(), s);
        playing++;
        s.setPhongID(this.id);
    }
    public int numberOnline(){
        return mSessions.size();
    }
    public void outPhong(ISession s) {
//		synchronized (this.mSessions) {
        this.mSessions.remove(s.getUID());
//		}

        s.setPhongID(0);
//                mLog.debug("[Out]Size of PH "+this.id + " is " +this.mSessions.size());
        if (playing > 0) {
            playing--;
        }
    }

    public void outPhong(long id) {
//		synchronized (this.mSessions) {
        this.mSessions.remove(id);
//		}
        playing--;
    }

    public void createTable(Room r) {
        synchronized (this.mTables) {
//			mLog.debug("Create Room: "+r.getRoomId());
            this.mTables.put(r.getRoomId(), r);
        }
    }

    public void deleteRoom(Room r) {
        mLog.debug("Delete Room: " + r.getRoomId());
//            try
//            {
////                if(r != null && r.getAttactmentData() != null)
////                    r.getAttactmentData().destroy();
//            }
//            catch(Exception ex)
//            {
//                mLog.error("deelete Room ", ex);
//            }
        synchronized (this.mTables) {

            this.mTables.remove(r.getRoomId());
        }
    }

    public Collection<Room> getRooms() {
        return this.mTables.values();
    }

    public long isCreableTable(int index) {
        Collection<Room> rooms = getRooms();
        for (Room r : rooms) {
            if (r.index == index) {
                return r.getRoomId();
            }
        }
        return 0l;
    }

    public int avaiableTable() {
        int index = 1;
        while (index < 25) {
            if (isCreableTable(index) == 0) {
                return index;
            }
            index++;
        }
        return 0;
    }
    
//    public Room xeGetAvailableRoom() {
//   		long matchID = 0;
//    	for(int i = 1; i < 25; i++) {
//    		matchID = isCreableTable(i);
//    		if(matchID > 0) return matchID;
//    	}
//    	return 0;
//    }

    public void broadcast(IResponseMessage aResMsg, ISession aSender) {
        synchronized (this.mTables) {
            Collection<Room> rooms = this.mTables.values();
            for (Room r : rooms) {
                SimpleTable t = (SimpleTable) r.getAttactmentData();
                t.broadcastMsg(aResMsg, t.getNewPlayings(), t.getNewWaitings(), null, false);
                //r.broadcastMessage(aResMsg, aSender, true);
            }
        }
    }

    public void broadcastZone(IResponseMessage aResMsg, ISession aSender, boolean isSameLevel) {
        Collection<Phong> phongs = this.zone.phongValues();
        for (Phong p : phongs) {
            if ((!isSameLevel) || (isSameLevel && (p.level == aSender.getRoomLevel()))) {
                p.broadcastMessage(aResMsg, aSender);
            }
        }
    }

    public void broadcastMessage(IResponseMessage aResMsg, ISession aSender) {
        Enumeration<ISession> enumSessions = null;
        synchronized (this.mSessions) {
            enumSessions = this.mSessions.elements();
        }
        while (true) {
            ISession session;
            while (true) {
                if (!(enumSessions.hasMoreElements())) {
                    return;
                }
                session = (ISession) enumSessions.nextElement();
                if ((session.getUID() != aSender.getUID())) {
                    break;
                }
            }
            try {
                if (session != null && aResMsg != null && !session.isClosed()) {
                    session.write(aResMsg);
                }

            } catch (Throwable t) {
                this.mLog.error("[PHONG] Broadcast. Room : " + this.id);
                this.mLog.error("[PHONG] Broadcast error", t);
            }
        }
    }

    /**
     * @return the playing
     */
    public int getPlaying() {
        return playing;
    }

    /**
     * @param playing the playing to set
     */
    public void setPlaying(int playing) {


        this.playing = playing;

    }
}
