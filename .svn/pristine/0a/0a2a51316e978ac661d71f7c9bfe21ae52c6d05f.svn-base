package com.tv.xeeng.game.tournement;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.ParseException;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Schedule {
	protected static SchedulerFactory sf = new StdSchedulerFactory();
	protected static Scheduler sched;
	private static Logger log = LoggerFactory.getLogger(Schedule.class);
	private static String GAME_MONITOR_CRON_EXPRESSION = "0/1 * * * * ?";
	//private static String COMMON_MONITOR_CRON_EXPRESSION = "0/10 * * * * ?";

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

/*	private static void addJob(Class<? extends Job> jobClass, String identity,
			String cronExpression) throws ParseException, SchedulerException {
		JobDetail job = newJob(jobClass).withIdentity(identity, "VIP").build();

		Trigger trigger = newTrigger().withIdentity(identity, "VIP")
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();

		sched.scheduleJob(job, trigger);

	}

	public static void makeSchedule() {
		try {
			addJob(TourManager.class, "TOUR_MONITOR",
					GAME_MONITOR_CRON_EXPRESSION);
			sched.start();
		} catch (ParseException ex) {
			log.error(ex.getMessage(), ex);
		} catch (SchedulerException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage(), ex);
		}

	}*/

}
