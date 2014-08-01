package com.tv.xeeng.base.shop;

import com.tv.xeeng.game.data.AIOConstants;

public class XEShopItem {

    private int id; // id field
    private int itemId; // item gì (default is 0, means Gold)
    private int cardinality; // số lượng bao nhiêu item
    private int mulFactor; // Số lượng item thực sự = cardinality * mulFactor
    private int price; // Giá tính theo đơn vị Xeeng
    private String desc;

    public XEShopItem() {
    }

    public XEShopItem(int id, int itemId, int cardinality, int mulFactor, int price, String desc) {
        this.id = id;
        this.itemId = itemId;
        this.cardinality = cardinality;
        this.mulFactor = mulFactor;
        this.price = price;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getCardinality() {
        return cardinality;
    }

    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }

    public int getMulFactor() {
        return mulFactor;
    }

    public void setMulFactor(int mulFactor) {
        this.mulFactor = mulFactor;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String toString() {
        return "" + id + AIOConstants.SEPERATOR_BYTE_1
                + itemId + AIOConstants.SEPERATOR_BYTE_1
                + cardinality + AIOConstants.SEPERATOR_BYTE_1
                + mulFactor + AIOConstants.SEPERATOR_BYTE_1
                + price + AIOConstants.SEPERATOR_BYTE_1
                + desc;
    }
}
