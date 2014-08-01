/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import com.tv.xeeng.memcached.data.XEDataUtils;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author yeuchimse
 */
public class XENewsEntity implements Serializable {

    private long id;
    private String title;
    private String htmlContent;
    private Date dateCreated;
    private Date dateModified;
    private long userCreated;
    private long userModified;

    private boolean detail = false;

    public XENewsEntity() {

    }

    @Override
    public String toString() {
        if (detail) {
            return XEDataUtils.serializeParams(id, title, htmlContent, dateCreated, dateModified);
        } else {
            return XEDataUtils.serializeParams(id, title, dateModified);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public long getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(long userCreated) {
        this.userCreated = userCreated;
    }

    public long getUserModified() {
        return userModified;
    }

    public void setUserModified(long userModified) {
        this.userModified = userModified;
    }

    public boolean isDetail() {
        return detail;
    }

    public void setDetail(boolean detail) {
        this.detail = detail;
    }
}
