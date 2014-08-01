/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.monitor;

import com.tv.xeeng.base.data.VMGQueue;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 *
 * @author tuanda
 */
public class Schedule {

    protected static SchedulerFactory sf = new StdSchedulerFactory();
    protected static Scheduler sched;
    private static Logger log = LoggerFactory.getLogger(Schedule.class);
    
//     private static String DEAD_CRON_EXPRESSION = "0 0/10 * * * ?";
    private static String GAME_MONITOR_CRON_EXPRESSION = "0/1 * * * * ?"; // mỗi giây
    private static String COMMON_MONITOR_CRON_EXPRESSION = "0/1 * * * * ?"; // mỗi giây
    private static String NUMBERONLINE_MONITOR_CRON_EXPRESSION = "0 0/5 * * * ?"; // mỗi 5 phút
    private static String ONLINE_OVERNIGHT_MONITOR_CRON_EXPRESSION = "5 0 0 * * ?"; // 00:00:05 mỗi ngày

    static {
        try {
            sched = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException ex) {
            ex.printStackTrace();
        }
    }

    public static Scheduler getSchedule() {
        return sched;
    }

    private static void addJob(Class<? extends Job> jobClass, String identity, String cronExpression) throws ParseException, SchedulerException {
        JobDetail job = newJob(jobClass).withIdentity(identity, "VIP").build();
        
        Trigger trigger = newTrigger().withIdentity(identity, "VIP").withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
        sched.scheduleJob(job, trigger);
    }

    public static void makeSchedule() {
        try {
            addJob(GameJob.class, "GAME_MONITOR", GAME_MONITOR_CRON_EXPRESSION);
            addJob(NumberOnlineJob.class, "NUMBER_ONLINE_MONITOR", NUMBERONLINE_MONITOR_CRON_EXPRESSION);
            // addJob(OnlineOvernightJob.class, "ONLINE_OVERNIGHT_MONITOR", ONLINE_OVERNIGHT_MONITOR_CRON_EXPRESSION);
            addJob(CommonMonitorJob.class, "COMMON_MONITOR", COMMON_MONITOR_CRON_EXPRESSION);       
            addJob(VMGQueue.class, "VMG QUEUE", GAME_MONITOR_CRON_EXPRESSION);
            sched.start();
        } catch (ParseException ex) {
            log.error(ex.getMessage(), ex);
        } catch (SchedulerException ex) {
            ex.printStackTrace();
            log.error(ex.getMessage(), ex);
        }
    }
}
