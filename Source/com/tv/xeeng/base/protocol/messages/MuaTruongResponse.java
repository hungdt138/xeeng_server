package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class MuaTruongResponse extends AbstractResponseMessage {

	public String mErrorMsg;
	public long mUid;
	

	public void setFailure(int aCode, String aErrorMsg) {
		mCode = aCode;
		mErrorMsg = aErrorMsg;
	}

	public void setSuccess(int aCode, long uid) {
		mCode = aCode;
		mUid = uid;
	}

	public IResponseMessage createNew() {
		return new MuaTruongResponse();
	}
}
