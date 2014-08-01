package com.tv.xeeng.base.protocol.messages;

import org.json.JSONObject;

import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.newbacay.data.NewBaCayPlayer;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class LatBaiResponse extends AbstractResponseMessage {

	public String mErrorMsg;
	public long uid;
        public NewBaCayPlayer bcPlayer;
        public int zoneId;
        
        public JSONObject resJson;
        public String value;
        
	public void setUid(long id){
		this.uid = id;
	}
	public void setFailure(int aCode, String aErrorMsg) {
		mCode = aCode;
		mErrorMsg = aErrorMsg;
	}

	public void setSuccess(int aCode) {
		mCode = aCode;
	}

	public IResponseMessage createNew() {
		return new LatBaiResponse();
	}
        
         @Override
         public IResponseMessage clone(ISession session)
         {
              LatBaiResponse resMsg = (LatBaiResponse)createNew();

                resMsg.session = session;
                resMsg.setID(this.getID());
                resMsg.mCode = mCode;
                resMsg.mErrorMsg = mErrorMsg;
                resMsg.uid = uid;
                resMsg.bcPlayer = bcPlayer;
                resMsg.zoneId = zoneId;
                resMsg.resJson = resJson;
                resMsg.value = value;
                return resMsg;
          }
}
