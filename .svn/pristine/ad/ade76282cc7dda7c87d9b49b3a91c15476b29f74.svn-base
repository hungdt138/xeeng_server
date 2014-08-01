/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.gameshow;

import java.util.ArrayList;

import com.tv.xeeng.game.data.AIOConstants;

/**
 *
 * @author tuanda
 */
public class LiveTVManager {
    private static ArrayList<ChannelLink> links = new ArrayList<ChannelLink>();
    
    public static void initChannels(){
        links.clear();
        
        
        links.add(new ChannelLink("rtsp://video.lifetv.vn:1935/mobi/vtv5.sdp", "VTV5", "Kenh VTV5", 5));
    }
    
    
    public static String channelData(){
        StringBuilder sb = new StringBuilder();
        for(ChannelLink c : links) {
            sb.append(c.id).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(c.name).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(c.desc).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(c.url).append(AIOConstants.SEPERATOR_BYTE_2);
        }
        if(sb.length() > 0) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }
}
