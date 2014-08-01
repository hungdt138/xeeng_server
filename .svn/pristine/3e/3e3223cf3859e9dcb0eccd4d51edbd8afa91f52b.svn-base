/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.room;

import com.tv.xeeng.game.room.Phong;

/**
 *
 * @author
 */
public class NRoomEntity {

    private int id; // auto increament id
//	public long playing; // number of players session join this room
    private int numTables; // quantity tables of this room
    private int available; // how many players can join this room now?
    private String zoneName;
    private String level;
    private int number; // number of room
    private int lv;
    private int isVip;
    private int minCash;
    private int zoneId;
    private Phong phong;

    public NRoomEntity() {
    }

    public NRoomEntity(int id, long playing, int availabe, int numTables,
            String zoneName, String level, int number, int lv) {
        this.id = id;
//		this.playing = playing;
        this.available = availabe;
        this.numTables = numTables;
        this.zoneName = zoneName;
        this.level = level;
        this.number = number;
        this.lv = lv;
    }

    public NRoomEntity(int id, long playing, int availabe, int numTables,
            String zoneName, String level, int number) {
        this.id = id;
//		this.playing = playing;
        this.available = availabe;
        this.numTables = numTables;
        this.zoneName = zoneName;
        this.level = level;
        this.number = number;
    }

    public NRoomEntity(int id, long playing, int availabe, int numTables,
            String zoneName, String level, int number, int lv, int minCash) {
        this.id = id;
//		this.playing = playing;
        this.available = availabe;
        this.numTables = numTables;
        this.zoneName = zoneName;
        this.level = level;
        this.number = number;
        this.lv = lv;
        this.minCash = minCash;
    }

    public NRoomEntity(int id, long playing, int availabe, int numTables,
            String level, int number, int lv, int minCash, int zoneId) {
        this.id = id;
//		this.playing = playing;
        this.available = availabe;
        this.numTables = numTables;
        this.zoneId = zoneId;
        this.level = level;
        this.number = number;
        this.lv = lv;
        this.minCash = minCash;
    }

    public int getLv() {
        return lv;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the playing
     */
    public long getPlaying() {

        return phong.getPlaying();
    }

    /**
     * @return the numTables
     */
    public int getNumTables() {
        return numTables;
    }

    /**
     * @return the available
     */
    public int getAvailable() {
        return available;
    }

    /**
     * @return the zoneName
     */
    public String getZoneName() {
        return zoneName;
    }

    /**
     * @return the level
     */
    public String getLevel() {
        return level;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return the minCash
     */
    public int getMinCash() {
        return minCash;
    }

    /**
     * @param minCash the minCash to set
     */
    public void setMinCash(int minCash) {
        this.minCash = minCash;
    }

    /**
     * @return the zoneId
     */
    public int getZoneId() {
        return zoneId;
    }

    /**
     * @return the Phong
     */
    public Phong getPhong() {
        return phong;
    }

    /**
     * @param Phong the Phong to set
     */
    public void setPhong(Phong phong) {
        this.phong = phong;
    }

    public int getIsVip() {
        return isVip;
    }

    public void setIsVip(int isVip) {
        this.isVip = isVip;
    }

}
