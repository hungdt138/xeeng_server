/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.base.protocol.messages.json;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetAllRoomRequest;
import com.tv.xeeng.base.protocol.messages.GetAllRoomResponse;
import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 * 
 * @author Administrator
 */
public class GetAllRoomJSON implements IMessageProtocol {
	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			GetAvatarListJSON.class);
        


	public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
			throws ServerException {
		JSONObject jsonData = (JSONObject) aEncodedObj;
//		GetAllRoomRequest req = (GetAllRoomRequest) aDecodingObj;
//		try {
//                    if(jsonData.has("v"))
//                    {
//			req.level = jsonData.getInt("level");
//                    }
//                    else
//                    {
//                        
//                    }
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		return true;
	}
        
        private void getInfo(GetAllRoomResponse rooms, StringBuilder sb, String sepEl, String sepArr)
        {
            if (rooms != null && rooms.getRooms() != null)
            {
                int roomSize = rooms.getRooms().size();

                for(int i = 0; i< roomSize; i++)
                {
                    NRoomEntity entity = rooms.getRooms().get(i);
                    int playing;
                    if(entity.getPhong() == null)
                    {
                        playing = 0;
                    }
                    else
                    {
                        playing = entity.getPhong().getPlaying();
                    }

                    sb.append(entity.getId()).append(sepEl);
                    sb.append(playing).append(sepEl);
                    sb.append(entity.getLv()).append(sepArr);
                }

            }

            if(sb.length()>0)
            {
                sb.deleteCharAt(sb.length()-1);
            }


        }
         
        
        
	public Object encode(IResponseMessage aResponseMessage)
			throws ServerException {
            JSONObject encodingObj = new JSONObject();
            try {
                GetAllRoomResponse rooms = (GetAllRoomResponse) aResponseMessage;
                
                if(rooms.session != null && rooms.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(Integer.toString(rooms.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                    if (rooms.mCode == ResponseCode.FAILURE) {
                             sb.append(rooms.mErrorMsg);
                    }else {
                        sb.append(rooms.value);
                        encodingObj.put("v", sb.toString());
                        return encodingObj;
                    }
                }
                
                    encodingObj.put("mid", aResponseMessage.getID());
                    
                    encodingObj.put("code", rooms.mCode);

                    if (rooms.mCode == ResponseCode.FAILURE) {
                            encodingObj.put("error_msg", rooms.mErrorMsg);
                    } else if (rooms.mCode == ResponseCode.SUCCESS) {
                        if(rooms.session != null && rooms.session.getByteProtocol()>AIOConstants.PROTOCOL_PRIMITIVE )
                        {            
                            encodingObj.put("v", rooms.value);

                            return encodingObj;
                        }
                        
                        
                        if(rooms.session != null && rooms.session.getByteProtocol()> AIOConstants.PROTOCOL_PRIMITIVE && rooms.session.isMobileDevice())
                        {
                            StringBuilder sb = new StringBuilder();

                            getInfo(rooms, sb, AIOConstants.SEPERATOR_ELEMENT, AIOConstants.SEPERATOR_ARRAY);

                            encodingObj.put("v", sb.toString());


                            return encodingObj;
                        }
                        
                        if(rooms.session != null && rooms.session.getByteProtocol()> AIOConstants.PROTOCOL_PRIMITIVE && !rooms.session.isMobileDevice())
                        {
                            StringBuilder sb = new StringBuilder();

                            getInfo(rooms, sb, AIOConstants.SEPERATOR_BYTE_1, AIOConstants.SEPERATOR_BYTE_2);

                            encodingObj.put("v", sb.toString());


                            return encodingObj;
                        }
                        
                        

                            JSONArray arrRooms = new JSONArray();
                            if (rooms != null && rooms.getRooms() != null) {
                                int roomSize = rooms.getRooms().size();
                                    for (int i = 0; i < roomSize; i++) {

                                         NRoomEntity roomEntity = rooms.getRooms().get(i);
                                        JSONObject jRoom = new JSONObject();
                                        jRoom.put("id", roomEntity.getId());
                                        jRoom.put("level", roomEntity.getLevel());
                                        jRoom.put("number", roomEntity.getNumber());
                                        int playing = 0;
                                        if(roomEntity.getPhong() != null)
                                            playing = roomEntity.getPhong().getPlaying();
                                        jRoom.put("playing", playing);
                                        jRoom.put("numTables", roomEntity.getNumTables());
                                        jRoom.put("capacity", roomEntity.getAvailable());
                                        jRoom.put("minCash", roomEntity.getMinCash());

                                            arrRooms.put(jRoom);
                                    }
                            }
                            encodingObj.put("rooms", arrRooms);
                    }
            } catch (JSONException ex) {
                    mLog.error("[Get All Room Encoder] ", ex.getStackTrace());
            }

            return encodingObj;

	}

}
