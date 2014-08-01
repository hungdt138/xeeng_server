package com.tv.xeeng.base.business;

import com.tv.xeeng.protocol.BusinessProperties;
import com.tv.xeeng.protocol.IBusinessPropertiesFactory;

public class BacayBusinessPropertiesFactory implements IBusinessPropertiesFactory{
	@Override
	public BusinessProperties createBusinessProperties() {
		
		return new BacayBusinessProperties();
	}
}
