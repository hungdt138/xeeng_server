/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.base.protocol.messages.json;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.UpdateUserInfoRequest;
import com.tv.xeeng.base.protocol.messages.UpdateUserInfoResponse;
import com.tv.xeeng.base.protocol.messages.UpdateUserMxhInfoRequest;
import com.tv.xeeng.base.protocol.messages.UpdateUserMxhInfoResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author Dinhpv
 */
public class UpdateUserMxhInfoJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(UpdateUserMxhInfoJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            UpdateUserMxhInfoRequest update = (UpdateUserMxhInfoRequest) aDecodingObj;
            
            String s = jsonData.getString("v");
            String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
//            	update.email = arr[0];
//            update.sex = arr[0].equals("1");
            if(!arr[0].equals(""))
               update.cityId = Integer.parseInt(arr[0]);
            update.address = arr[1];
            
            if(!arr[2].equals(""))
                update.jobId = Integer.parseInt(arr[2]);
            
            long birthday = 0;
            if(!arr[3].equals(""))
                birthday = Long.parseLong(arr[3]);
            
            if(birthday>0)
                update.birthday = new Date(birthday);
            
            update.hobby = arr[4];
            update.nickSkype = arr[5];
            update.nickYahoo = arr[6];
            update.phoneNumber = arr[7];
            
            if(!arr[8].equals(""))
                update.characterId = Integer.parseInt(arr[8]);
            
            if(!arr[9].equals(""))
                update.avatarFileId = Long.parseLong(arr[9]);
            
            update.status = arr[10];

            return true;
            
            
            
        } catch (Throwable t) {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
           
            UpdateUserMxhInfoResponse update = (UpdateUserMxhInfoResponse) aResponseMessage;
            
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(update.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
            if (update.mCode == ResponseCode.FAILURE) {
                     sb.append(update.mErrorMsg);
            }
            encodingObj.put("v", sb.toString());
            return encodingObj;
            
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
