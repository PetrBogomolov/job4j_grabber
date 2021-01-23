package quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static String getSetting(ClassLoader loader, String fileSittings, String setting) {
        Properties properties = new Properties();
        try {
            properties.load(loader.getResourceAsStream(fileSittings));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(setting);
    }

    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            ClassLoader loader = AlertRabbit.class.getClassLoader();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(
                            Integer.parseInt(
                                    getSetting(loader, "rabbit.properties","rabbit.interval")
                            )
                    )
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }
}
