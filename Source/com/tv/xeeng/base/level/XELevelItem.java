package com.tv.xeeng.base.level;

import com.tv.xeeng.memcached.data.XEDataUtils;

public class XELevelItem {

    private int id;
    private String levelLabel;
    private int minCash;
    private String cashList;
    private int isVip;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevelLabel() {
        return levelLabel;
    }

    public void setLevelLabel(String levelLabel) {
        this.levelLabel = levelLabel;
    }

    public int getMinCash() {
        return minCash;
    }

    public void setMinCash(int minCash) {
        this.minCash = minCash;
    }

    public XELevelItem(int id, String levelLabel, int minCash, String cashList, int isVip) {
        this.id = id;
        this.levelLabel = levelLabel;
        this.minCash = minCash;
        this.cashList = cashList;
        this.isVip = isVip;
    }

    @Override
    public String toString() {
        return XEDataUtils.serializeParams(id, levelLabel, minCash, cashList, isVip);
    }

    public String getCashList() {
        return cashList;
    }

    public void setCashList(String cashList) {
        this.cashList = cashList;
    }

    public int isIsVip() {
        return isVip;
    }

    public void setIsVip(int isVip) {
        this.isVip = isVip;
    }
}
