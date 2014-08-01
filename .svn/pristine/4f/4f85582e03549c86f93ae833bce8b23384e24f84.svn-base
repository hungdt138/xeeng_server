package com.tv.xeeng.server.socket;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;

import com.tv.xeeng.base.bytebuffer.ByteBufferFactory;
import com.tv.xeeng.base.bytebuffer.IByteBuffer;
import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.CancelRequest;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.entity.CacheEntity;
import com.tv.xeeng.entity.IPCacheEntity;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.MessagesID;
import com.tv.xeeng.game.room.Phong;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.game.room.Zone;
import com.tv.xeeng.protocol.IBusiness;
import com.tv.xeeng.protocol.IResponsePackage;
import com.tv.xeeng.protocol.MessageFactory;
import com.tv.xeeng.server.Server;
import com.tv.xeeng.workflow.IWorkflow;


public class SocketHandler extends SimpleChannelUpstreamHandler {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
            SocketHandler.class);
    private IWorkflow mWorkflow;
//    private static final int MAX_LENGTH_PRINT = 150;
    private static final int MAX_RETRY_CONNECTED = 10000;
    private static final int CALCULATE_RETRY_CONNECTED = 600000;
    private static ConcurrentHashMap<String, IPCacheEntity> lstIps = new ConcurrentHashMap<>();

    public SocketHandler(IWorkflow aWorkflow) {
        this.mWorkflow = aWorkflow;
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        ISession session = null;
        String requestString = null;
        byte[] requestBytes = null;
        int packageSize;
        try {
             mLog.debug("[Netty Socket] messageReceived : "+ctx.getName()+" : "+e+" : ");
            session = (ISession) ctx.getAttachment();
            
            // for close wait
            if(session == null)
            {
                ctx.getChannel().close();
            }
            mLog.debug("[Netty Socket] messageReceived: "+session.getUserName()+" : "+session.getIP());
            
            byte[] resData = null;
            ChannelBuffer requestBuffer = (ChannelBuffer) e.getMessage();

            if (requestBuffer.readable()) {

                requestBytes = requestBuffer.array();
                requestString = new String(requestBytes);
                packageSize = requestBytes.length;

                if (requestString.equalsIgnoreCase("<policy-file-request/>" + '\0')) {
                    session.write("<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>" + '\0');
                    return;
                }
                
                int firstByte = requestBytes[0];
                if (firstByte == AIOConstants.FIRST_BYTE_GAME) {
//                    if (packageSize < MAX_LENGTH_PRINT) {
//                        mLog.debug("[Netty Socket] messageReceived: " + requestString);
//                    }

                    // New Protocol
                    byte[] data1 = new byte[packageSize - 1];
                    System.arraycopy(requestBytes, 1, data1, 0, data1.length);                    
                    IByteBuffer dataBuffer = ByteBufferFactory.wrap(data1);
                    IByteBuffer resultBuffer = this.mWorkflow.process(session, dataBuffer);

                    if (resultBuffer != null) {
                        resData = resultBuffer.array();
                    }
                } 
                // System.out.println(requestString);
            }

            if ((!(session.isClosed())) && (resData != null)) {
                boolean isDirect = session.isDirect();

                ChannelBuffer responseBuffer = ChannelBuffers
                        .copiedBuffer(resData);
                //session.write(responseBuffer);
                
                ChannelFuture future = e.getChannel().write(responseBuffer);
                if (!(isDirect)) {
                    future.addListener(ChannelFutureListener.CLOSE);
                }
            }
        } catch (ServerException se) {
//            if (requestBytes != null && requestBytes.length < MAX_LENGTH_PRINT) {
                this.mLog.error("[Netty Socket] Request Process Error + messageReceive " + requestString, se);
//            } 
        }
    }

    private void cancelTable(ISession session) {
        IResponsePackage responsePkg = session.getDirectMessages();
        MessageFactory msgFactory = session.getMessageFactory();
        IBusiness business;
        
        long uid = session.getUID();
        UserDB userDb = new UserDB();
        
        try {
            userDb.logout(session.getUID(), session.getCollectInfo().toString());
        } catch (SQLException ex) {
            this.mLog.error(ex.getMessage(), ex);
        }
        session.setChatRoom(0);

        Zone zone = session.findZone(session.getCurrentZone());

        if (session.getPhongID() != 0) {
            Phong phong = zone.getPhong(session.getPhongID());
            if (phong != null) {
                phong.outPhong(session);
            }
        }

        long matchID = 0; 
        Room room = session.getRoom();
        if (room != null) {
            matchID = room.getRoomId();
        }

        if (matchID < 1) {
            return;
        }

        business = msgFactory.getBusiness(MessagesID.MATCH_CANCEL);
        CancelRequest rqMatchCancel = (CancelRequest) msgFactory
                .getRequestMessage(MessagesID.MATCH_CANCEL);
        rqMatchCancel.uid = uid;
        rqMatchCancel.mMatchId = matchID;
        rqMatchCancel.isLogout = true;
        rqMatchCancel.isSendMe = false;
        
        try {
            business.handleMessage(session, rqMatchCancel, responsePkg);
        } catch (ServerException se) {
            this.mLog.error("[Netty Socket] Exception Catch Error!",
                    se.getCause());
        } catch (Throwable e) {
            this.mLog.error("[Netty Socket] Exception Catch Error!",
                    e.getCause());
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        
    	try {
            ChannelFuture closeFuture = e.getChannel().close();
            closeFuture.addListener(ChannelFutureListener.CLOSE);
            //closeFuture.addListener(Server.CLOSE_ON_FAILURE);
            //closeOnFlush(e.getChannel());
        } catch (Exception ex) {
            ex.printStackTrace();
            this.mLog.error(ex.getMessage(), ex);
        }

    }

    private boolean ipCached(Channel currentChannel) {
        String remoteAdress = currentChannel.getRemoteAddress().toString();
            try {
                String[] ipElement = remoteAdress.split(":");
                String ip = ipElement[0];
                if (lstIps.containsKey(ip)) {
                    IPCacheEntity cacheIP = lstIps.get(ip);
                    int countConnected = cacheIP.getLstInfoConnect().size();
                    if (countConnected > 6) {
                        CacheEntity prev = cacheIP.getLstInfoConnect().get(0);
                        long currentTime = System.currentTimeMillis();
                        int countOpen = 0;
                        for (int i = 1; i < countConnected; i++) {
                            CacheEntity next = cacheIP.getLstInfoConnect().get(i);
                            if (currentTime - next.getDateCreated() > CALCULATE_RETRY_CONNECTED) {
                                continue;
                            }
                            if (next.getDateCreated() - prev.getDateCreated() < MAX_RETRY_CONNECTED) {
                                countOpen++;
                            }
                        }

                        if (countOpen > 3) {
                            this.mLog.warn("Spam connected ip " + ip);
                            //closeOnFlush(currentChannel);
                            ChannelFuture closeFuture = currentChannel.close();
                            closeFuture.addListener(Server.CLOSE);
                            return true;
                        }
                    }

                    CacheEntity newConnection = new CacheEntity();
                    cacheIP.addNewConnect(newConnection);

                } else {
                    IPCacheEntity cacheIp = new IPCacheEntity();
                    CacheEntity cacheEntity = new CacheEntity();
                    cacheIp.addNewConnect(cacheEntity);
                    lstIps.put(ip, cacheIp);
                }

            } catch (Exception ex) {
                mLog.error(" chong spam connect" + ex.getMessage(), ex);
            }
            return false;
    }
    
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        Channel currentChannel;
        try {
            //mLog.debug("ChannelEventID:" + e.);
            currentChannel = e.getChannel();
            this.mLog.debug("[Netty Socket] Channel Connected: " + currentChannel.getRemoteAddress() + ", " + currentChannel.getId());
            
            if(Server.isCachedID) {
                if(ipCached(currentChannel)) {
                    return;
                }
            }

            ISession session = this.mWorkflow.sessionCreated(currentChannel);
            session.setIP(currentChannel.getRemoteAddress().toString());
            ctx.setAttachment(session);
            super.channelConnected(ctx, e);
            
        } catch (ServerException se) {
            this.mLog.error("[Netty Socket] Channel Connected Exception", se);
        } catch (Exception ex) {

            mLog.error("error close wait 1 " + ex.getMessage(), ex);
        }
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx,
            ChannelStateEvent e) throws Exception {
        try {
            ISession session = (ISession) ctx.getAttachment();
            if (session != null) {
                this.mLog.debug("Channel Disconnected: " + session.getID());
                try {
                    if (session.isLoggedIn()) {
                        cancelTable(session);
                    }
                } catch (Exception ex) {
                    try {
                        mLog.error(ex.getMessage(), ex);
                    } catch (Exception ex1) {
                    }
                }
                
                try {
                    session.sessionClosed();
                } catch (Exception ex) {
                    mLog.error(ex.getMessage(), ex);
                }
            }
            super.channelDisconnected(ctx, e);
            
        } catch (Exception ex) {
            mLog.error("error close wait 2" + ex.getMessage(), ex);
        }
    }
 @Override
    public void channelBound(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        try {
            super.channelBound(ctx, e);
        } catch (Exception ex) {
            mLog.error("error close wait 3" + ex.getMessage(), ex);
        }
    }
 
 @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        try {
            super.channelClosed(ctx, e);
        } catch (Exception ex) {
            mLog.error("error close wait 4" + ex.getMessage(), ex);
        }

    }
 
 @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        try {
            super.channelOpen(ctx, e);
        } catch (Exception ex) {
            mLog.error("error close wait 5" + ex.getMessage(), ex);
        }
    }
 
 @Override
    public void channelUnbound(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        try {
            super.channelUnbound(ctx, e);
        } catch (Exception ex) {
            mLog.error("error close wait 6" + ex.getMessage(), ex);
        }
    }
 
// void closeOnFlush(Channel ch) {
//        if (ch.isConnected()) {
//            ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//        }
//    }
}
