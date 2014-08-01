/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.memcached.data;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.memcached.IMemcacheClient;
import com.tv.xeeng.memcached.MemcacheClientPool;
import org.slf4j.Logger;

/**
 *
 * @author thanhnvt
 */
public class XEGlobalCache extends CacheUserInfo {

    private static final Logger logger = LoggerContext.getLoggerFactory().getLogger(XEGlobalCache.class);

    public final static int TIMEOUT_2_MIN = 2 * 60;
    public final static int TIMEOUT_5_MIN = 5 * 60;
    public final static int TIMEOUT_10_MIN = 10 * 60;
    public final static int TIMEOUT_15_MIN = 15 * 60;
    public final static int TIMEOUT_20_MIN = 20 * 60;
    public final static int TIMEOUT_25_MIN = 25 * 60;
    public final static int TIMEOUT_30_MIN = 30 * 60;

    private final static String namespace = "Global";

    public static void setCache(String name, Object value, int timeout) {
        logger.debug("[+] Set cache: " + name);

        if (!name.contains("_")) {
            name = namespace + "_" + name;
        }

        MemcacheClientPool pool = CacheUserInfo.getCachedPool();
        IMemcacheClient client = pool.borrowClient();

        if (value != null) {
            client.set(name, timeout, value);
        }

        pool.returnClient(client);
    }

    public static Object getCache(String name) {
        logger.debug("[+] Get cache: " + name);

        if (!name.contains("_")) {
            name = namespace + "_" + name;
        }

        MemcacheClientPool pool = CacheUserInfo.getCachedPool();
        IMemcacheClient client = pool.borrowClient();

        Object value = client.get(name);

        pool.returnClient(client);

        return value;
    }

    public static void deleteCacheUserInventory(String name) {
        logger.debug("[+] Delete cache: " + name);

        if (!name.contains("_")) {
            name = namespace + "_" + name;
        }
        
        MemcacheClientPool pool = CacheUserInfo.getCachedPool();
        IMemcacheClient client = pool.borrowClient();

        client.delete(name);

        pool.returnClient(client);
    }
}
