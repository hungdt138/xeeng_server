package com.tv.xeeng.memcached;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;

public class MemcacheClientPool
{
  private final Logger mLog;
  private final List<IMemcacheClient> mClientList;
  
  @SuppressWarnings("unused")
  private final int MIN_CLIENTS = 30;
  
  @SuppressWarnings("unused")
  private final int MAX_CLIENTS = 40;
  
  private String MEMCACHE_SERVER;
  private String NAMESPACE;
  
  private boolean mStarted;
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public MemcacheClientPool()
  {
    this.mLog = LoggerContext.getLoggerFactory().getLogger(MemcacheClientPool.class);
    this.mClientList = Collections.synchronizedList(new LinkedList());
    this.mStarted = false;
  }

  public void start(String aServerList, String aNameSpace) {
    this.MEMCACHE_SERVER = aServerList;
    this.NAMESPACE = aNameSpace;
    
    synchronized (this.mClientList)
    {
      for (int i = 0; i < MIN_CLIENTS; ++i)
      {
        IMemcacheClient client = makeAClient();
        if (client != null)
        {
          this.mClientList.add(client);
        }
      }

      this.mStarted = true;
    }
    this.mLog.info("Memcache is already started.");
  }

  public void shutdown()
  {
    synchronized (this.mClientList)
    {
      if (this.mStarted)
      {
        while (!(this.mClientList.isEmpty()))
        {
          IMemcacheClient client = (IMemcacheClient)this.mClientList.remove(0);
          if (client != null)
          {
            client.close();
          }
        }

        this.mStarted = false;
      }
    }
  }

  private IMemcacheClient makeAClient()
  {
    try
    {
      return new MemcacheClientImpl(this.MEMCACHE_SERVER, this.NAMESPACE);
    }
    catch (Throwable t) {
      this.mLog.error("[MEMCACHE]", t);
      return null;
    }
  }

  public IMemcacheClient borrowClient()
  {
//    synchronized (this.mClientList)
//    {
      if (this.mStarted)
      {
        IMemcacheClient client = null;
        if (this.mClientList.isEmpty())
        {
          client = makeAClient();
        }
        else
          client = (IMemcacheClient)this.mClientList.remove(0);        
          return client;
      }

      return null;
//    }
  }

  public void returnClient(IMemcacheClient aClient)
  {
    if ((aClient != null) && (this.mStarted))
    {
//      synchronized (this.mClientList)
//      {
        if (this.mClientList.size() >= MAX_CLIENTS)
        {
          mLog.warn("cache pool >= max client");
          aClient.close();
        }
        else
        {
          this.mClientList.add(aClient);
        }
//      }
    }
    else if (aClient != null)
    {
      aClient.close();
    }
  }
}