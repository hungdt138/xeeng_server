package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class BookTourResponse extends AbstractResponseMessage {
	//public ArrayList<UserEntity> top10 = new ArrayList<UserEntity>();
    public String eRRMess;
    public int tour;
    //public String mess;
    public void setSuccess(int tourID) {
    	//mess = m;
    	tour = tourID;
        mCode = ResponseCode.SUCCESS;
    }
    
    public void setFailure(int aCode, String msg) {
        mCode = ResponseCode.FAILURE;
        eRRMess = msg;
    }

    public IResponseMessage createNew() {
        return new BookTourResponse();
    }
}
