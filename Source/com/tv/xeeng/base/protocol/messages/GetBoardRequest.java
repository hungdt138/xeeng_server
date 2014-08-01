package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GetBoardRequest extends AbstractRequestMessage
{
//    public long uid;
    public int pageIndex = 0;
    public int pageSize = 0;
   
    public IRequestMessage createNew()
    {
        return new GetBoardRequest();
    }

}
