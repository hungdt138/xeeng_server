package com.tv.xeeng.protocol;

import com.tv.xeeng.base.session.ISession;

public abstract class AbstractResponseMessage implements IResponseMessage {

    private int mMsgId;
    public int mCode;
    public ISession session;
//  public StringBuilder debugSb = new StringBuilder();
    private long destUID = -1;//0: broadCast; -1: notSend

    @Override
    public void setDestUID(long destUID) {
        this.destUID = destUID;
    }

    @Override
    public long getDestID() {
        return destUID;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;

    }

    public void setID(int aMsgId) {
        this.mMsgId = aMsgId;
    }

    @Override
    public int getID() {
        return this.mMsgId;
    }

    @Override
    protected AbstractResponseMessage clone() {
        AbstractResponseMessage resMsg = (AbstractResponseMessage) createNew();
        resMsg.setID(this.mMsgId);
//    resMsg.session = session;
        return resMsg;
    }

    @Override
    public IResponseMessage clone(ISession session) {
        AbstractResponseMessage resMsg = (AbstractResponseMessage) createNew();
        resMsg.setID(this.mMsgId);
        //resMsg.session = session;
        return resMsg;
    }
}