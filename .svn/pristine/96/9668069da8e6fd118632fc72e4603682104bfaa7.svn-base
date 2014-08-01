/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.shop.giftgame;

import com.tv.xeeng.game.data.AIOConstants;

/**
 *
 * @author tuanda
 */
public class GiftGameEntity {

    public int id;
    public String name;
    public String category;
    public int categoryID;
    public String icon;
    public String bigIcon;
    public long price;
    public String activeChat;
    public String passiveChat;
    public GiftGameEntity() {
    }

    public GiftGameEntity(int id, String name, String category, int categoryID, 
            String icon, String bigIcon, long price,String activeChat,String passiveChat) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.categoryID = categoryID;
        this.icon = icon;
        this.bigIcon = bigIcon;
        this.price = price;
        this.activeChat = activeChat;
        this.passiveChat = passiveChat;
    }

    public String toString(boolean isJava) {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(name).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(category).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(isJava ? icon : bigIcon).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(price);
        return sb.toString();
    }
}
