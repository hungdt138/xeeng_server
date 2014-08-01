package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class CopyOfGuestLoginRequest extends AbstractRequestMessage
{
    public int partnerId  	= 0;
    public int refCode 		= 0;
    public String deviceUId = "";    
    public String mobileVersion = "";
        
    public IRequestMessage createNew()
    {
        return new CopyOfGuestLoginRequest();
    }

}
