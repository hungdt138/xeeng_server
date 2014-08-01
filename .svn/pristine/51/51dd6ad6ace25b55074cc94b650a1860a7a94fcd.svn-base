package com.tv.xeeng.protocol;

import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.session.ISession;


public abstract interface IBusiness
{
  public static final int PROCESS_UNSUPPORTED = 0;
  public static final int PROCESS_OK = -1;
  public static final int PROCESS_FAILURE = -2;
  public static final int PROCESS_IGNORE = -3;
  public static final int PROCESS_ABORT = -4;

  public abstract int handleMessage(ISession paramISession, IRequestMessage paramIRequestMessage, IResponsePackage paramIResponsePackage)
    throws ServerException;
}