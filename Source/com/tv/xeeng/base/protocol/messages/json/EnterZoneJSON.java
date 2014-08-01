package com.tv.xeeng.base.protocol.messages.json;

import java.util.logging.Level;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;


import java.nio.charset.Charset;
import org.json.JSONArray;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.EnterZoneRequest;
import com.tv.xeeng.base.protocol.messages.EnterZoneResponse;
import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.Base64;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class EnterZoneJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(EnterZoneJSON.class);
    
    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            EnterZoneRequest enterZone = (EnterZoneRequest) aDecodingObj;
            if(jsonData.has("v"))
            {
                String[] arr = jsonData.getString("v").split(AIOConstants.SEPERATOR_BYTE_1);
                if(arr.length > 1)
                {
                    enterZone.zoneID = Integer.parseInt(arr[0]);
                    enterZone.cacheVersion = Integer.parseInt(arr[1]);
                }
                else
                {
                   enterZone.zoneID = jsonData.getInt("v");
                   enterZone.cacheVersion = -1; // not use cache version
                }
                return true;
                
            }
            enterZone.zoneID = jsonData.getInt("zone");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }
    
    
    private String getMidAllRoom(EnterZoneResponse response)
    {
        
            StringBuilder sb = new StringBuilder();

            if (response.lstRooms != null)
            {

                int roomSize = response.lstRooms.size();
               
                for(int i = 0; i< roomSize; i++)
                {
                    NRoomEntity entity = response.lstRooms.get(i);
                    int playing;
                    if(entity.getPhong() == null)
                    {
                        playing = 0;
                    }
                    else
                    {
                        playing = entity.getPhong().getPlaying();
                    }

//                    response.debugSb.append(entity.getId()).append(response.SEPERATOR);
//                    response.debugSb.append(playing).append(response.SEPERATOR);
                    sb.append(entity.getId()).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(playing).append(AIOConstants.SEPERATOR_BYTE_1);
                    
                    sb.append(entity.getLv()).append(AIOConstants.SEPERATOR_BYTE_2);
                    
                }
                
                
                 sb.append(response.timeout);   
                 
               
                return sb.toString();


            }

//            encodingObj.put("v", sb.toString());

            return sb.toString();
                            
    }
    
    
    private String getAllRoomByte(EnterZoneResponse response)
    {
        
            StringBuilder sb = new StringBuilder();

            if (response.lstRooms != null)
            {

                int roomSize = response.lstRooms.size();
                byte[] roomBytes = new byte[roomSize * 5+1];
                roomBytes[roomSize * 5] = (byte)response.timeout;
                for(int i = 0; i< roomSize; i++)
                {
                    NRoomEntity entity = response.lstRooms.get(i);
                    int playing;
                    if(entity.getPhong() == null)
                    {
                        playing = 0;
                    }
                    else
                    {
                        playing = entity.getPhong().getPlaying();
                    }


                    sb.append(entity.getId()).append(AIOConstants.SEPERATOR_ELEMENT);
                    sb.append(playing).append(AIOConstants.SEPERATOR_ELEMENT);
                    
                    sb.append(entity.getLv()).append(AIOConstants.SEPERATOR_ARRAY);
                    
                }
                
                
                 sb.append(response.timeout);   
                 

               
                return sb.toString();
//                return Base64.encode(roomBytes);

            }

//            encodingObj.put("v", sb.toString());

            return sb.toString();
                            
    }
    
    private JSONArray getAllRooms(EnterZoneResponse response)
    {
            JSONArray arrRooms = new JSONArray();
            if (response.lstRooms != null) {
                
                for (int i = 0; i < response.lstRooms.size(); i++) {
                   try {
                       
                        NRoomEntity roomEntity = response.lstRooms.get(i);
                        JSONObject jRoom = new JSONObject();

                        jRoom.put("id", roomEntity.getId());

                        jRoom.put("level", roomEntity.getLevel());
                        jRoom.put("number", roomEntity.getNumber());
                        int playing = 0;
                        if(roomEntity.getPhong() != null) playing = roomEntity.getPhong().getPlaying();
                        jRoom.put("playing", playing);
                        jRoom.put("numTables", roomEntity.getNumTables());
                        jRoom.put("capacity", roomEntity.getAvailable());
                        jRoom.put("minCash", roomEntity.getMinCash());

                        arrRooms.put(jRoom);
                    } catch (JSONException ex) {
                        mLog.error(" get allRoom error", ex);
                    }
                }
            }
            return arrRooms;
    }
    
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            EnterZoneResponse enterZone = (EnterZoneResponse) aResponseMessage;
            if(enterZone.session != null && enterZone.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder valueSb = new StringBuilder();
                valueSb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                valueSb.append(Integer.toString(enterZone.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (enterZone.mCode == ResponseCode.FAILURE) {
                    valueSb.append(enterZone.mErrorMsg);
                }
                else
                {
                    enterZone.value = enterZone.value + AIOConstants.SEPERATOR_BYTE_2 + Integer.toString(enterZone.timeout);
                    valueSb.append(enterZone.value);
                }
                
                encodingObj.put("v", valueSb.toString());
                
                return encodingObj;
            }
            
            encodingObj.put("mid", aResponseMessage.getID());
            
            encodingObj.put("code", enterZone.mCode);
            if (enterZone.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", enterZone.mErrorMsg);
            } else if (enterZone.mCode == ResponseCode.SUCCESS) {
//                encodingObj.put("timeout", enterZone.timeout);    
                
                if(enterZone.session != null && enterZone.session.getByteProtocol()> AIOConstants.PROTOCOL_PRIMITIVE)
                {
                     enterZone.value = enterZone.value + AIOConstants.SEPERATOR_ARRAY + Integer.toString(enterZone.timeout);
                     encodingObj.put("v",enterZone.value);
//                    encodingObj.put("v",getAllRoomByte(enterZone));
                }
                else
                {
                    encodingObj.put("timeout", enterZone.timeout);    
                    encodingObj.put("rooms", getAllRooms(enterZone));
                }
                
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
