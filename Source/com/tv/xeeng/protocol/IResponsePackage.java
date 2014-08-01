package com.tv.xeeng.protocol;

import java.util.Vector;

import com.tv.xeeng.base.session.ISession;

public abstract interface IResponsePackage
{
  public abstract PackageHeader getResponseHeader();

  public abstract void addMessage(IResponseMessage paramIResponseMessage);

  public abstract void addPackage(IResponsePackage paramIResponsePackage);

  public abstract void prepareEncode(ISession paramISession);

  public abstract Vector<IResponseMessage> optAllMessages();
}