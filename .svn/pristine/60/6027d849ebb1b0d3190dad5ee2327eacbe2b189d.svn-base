package com.tv.xeeng.base.protocol.messages;

import java.util.Vector;

import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class GetMostPlayingResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public Vector<UserEntity> mMostPlayingist;

    public void setSuccess(int aCode, Vector<UserEntity> aMostPlayingList) {
        mCode = aCode;
        mMostPlayingist = aMostPlayingList;
    }

    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew() {
        return new GetMostPlayingResponse();
    }
}
