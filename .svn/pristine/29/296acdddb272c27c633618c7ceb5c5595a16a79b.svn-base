package com.tv.xeeng.server.socket;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.tv.xeeng.workflow.IWorkflow;

public class SocketPipelineFactory implements ChannelPipelineFactory {

    private IWorkflow mWorkflow;

    public SocketPipelineFactory(IWorkflow aWorkflow) {
        this.mWorkflow = aWorkflow;
    }

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline;
        try {

            pipeline = Channels.pipeline();            
            pipeline.addLast("decoder", new SimpleChannelUpstreamHandler());
            pipeline.addLast("handler", new SocketHandler(this.mWorkflow));

            return pipeline;
        } catch (Throwable t) {
            throw new Exception(t);
        }
    }
}
