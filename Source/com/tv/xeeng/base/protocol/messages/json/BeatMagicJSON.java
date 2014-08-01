/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BeatMagicRequest;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;


/**
 *
 * @author tuanda
 */
public class BeatMagicJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(BeatMagicJSON.class);

    @Override
    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            // request data
            JSONObject jsonData = (JSONObject) aEncodedObj;
            // cancel request message
            BeatMagicRequest an = (BeatMagicRequest) aDecodingObj;

            try {
                String v = jsonData.getString("v");
                String[] arr = v.split(AIOConstants.SEPERATOR_BYTE_1);
                //if (arr.length == 3) {
                    an.code = Long.parseLong(arr[2]);
                    an.matchID = Long.parseLong(arr[1]);
                    an.zoneID = Integer.parseInt(arr[0]);
                
                return true;
            } catch (Exception ex) {
                mLog.error(ex.getMessage(), ex);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        return null;
    }
}
