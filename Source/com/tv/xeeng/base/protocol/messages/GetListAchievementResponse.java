package com.tv.xeeng.base.protocol.messages;


import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class GetListAchievementResponse extends AbstractResponseMessage {
	
	public String message;
	public String value;
        
        
        public IResponseMessage createNew()
        {
            return new GetListAchievementResponse();
        }

        
}
