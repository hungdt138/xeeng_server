package com.tv.xeeng.base.protocol.messages;


import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.phom.data.Poker;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class BocPhomResponse extends AbstractResponseMessage {

    public String message;
    public Poker card;
    public boolean isHabai;

    public void setSuccess(int aCode, Poker ca) {
        mCode = aCode;
        card = ca;
    }

    public void setSuccess(int aCode) {
        mCode = aCode;
    }

    public void setFailure(int aCode, String msg) {
        mCode = aCode;
        message = msg;
    }

    public IResponseMessage createNew() {
        return new BocPhomResponse();
    }
    
    public IResponseMessage clone(ISession session) {
        BocPhomResponse resMsg = (BocPhomResponse)createNew();
        resMsg.session = session;
        resMsg.setID(this.getID());
        resMsg.mCode = mCode;
        resMsg.message = message;
        resMsg.card = card;
        resMsg.isHabai = isHabai;
        return resMsg;
    }
}
