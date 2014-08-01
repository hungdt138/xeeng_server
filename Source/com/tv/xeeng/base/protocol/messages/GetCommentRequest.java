package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GetCommentRequest extends AbstractRequestMessage
{

    
    public int systemObjectId;
    public long systemObjectRecordId;
    public int page;
    public int size;
    
    public IRequestMessage createNew()
    {
        return new GetCommentRequest();
    }

}
