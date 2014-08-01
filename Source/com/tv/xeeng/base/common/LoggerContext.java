package com.tv.xeeng.base.common;

public class LoggerContext
{
  static ILoggerFactory mLoggerFactory;

  public static ILoggerFactory getLoggerFactory()
  {
    if (mLoggerFactory == null)
    {
      new SimpleLoggerFactory();
    }

    return mLoggerFactory;
  }
}