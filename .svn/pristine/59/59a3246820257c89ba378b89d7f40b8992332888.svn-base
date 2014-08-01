/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.logger;

import java.io.IOException;

/**
 *
 * @author tuanda
 */
public class Logger {

    
    public String fileName;
    public LogWriter writer;
    public LogManager manage;
    public void setManager(LogManager m){
        manage = m;
                
    }
    public Logger(String file, LogManager m) {
        this.fileName = file;
        manage = m;
        writer = new LogWriter(fileName);
    }

    public void error(String mess) {
        try {
            writer.write(mess, "ERROR");
        } catch (IOException ex) {
        }
    }

    public void warn(String mess) {
        try {
            writer.write(mess, "WARNING");
        } catch (IOException ex) {
        }
    }
    
    public void debug(String mess) {
        try {
            writer.write(mess, "DEBUG");
        } catch (IOException ex) {
        }
    }
    
    public void info(String mess) {
        try {
            writer.write(mess, "INFO");
        } catch (IOException ex) {
        }
    }
}
