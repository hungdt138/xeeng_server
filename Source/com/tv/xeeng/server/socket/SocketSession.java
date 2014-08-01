package com.tv.xeeng.server.socket;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.session.AbstractSession;
import com.tv.xeeng.server.Server;


public class SocketSession extends AbstractSession {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
            SocketSession.class);

    @Override
    protected boolean writeResponse(byte[] aEncodedData)
            throws ServerException {
        try {
        	
            ChannelBuffer responseBuffer = ChannelBuffers.copiedBuffer(aEncodedData);
            Object obj = getProtocolOutput();
                mLog.debug("Channel");
                Channel outChannel = (Channel) obj;

                if ((outChannel != null) && (outChannel.isOpen())) {
                    //synchronized (outChannel) {
                    ChannelFuture future = outChannel.write(responseBuffer);
                    //if (!(isDirect)) {
                    if (!future.isDone()) {
                        //mLog.debug("Write not done!");
                        //future.addListener(ChannelFutureListener.CLOSE);
                    }
                    //}
                }

            return true;
        } catch (Throwable t) {
            throw new ServerException(t);
        }
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    public void close() {
        Channel outChannel = (Channel) getProtocolOutput();
        try {
            if ((outChannel != null)) {
                ChannelFuture closeFuture = outChannel.close();
                closeFuture.addListener(Server.CLOSE);
            }
        } catch (Exception ex) {
            mLog.error(ex.getMessage(), ex);
        }
        super.close();
    }

    void closeOnFlush(Channel ch) {
        if (ch.isConnected()) {
            ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}