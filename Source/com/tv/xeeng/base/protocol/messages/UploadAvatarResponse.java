package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class UploadAvatarResponse extends AbstractResponseMessage {
	public String mErrorMsg;
        public long fileId;
        public String value;
	public void setSuccess(int aCode) {
		mCode = aCode;
	}
        
        public void setSuccess(String value) {
		mCode = ResponseCode.SUCCESS;
                this.value = value;
	}

	public void setFailure(int aCode, String aErrorMsg) {
		mCode = aCode;
		mErrorMsg = aErrorMsg;
	}

	public IResponseMessage createNew() {
		return new UploadAvatarResponse();
	}
}
