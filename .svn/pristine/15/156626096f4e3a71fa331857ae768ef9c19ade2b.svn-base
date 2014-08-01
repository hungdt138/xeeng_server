package com.tv.xeeng.base.protocol.messages;

import java.util.ArrayList;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class GetTopPlayerTourResponse extends AbstractResponseMessage {
	public ArrayList<UserEntity> top10 = new ArrayList<UserEntity>();
    public String eRRMess;
    public void setSuccess(ArrayList<UserEntity> t) {
    	top10 = t;
        mCode = ResponseCode.SUCCESS;
    }
    
    public void setFailure(int aCode, String msg) {
        mCode = ResponseCode.FAILURE;
        eRRMess = msg;
    }

    public IResponseMessage createNew() {
        return new GetTopPlayerTourResponse();
    }
}
