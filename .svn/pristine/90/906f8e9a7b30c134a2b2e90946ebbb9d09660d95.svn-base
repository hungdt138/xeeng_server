package com.tv.xeeng.base.protocol.messages;

import java.util.Vector;

import com.tv.xeeng.game.data.AvatarEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class GetAvatarListResponse extends AbstractResponseMessage
{

    public String mErrorMsg;
    
    public Vector<AvatarEntity>  mAvatarList;

    public void setSuccess(int aCode, Vector<AvatarEntity>  aAvatarList)
    {
        mCode = aCode;
        mAvatarList = aAvatarList;
    }

    public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew()
    {
        return new GetAvatarListResponse();
    }
}
