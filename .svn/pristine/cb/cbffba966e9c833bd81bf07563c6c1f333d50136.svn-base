/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author tuanda
 */
public class ChallengeResponse extends AbstractResponseMessage{

    public long money;
    public long uid;
    public boolean isChan;
    
    public String errMsg;
   
    
    public void setSuccess(int code, long money, long uid, boolean isChan)
    {
        this.mCode = code;
        this.money = money;
        this.uid = uid;
        this.isChan = isChan;
    }
    
    
    
    public void setFailure(int code, String errorMsg)
    {
        this.mCode = code;
        this.errMsg = errorMsg;
    }
    
    
    public IResponseMessage createNew() {
        return new ChallengeResponse();
    }
    
}
