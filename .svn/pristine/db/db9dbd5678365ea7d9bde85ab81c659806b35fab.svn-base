package com.tv.xeeng.server;

import com.tv.xeeng.workflow.IWorkflow;

public abstract class AbstractServer
  implements IServer
{
  protected IWorkflow mWorkflow;
  protected int mPort;
  protected int mConnectTimeout;
  protected int mReceiveBufferSize;
  protected boolean mReuseAddress;
  protected boolean mTcpNoDelay;

  public void setWorkflow(IWorkflow aWorkflow)
  {
    this.mWorkflow = aWorkflow;
  }

  public void setServerPort(int aPort)
  {
    this.mPort = aPort;
  }

  public void setConnectTimeout(int aConnectTimeout)
  {
    this.mConnectTimeout = aConnectTimeout;
  }

  public void setReceiveBufferSize(int aReceiveBufferSize)
  {
    this.mReceiveBufferSize = aReceiveBufferSize;
  }

  public void setReuseAddress(boolean aReuseAddress)
  {
    this.mReuseAddress = aReuseAddress;
  }

  public void setTcpNoDelay(boolean aTcpNoDelay)
  {
    this.mTcpNoDelay = aTcpNoDelay;
  }

  public void start()
  {
    startServer();
    this.mWorkflow.serverStarted();
  }

  protected abstract void startServer();

  public void stop()
  {
    this.mWorkflow.serverStoppted();
  }
  
}