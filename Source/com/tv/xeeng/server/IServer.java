package com.tv.xeeng.server;

import com.tv.xeeng.base.session.ISessionFactory;
import com.tv.xeeng.workflow.IWorkflow;

public abstract interface IServer

{
  public abstract void setWorkflow(IWorkflow paramIWorkflow);

  public abstract void setServerPort(int paramInt);

  public abstract void setConnectTimeout(int paramInt);

  public abstract void setReceiveBufferSize(int paramInt);

  public abstract void setReuseAddress(boolean paramBoolean);

  public abstract void setTcpNoDelay(boolean paramBoolean);

  public abstract void start();

  public abstract void stop();

  public abstract ISessionFactory getSessionFactory();
  
}