package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class XESearchTableByMatchIDRequest extends AbstractRequestMessage {
	private int matchID;
	@Override
	public IRequestMessage createNew() {
		// TODO Auto-generated method stub
		return new XESearchTableByMatchIDRequest();
	}
	public int getMatchID() {
		return matchID;
	}
	public void setMatchID(int matchID) {
		this.matchID = matchID;
	}

}
