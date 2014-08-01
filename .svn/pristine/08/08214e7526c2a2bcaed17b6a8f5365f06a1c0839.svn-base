package com.tv.xeeng.base.protocol.messages;




import java.util.List;

import com.tv.xeeng.game.data.BlogEntity;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class GetNewestAlbumResponse extends AbstractResponseMessage {
        public String errMsg;
	
        public String value;
	public void setFailure(String message)
        {
            mCode = ResponseCode.FAILURE;
            errMsg = message;
            
        }

	public IResponseMessage createNew() {
		return new GetNewestAlbumResponse();
	}
}
