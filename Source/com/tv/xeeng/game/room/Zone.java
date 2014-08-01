package com.tv.xeeng.game.room;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.logger.LogManager;
import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.databaseDriven.DatabaseDriver;
import com.tv.xeeng.databaseDriven.RoomDB;
import com.tv.xeeng.db.DBException;
import com.tv.xeeng.game.data.Couple;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.tienlen.data.TienLenTable;
import com.tv.xeeng.memcached.data.CacheMatch;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Zone {

    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(Zone.class);
    public static final long UNSPECIFIED_ROOM_ID = -1L;
    public static final int JOIN_UNLIMITED = -1;
    private int mRoomCapacity = -1;
    private int mPlayerSize = -1;
    private final ConcurrentHashMap<Long, Room> mRooms;
    private final Vector<Long> mRoomIds;
    @SuppressWarnings("unused")
    private ZoneManager.IdRoomGenerator mIdGenerator;
    private int mZoneId;
    private String mZoneName;
    private int mJoinLimited;
    private static boolean isIncreasing = false;
    private static LogManager loggers;

    public static LogManager getLoggers() {
        return loggers;
    }

    public static com.tv.xeeng.base.logger.Logger getLogger(String fileName) {
        return loggers.getLogger(fileName);
    }

    public Zone() {
        this.mRooms = new ConcurrentHashMap<>();
        this.mRoomIds = new Vector<>();
        loggers = new LogManager();
    }

    void setIdRoomGenerator(ZoneManager.IdRoomGenerator aIdGenerator) {
        this.mIdGenerator = aIdGenerator;
    }

    public void setRoomCapacity(int aRoomCapacity) {
        this.mRoomCapacity = aRoomCapacity;
    }

    public void setPlayerSize(int aPlayerSize) {
        this.mPlayerSize = aPlayerSize;
    }

    void setZoneId(int aZoneId) {
        this.mZoneId = aZoneId;

    }

    public int getZoneId() {
        return this.mZoneId;
    }

    void setZoneName(String aZoneName) {
        this.mZoneName = aZoneName;
    }

    public String getZoneName() {
        return this.mZoneName;
    }

    void setJoinLimited(int aJoinLimited) {
        this.mJoinLimited = aJoinLimited;
    }

    public int getJoinLimited() {
        return this.mJoinLimited;
    }

    public Room findRoom(long aRoomId) {
        synchronized (this.mRooms) {
            return ((Room) this.mRooms.get(Long.valueOf(aRoomId)));
        }
    }

    public Room createRoom(String des, long id, int phong) {
//		synchronized (this.mRooms) {
        Room newRoom = new Room(this.mRoomCapacity, this.mPlayerSize, this);
        try {
            DatabaseDriver db = new DatabaseDriver();
            long roomId = db.logMatch(id, phong, 0); //wrong table index
            newRoom.setRoomId(roomId);
            newRoom.phongID = phong;
            addRoom(roomId, newRoom);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newRoom;
//		}
    }

    void addRoom(long aRoomId, Room aRoom) {
        Phong ph = this.getPhong(aRoom.getPhongID());
        ph.createTable(aRoom);

        synchronized (this.mRooms) {
            this.mRooms.put(Long.valueOf(aRoomId), aRoom);
            this.mRoomIds.add(Long.valueOf(aRoomId));
        }
    }

    public void deleteRoom(Room aRoom) {
        Phong ph = this.getPhong(aRoom.phongID);
        if (ph != null) {
            ph.deleteRoom(aRoom);
        }
//		mLog.debug("[Zone]: " + aRoom.getZoneID() + " delete Table "
//				+ aRoom.index + " in Room " + aRoom.phongID + " has matchID "
//				+ aRoom.getRoomId());
        if ((aRoom != null) && (!(aRoom.isPermanent()))) {
            long roomId = aRoom.getRoomId();
            synchronized (this.mRooms) {
                this.mRooms.remove(Long.valueOf(roomId));
                this.mRoomIds.remove(new Long(roomId));
            }
            CacheMatch.delete(roomId);
        }
    }

    public int getNumPlaylingRoom() {
        int count = 0;

        Enumeration<Room> eRooms;
        synchronized (this.mRooms) {
            eRooms = this.mRooms.elements();
        }

        while ((eRooms != null) && (eRooms.hasMoreElements())) {
            Room room = (Room) eRooms.nextElement();
            long roomStatus = room.getStatus();
            if (roomStatus == 1L) {
                ++count;
            }

        }

        return count;
    }

    /**
     *
     * @param zoneID -- id tienlen zone,...
     * @return -- rooms are playing
     */
    public Vector<RoomEntity> dumpPlayingRooms(int zoneID) {
        Vector<RoomEntity> roomEntities = new Vector<>();
        //mLog.debug("RoomSize:" + mRooms.size());

        for (int i = 0; i < this.mRooms.size(); i++) {
            try {
                long roomId = ((Long) this.mRoomIds.get(i)).longValue();
                Room room = (Room) this.mRooms.get(roomId);
                /*
                 * if ((room.getStatus() == 1L) && (room.getZoneID() == zoneID))
                 * { roomEntities.add(room.dumpRoom()); }
                 */
                if (room != null && room.getZoneID() == zoneID) {
                    SimpleTable table = room.getAttactmentData();
                    if (table.isPlaying) {
                        //mLog.debug("Table:" + table.matchID);
                        roomEntities.add(room.dumpRoom());
                    }
                }
            } catch (Exception ex) {
                mLog.error("dump playing room error " + ex.getMessage(), ex);
            }
        }

        return roomEntities;
    }

    public Vector<RoomEntity> dumpRooms(int zoneID) {
        Vector<RoomEntity> roomEntities = new Vector<>();
        for (int i = 0; i < this.mRooms.size(); i++) {
            try {
                long roomId = ((Long) this.mRoomIds.get(i)).longValue();
                Room room = (Room) this.mRooms.get(roomId);
                if (room != null && room.getZoneID() == zoneID) {
                    roomEntities.add(room.dumpRoom());
                }
            } catch (Exception ex) {
                mLog.error("dump playing room error " + ex.getMessage(), ex);
            }
        }
        return roomEntities;
    }

    public Vector<RoomEntity> dumpNewPlayingRooms(int zoneID) {
        Vector<RoomEntity> roomEntities = new Vector<>();
        //mLog.debug("RoomSize:" + mRooms.size());
        Enumeration<Room> eRooms = mRooms.elements();
        while (eRooms.hasMoreElements()) {
            Room room = eRooms.nextElement();
            if (room != null && room.getZoneID() == zoneID) {
                roomEntities.add(room.dumpRoom());
            }
        }
        return roomEntities;
    }

    public Vector<RoomEntity> dumpPlayingRooms(int aOffset, int aLength,
            int zoneID) {
        Vector<RoomEntity> roomEntities = new Vector<>();

        //synchronized (this.mRooms) {
        int loopIdx = (aOffset >= 0) ? aOffset : 0;

        int numRooms = this.mRooms.size();

        int results = 0;
        while (true) {
            if ((loopIdx >= numRooms) || (results >= aLength)) {
                break;
            }

            long roomId = ((Long) this.mRoomIds.get(loopIdx)).longValue();

            Room room = findRoom(roomId);

            if ((room.getStatus() == 1L) && (room.getZoneID() == zoneID)) {
                roomEntities.add(room.dumpRoom());

                ++results;
            }

            ++loopIdx;
        }
        //}

        return roomEntities;
    }

    public int getNumWaitingRoom() {
        int count = 0;

        Enumeration<Room> eRooms;
        //synchronized (this.mRooms) {
        eRooms = this.mRooms.elements();
        //}

        while ((eRooms != null) && (eRooms.hasMoreElements())) {
            Room room = (Room) eRooms.nextElement();
            long roomStatus = room.getStatus();
            if (roomStatus == 2L) {
                ++count;
            }

        }

        return count;
    }

    public Vector<RoomEntity> dumpWaitingRooms(int aOffset, int aLength) {
        Vector<RoomEntity> roomEntities = new Vector<>();

        //synchronized (this.mRooms) {
        int loopIdx = (aOffset >= 0) ? aOffset : 0;

        int numRooms = this.mRooms.size();

        int results = 0;
        while (true) {
            if ((loopIdx >= numRooms) || (results >= aLength)) {
                break;
            }

            long roomId = ((Long) this.mRoomIds.get(loopIdx)).longValue();

            Room room = findRoom(roomId);

            if (room.getStatus() == 2L) {
                roomEntities.add(room.dumpRoom());

                ++results;
            }

            ++loopIdx;
        }
        //}

        return roomEntities;
    }

    public Vector<RoomEntity> dumpWaitingRooms(int aOffset, int aLength,
            int aLevel, int minLevel, int zoneID) {
        Vector<RoomEntity> roomEntities = new Vector<>();

        //synchronized (this.mRooms) {
        int loopIdx = (aOffset >= 0) ? aOffset : 0;
        // System.out.println("Dump waiting : " + numRooms);
        int results = 0;
        while (true) {
            if ((loopIdx >= this.mRooms.size()) || (results >= aLength)) {
                break;
            }

            long roomId = ((Long) this.mRoomIds.get(loopIdx)).longValue();

            Room room = findRoom(roomId);

            if (room.getNumOnline() == 0) {
                try {
                    mLog.error("Room : " + room.getName()
                            + " is Empty. ; roomID : " + room.getRoomId()
                            + "; playingSize : " + room.playingSize());
                    room.allLeft();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ((room.getLevel() <= aLevel)
                    && (room.getNumOnline() > 0)
                    && (room.getLevel() >= minLevel)
                    && (room.getZoneID() == zoneID)) {
                roomEntities.add(room.dumpRoom());

                ++results;
            }

            ++loopIdx;
        }
        //}

        return roomEntities;
    }

    public Vector<RoomEntity> dumpWaitingRooms() {
        Vector<RoomEntity> roomEntities = new Vector<>();

        //synchronized (this.mRooms) {
        for (int i = 0; i < this.mRooms.size(); i++) {
            long roomId = ((Long) this.mRoomIds.get(i)).longValue();
            Room room = findRoom(roomId);

            /*
             * if ((room.getStatus() == 1L) && (room.getZoneID() == zoneID))
             * { roomEntities.add(room.dumpRoom()); }
             */
            if (!room.isPlaying() || room.getStatus() == 2L) {
                roomEntities.add(room.dumpRoom());
            }

        }

        //}
        return roomEntities;
    }

    public boolean deleteAllRoomByOwner(String owner) {
        synchronized (this.mRooms) {
            Enumeration<Long> keys = this.mRooms.keys();
            while (keys.hasMoreElements()) {
                long k = keys.nextElement();
                Room room = this.mRooms.get(Long.valueOf(k));
                if (owner.toLowerCase().equalsIgnoreCase(
                        room.getOwnerName().toLowerCase())) {
                    room.allLeft();
                }
            }
        }
        return true;
    }

    public Room findRoomByOwner(String owner) {
        Enumeration<Long> keys;

        keys = this.mRooms.keys();

        while (keys.hasMoreElements()) {
            long k = keys.nextElement();
            Room room = this.mRooms.get(Long.valueOf(k));

            if (owner.toLowerCase()
                    .compareTo(room.getOwnerName().toLowerCase()) == 0) {
                return room;
            }
        }

        return null;
    }

    public ArrayList<PhomTable> dumpWaitingPhomTables(int phongID) {
        ArrayList<PhomTable> tables = new ArrayList<>();
        // int numRooms = this.mRooms.size();
        // System.out.println("Dump waiting : " + numRooms);
        Iterable<Room> rooms = this.mRooms.values();
        for (Room room : rooms) {
            // Room room = findRoom(rId);
            if ((room.getPhongID() == phongID) && (room.getNumOnline() > 0)) {
                tables.add((PhomTable) room.getAttactmentData());
            }
        }

        return tables;
    }

    public ArrayList<TienLenTable> dumpWaitingTienLenTables(int phongID) {
        ArrayList<TienLenTable> tables = new ArrayList<>();
        // int numRooms = this.mRooms.size();
        // System.out.println("Dump waiting : "+numRooms);
        for (long rId : this.mRoomIds) {
            Room room = findRoom(rId);
            if ((room.getPhongID() == phongID) && (room.getNumOnline() > 0)) {
                tables.add((TienLenTable) room.getAttactmentData());
            }
        }

        return tables;
    }

    public List<SimpleTable> dumpWaitingTables(int phongId) {
        ArrayList<SimpleTable> tables = new ArrayList<>();

        for (long rId : this.mRoomIds) {
            Room room = findRoom(rId);
            if ((room.getPhongID() == phongId) && (room.getNumOnline() > 0)) {
                tables.add(room.getAttactmentData());
            }
        }

        return tables;
    }

    public List<SimpleTable> dumpNewWaitingTables(int phongId) {
        ArrayList<SimpleTable> tables = new ArrayList<>();
        Phong phong = this.getPhong(phongId);
        if (phong != null) {
            List<Room> deleteTables = new ArrayList<>();
            for (Room room : phong.getRooms()) {
                if (room != null) {
                    SimpleTable table = room.getAttactmentData();
                    if (table.getTableSize() == 0) {
                        mLog.error("error table(fix bug table)" + table.matchID);
                        deleteTables.add(room);
                    }

                    tables.add(room.getAttactmentData());
                }
            }

            int deleteSize = deleteTables.size();
            for (int i = 0; i < deleteSize; i++) {
                phong.deleteRoom(deleteTables.get(i));
            }
        }

        return tables;
    }

    public Couple<Integer, Long> fastPlay(long lastFastMatchId, long maxCashBet, int levelId) throws Exception,
            SQLException, DBException {
        try {
            //ArrayList<Integer> iDs = getRoomSameLevel(room);
            // for (int roomID : iDs) {
            // Hashtable<Integer, Long> temp = tableInRoom.get(roomID);
            //Collection<Phong> phongs = this.mPhongs.values();
            List<Phong> lstPhongs = new ArrayList<>(this.mPhongs.values());
            // Enumeration<Integer> keys = temp.keys();
            int lastIndex = 0;
            long foundMatchId = 0;
            long foundBetMoney = 0;
            int foundIndex = 0;
            int phongSize = lstPhongs.size();
            int phongSize1 = phongSize - 1;
            isIncreasing = !isIncreasing;
            boolean flag = isIncreasing;
            int foundTimes = 0;

            for (int i = phongSize1; i > -1; i--) {
                Phong p;

                if (flag) {
                    p = lstPhongs.get(phongSize1 - i);
                } else {
                    p = lstPhongs.get(i);
                }

                if (p == null || p.level != levelId) {
                    continue;
                }
                
                List<Room> rooms = new ArrayList<>(p.getRooms());
                int roomSize = rooms.size();

                for (int j = 0; j < roomSize; j++) {
                    // long matchID = temp.get(k);
                    // Room r = this.findRoom(matchID);
                    Room r = rooms.get(j);
                    if (r == null) {
                        continue;
                    }

                    long matchID = r.getRoomId();
                    int k = r.index;

                    SimpleTable t = (SimpleTable) r.getAttactmentData();
                    switch (mZoneId) {
                        case ZoneID.TIENLEN: {
                            if (!((TienLenTable) t).isPlaying
                                    && !((TienLenTable) t).isFull() && t.firstCashBet <= maxCashBet
                                    && (!t.dontWantAnyUser || t.getTableSize() == 1)) {
                                // check last fast matchId 2 times dont want to came the same room
                                if (matchID == lastFastMatchId) {
                                    lastIndex = k;
                                    continue;
                                } else {
                                    if (foundBetMoney < t.firstCashBet) {
                                        foundBetMoney = t.firstCashBet;
                                        foundMatchId = t.matchID;
                                        foundIndex = k;
                                        foundTimes++;
                                        if (foundTimes > 2) {
                                            return new Couple<>(foundIndex, foundMatchId);
                                        }
                                    }
                                }
//							return new Couple<Integer, Long>(k, matchID);
                            }
                            break;
                        }
                        case ZoneID.PHOM: {
                            if (!((PhomTable) t).isPlaying
                                    && !((PhomTable) t).isFull() && t.firstCashBet <= maxCashBet
                                    && (!t.dontWantAnyUser || t.getTableSize() == 1)) {
                                // check last fast matchId 2 times dont want to came the same room
                                if (matchID == lastFastMatchId) {
                                    lastIndex = k;
                                    continue;
                                } else {
                                    if (foundBetMoney < t.firstCashBet) {
                                        foundBetMoney = t.firstCashBet;
                                        foundMatchId = t.matchID;
                                        foundIndex = k;
                                        foundTimes++;
                                        if (foundTimes > 2) {
                                            return new Couple<>(foundIndex, foundMatchId);
                                        }
                                    }
                                }

//                                                    return new Couple<Integer, Long>(k, matchID);
                            }

                            break;
                        }

//                      case ZoneID.NEW_BA_CAY:
//                      case ZoneID.BAU_CUA_TOM_CA:
//                      case ZoneID.PIKACHU:
//                      case ZoneID.AILATRIEUPHU:
                        default: {
                            if (!t.isPlaying
                                    && !t.isFullTable() && t.firstCashBet <= maxCashBet) {
                                // check last fast matchId 2 times dont want to came the same room
                                if (matchID == lastFastMatchId) {
                                    lastIndex = k;
                                    continue;
                                } else {
                                    if (foundBetMoney < t.firstCashBet) {
                                        foundBetMoney = t.firstCashBet;
                                        foundMatchId = t.matchID;
                                        foundIndex = k;
                                        foundTimes++;
                                        if (foundTimes > 2) {
                                            return new Couple<>(foundIndex, foundMatchId);
                                        }
                                    }
                                }
                            }

                            break;
                        }

//					default:
//						break;
                    }
                }
            }

            if (foundIndex > 0) {
                return new Couple<>(foundIndex, foundMatchId);
            }
            if (lastIndex > 0) {
                // there 's only one free table
                return new Couple<>(lastIndex, lastFastMatchId);
            }
            // }
            return null;
        } catch (Exception e) {
            throw new Exception("Không tìm được bàn chơi!");
        }

    }

    public long botfastPlay(long maxCashBet) {
        List<Phong> lstPhongs = new ArrayList<>(this.mPhongs.values());
        // Enumeration<Integer> keys = temp.keys();
        int lastIndex = 0;
        long foundMatchId = 0;
        long foundBetMoney = 0;
        int foundIndex = 0;
        int phongSize = lstPhongs.size();
        int phongSize1 = phongSize - 1;
        int foundTimes = 0;

        for (int i = phongSize1; i > -1; i--) {
            Phong p;
            p = lstPhongs.get(i);
            List<Room> rooms = new ArrayList<>(p.getRooms());
            int roomSize = rooms.size();

            for (int j = 0; j < roomSize; j++) {
                // long matchID = temp.get(k);
                // Room r = this.findRoom(matchID);
                Room r = rooms.get(j);
                if (r == null) {
                    continue;
                }

                long matchID = r.getRoomId();
                int k = r.index;

                SimpleTable t = (SimpleTable) r.getAttactmentData();
                if (t.isKickoutBot) {
                    continue;
                }

                switch (mZoneId) {
                    case ZoneID.TIENLEN: {
                        if (!((TienLenTable) t).isPlaying
                                && !((TienLenTable) t).isFull() && t.firstCashBet <= maxCashBet
                                && (t.getTableSize() == 1)) {
                            if (foundBetMoney < t.firstCashBet) {
                                //foundBetMoney = t.firstCashBet;
                                foundMatchId = t.matchID;
                                //foundIndex = k;
                                foundTimes++;
                                matchID = t.matchID;
                                if (foundTimes > 2) {
                                    return foundMatchId;
                                }
                            }
                            return matchID;
                        }
                        break;

                    }
                    case ZoneID.PHOM: {
                        if (!((PhomTable) t).isPlaying
                                && !((PhomTable) t).isFull() && t.firstCashBet <= maxCashBet
                                && (t.getTableSize() == 1)) {
                            if (foundBetMoney < t.firstCashBet) {
                                foundBetMoney = t.firstCashBet;
                                foundMatchId = t.matchID;
                                foundIndex = k;
                                foundTimes++;
                                if (foundTimes > 2) {
                                    return foundMatchId;
                                }
                            }
                        }
//                                                    return new Couple<Integer, Long>(k, matchID);
                        break;
                    }
//                                        case ZoneID.NEW_BA_CAY:
//                                        case ZoneID.BAU_CUA_TOM_CA:
//                                        case ZoneID.PIKACHU:
//                                        case ZoneID.AILATRIEUPHU:
                    default: {
                        if (!t.isPlaying
                                && !t.isFullTable() && t.firstCashBet <= maxCashBet && (t.getTableSize() == 1)
                                && !t.hasBotUser()) {
                            if (foundBetMoney < t.firstCashBet) {
                                foundBetMoney = t.firstCashBet;
                                foundMatchId = t.matchID;
                                foundIndex = k;
                                foundTimes++;
                                if (foundTimes > 2) {
                                    return foundMatchId;
                                }
                            }
                        }
                        break;
                    }
//					default:
//						break;
                }
            }
        }

        if (foundIndex > 0) {
            return foundMatchId;
        }
        return 0;
    }
    // RoomID --> (TableIndex -->MatchID)
    private final ConcurrentHashMap<Integer, Phong> mPhongs = new ConcurrentHashMap<>();

    // public Hashtable<Integer, Hashtable<Integer, Long>> tableInRoom = new
    // Hashtable<Integer, Hashtable<Integer, Long>>();
    public Phong getPhong(int index) {
        try {
            return this.mPhongs.get(index);
        } catch (Exception e) {
            mLog.error("getPhong error" + e.getMessage());
            return null;
        }
    }

    public Phong phongAvailable() {
        List<Phong> lstPhongs = new ArrayList<>(this.mPhongs.values());
        // Enumeration<Integer> keys = temp.keys();
        int phongSize = lstPhongs.size();
        int availableIndex = 0;
        int playing = -1000;
        int phongId = 0;
        for (int i = 0; i < phongSize; i++) {
            Phong phong = lstPhongs.get(i);
            int tmpPlaying = phong.getPlaying();

            if (phong.level > 1 || tmpPlaying > 48) {
                continue;
            }

            if (playing < tmpPlaying || (playing == tmpPlaying && phong.id > phongId)) {
                availableIndex = i;
                playing = tmpPlaying;
                phongId = phong.id;
            }

        }

        return lstPhongs.get(availableIndex);
    }

    public Phong phongAvailable(int levelId) {
        List<Phong> lstPhongs = new ArrayList<>(this.mPhongs.values());
        // Enumeration<Integer> keys = temp.keys();
        int phongSize = lstPhongs.size();
        int availableIndex = 0;
        int playing = -1000;
        int phongId = 0;
        for (int i = 0; i < phongSize; i++) {
            Phong phong = lstPhongs.get(i);
            int tmpPlaying = phong.getPlaying();

            if (phong.level != levelId || tmpPlaying > 48) {
                continue;
            }

            if (playing < tmpPlaying || (playing == tmpPlaying && phong.id > phongId)) {
                availableIndex = i;
                playing = tmpPlaying;
                phongId = phong.id;
            }

        }

        return lstPhongs.get(availableIndex);
    }

    public Collection<Phong> phongValues() {
        return this.mPhongs.values();
    }

    public Phong getFirstPhong() {
        for (Phong p : this.mPhongs.values()) {
            return p;
        }

        return null;
    }

    public void initPhong() throws SQLException, DBException {
        RoomDB db = new RoomDB();
        List<NRoomEntity> rs = db.getRooms(mZoneId);
        for (NRoomEntity r : rs) {

            int id = r.getId();
            /*
             * Hashtable<Integer, Long> temp = new Hashtable<Integer, Long>();
             * tableInRoom.put(id, temp);
             */
            Phong p = new Phong(id, this.mZoneId, r.getLv(), this);
            r.setPhong(p);
            this.mPhongs.put(p.id, p);

        }
    }

    public int numberOnline() {
        int res = 0;
        Enumeration<Phong> p = this.mPhongs.elements();
        while (p.hasMoreElements()) {
            Phong ph = p.nextElement();
            res += ph.numberOnline();
        }

        return res;
    }

    public int getNumOfUser() {
        int res = 0;
        Enumeration<Room> rooms = this.mRooms.elements();
        while (rooms.hasMoreElements()) {
            Room room = rooms.nextElement();
            res += room.getEnteringSession().size();
        }

        return res;
    }

    public List<Room> xeGetTablesByLevel(int levelID) {
        ArrayList<Room> roomLst = new ArrayList<Room>();
        for (Phong p : mPhongs.values()) {
            if (p.level == levelID) {
                roomLst.addAll(p.getRooms());
            }
        }
        return roomLst;
    }

    public Phong xeGetPhong(int levelID) {
        for (Phong p : mPhongs.values()) {
            if (p.level == levelID) {
                return p;
            }
        }
        return null;
    }
}
