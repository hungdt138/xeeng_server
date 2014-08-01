/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages.json;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.BetRequest;
import com.tv.xeeng.base.protocol.messages.BetResponse;
import com.tv.xeeng.base.protocol.messages.CancelShowHandRequest;
import com.tv.xeeng.base.protocol.messages.CancelShowHandResponse;
import com.tv.xeeng.game.baucuatomca.data.BauCuaTomCaPlayer;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class CancelShowHandJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(CancelShowHandJSON.class);
    

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            CancelShowHandRequest cancelReq = (CancelShowHandRequest)aDecodingObj;
            
            if(jsonData.has("playings"))
            {
                JSONArray playersArr = jsonData.getJSONArray("playings");

                List<BauCuaTomCaPlayer> players = new ArrayList<BauCuaTomCaPlayer>(); 
                for(int i = 0; i< playersArr.length(); i++)
                {
                    JSONObject jPlayer = playersArr.getJSONObject(i);
                    BauCuaTomCaPlayer bctcPlayer = new BauCuaTomCaPlayer();
                    bctcPlayer.id = jPlayer.getLong("uid");
                    if(jPlayer.has("holo"))
                        bctcPlayer.setHolo(jPlayer.getLong("holo"));
                    if(jPlayer.has("tom"))
                        bctcPlayer.setTom(jPlayer.getLong("tom"));
                    if(jPlayer.has("cua"))
                        bctcPlayer.setCua(jPlayer.getLong("cua"));
                    if(jPlayer.has("ca"))
                        bctcPlayer.setCa(jPlayer.getLong("ca"));
                    if(jPlayer.has("ga"))
                        bctcPlayer.setGa(jPlayer.getLong("ga"));
                    if(jPlayer.has("huou"))
                        bctcPlayer.setHuou(jPlayer.getLong("huou"));

                    players.add(bctcPlayer);
                }

                cancelReq.players = players;
            
            }
            
            if(jsonData.has("lstPlayerId"))
            {
                cancelReq.lstPlayerId = jsonData.getString("lstPlayerId");
            }
            
            
            return true;
        } catch (JSONException ex) {
            mLog.error(ex.getStackTrace().toString());
            return false;
        }
    }
    
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try
        {
            JSONObject encodingObj = new JSONObject();
            // put response data into json object
            encodingObj.put("mid", aResponseMessage.getID());
            // cast response obj
            CancelShowHandResponse cancel = (CancelShowHandResponse) aResponseMessage;
            encodingObj.put("code", cancel.mCode);
            if(cancel.mCode == ResponseCode.SUCCESS){
                encodingObj.put("msg", cancel.msg);
            }else {
            	encodingObj.put("error", cancel.msg);
            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t)
        {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
    
}
