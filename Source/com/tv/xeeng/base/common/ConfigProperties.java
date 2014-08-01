package com.tv.xeeng.base.common;

public class ConfigProperties
{
  private Properties mProps;

  public void load(String aSource)
    throws ServerException
  {
    this.mProps = new Properties(aSource);
  }

  public String getString(String aKey)
  {
    return this.mProps.getString(aKey);
  }

  public int getInt(String aKey)
  {
    return this.mProps.getInt(aKey);
  }

  public boolean getBoolean(String aKey)
  {
    return this.mProps.getBoolean(aKey);
  }
}