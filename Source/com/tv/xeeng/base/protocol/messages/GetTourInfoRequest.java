package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GetTourInfoRequest extends AbstractRequestMessage {
	public int tourID;

	public IRequestMessage createNew() {
		return new GetTourInfoRequest();
	}
}