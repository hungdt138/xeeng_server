/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.UpdateUserInfoRequest;
import com.tv.xeeng.base.protocol.messages.UpdateUserInfoResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author Dinhpv
 */
public class UpdateUserInfoJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(UpdateUserInfoJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            UpdateUserInfoRequest update = (UpdateUserInfoRequest) aDecodingObj;
            if(jsonData.has("v")) {
            	String s = jsonData.getString("v");
            	String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
//            	update.email = arr[0];
            	update.oldPassword = arr[0];
                update.newPassword = arr[1];
//            	update.address = arr[3];
                if(arr.length>2)
                {
                
                    update.sex = arr[2].equals("1");
                    update.age = Integer.parseInt(arr[3]);	
                }
            	return true;
            }
            try {
                update.email = jsonData.getString("email");
            }catch (Exception e2){
                update.email = "";
            }
            
            update.newPassword = jsonData.getString("new_password");
            update.oldPassword = jsonData.getString("old_password");
            update.address =jsonData.getString("address");
            update.sex =jsonData.getBoolean("sex");
            
            try{
                update.age =jsonData.getInt("age");
            }
            catch(Exception ex){
                //update.age = "";
            }
            
            try{
                update.number = jsonData.getString("PhoneNumber");
            }catch(Exception ex){
                update.number = "";
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
           
            UpdateUserInfoResponse update = (UpdateUserInfoResponse) aResponseMessage;
            if(update.session != null && update.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(update.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (update.mCode == ResponseCode.FAILURE) {
                	 sb.append(update.mErrorMsg);
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", update.mCode);
            if (update.mCode == ResponseCode.FAILURE) {
                encodingObj.put("error_msg", update.mErrorMsg);
            } else if (update.mCode == ResponseCode.SUCCESS) {
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
