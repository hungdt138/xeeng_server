package com.tv.xeeng.base.protocol.messages;

import java.util.Vector;

import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class GetFrientListResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    //public int mNumPlayingRoom;
    public Vector<UserEntity> mFrientList;

    public void setSuccess(int aCode, Vector<UserEntity> aFrientList) {
        mCode = aCode;
        // mNumPlayingRoom = aNumPlayingRoom;
        mFrientList = aFrientList;
    }

    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew() {
        return new GetFrientListResponse();
    }
}
