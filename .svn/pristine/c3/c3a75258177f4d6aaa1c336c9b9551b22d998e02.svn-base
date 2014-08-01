package com.tv.xeeng.base.protocol.messages.json;






import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetWaitingListRequest;
import com.tv.xeeng.base.protocol.messages.GetWaitingListResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.game.phom.data.PhomTable;
import com.tv.xeeng.game.room.RoomEntity;
import com.tv.xeeng.game.tienlen.data.TienLenTable;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class GetWaitingListJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GetWaitingListJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            // request data
            JSONObject jsonData = (JSONObject) aEncodedObj;
            // request messsage
            GetWaitingListRequest getWaitingList = (GetWaitingListRequest) aDecodingObj;
            // parsing
            getWaitingList.mOffset = jsonData.getInt("offset");
            getWaitingList.mLength = jsonData.getInt("length");
            getWaitingList.level = jsonData.getInt("level");
            getWaitingList.minLevel = jsonData.getInt("minLevel");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            // put response data into json object
            encodingObj.put("mid", aResponseMessage.getID());
            // cast response obj
            GetWaitingListResponse getWaitingList = (GetWaitingListResponse) aResponseMessage;
            encodingObj.put("code", getWaitingList.mCode);
            if (getWaitingList.mCode == ResponseCode.FAILURE) {
            } else if (getWaitingList.mCode == ResponseCode.SUCCESS) {
                encodingObj.put("num_playing_room", getWaitingList.mNumPlayingRoom);
                // room dummy
                JSONArray arrRooms = new JSONArray();
                if (getWaitingList.mWaitingRooms != null) {
                    for (RoomEntity roomEntity : getWaitingList.mWaitingRooms) {
                        // with each playing room
                        JSONObject jRoom = new JSONObject();
                        jRoom.put("room_id", roomEntity.mRoomId);
                        jRoom.put("room_name", roomEntity.mRoomName);

                        jRoom.put("room_owner", roomEntity.mRoomOwnerName);



                        if (roomEntity.mPassword != null) {
                            jRoom.put("isSecure", true);
                            jRoom.put("password", roomEntity.mPassword);
                        } else {
                            jRoom.put("isSecure", false);
                        }
                        // attached object
                        switch (getWaitingList.zoneID) {
                            
                            case ZoneID.PHOM: {
                                PhomTable table = (PhomTable) roomEntity.mAttactmentData;
                                if (table != null) {

                                    roomEntity.mPlayingSize = table.getPlayings().size() + table.getWaitings().size();

                                    jRoom.put("capacity", table.getMaximumPlayer());
                                    jRoom.put("minBet", table.getMinBet());
                                    PhomPlayer roomOwner = (PhomPlayer) table.owner;
                                    jRoom.put("level", roomOwner.level);
                                    jRoom.put("avatar", roomOwner.avatarID);
                                    jRoom.put("username", roomOwner.username);
                                    jRoom.put("isPlaying", table.isPlaying);
                                }
                                break;
                            }
                            
                            case ZoneID.TIENLEN: {
                                TienLenTable table = (TienLenTable) roomEntity.mAttactmentData;
                                if (table != null) {
                                    jRoom.put("capacity", table.getMaximumPlayer());
                                    jRoom.put("minBet", table.getMinBet());
                                    
                                    jRoom.put("level", table.owner.level);
                                    jRoom.put("avatar", table.owner.avatarID);
                                    jRoom.put("username", table.owner.username);
                                    jRoom.put("isPlaying", table.isPlaying);
                                }
                                break;
                            }
                            default:
                                break;
                        }
                        jRoom.put("playing_size", roomEntity.mPlayingSize);
                        // then add to array of rooms

                        if (roomEntity.mPlayingSize > 0) {
                            arrRooms.put(jRoom);
                        }
                    }
                }
                encodingObj.put("waiting_rooms", arrRooms);
            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
