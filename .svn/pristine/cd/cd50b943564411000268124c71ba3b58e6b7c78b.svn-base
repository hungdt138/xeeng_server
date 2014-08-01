package com.tv.xeeng.protocol;

import java.util.concurrent.ConcurrentHashMap;

public class BusinessProperties
{
  private ConcurrentHashMap<String, Object> mAttrs = null;
  
  @SuppressWarnings("unused")
  private final String SESSION_USER_ID = "session.user.id";
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public BusinessProperties()
  {
    this.mAttrs = new ConcurrentHashMap();
  }

  public void setAttribute(String aKey, Object aValue)
  {
    if (aValue == null)
    {
      this.mAttrs.remove(aKey);
    }
    else
      this.mAttrs.put(aKey, aValue);
  }

  public Object getAttribute(String aKey)
  {
    Object value = this.mAttrs.get(aKey);
    return value;
  }

  public void setID(Long aId)
  {
    setAttribute("session.user.id", aId);
  }

  public long getID()
  {
    Long userId = (Long)getAttribute("session.user.id");
    if (userId == null)
    {
      userId = Long.valueOf(0L);
    }

    return userId.longValue();
  }

  public void freeResources()
  {
    this.mAttrs.clear();
  }
}