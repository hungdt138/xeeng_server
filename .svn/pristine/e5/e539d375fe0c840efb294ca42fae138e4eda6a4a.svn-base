package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class XENewRequest extends AbstractRequestMessage {
	public int zoneID;
	public int levelID;
	public long moneyBet;
	public int maxPlayers;
	public String tableName;
	@Override
	public IRequestMessage createNew() {
		return new XENewRequest();
	}
}
