package com.tv.xeeng.base.room;


public class NTableEntity
{
    private String Id;
    private long matchId;
    @SuppressWarnings("unused")
	private String name; //for future
    private int ownerId;
    private int capacity;
    private int enteringSize;
    private int playingSize;
    private Object attactmentData;
    private String password;




    /**
     * @return the Id
     */
    public String getId() {
        return Id;
    }

    /**
     * @param Id the Id to set
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    
    /**
     * @return the mRoomOwnerName
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * @param mRoomOwnerName the mRoomOwnerName to set
     */
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * @return the enteringSize
     */
    public int getEnteringSize() {
        return enteringSize;
    }

    /**
     * @param enteringSize the enteringSize to set
     */
    public void setEnteringSize(int enteringSize) {
        this.enteringSize = enteringSize;
    }

    /**
     * @return the playingSize
     */
    public int getPlayingSize() {
        return playingSize;
    }

    /**
     * @param playingSize the playingSize to set
     */
    public void setPlayingSize(int playingSize) {
        this.playingSize = playingSize;
    }

    /**
     * @return the mAttactmentData
     */
    public Object getAttactmentData() {
        return attactmentData;
    }

    /**
     * @param mAttactmentData the mAttactmentData to set
     */
    public void setAttactmentData(Object mAttactmentData) {
        this.attactmentData = mAttactmentData;
    }

    /**
     * @return the mPassword
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param mPassword the mPassword to set
     */
    public void setPassword(String mPassword) {
        this.password = mPassword;
    }

    /**
     * @return the matchId
     */
    public Long getMatchId() {
        return matchId;
    }

    /**
     * @param matchId the matchId to set
     */
    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

}