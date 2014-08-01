package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.protocol.IResponseMessage;

public class XENewResponse extends XEResponseMessage {
	private long matchID;
	private long moneyBet;
	private long ownerCash;
	public long getMatchID() {
		return matchID;
	}
	public void setMatchID(long matchID) {
		this.matchID = matchID;
	}
	public long getMoneyBet() {
		return moneyBet;
	}
	public void setMoneyBet(long moneyBet) {
		this.moneyBet = moneyBet;
	}
	public long getOwnerCash() {
		return ownerCash;
	}
	public void setOwnerCash(long ownerCash) {
		this.ownerCash = ownerCash;
	}
	@Override
	public IResponseMessage createNew() {
		return new XENewResponse();
	}
	public String getEncodedItems() {
		StringBuilder sb = new StringBuilder();
		sb.append(matchID).append(AIOConstants.SEPERATOR_BYTE_1);
		sb.append(moneyBet).append(AIOConstants.SEPERATOR_BYTE_1);
		sb.append(ownerCash);
		return sb.toString();
	}
}
