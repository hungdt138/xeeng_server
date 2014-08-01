/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author tuanda
 */
public class LogManager {

    ConcurrentHashMap<String, Logger> logs = new ConcurrentHashMap<String, Logger>();

    public LogManager() {
    }

    public void appendLog(Logger log) {
        logs.put(log.fileName, log);
    }

    public Logger getLogger(String fileName) {
        Logger l = this.logs.get(fileName);
        if (l == null) {
            l = new Logger(fileName, this);
            this.appendLog(l);

        }
        return l;
    }
}
