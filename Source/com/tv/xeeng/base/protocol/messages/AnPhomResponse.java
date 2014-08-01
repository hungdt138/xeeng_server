package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class AnPhomResponse extends AbstractResponseMessage {

    public String message;
    public long money;
    public long uid;
    public long p_uid;
    public boolean chot;
    public boolean isHaBai;

    public void setSuccess(int aCode, long money, long uid, long p_uid) {
        mCode = aCode;
        this.uid = uid;
        this.money = money;
        this.p_uid = p_uid;
    }

    public void setFailure(int aCode, String msg) {
        mCode = aCode;
        message = msg;
    }

    public IResponseMessage createNew() {
        return new AnPhomResponse();
    }

    public IResponseMessage clone(ISession session) {
        AnPhomResponse resMsg = (AnPhomResponse) createNew();
        resMsg.message = message;
        resMsg.session = session;
        resMsg.setID(this.getID());
        resMsg.mCode = mCode;
        resMsg.money = money;
        resMsg.uid = uid;
        resMsg.p_uid = p_uid;
        resMsg.chot = chot;
        resMsg.isHaBai = isHaBai;

        return resMsg;
    }
}
