package com.tv.xeeng.server.socket;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.session.ISessionFactory;
import com.tv.xeeng.server.AbstractServer;


public class SocketServer extends AbstractServer
{
  @SuppressWarnings("unused")
  private final Logger mLog;
  private ISessionFactory mSessionFactory;

  public SocketServer()
  {
    this.mLog = LoggerContext.getLoggerFactory().getLogger(SocketServer.class);
  }
@Override
  protected void startServer()
  {
    ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

    bootstrap.setPipelineFactory(new SocketPipelineFactory(this.mWorkflow));
    
    // HeartbeatHandler heartbeatHandler = new HeartbeatHandler();
    bootstrap.setOption("child.tcpNoDelay", Boolean.valueOf(this.mTcpNoDelay));
    bootstrap.setOption("child.receiveBufferSize", Integer.valueOf(this.mReceiveBufferSize));
    bootstrap.setOption("child.sendBufferSize", Integer.valueOf(this.mReceiveBufferSize));
    
    // add for close_wait
    bootstrap.setOption("child.keepAlive", true);
    
    bootstrap.setOption("connectTimeoutMillis", Integer.valueOf(this.mConnectTimeout));
    bootstrap.setOption("reuseAddress", Boolean.valueOf(this.mReuseAddress));
    bootstrap.setOption("use-nio", true);
    bootstrap.setOption("backlog", 1000);
    
    //bootstrap.setOption("writeBufferHighWaterMark", 4096);
    //bootstrap.setOption("writeBufferLowWaterMark", 1024);

    Channel serverChannel = bootstrap.bind(new InetSocketAddress(this.mPort));
    ChannelFuture closeFuture = serverChannel.getCloseFuture();
    closeFuture.addListener(new CloseFutureListener(this, this));
    
  }

  @Override
  public ISessionFactory getSessionFactory()
  {
    if (this.mSessionFactory == null)
    {
      this.mSessionFactory = new SocketSessionFactory();
    }
    return this.mSessionFactory;
  }

  class CloseFutureListener implements ChannelFutureListener
  {
    private AbstractServer mServer;

    public CloseFutureListener(AbstractServer aServer, AbstractServer paramAbstractServer)
    {
      this.mServer = aServer;
    }

    public void operationComplete() throws Exception
    {
      try
      {
        this.mServer.stop();
      }
      catch (Throwable t) {
        //NettySocketServer.access$000(this.this$0).error("[SERVER STOP]", t);
      }
    }
    @Override
    public void operationComplete(ChannelFuture chanel)
    {
      try
      {
        this.mServer.stop();
      }
      catch (Throwable t) {
        //NettySocketServer.access$000(this.this$0).error("[SERVER STOP]", t);
      }
    }
  }
}