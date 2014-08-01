package com.tv.xeeng.db;

public class DBException extends Throwable
{
  private static final long serialVersionUID = -1115911859L;

  public DBException(String aMsg)
  {
    super(aMsg);
  }

  public DBException(Throwable aT)
  {
    super(aT);
  }
}