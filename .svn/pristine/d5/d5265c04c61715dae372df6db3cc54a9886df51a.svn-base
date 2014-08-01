package com.tv.xeeng.base.protocol.messages;






import org.json.JSONObject;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class ShowHandResponse extends AbstractResponseMessage {

    public JSONObject showHandJson;
    
    public void setSuccess(JSONObject showHandJson)
    {
        this.showHandJson = showHandJson;
        mCode = ResponseCode.SUCCESS;
    }
    
    public IResponseMessage createNew() {
        return new ShowHandResponse();
    }
}
