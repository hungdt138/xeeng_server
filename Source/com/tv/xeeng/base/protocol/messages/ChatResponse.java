package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class ChatResponse extends AbstractResponseMessage {

    public String mErrorMsg;
   // public boolean  mUid;
    public String mMessage;
      public String mUsername;
        public long roomid;
        public int phong= 0;
        
   public long uid;     
   public int type;
    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess(int aCode) {
        mCode = aCode;
   //     mUid = aUid;
    }

    public void setMessage(String aMessage) {
        mMessage = aMessage;
    }
    public void setUsername(String aUsername) {
        mUsername = aUsername;
    }

     public void setRoomID(long aroomid) {
        roomid = aroomid;
    }
     public void setType(int aType) {
        type = aType;
    }


    
    public IResponseMessage createNew() {
        return new ChatResponse();
    }
    
    @Override
     public IResponseMessage clone(ISession session)
     {
          ChatResponse resMsg = (ChatResponse)createNew();

          resMsg.session = session;
          resMsg.setID(this.getID());
          resMsg.mCode = mCode;
          resMsg.mErrorMsg = mErrorMsg;

          resMsg.mMessage = mMessage;
          resMsg.mUsername = mUsername;
          resMsg.roomid = roomid;
          resMsg.phong= phong;
          resMsg.type = type;
          resMsg.uid = uid;

        return resMsg;
     }
    
}
