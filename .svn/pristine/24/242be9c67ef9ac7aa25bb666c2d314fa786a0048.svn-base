/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.gameshow;

import java.util.ArrayList;
import java.util.Calendar;

import com.tv.xeeng.game.data.AIOConstants;

/**
 *
 * @author tuanda
 */
public class GameShowManager {

    private static ArrayList<GameShow> gs = new ArrayList<GameShow>();

    public static void init() {
        gs.clear();
        ChannelLink vtv3 = new ChannelLink("rtsp://video.lifetv.vn:1935/mobi/vtv3.sdp", "VTV3", "VTC3", 3);
        int sH = 21;
        int sM = 0;
        int eH = 22;
        int eM = 0;
        int d = 3;
        GameShow altp = new GameShow(vtv3, "Ai La Trieu Phu", "Tro choi Ai La Trieu Phu", 1, sH, eH, sM, eM, d);
        gs.add(altp);
    }

    public static String gameShowData() {
        StringBuilder sb = new StringBuilder();
        Calendar cal = Calendar.getInstance();
        int d = cal.get(Calendar.DAY_OF_WEEK);
        
        int h = cal.get(Calendar.HOUR);
        int m = cal.get(Calendar.MINUTE);
        
        for (GameShow c : gs) {
            boolean b = ((d==c.date) && h>= c.startH && h <=c.endH && m>=c.startM && m<=c.endM);
            sb.append(c.name).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(c.desc).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(b?"1":"0").append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(c.link.url).append(AIOConstants.SEPERATOR_BYTE_2);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
