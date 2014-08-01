package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GetUserInfoRequest extends AbstractRequestMessage
{
    public long mUid;
    public int partnerId = 0;
    public String mobileVersion="";

    public IRequestMessage createNew() {
        return new GetUserInfoRequest();
    }
}
