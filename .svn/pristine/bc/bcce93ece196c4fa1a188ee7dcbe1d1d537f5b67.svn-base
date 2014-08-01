/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import org.json.JSONObject;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class ChallengeOtherPlayerResponse extends AbstractResponseMessage{

    
    public JSONObject challengeJson;
    
    public void setSuccess(JSONObject showHandJson)
    {
        this.challengeJson = showHandJson;
        mCode = ResponseCode.SUCCESS;
    }
    
    public IResponseMessage createNew() {
        return new ChallengeOtherPlayerResponse();
    }
    
}
