package com.tv.xeeng.base.protocol.messages.json;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.RegisterRequest;
import com.tv.xeeng.base.protocol.messages.RegisterResponse;
import com.tv.xeeng.databaseDriven.FriendDB;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.GioiThieuEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import org.json.JSONObject;
import org.slf4j.Logger;

public class RegisterJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
            RegisterJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj)
            throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            RegisterRequest register = (RegisterRequest) aDecodingObj;
            if (jsonData.has("v")) {
                String[] arrValues = jsonData.getString("v").split(AIOConstants.SEPERATOR_BYTE_1);

                register.loginName = arrValues[0];
                register.mPassword = arrValues[1];
                register.partnerId = Integer.parseInt(arrValues[2]);
                register.protocol = Integer.parseInt(arrValues[3]);
                register.deviceUid = arrValues[4];

                mLog.debug("DeviceUid " + register.deviceUid);

                if (arrValues.length > 4) {
                    register.isMale = arrValues[5].equals("1");

                    if (arrValues.length > 5) {
                        register.linkGuiTang = arrValues[6];
                        register.isMxh = arrValues[7].equals("1");

                        try {
                            if (arrValues.length > 7) {
                                String refCode = arrValues[8];
                                register.refCode = refCode;
                                register.deviceType = arrValues[9];
                                if (arrValues.length > 9) {
                                    register.clientSessionId = arrValues[10];
                                    if (arrValues.length > 10) {
                                        register.registerTime = Integer.parseInt(arrValues[11]);
                                        if (arrValues.length > 11) {
                                            register.mUsername = arrValues[12];
                                            register.cmnd = arrValues[13];
                                            register.xePhoneNumber = arrValues[14];
                                        }
                                    }
                                }
                            }
                            try {
                                long refGioiThieuId = Long.parseLong(register.linkGuiTang);
                                FriendDB db = new FriendDB();
                                GioiThieuEntity gioiThieuEntity = db.getRefGioithieu(refGioiThieuId);
                                register.gioiThieuUid = gioiThieuEntity.getUserId();
                                register.phone = gioiThieuEntity.getPhoneOrMail();
                                register.partnerId = gioiThieuEntity.getPartnerId();
                            } catch (Exception ex) {
                            }
                        } catch (Exception ex) {
                            mLog.error(ex.getMessage(), ex);
                        }
                    }
                }
                return true;
            }

            return false;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage)
            throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            RegisterResponse register = (RegisterResponse) aResponseMessage;
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(register.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
            if (register.mCode == ResponseCode.FAILURE) {
                sb.append(register.mErrorMsg);
            }
            sb.append(register.values);

            encodingObj.put("v", sb.toString());
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
