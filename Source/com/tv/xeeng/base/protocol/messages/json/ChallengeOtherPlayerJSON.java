/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;




import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelShowHandResponse;
import com.tv.xeeng.base.protocol.messages.ChallengeOtherPlayerRequest;
import com.tv.xeeng.base.protocol.messages.ChallengeOtherPlayerResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.SimplePlayer;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class ChallengeOtherPlayerJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ChallengeOtherPlayerJSON.class);
    

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            ChallengeOtherPlayerRequest challengeReq = (ChallengeOtherPlayerRequest)aDecodingObj;
            
            JSONArray playersArr = jsonData.getJSONArray("playings");
            challengeReq.uid = jsonData.getLong("uid");
            List<SimplePlayer> players = new ArrayList<SimplePlayer>(); 
            
            for(int i = 0; i< playersArr.length(); i++)
            {
                JSONObject jPlayer = playersArr.getJSONObject(i);
                SimplePlayer player = new SimplePlayer();
                
                if(jPlayer.has("uid"))
                    player.id = jPlayer.getLong("uid");
                
                if(jPlayer.has("money"))
                    player.setBetOther(jPlayer.getLong("money"));
                
                players.add(player);
            }
            
            challengeReq.players = players;
            
           
            
            
            return true;
        } catch (JSONException ex) {
            mLog.error(ex.getStackTrace().toString());
            return false;
        }
    }
    
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try
        {
            ChallengeOtherPlayerResponse cancel = (ChallengeOtherPlayerResponse) aResponseMessage;   
            return cancel.challengeJson;
        } catch (Throwable t)
        {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
    
}
