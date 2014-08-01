package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class AddFriendByNameResponse extends AbstractResponseMessage
{

    public String mErrorMsg;
    public UserEntity user;
    public void setSuccess(int aCode, UserEntity u)
    {
        mCode = aCode;
        user = u;
    }

    public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew()
    {
        return new AddFriendByNameResponse();
    }
}
