package com.tv.xeeng.server;

import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.monitor.Schedule;
import com.tv.xeeng.game.chat.data.ChatRoomZone;
import com.tv.xeeng.workflow.SimpleWorkflow;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import java.util.LinkedHashMap;

/**
 *
 * @author tuanda
 */
public class Server {

    /**
     * thanhnvt
     */
    public static LinkedHashMap<Long, Boolean> userOnlineList = new LinkedHashMap<Long, Boolean>();
    
    public static double REAL_GOT_MONEY;
    
    public static int MONEY_TRANSFER_DAY_LIMIT;
    
    public static int MONEY_TRANSFER_TAX;
    
    private static SimpleWorkflow worker;
    
    public static String numberOnline = "";
    
    public static boolean isCachedID = true;
    
    public static ChannelFutureListener CLOSE = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
            future.getChannel().close();
        }
    };
    
    public static ChannelFutureListener NONE = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
        }
    };
        
    public static ChannelFutureListener CLOSE_ON_FAILURE = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
            if (!future.isSuccess()) {
                future.getChannel().close();
            }
        }
    };

    public static void main(String[] args) throws ServerException {
        try {
            worker = new SimpleWorkflow();
            worker.start();
        } catch (ServerException ex) {
            ex.printStackTrace();
        }        
        
        Schedule.makeSchedule();
    }

    public static void changeCachedIP() {
        isCachedID = (isCachedID ? false : true);
    }

    public static SimpleWorkflow getWorker() {
        return worker;
    }

    public static ChatRoomZone getChatRoomZone() {
        return worker.getChatZone();
    }
}
