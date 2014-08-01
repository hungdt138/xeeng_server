/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.gameshow;

import java.util.Date;

/**
 *
 * @author tuanda
 */
public class GameShow {
    public ChannelLink link;
    public String name;
    public String desc;
    public int id;
    public int startH;
    public int endH;
    public int startM;
    public int endM;
    public int date;

    public GameShow() {
    }

    public GameShow(ChannelLink link, String name, String desc, int id, 
            int startH, int endH, int startM, int endM, int date) {
        this.link = link;
        this.name = name;
        this.desc = desc;
        this.id = id;
        this.startH = startH;
        this.endH = endH;
        this.startM = startM;
        this.endM = endM;
        this.date = date;
    }

    
    
    
}
