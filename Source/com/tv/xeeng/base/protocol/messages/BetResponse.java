/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class BetResponse extends AbstractResponseMessage{

    public long money;
    public long uid;
    public String errMsg;
    public int zoneId;
    
    public String cards;
    public JSONArray betInfo;
    public JSONObject betJson;
    
    public String value;
    
    public void setBacaySuccess(int code, long money, long uid, String cards)
    {
        this.mCode = code;
        this.money = money;
        this.uid = uid;
        this.zoneId = ZoneID.NEW_BA_CAY;
        this.cards = cards;
    }
    
    public void setSuccess(int code, long uid, JSONArray betInfo, int zoneId)
    {
        this.mCode = code;
        
        this.uid = uid;
        this.betInfo = betInfo;
        this.zoneId = zoneId;
    }
    
    public void setFailure(int code, String errorMsg)
    {
        this.mCode = code;
        this.errMsg = errorMsg;
    }
    
    
    public IResponseMessage createNew() {
        return new BetResponse();
    }
    
}
