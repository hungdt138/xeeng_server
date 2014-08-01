package com.tv.xeeng.memcached;

import com.tv.xeeng.base.common.LoggerContext;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.spy.memcached.MemcachedClient;
import org.slf4j.Logger;

class MemcacheClientImpl extends AbstractMemcacheClient {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(MemcacheClientImpl.class);

    private MemcachedClient mClient;
    private String mNameSpace;
//  private String host = "localhost";
    private String host;// = "192.168.50.107";
    private int port = 9501;

    @SuppressWarnings("unused")
    private final int MAX_KEY_LEN = 450;

    public MemcacheClientImpl(String aAddrList, String aNameSpace)
            throws Throwable {
        Properties appConfig = new Properties();
        appConfig.load(new FileInputStream("conf/c3p0.properties"));
        host = appConfig.getProperty("memcachedServer");

        this.mClient = new MemcachedClient(new InetSocketAddress(host, port));
        this.mNameSpace = aNameSpace;
    }

    @Override
    public void set(String aKey, int aExpiration, Object aSerializableObj) {
        if (aSerializableObj != null) {
            try {
                String fullKey = this.mNameSpace + "_" + aKey;

                fullKey.replace("/", "SsS");

                if (fullKey.length() <= MAX_KEY_LEN) {

                    this.mClient.set(fullKey, aExpiration, aSerializableObj);
                }
            } catch (Throwable t) {
                this.mLog.error("[MEMCACHE]", t);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object get(String aKey) {
        Object valObj = null;
        String fullKey = this.mNameSpace + "_" + aKey;

        fullKey.replace("/", "SsS");
        if (fullKey.length() <= MAX_KEY_LEN) {
            Future f = this.mClient.asyncGet(fullKey);
            try {
                valObj = f.get(5L, TimeUnit.SECONDS);
            } catch (Throwable t) {
                this.mLog.warn("[MEMCACHE] get key = " + fullKey + " timeout within 5 seconds.");

                f.cancel(true);
            }
        }

        return valObj;
    }

    public void delete(String aKey) {
        String fullKey;
        try {
            fullKey = this.mNameSpace + "_" + aKey;

            fullKey.replace("/", "SsS");
            this.mClient.delete(fullKey);
        } catch (Throwable t) {
            this.mLog.error("[MEMCACHE]", t);
        }
    }

    public void close() {
        try {
            this.mClient.shutdown();
        } catch (Throwable t) {
            this.mLog.error("[MEMCACHE]", t);
        }
    }
}
