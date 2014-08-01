package com.tv.xeeng.workflow;

import com.tv.xeeng.base.bytebuffer.IByteBuffer;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.protocol.BusinessProperties;


public abstract interface IWorkflow
{
  public abstract void start()
    throws ServerException;

  public abstract IByteBuffer process(ISession paramISession, IByteBuffer paramIByteBuffer)
    throws ServerException;
  
  public abstract WorkflowConfig getWorkflowConfig();

  public abstract ISession sessionCreated(Object paramObject)
    throws ServerException;

  public abstract BusinessProperties createBusinessProperties();

  public abstract void serverStarted();

  public abstract void serverStoppted();

}