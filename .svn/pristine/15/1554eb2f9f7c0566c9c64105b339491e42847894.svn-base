package com.tv.xeeng.base.protocol.messages.json;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetUserInfoRequest;
import com.tv.xeeng.base.protocol.messages.GetUserInfoResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;
import org.json.JSONObject;
import org.slf4j.Logger;

public class GetUserInfoJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GetUserInfoJSON.class);

    private final int PARTNER_APPSTORE_ID = 94;
    private final String APPSTORE_REVIEW_VERSION = "1.0.4";

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            GetUserInfoRequest getUserInfo = (GetUserInfoRequest) aDecodingObj;
            if (jsonData.has("v")) {
                String v = jsonData.getString("v");
                String[] arrValues;

                arrValues = v.split(AIOConstants.SEPERATOR_BYTE_1);
                getUserInfo.mUid = Long.parseLong(arrValues[0]);

                if (arrValues.length >= 3) {
                    getUserInfo.partnerId = Integer.parseInt(arrValues[1]);
                    getUserInfo.mobileVersion = arrValues[2];
                }
//                getUserInfo.mUid = jsonData.getLong("v");
            } else {
                getUserInfo.mUid = jsonData.getLong("uid");
            }
            return true;
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    private String finalProtocol(GetUserInfoResponse getFrientList) {
        StringBuilder sb = new StringBuilder();
        sb.append(getFrientList.mUid).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.mUsername).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.AvatarID).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.level).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.money).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.playsNumber).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.experience).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.isFriend ? 1 : 0).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.mIsMale ? 1 : 0).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.mAge).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.vipName).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.xeeng).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.loginName).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.cmnd).append(AIOConstants.SEPERATOR_BYTE_1);
        sb.append(getFrientList.xePhoneNumber);

        // hard code for app store review
        mLog.debug("---THANGTD USERINFO RESPONE DEBUG---partnerId: " + getFrientList.partnerId + " version: " + getFrientList.mobileVersion);
        if (PARTNER_APPSTORE_ID == getFrientList.partnerId && APPSTORE_REVIEW_VERSION.equals(getFrientList.mobileVersion)) {
            sb.append(AIOConstants.SEPERATOR_BYTE_1).append("0");
        }

        return sb.toString();
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();

            GetUserInfoResponse getUserInfo = (GetUserInfoResponse) aResponseMessage;
            if (getUserInfo.session != null && getUserInfo.session.getByteProtocol() > AIOConstants.PROTOCOL_ADVERTISING) {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(getUserInfo.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (getUserInfo.mCode == ResponseCode.FAILURE) {
                    sb.append(getUserInfo.mErrorMsg);
                } else {
                    sb.append(finalProtocol(getUserInfo));
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }

            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", getUserInfo.mCode);
            if (getUserInfo.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", getUserInfo.mErrorMsg);
            } else if (getUserInfo.mCode == ResponseCode.SUCCESS) {
                encodingObj.put("uid", getUserInfo.mUid);
                encodingObj.put("username", getUserInfo.mUsername);
                encodingObj.put("age", getUserInfo.mAge);
                encodingObj.put("is_male", getUserInfo.mIsMale);
                encodingObj.put("avatar", getUserInfo.AvatarID);
                encodingObj.put("money", getUserInfo.money);
                encodingObj.put("level", getUserInfo.level);
                encodingObj.put("playsNumber", getUserInfo.playsNumber);
                encodingObj.put("is_friend", getUserInfo.isFriend);
                encodingObj.put("experience", getUserInfo.experience);
                encodingObj.put("vipName", getUserInfo.vipName);
                encodingObj.put("xeeng", getUserInfo.xeeng);
                encodingObj.put("loginName", getUserInfo.loginName);
                encodingObj.put("cmnd", getUserInfo.cmnd);
                encodingObj.put("xePhoneNumber", getUserInfo.xePhoneNumber);
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }

}
