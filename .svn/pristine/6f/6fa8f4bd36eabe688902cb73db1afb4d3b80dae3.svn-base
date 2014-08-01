/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.base.protocol.messages.json;


import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.FindFriendsRequest;
import com.tv.xeeng.base.protocol.messages.FindFriendsResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author Dinhpv
 */
public class FindFriendsJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(FindFriendsJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            FindFriendsRequest findFriends = (FindFriendsRequest) aDecodingObj;
            
            	String s = jsonData.getString("v");
            	String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
                if(arr.length>2)
                {
                
    //            	update.email = arr[0];
                    findFriends.isMale = arr[0].equals("1");
                    findFriends.cityId = Integer.parseInt(arr[1]);
                    findFriends.jobId = Integer.parseInt(arr[2]);
                    if(arr[3].equals(""))
                      findFriends.fromYear = 0;
                    else
                      findFriends.fromYear = Integer.parseInt(arr[3]);

                    if(arr[4].equals(""))
                        findFriends.toYear = 0;
                    else
                        findFriends.toYear = Integer.parseInt(arr[4]);

                    findFriends.characterId = Integer.parseInt(arr[5]);
                    findFriends.hasAvatar = arr[6].equals("1");
                    if(arr.length>7)
                        findFriends.name = arr[7];
                    else
                        findFriends.name = "";
                }
                else
                {
                    findFriends.pageIndex = Integer.parseInt(arr[0]);
                    findFriends.requestId = Long.parseLong(arr[1]);
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
           
            FindFriendsResponse update = (FindFriendsResponse) aResponseMessage;
            
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
            sb.append(Integer.toString(update.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
            if (update.mCode == ResponseCode.FAILURE) {
                     sb.append(update.errMsg);
            }
            else
            {
                if(update.value != null)
                {
                    sb.append(update.value);
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
