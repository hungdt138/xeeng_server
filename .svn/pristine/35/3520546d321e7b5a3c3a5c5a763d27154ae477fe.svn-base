package com.tv.xeeng.base.protocol.messages;



import java.util.Date;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.phom.data.Poker;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class SendAdvResponse extends AbstractResponseMessage {

    public String message;
    public String adv;
    public String active;
    public long activateTime=0;
    public void setSuccess( String adv) {
        mCode = ResponseCode.SUCCESS;
        this.adv = adv;
    }

    public SendAdvResponse() {
    }

    

    public void setFailure(int aCode, String msg) {
        mCode = aCode;
        message = msg;
    }

    public IResponseMessage createNew() {
        return new SendAdvResponse();
    }
}
