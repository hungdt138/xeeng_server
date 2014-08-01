package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class BanTruongResponse extends AbstractResponseMessage {

	public String mErrorMsg;
	public long mUid;
	public long money;

	public void setFailure(int aCode, String aErrorMsg) {
		mCode = aCode;
		mErrorMsg = aErrorMsg;
	}

	public void setSuccess(int aCode, long money, long uid) {
		mCode = aCode;
		mUid = uid;
		this.money = money;
	}

	public IResponseMessage createNew() {
		return new BanTruongResponse();
	}
}
