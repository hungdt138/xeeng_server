package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.ChargingRequest;
import com.tv.xeeng.base.protocol.messages.ChargingResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ChargingInfo;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class ChargingJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ChargingJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            ChargingRequest request = (ChargingRequest) aDecodingObj;
            if (jsonData.has("v")) {

                String v = jsonData.getString("v");
                String[] arrValues;
                arrValues = v.split(AIOConstants.SEPERATOR_BYTE_1);
                request.partnerId = Integer.parseInt(arrValues[0]);
                if (arrValues.length > 1) {
                    request.refCode = arrValues[1];
                }

                return true;
            }

            if (jsonData.has("partnerid")) {
                request.partnerId = jsonData.getInt("partnerid");
            } else {
                request.partnerId = 0; //default is vipcom
            }
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            ChargingResponse send = (ChargingResponse) aResponseMessage;

            if (send.session != null && send.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(send.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (send.mCode == ResponseCode.FAILURE) {
                    sb.append(send.errMgs);
                } else {
                    if (send.value != null) {
                        sb.append(send.value);
                    } else {
                        int size = send.mess.size();
                        for (int i = 0; i < size; i++) {
                            ChargingInfo m = send.mess.get(i);
                            sb.append(m.number).append(AIOConstants.SEPERATOR_BYTE_1);
                            sb.append(m.value).append(AIOConstants.SEPERATOR_BYTE_1);
                            sb.append(m.desc).append(AIOConstants.SEPERATOR_BYTE_2);
                        }

                        if (size > 0) {
                            sb.deleteCharAt(sb.length() - 1);
                        }
                    }

                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }

            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", send.mCode);
            if (send.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", send.errMgs);
            } else {
                if (send.session != null && send.session.getByteProtocol() > AIOConstants.PROTOCOL_PRIMITIVE) {
                    int size = send.cardInfo.size();
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < size; i++) {
                        ChargingInfo m = send.cardInfo.get(i);
                        sb.append(m.number).append(AIOConstants.SEPERATOR_ELEMENT);
                        sb.append(m.value).append(AIOConstants.SEPERATOR_ELEMENT);
                        sb.append(m.desc).append(AIOConstants.SEPERATOR_ARRAY);
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }

                    encodingObj.put("v", sb.toString());
                    return encodingObj;
                }
                JSONArray arrValues = new JSONArray();
                for (ChargingInfo m : send.mess) {
                    JSONObject jCell = new JSONObject();
                    jCell.put("number", m.number);
                    jCell.put("value", m.value);
                    jCell.put("description", m.desc);
                    arrValues.put(jCell);
                }
                encodingObj.put("messages", arrValues);
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
