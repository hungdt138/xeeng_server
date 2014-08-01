package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class BuyAvatarResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public String value;

    public void setSuccess(String v) {
        mCode = ResponseCode.SUCCESS;
        value = v;
    }

    public void setFailure(String aErrorMsg) {
        mCode = ResponseCode.FAILURE;
        mErrorMsg = aErrorMsg;
    }

    @Override
    public IResponseMessage createNew() {
        return new BuyAvatarResponse();
    }
}
