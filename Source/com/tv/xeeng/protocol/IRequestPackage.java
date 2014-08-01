package com.tv.xeeng.protocol;

import com.tv.xeeng.base.common.ServerException;

public abstract interface IRequestPackage
{
  public abstract PackageHeader getRequestHeader();

  public abstract IRequestMessage next();

  public abstract int size();

  public abstract void reset();

  public abstract IRequestMessage lastMessage();

  public abstract boolean contains(IRequestMessage paramIRequestMessage);

  public abstract boolean hasNext();

  public abstract void addMessage(IRequestMessage paramIRequestMessage)
    throws ServerException;
}