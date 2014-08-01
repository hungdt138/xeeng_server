package com.tv.xeeng.base.protocol.messages;

import java.util.ArrayList;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.tournement.TournementEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class GetTournementListResponse extends AbstractResponseMessage {

	
	public String errMsg;
	public ArrayList<TournementEntity> tours = new ArrayList<TournementEntity>();

	public void setFailure(String errorMsg) {
		this.mCode = ResponseCode.FAILURE;
		this.errMsg = errorMsg;
	}

	public void setSuccess(ArrayList<TournementEntity> t){
		tours = t;
		mCode = ResponseCode.SUCCESS;
	}
	public IResponseMessage createNew() {
		return new GetTournementListResponse();
	}

}
