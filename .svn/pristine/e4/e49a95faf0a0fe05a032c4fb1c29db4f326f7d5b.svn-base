/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.daugia;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tv.xeeng.game.data.AIOConstants;

/**
 *
 * @author tuanda
 */
public class BidInfo {
public int id;
    public ProductInfo product;
    public long max;
    public long min;
    public long step;
    public String name;
    public String desc;
    public BidType type;
    public Date startDate;
    public Date endDate;

    public BidInfo() {
    }

    private String dateToString(Date d) {
        SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("hh:mm dd/MM/yyyy");
        StringBuilder nowYYYYMMDD = new StringBuilder(
                dateformatYYYYMMDD.format(d));
        return nowYYYYMMDD.toString();
    }

    public BidInfo(int id, ProductInfo product, long max, long min, long step, String name, String desc, BidType type, Date startDate, Date endDate) {
        this.id = id;
        this.product = product;
        this.max = max;
        this.min = min;
        this.step = step;
        this.name = name;
        this.desc = desc;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    

    public String toStringMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(product.toStringMessage()).append(AIOConstants.SEPERATOR_BYTE_2);
        sb.append(id).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(name).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(desc).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(max).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(min).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(step).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(dateToString(startDate)).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(dateToString(endDate)).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(type.toStringMessage()).append(AIOConstants.SEPERATOR_BYTE_1);
        return sb.toString();
    }
}
