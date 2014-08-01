package com.tv.xeeng.base.protocol.messages;


import java.util.ArrayList;

import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.phom.data.PhomPlayer;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class ReadyResponse extends AbstractResponseMessage {
	public String mErrorMsg;
        public long mUid;
        public boolean ready;
        public int zone;
        public ArrayList<PhomPlayer> mWaitingPlayerPhom;
        public ArrayList<PhomPlayer> mPlayerPhom;
        
    public void setSuccess(int aCode, long aUid,boolean r)
    {
        mCode = aCode;
        mUid = aUid;
        ready = r;
    }


    public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew()
    {
        return new ReadyResponse();
    }
    
    public ReadyResponse clone(ISession session)
      {
          ReadyResponse resMsg = (ReadyResponse)createNew();
            resMsg.mUid = mUid;
            resMsg.session = session;
            resMsg.setID(this.getID());
            resMsg.mCode = mCode;
            resMsg.ready = ready;
            resMsg.zone = zone;
            
            

           return resMsg;
      }
}
