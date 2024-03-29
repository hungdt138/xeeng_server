package com.tv.xeeng.base.protocol.messages;

import java.util.Vector;

import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class GetFreeFriendListResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public Vector<UserEntity> mFrientList = new Vector<UserEntity>();
    

    public void setSuccess(int aCode, Vector<UserEntity> aFrientList) {
        mCode = aCode;
        mFrientList = aFrientList;
    }

    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew() {
        return new GetFreeFriendListResponse();
    }
}
