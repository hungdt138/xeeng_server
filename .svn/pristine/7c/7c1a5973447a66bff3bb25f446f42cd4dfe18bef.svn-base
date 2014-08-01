/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import java.util.logging.Level;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.UseCardRequest;
import com.tv.xeeng.base.protocol.messages.UseCardResponse;
import com.tv.xeeng.databaseDriven.DBCache;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class ReloadCacheJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ReloadCacheJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        JSONObject jsonData = (JSONObject) aEncodedObj;
        try {
            if(jsonData.has("isUseCache")){
                DBCache.isUseCache = jsonData.getBoolean("isUseCache");
            }
            
            if(jsonData.has("isUsePhom")){
                DBCache.isUsePhom = jsonData.getBoolean("isUsePhom");
           
            }
            //JSONObject jsonData = (JSONObject) aEncodedObj;
            //ReloadCacheRequest register = (ReloadCacheRequest) aDecodingObj;
            return true;
        } catch (JSONException ex) {
                mLog.error(ex.getMessage(), ex);
            }
        
        return true;
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        return null;
    }
}