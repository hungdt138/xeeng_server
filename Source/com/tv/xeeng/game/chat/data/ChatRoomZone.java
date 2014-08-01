/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.chat.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.databaseDriven.InfoDB;
import com.tv.xeeng.game.data.AIOConstants;


/**
 *
 * @author tuanda
 */
public class ChatRoomZone {
    private  List<ChatRoom> rooms = new ArrayList<ChatRoom>();
    private static final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ChatRoomZone.class);
    private static final int GAME_GLOBAL = 1000;
    
    public  String getAllRooms(int cacheVersion)
    {
        
        int roomSize = rooms.size() ; //1 for game chat room
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(cacheVersion)).append(AIOConstants.SEPERATOR_BYTE_3);
        for(int i= 0; i< roomSize; i++)
        {
            ChatRoom room = rooms.get(i);
            ChatRoomEntity entity = room.getEntity();
            sb.append(entity.getChatRoomId()).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(entity.getName()).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(entity.getStt()).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(room.getPlaying()).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(entity.isIsFanClub()?"1":"0").append(AIOConstants.SEPERATOR_BYTE_2);
        }

        if(sb.length()>0)
        {
            sb.deleteCharAt(sb.length() -1);
        }
       
        return sb.toString();
    }
    
    public  String getOnlyPlaying()
    {
        
        int roomSize = rooms.size() ; //1 for game chat room
        StringBuilder sb = new StringBuilder();
        
        for(int i= 0; i< roomSize; i++)
        {
            ChatRoom room = rooms.get(i);
            ChatRoomEntity entity = room.getEntity();
            sb.append(entity.getChatRoomId()).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(room.getPlaying()).append(AIOConstants.SEPERATOR_BYTE_2);
        }

        if(sb.length()>0)
        {
            sb.deleteCharAt(sb.length() -1);
        }
       
        return sb.toString();
    }
    
    
    public ChatRoom findChatRoom(int chatRoomId)
    {
        int size = rooms.size();
        for(int i = 0; i< size; i++)
        {
            ChatRoom room = rooms.get(i);
            if(room.getEntity().getChatRoomId() == chatRoomId)
                return room;
        }
        
        return null;
    }
    
    
    public  void initZone()
    {
        try {
            InfoDB db = new InfoDB();
            List<ChatRoomEntity> lstRoomEntites = db.getChatRooms();
            
            ChatRoomEntity entity = new ChatRoomEntity(GAME_GLOBAL, "Hội Chém Gió Siêu Đẳng", "Giang hồ hiểm ác không bằng mạng lag thất thường", 50, false);
            
            lstRoomEntites.add(entity);
            
            int size = lstRoomEntites.size();
            for(int i = 0; i< size; i++)
            {
                ChatRoom room = new ChatRoom(lstRoomEntites.get(i));
                rooms.add(room);
            }
            
        } catch (SQLException ex) {
            mLog.error(ex.getMessage(), ex);
        }
        
    }
    
    public void Reload()
    {
        initZone();
    }
    
}
