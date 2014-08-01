package com.tv.xeeng.base.protocol.messages;

import java.util.ArrayList;

import com.tv.xeeng.game.data.Message;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class ReceiveMessageResponse extends AbstractResponseMessage {

    public String errMgs;
    public ArrayList<Message> mess = new ArrayList<Message>();
    public String value; //for  mxh
    
    public IResponseMessage createNew() {
        return new ReceiveMessageResponse();
    }

    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        errMgs = aErrorMsg;
    }
    public void setSuccess(int aCode, ArrayList<Message> me) {
        mCode = aCode;
        this.mess = me;
    }
}
