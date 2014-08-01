package com.tv.xeeng.base.protocol.messages;


import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class GetResourceResponse extends AbstractResponseMessage {
	
	public String message;
	public String value;
        public void setFailure(String aErrorMsg)
        {
            mCode = ResponseCode.FAILURE;
            this.message = aErrorMsg;
        }
        
        public void setSuccess(String value)
        {
            mCode = ResponseCode.SUCCESS;
            this.value = value;
        }
        
        public IResponseMessage createNew()
        {
            return new GetResourceResponse();
        }

        
}
