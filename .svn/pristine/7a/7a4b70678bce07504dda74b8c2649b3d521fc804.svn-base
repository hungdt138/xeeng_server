package com.tv.xeeng.base.protocol.messages;

import java.util.Vector;

import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class GetBestPlayerResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public Vector<UserEntity> mBestPlayerList;

    public void setSuccess(int aCode, Vector<UserEntity> aBestList) {
        mCode = aCode;
        mBestPlayerList = aBestList;
    }

    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew() {
        return new GetBestPlayerResponse();
    }
}
