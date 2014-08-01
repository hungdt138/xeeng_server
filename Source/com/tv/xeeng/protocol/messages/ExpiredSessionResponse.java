package com.tv.xeeng.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class ExpiredSessionResponse extends AbstractResponseMessage
{
  public String mErrorMsg;

  public IResponseMessage createNew()
  {
    return new ExpiredSessionResponse();
  }
}