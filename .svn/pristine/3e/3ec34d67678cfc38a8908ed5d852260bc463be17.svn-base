/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import com.tv.xeeng.memcached.data.XEDataUtils;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 *
 * @author thanhnvt
 */
public class XEPrivateMessageEntity {

    private long id;
    private long fromUserId;
    private long toUserId;
    private String title;
    private String content;
    private Timestamp dateSent;
    private boolean detail;

    public XEPrivateMessageEntity() {
    }

    public XEPrivateMessageEntity(long fromUserId, long toUserId, String title, String content) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.title = title;
        this.content = content;
    }

    public XEPrivateMessageEntity(long id, long fromUserId, long toUserId, String title, String content, Timestamp dateSent) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.title = title;
        this.content = content;
        this.dateSent = dateSent;
    }

    @Override
    public String toString() {
        if (detail) {
            return XEDataUtils.serializeParams(id, fromUserId, title, content, new SimpleDateFormat("dd/MM/YYYY HH:mm").format(dateSent));
        } else {
            return XEDataUtils.serializeParams(id, fromUserId, title, new SimpleDateFormat("dd/MM/YYYY HH:mm").format(dateSent));
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public long getToUserId() {
        return toUserId;
    }

    public void setToUserId(long toUserId) {
        this.toUserId = toUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getDateSent() {
        return dateSent;
    }

    public void setDateSent(Timestamp dateSent) {
        this.dateSent = dateSent;
    }

    public boolean isDetail() {
        return detail;
    }

    public void setDetail(boolean detail) {
        this.detail = detail;
    }
}
