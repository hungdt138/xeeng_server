/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.shop.avatar;

import com.tv.xeeng.game.data.AIOConstants;

/**
 *
 * @author tuanda
 */
public class AvatarEntity {
    public int id;
    public String name;
    public String category;
    public int categoryID;
    public String icon;
    public String bigIcon;
    public String detailIcon;
    public String description;
    public long price;
    public int appearDate;
    public int likeRate;
    public boolean isMale;
    public long ownerID;

    public AvatarEntity(int id, String name, String category, int categoryID, String icon, String bigIcon, String detailIcon, String description, long price, int appearDate, int likeRate, boolean isMale, long ownerID) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.categoryID = categoryID;
        this.icon = icon;
        this.bigIcon = bigIcon;
        this.detailIcon = detailIcon;
        this.description = description;
        this.price = price;
        this.appearDate = appearDate;
        this.likeRate = likeRate;
        this.isMale = isMale;
        this.ownerID = ownerID;
    }

    
    public String getDetail(boolean isJava) {
        return id+AIOConstants.SEPERATOR_BYTE_1+(isJava?bigIcon:detailIcon)+
                AIOConstants.SEPERATOR_BYTE_1+description;
    }
    public String toString(boolean isJava) {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(name).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(category).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(isJava?icon:bigIcon).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(price).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(isMale?"1":"0").append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(appearDate).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(likeRate).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(ownerID);
        return sb.toString();
    }
}
