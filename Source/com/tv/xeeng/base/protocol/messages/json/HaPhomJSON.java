package com.tv.xeeng.base.protocol.messages.json;

import java.util.ArrayList;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.HaPhomRequest;
import com.tv.xeeng.base.protocol.messages.HaPhomResponse;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class HaPhomJSON implements IMessageProtocol {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(HaPhomJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException {
        try {
            JSONObject jsonData = (JSONObject) aEncodedObj;
            HaPhomRequest ha = (HaPhomRequest) aDecodingObj;
            if(jsonData.has("v")){
            	String s = jsonData.getString("v");
            	String[] arr = s.split(AIOConstants.SEPERATOR_BYTE_1);
            	ha.matchID = Long.parseLong(arr[0]);
            	ha.u = Integer.parseInt(arr[1]);
            	ha.cards1 = arr[2];
            	if (ha.u == 0) {
                    try
                    {
            		ha.cards = getCards(arr[2]);
                    }
                    catch(Exception ex)
                    {
                        
                    }
            	}else {
            		ha.cards = getCards(arr[2]);
            		ha.card = Integer.parseInt(arr[3]);
            	}
            	return true;
            }
            ha.matchID = jsonData.getLong("match_id");
            ha.u = jsonData.getInt("u");
            String temp = jsonData.getString("cards");
            ha.cards1 = temp;
            
            if (ha.u == 0) {
                try{
                    ha.cards = getCards(temp);
                }catch(Exception e){

                }

            } else {
                ha.card = jsonData.getInt("card");
                ha.cards = getCards(temp);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private  ArrayList<ArrayList<Integer>> getCards(String input) throws Exception {
         ArrayList<ArrayList<Integer>> res = new ArrayList<ArrayList<Integer>>();
         String[] i1 = input.split(";");
         for(String i : i1){
              ArrayList<Integer> temp = new ArrayList<Integer>();
              String[] i2 = i.split("#");
              for(String j : i2){
                  
                  temp.add(Integer.parseInt(j));
              }
              res.add(temp);
         }
         return res;
    }
    public Object encode(IResponseMessage aResponseMessage) throws ServerException {
        try {
            JSONObject encodingObj = new JSONObject();
            HaPhomResponse ha = (HaPhomResponse) aResponseMessage;
            if(ha.session != null && ha.session.getByteProtocol()> AIOConstants.PROTOCOL_ADVERTISING)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(aResponseMessage.getID())).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(Integer.toString(ha.mCode)).append(AIOConstants.SEPERATOR_NEW_MID);
                if (ha.mCode == ResponseCode.FAILURE) {
                	 sb.append(ha.message);
                }else {
                	sb.append(ha.uid).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(ha.cards).append(AIOConstants.SEPERATOR_BYTE_1);
                    sb.append(ha.u);
                    
                    if(ha.u == 1)
                    {
                        sb.append(AIOConstants.SEPERATOR_BYTE_1).append(ha.card);
                    }
                }
                encodingObj.put("v", sb.toString());
                return encodingObj;
            }
            encodingObj.put("mid", aResponseMessage.getID());
            encodingObj.put("code", ha.mCode);
            if (ha.mCode == ResponseCode.SUCCESS) {
                
                if(ha.session != null && ha.session.getByteProtocol()> AIOConstants.PROTOCOL_PRIMITIVE)
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ha.uid).append(AIOConstants.SEPERATOR_ELEMENT);
                    sb.append(ha.cards).append(AIOConstants.SEPERATOR_ELEMENT);
                    sb.append(ha.u);
                    
                    if(ha.u == 1)
                    {
                        sb.append(AIOConstants.SEPERATOR_ELEMENT).append(ha.card);
                    }
                    encodingObj.put("v", sb.toString());
                    return encodingObj;
                }
                
                encodingObj.put("U", ha.u);
                
                
                if (ha.u == 1) {
                    encodingObj.put("card", ha.card);
                }
                //JSONObject phomsJSON = new JSONObject();
//                for (int i = 0; i < ha.cards.size(); i++) {
//                    ArrayList<Integer> phom = ha.cards.get(i);
//                    JSONArray phomJSON = new JSONArray();
//                    for (int c : phom) {
//                        JSONObject obj = new JSONObject();
//                        obj.put("card", c);
//                        phomJSON.put(obj);
//                    }
//                    phomsJSON.put(phomJSON);
//                }
                encodingObj.put("phoms", ha.cards);
                encodingObj.put("uid", ha.uid);

            } else {
                encodingObj.put("error", ha.message);
            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t) {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }
}
