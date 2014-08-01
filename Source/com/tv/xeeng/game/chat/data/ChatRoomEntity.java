/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.chat.data;

/**
 *
 * @author tuanda
 */
public class ChatRoomEntity {
    private int chatRoomId;
    private String name;
    private String stt;
    private int maxPlayers;
    private boolean isFanClub = false;
    public ChatRoomEntity(int chatRoomId, String name, String stt, int maxPlayers,boolean isFanC)
    {
        this.chatRoomId = chatRoomId;
        this.name = name;
        this.stt = stt;
        this.maxPlayers = maxPlayers;
        isFanClub = isFanC;
    }

    public boolean isIsFanClub() {
        return isFanClub;
    }

    public void setIsFanClub(boolean isFanClub) {
        this.isFanClub = isFanClub;
    }

    
    /**
     * @return the chatRoomId
     */
    public int getChatRoomId() {
        return chatRoomId;
    }

    /**
     * @param chatRoomId the chatRoomId to set
     */
    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the stt
     */
    public String getStt() {
        return stt;
    }

    /**
     * @param stt the stt to set
     */
    public void setStt(String stt) {
        this.stt = stt;
    }

    /**
     * @return the maxPlayers
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * @param maxPlayers the maxPlayers to set
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    
}
