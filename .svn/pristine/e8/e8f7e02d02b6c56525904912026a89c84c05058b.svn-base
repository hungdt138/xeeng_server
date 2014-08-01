package com.tv.xeeng.base.protocol.messages;



import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class HaPhomResponse extends AbstractResponseMessage {
	
	public String message;
	//public ArrayList<ArrayList<Integer>> cards;
	public int u;
	public int card;
    public String cards;
    public long uid;
//    public void setSuccess(int aCode, ArrayList<ArrayList<Integer>> cas, int U, int card)
//    {
//        mCode = aCode;
//        cards = cas;
//        u = U;
//        this.card = card;
//    }
    public void setSuccess(int aCode, int U, int card)
    {
        mCode = aCode;
        //cards = cas;
        u = U;
        this.card = card;
    }
    public void setFailure(int aCode, String msg){
    	mCode = aCode;
    	message = msg;
    }
    public IResponseMessage createNew()
    {
        return new HaPhomResponse();
    }
    
    @Override
    public IResponseMessage clone(ISession session)
      {
          HaPhomResponse resMsg = (HaPhomResponse)createNew();
            resMsg.message = message;
            resMsg.session = session;
            resMsg.setID(this.getID());
            resMsg.mCode = mCode;
            resMsg.u = u;
            resMsg.card = card;
            resMsg.cards = cards;
            resMsg.uid = uid;
            

           return resMsg;
      }
    
}
