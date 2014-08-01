package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.ChangeSettingRequest;
import com.tv.xeeng.base.protocol.messages.ChangeSettingResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class ChangeSettingJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
            ChangeSettingJSON.class);

    @Override
    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
            throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            ChangeSettingRequest changeS = (ChangeSettingRequest) aDecodingObj;


            String s = jsonData.getString("v");
            String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
            changeS.matchID = Long.parseLong(arr[0]);
            changeS.size = Integer.parseInt(arr[1]);
            changeS.money = Long.parseLong(arr[2]);
            if (arr.length == 6) {
                changeS.anCayMatTien = (arr[3].equals("1") ? true : false);
                changeS.isUKhan = (arr[4].equals("1") ? true : false);
                changeS.taiGuiUDen = (arr[5].equals("1") ? true : false);
            } else if (arr.length == 4) {
                changeS.isHidePoker = (arr[3].equals("1") ? true : false);
            } else if (arr.length == 5) {// New Pika
                changeS.modePika = Integer.parseInt(arr[3]);
                changeS.sizeMatrix = Integer.parseInt(arr[4]);
            }
            return true;

        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    @Override
    public Object encode(IResponseMessage aResponseMessage)
            throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            ChangeSettingResponse changeS = (ChangeSettingResponse) aResponseMessage;
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(changeS.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
            if (changeS.mCode == ResponseCode.FAILURE) {
                sb.append(changeS.mErrorMsg);
            } else {
                sb.append(changeS.money).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(changeS.size);
                switch (changeS.zoneID) {
                    case ZoneID.PHOM:
                        sb.append(AIOConstants.SEPERATOR_BYTE_1).append(changeS.anCayMatTien ? "1" : "0");
                        sb.append(AIOConstants.SEPERATOR_BYTE_1).append(changeS.taiGuiUDen ? "1" : "0");
                        sb.append(AIOConstants.SEPERATOR_BYTE_1).append(changeS.isUKhan ? "1" : "0");
                        break;

                    case ZoneID.TIENLEN:
                        sb.append(AIOConstants.SEPERATOR_BYTE_1).append(changeS.isHidePoker ? "1" : "0");
                        break;

                    default:
                        break;
                }
            }
            encodingObj.put("v", sb.toString());
            return encodingObj;

        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
