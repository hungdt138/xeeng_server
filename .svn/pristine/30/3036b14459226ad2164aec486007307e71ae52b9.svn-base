package com.tv.xeeng.protocol;

import com.tv.xeeng.base.common.ServerException;

public abstract interface IMessageProtocol
{
  public abstract boolean decode(Object paramObject, IRequestMessage paramIRequestMessage)
    throws ServerException;

  public abstract Object encode(IResponseMessage paramIResponseMessage)
    throws ServerException;
}