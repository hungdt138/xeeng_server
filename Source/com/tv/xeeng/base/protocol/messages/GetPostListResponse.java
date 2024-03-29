package com.tv.xeeng.base.protocol.messages;

import java.util.Vector;

import com.tv.xeeng.game.data.PostEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class GetPostListResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public Vector<PostEntity> mPostList;

    public void setSuccess(int aCode, Vector<PostEntity> aPostList) {
        mCode = aCode;
        mPostList = aPostList;
    }

    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew() {
        return new GetPostListResponse();
    }
}
