package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GuiTangLinkRequest extends AbstractRequestMessage {
    
    public String phoneNumber;
    public IRequestMessage createNew()
    {
        return new GuiTangLinkRequest();
    }
}
