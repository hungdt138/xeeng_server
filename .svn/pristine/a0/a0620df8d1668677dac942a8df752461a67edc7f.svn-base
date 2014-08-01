/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.databaseDriven;


import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PooledDataSource;
import com.tv.xeeng.base.common.LoggerContext;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author tuanda
 */
public class DBPoolConnection {
    private static DataSource ds = getDataSource();
    private static Logger log = LoggerContext.getLoggerFactory()
			.getLogger(DBPoolConnection.class);
    private static long lastChecked = System.currentTimeMillis();
    private static final long PERIOD_CHECK = 3*60*1000; //1000
    
    private static  DataSource getDataSource()
    {
        
            ComboPooledDataSource ds = null;
            try {
                //init pool data source
                Properties appConfig = new Properties();
                
                appConfig.load(new FileInputStream("conf/c3p0.properties"));
                String DB_URL = appConfig.getProperty("jdbcUrl");
                String DB_USERNAME = appConfig.getProperty("user");
                String DB_PASSWORD = appConfig.getProperty("password");
                int MIN_POOL_SIZE = Integer.parseInt(appConfig.getProperty("minPoolSize"));
                int MAX_POOL_SIZE = Integer.parseInt(appConfig.getProperty("maxPoolSize"));
                int ACQUIRE_INCREMENT = Integer.parseInt(appConfig.getProperty("acquireIncrement"));
                int MAX_IDLE_TIME = Integer.parseInt(appConfig.getProperty("maxIdleTime"));

                DataSource unpooled =DataSources.unpooledDataSource(DB_URL, DB_USERNAME, DB_PASSWORD);
                Map<String, Object> overrideProps = new HashMap<String, Object>();

                overrideProps.put("maxPoolSize", MAX_POOL_SIZE);
                overrideProps.put("minPoolSize", MIN_POOL_SIZE);
                overrideProps.put("acquireIncrement", ACQUIRE_INCREMENT);
                overrideProps.put("maxStatements", 180);
                overrideProps.put("maxIdleTime", MAX_IDLE_TIME);

                return DataSources.pooledDataSource(unpooled, overrideProps);
            
            } catch (Exception ex) {
            	 log.error("SQL issue " + ex.getMessage());
            } 
        
        return ds;
    }
    
    public static  Connection getConnection()
    {
        Connection con = null;
        try {
//            if(System.currentTimeMillis() - lastChecked> PERIOD_CHECK)
//            {
                int idleConnection = ((PooledDataSource)ds).getNumIdleConnections();
                //log.debug("[All connection]" +((PooledDataSource)ds).getNumConnectionsAllUsers() );
                //log.debug("[Idle connection]" +idleConnection );
                //log.debug("Unclosed connection " + ((PooledDataSource)ds).getNumUnclosedOrphanedConnections());
    //            System.out.println("idle connection " + idleConnection);
                if(idleConnection == 0)
                {
                    log.debug("idleConnection = 0 => create new DataSource");
//                    DataSources.destroy(ds);
                    ds = getDataSource();
                }
//                lastChecked = System.currentTimeMillis();
//            }
            con = ds.getConnection();
//            System.out.println(con);
        } catch (Exception ex) {
            try {
                log.debug("SQL issue " + ex.getMessage());
                ds = getDataSource();
                con = ds.getConnection();
            } catch (Exception ex1) {
                log.error("SQL issue " + ex.getMessage());
            }
        }
        return con;
    }
}
