/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import java.io.Serializable;

/**
 * 
 * @author tuanda
 */
public class EventEntity implements Serializable {
    private String tittle;
    private String content;
    private int eventId;
    private int picId;
    private int partnerId;
    private boolean concurrent;
    private int gameId;
    private boolean thumb;
    private boolean detailImage;
    private String thumbDetail;
    private String picDetail;
    private int eventType;

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public EventEntity(String title, String content1, String content2, int eventId, int gameID) {
        this.eventId = eventId;
        this.tittle = title;
        this.gameId = gameID;
        if (content2 != null && !content2.equals("")) {
            this.content = content1 + AIOConstants.SEPERATOR_BYTE_1 + content2;
            detailImage = true;
        } else {
            this.content = content1;
        }

    }

    public int getGameId() {
        return gameId;
    }

    /**
     * @return the tittle
     */
    public String getTittle() {
        return tittle;
    }

    /**
     * @param tittle
     *            the tittle to set
     */
    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the eventId
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * @param eventId
     *            the eventId to set
     */
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    /**
     * @return the picId
     */
    public int getPicId() {
        return picId;
    }

    /**
     * @param picId
     *            the picId to set
     */
    public void setPicId(int picId) {
        this.picId = picId;
    }

    /**
     * @return the thumb
     */
    public boolean isThumb() {
        return thumb;
    }

    /**
     * @param thumb
     *            the thumb to set
     */
    public void setThumb(boolean thumb) {
        this.thumb = thumb;
    }

    /**
     * @return the thumbDetail
     */
    public String getThumbDetail() {
        return thumbDetail;
    }

    /**
     * @param thumbDetail
     *            the thumbDetail to set
     */
    public void setThumbDetail(String thumbDetail) {
        this.thumbDetail = thumbDetail;
    }

    /**
     * @return the picDetail
     */
    public String getPicDetail() {
        return picDetail;
    }

    /**
     * @param picDetail
     *            the picDetail to set
     */
    public void setPicDetail(String picDetail) {
        this.picDetail = picDetail;
    }

    /**
     * @return the detailImage
     */
    public boolean isDetailImage() {
        return detailImage;
    }

    /**
     * @return the partnerId
     */
    public int getPartnerId() {
        return partnerId;
    }

    /**
     * @param partnerId
     *            the partnerId to set
     */
    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    /**
     * @return the concurrent
     */
    public boolean isConcurrent() {
        return concurrent;
    }

    /**
     * @param concurrent
     *            the concurrent to set
     */
    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getEventId()).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(this.getTittle()).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(this.getEventType());
        return sb.toString();
    }
}
