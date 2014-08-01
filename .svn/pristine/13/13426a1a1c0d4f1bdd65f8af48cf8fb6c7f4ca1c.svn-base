package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class PikachuHelpResponse extends AbstractResponseMessage {

	public String mErrorMsg;
	public boolean isHelp;

	public void setFailure(int aCode, String aErrorMsg) {
		mCode = aCode;
		mErrorMsg = aErrorMsg;
	}

	public void setSuccess(boolean h) {
		isHelp = h;
		mCode = ResponseCode.SUCCESS;
	}

	public IResponseMessage createNew() {
		return new PikachuHelpResponse();
	}
}
