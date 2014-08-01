package com.tv.xeeng.base.common;
import org.slf4j.Logger;

@SuppressWarnings("serial")
public class ServerException extends Throwable
{
    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(ServerException.class);
  public ServerException(String aMsg)
  {
    super(aMsg);
    mLog.error("Server exception : "+aMsg);
  }

  public ServerException(Throwable aCause)
  {
    super(aCause);
    mLog.error("Server exception : "+aCause.getMessage());
  }
}