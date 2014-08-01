package com.tv.xeeng.base.protocol.messages;


import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class GuiPhomResponse extends AbstractResponseMessage {
	
	public String message;
	public long dUID;
	public long sUID;
	public int phomID;
	//public ArrayList<Integer> cards;
        public String cards;
    public void setSuccess(int aCode, long duid, long suid, int phom)
    {
        mCode = aCode;
        //cards = cas;
        dUID = duid;
        sUID = suid;
        phomID = phom;
    }
    public void setFailure(int aCode, String msg){
    	mCode = aCode;
    	message = msg;
    }
    public IResponseMessage createNew()
    {
        return new GuiPhomResponse();
    }
    
    public IResponseMessage clone(ISession session)
      {
          GuiPhomResponse resMsg = (GuiPhomResponse)createNew();
            resMsg.message = message;
            resMsg.session = session;
            resMsg.setID(this.getID());
            resMsg.mCode = mCode;
            resMsg.dUID = dUID;
            resMsg.sUID = sUID;
            resMsg.phomID = phomID;
            resMsg.cards = cards;
            

           return resMsg;
      }
    
}
