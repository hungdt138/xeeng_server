package com.tv.xeeng.base.protocol.messages;
import java.util.ArrayList;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuPlayer;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class SendQuestionResponse extends AbstractResponseMessage {

	public String mErrorMsg;
	public String detailImage; // for DHBC
	public String detail;
	public int best;
	public int point;
	public String[] answer;
	public ArrayList<TrieuPhuPlayer> data;
	public String link = null;//music data
	public int zone;
	
	public void setFailure(String aErrorMsg) {
		mCode = ResponseCode.FAILURE;
		mErrorMsg = aErrorMsg;
	}
	
	public void setSuccess(String d, String[] a, int b, int p,
			ArrayList<TrieuPhuPlayer> da, int z) {
		data = da;
		point = p;
		detail = d;
		best = b;
		answer = a;
                zone = z;
		mCode = ResponseCode.SUCCESS;
	}

	public IResponseMessage createNew() {
		return new SendQuestionResponse();
	}
}
