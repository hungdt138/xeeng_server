package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class BetEuroMatchResponse extends AbstractResponseMessage {
	public String message;

	public void setResponse(int aCode, String message) {
		mCode = aCode;
                this.message = message;
	}

	

	public IResponseMessage createNew() {
		return new BetEuroMatchResponse();
	}
}
