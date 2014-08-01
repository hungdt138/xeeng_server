package com.tv.xeeng.base.protocol.messages.json;

import java.util.List;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.EnterRoomRequest;
import com.tv.xeeng.base.protocol.messages.EnterRoomResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimpleTable;
import com.tv.xeeng.game.newpika.data.NewPikaPlayer;
import com.tv.xeeng.game.newpika.data.NewPikaTable;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class EnterRoomJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
            EnterRoomJSON.class);

    @Override
    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
            throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            EnterRoomRequest enterRoom = (EnterRoomRequest) aDecodingObj;
            if (jsonData.has("v")) {
                enterRoom.roomID = jsonData.getInt("v");
                return true;
            }

            enterRoom.roomID = jsonData.getInt("idRoom");
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    private void getMobileEnterRoomValue(EnterRoomResponse enterRoom, StringBuilder sb) {
//            StringBuilder sb = new StringBuilder();
//            sb.append(Integer.toString(MessagesID.EnterRoom)).append(AIOConstants.SEPERATOR_BYTE_1);
//            sb.append(Integer.toString(enterRoom.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);

        if (enterRoom.mCode == ResponseCode.FAILURE) {
            sb.append(enterRoom.mErrorMsg);
        } else {
            List<SimpleTable> tbls = enterRoom.tables;
            int tableSize = 0;
            if (tbls != null) {
                tableSize = tbls.size();

                for (int i = 0; i < tableSize; i++) {
                    SimpleTable table = tbls.get(i);
                    if(table instanceof NewPikaTable) {
                        NewPikaTable ntable = (NewPikaTable) table;
                        sb.append(ntable.mode).append(AIOConstants.SEPERATOR_BYTE_1);
                        if(ntable.mode == NewPikaTable.ADVENTURE_MODE) {
                            sb.append(((NewPikaPlayer)ntable.owner).levelAdvanture).append(AIOConstants.SEPERATOR_BYTE_1);
                        }
                        sb.append(ntable.typeMatrix).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(ntable.typeClientPlay).append(AIOConstants.SEPERATOR_BYTE_1);
                        if(ntable.mode != NewPikaTable.ADVENTURE_MODE) {
                            sb.append(ntable.dificultLevel).append(AIOConstants.SEPERATOR_BYTE_1);
                        }
                    }
                    if (table != null) {
                        sb.append(table.getTableIndex()).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.getTableSize()).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.firstCashBet).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.matchID).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.maximumPlayer).append(AIOConstants.SEPERATOR_BYTE_1);
                        /** MODIFIED by TUNG 
                        sb.append(table.isPlaying ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_2);*/
                        sb.append(table.isPlaying ? "1" : "0").append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.name).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.getRoom().getZoneID()).append(AIOConstants.SEPERATOR_BYTE_2);
                    }
                }
            }

            if (tableSize > 0) {

                sb.deleteCharAt(sb.length() - 1);
            }
        }


    }

    private void getFlashEnterRoomValue(EnterRoomResponse enterRoom, StringBuilder sb) {
//            StringBuilder sb = new StringBuilder();
//            sb.append(Integer.toString(MessagesID.EnterRoom)).append(AIOConstants.SEPERATOR_BYTE_1);
//            sb.append(Integer.toString(enterRoom.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);

        if (enterRoom.mCode == ResponseCode.FAILURE) {
            sb.append(enterRoom.mErrorMsg);
        } else {
            List<SimpleTable> tbls = enterRoom.tables;
            int tableSize = 0;
            if (tbls != null) {
                tableSize = tbls.size();

                for (int i = 0; i < tableSize; i++) {
                    SimpleTable table = tbls.get(i);
                    if (table != null) {
                        sb.append(table.getTableIndex()).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.getTableSize()).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.firstCashBet).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.matchID).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.name).append(AIOConstants.SEPERATOR_BYTE_1);
                        sb.append(table.owner.username).append(AIOConstants.SEPERATOR_BYTE_2);

                    }
                }
            }

            if (tableSize > 0) {
//                    System.out.println("capacity " + sb.length());
                sb.deleteCharAt(sb.length() - 1);
            }
        }

    }

    @Override
    public Object encode(IResponseMessage aResponseMessage)
            throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            EnterRoomResponse enterRoom = (EnterRoomResponse) aResponseMessage;

            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(MessagesID.EnterRoom)).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(enterRoom.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);

            if (enterRoom.session.isMobileDevice()) {
                getMobileEnterRoomValue(enterRoom, sb);
                encodingObj.put("v", sb.toString());
            } else {
                getFlashEnterRoomValue(enterRoom, sb);
                encodingObj.put("v", sb.toString());
            }
            return encodingObj;

        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}