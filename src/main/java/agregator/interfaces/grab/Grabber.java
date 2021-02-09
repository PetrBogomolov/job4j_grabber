package agregator.interfaces.grab;

import agregator.interfaces.parse.Parse;
import agregator.interfaces.store.PsqlStore;
import agregator.interfaces.store.Store;
import agregator.model.Post;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import quartz.AlertRabbit;
import java.io.IOException;
import java.util.Properties;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {

    private final Properties properties = new Properties();

    public Store getStore() {
        return new PsqlStore();
    }

    public Scheduler getScheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void setConfig(String fileSittings) {
        ClassLoader loader = AlertRabbit.class.getClassLoader();
        try {
            properties.load(loader.getResourceAsStream(fileSittings));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(properties.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            for (int numberOfPage = 1; numberOfPage <= 5; numberOfPage++) {
                String url = String.format("https://www.sql.ru/forum/job-offers/%d", numberOfPage);
                for (Post post : parse.list(url)) {
                    try {
                        store.save(post);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
