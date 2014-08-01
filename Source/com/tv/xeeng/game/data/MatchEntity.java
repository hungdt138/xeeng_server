/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import com.tv.xeeng.game.room.Room;

/**
 *
 * @author tuanda
 */
public class MatchEntity {
    private long matchId;
    private int zoneId;
    private Room room;
    private int phongID;
    public MatchEntity(long matchId, int zoneId, Room room, int ph)
    {
        this.matchId = matchId;
        this.zoneId = zoneId;
        this.room = room;
        this.phongID = ph;
    }
    
    public int getPhongID() {
		return phongID;
	}
    /**
     * @return the matchId
     */
    public long getMatchId() {
        return matchId;
    }

    /**
     * @param matchId the matchId to set
     */
    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    /**
     * @return the zoneId
     */
    public int getZoneId() {
        return zoneId;
    }

    /**
     * @param zoneId the zoneId to set
     */
    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @return the room
     */
    public Room getRoom() {
        return room;
    }

    /**
     * @param room the room to set
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    
    
}
