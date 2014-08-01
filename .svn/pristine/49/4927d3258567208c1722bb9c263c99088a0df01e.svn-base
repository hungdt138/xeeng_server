package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GuestLoginRequest extends AbstractRequestMessage
{
    public int partnerId  	= 0;
    public int refCode 		= 0;
    public String deviceUId = "";    
    public String mobileVersion = "";
    public int regTime = 0;

    private String osName;
    private String osVersion;
    private String osMAC;
        
    public IRequestMessage createNew()
    {
        return new GuestLoginRequest();
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsMAC() {
        return osMAC;
    }

    public void setOsMAC(String osMAC) {
        this.osMAC = osMAC;
    }
}
