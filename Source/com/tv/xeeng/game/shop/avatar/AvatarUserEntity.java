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
public class AvatarUserEntity {
    public String icon;
    public String bigIcon;
    public int avatarID;
    public String name;
    public long price;
    public boolean isMale;
    public int dateLeft;
    //public int idChoose;
    
    public AvatarUserEntity() {
    }

    public AvatarUserEntity(String icon, String bigIcon, int avatarID, String name, long price, boolean isMale, int dateLeft) {
        this.icon = icon;
        this.bigIcon = bigIcon;
        this.avatarID = avatarID;
        this.name = name;
        this.price = price;
        this.isMale = isMale;
        this.dateLeft = dateLeft;
    }
    public String toString(boolean isJava) {
        StringBuilder sb = new StringBuilder();
        sb.append(avatarID).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(name).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(isJava?icon:bigIcon).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(price).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(isMale?"1":"0").append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(dateLeft);
        return sb.toString();
    }
    
}
