/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.AllPhomPokerResponse;
import com.tv.xeeng.base.protocol.messages.GetPokerResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class AllPhomPokerJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(JoinedJSON.class);

    @Override
    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            encodingObj.put("mid", aResponseMessage.getID());
            AllPhomPokerResponse getPoker = (AllPhomPokerResponse) aResponseMessage;
            encodingObj.put("code", getPoker.mCode);
            if (getPoker.mCode == ResponseCode.FAILURE) {
            } else if (getPoker.mCode == ResponseCode.SUCCESS) {
               JSONArray players = new JSONArray();
               for(PhomPlayer p : getPoker.playings){
                   JSONObject obj = new JSONObject();
                   obj.put("uid", p.id);
                   obj.put("cards", p.allPokersToString());
               }
               encodingObj.put("cards", players);
            }
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
