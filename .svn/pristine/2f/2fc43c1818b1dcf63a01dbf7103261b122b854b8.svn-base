/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import org.json.JSONArray;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.ZoneID;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class CancelChallengeResponse extends AbstractResponseMessage{

    
    public String msg;
    
    public void setSuccess(String msg)
    {
        mCode = ResponseCode.SUCCESS;
        this.msg = msg;
    }
    
    public void setFailure(String msg)
    {
        mCode = ResponseCode.FAILURE;
        this.msg = msg;
    }
    
    public IResponseMessage createNew() {
        return new CancelChallengeResponse();
    }
    
}
