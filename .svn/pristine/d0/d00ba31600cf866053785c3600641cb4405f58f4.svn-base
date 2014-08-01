package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class GetImageResponse  extends AbstractResponseMessage {

    public String mErrorMsg;
    public String image;
    public String name;
    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess(int aCode, String i, String n) {
        mCode = aCode;
        this.image = i;
        this.name = n;
    }

    public IResponseMessage createNew() {
        return new GetImageResponse();
    }

}
