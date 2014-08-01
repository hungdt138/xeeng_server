package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class ChangeSettingRequest extends AbstractRequestMessage
{
    public int roomID;
    public long matchID;
    // Phom    
    public boolean isUKhan = false;
    public boolean anCayMatTien = true; // default
    public boolean taiGuiUDen = false;
    public boolean truong = true;
    public long money;
    public int size;
    
    //tienlen
    public boolean isHidePoker = true;
    
    //New-Pikachu
    public int sizeMatrix;
    public int modePika;
    @Override
    public IRequestMessage createNew()
    {
        return new ChangeSettingRequest();
    }
}
