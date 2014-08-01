package com.tv.xeeng.protocol;

import com.tv.xeeng.base.session.ISession;

public abstract interface IResponseMessage
{
    public abstract void setDestUID(long destUID);
    public abstract long getDestID();
    
    
    public abstract void setSession(ISession session);
    public abstract int getID();

    public abstract IResponseMessage createNew();
    public abstract IResponseMessage clone(ISession session);
}