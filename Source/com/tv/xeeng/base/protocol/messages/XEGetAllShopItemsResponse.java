package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.IResponseMessage;

public class XEGetAllShopItemsResponse extends XEResponseMessage {
	public IResponseMessage createNew() {
		return new XEGetAllShopItemsResponse();
	}
}