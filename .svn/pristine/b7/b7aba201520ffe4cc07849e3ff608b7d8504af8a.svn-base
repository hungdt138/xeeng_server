package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class LoginRequest extends AbstractRequestMessage
{
    public String loginName;
    public String mUsername;
    public String mPassword;
    public String mobileVersion="";
    public String flashVersion="";
    public String screen="";
    public String device="";
    public String cp="0";
    public int partnerId = 0;
    public String refCode="0";
    public int gamePosition;
    public boolean isMxh;
    public long deviceId=-1;

    private String osName;
    private String osVersion;
    private String osMAC;
    
    public boolean ver35 = false;
    
    //public String verID;
    public boolean shutDown= false;
    public int zoneId=0;
    public int protocol = 0;
    public IRequestMessage createNew()
    {
        return new LoginRequest();
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
