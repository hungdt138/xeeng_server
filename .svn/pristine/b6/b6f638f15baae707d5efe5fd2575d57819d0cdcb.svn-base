package com.tv.xeeng.base.protocol.messages;


import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.phom.data.Poker;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class ZoneCacheResponse extends AbstractResponseMessage {

    public String message;
    public String gameInfo;
    public String active;
    public void setSuccess( String adv) {
        mCode = ResponseCode.SUCCESS;
        this.gameInfo = adv;
    }

    public ZoneCacheResponse() {
    }

    

    public void setFailure(int aCode, String msg) {
        mCode = aCode;
        message = msg;
    }

    public IResponseMessage createNew() {
        return new ZoneCacheResponse();
    }
}
