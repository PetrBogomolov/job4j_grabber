package quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static String getSetting(Properties properties, ClassLoader loader,
                                    String fileSittings, String setting) {
        try {
            properties.load(loader.getResourceAsStream(fileSittings));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(setting);
    }

    public static Connection getConnection(Properties properties, ClassLoader loader)
            throws SQLException, ClassNotFoundException {
        Class.forName(
                getSetting(properties, loader, "rabbit.properties", "driver")
        );
        return DriverManager.getConnection(
                getSetting(properties, loader, "rabbit.properties", "url"),
                getSetting(properties, loader, "rabbit.properties", "login"),
                getSetting(properties, loader, "rabbit.properties", "password")
        );
    }

    public static void main(String[] args) {
        ClassLoader loader = AlertRabbit.class.getClassLoader();
        Properties properties = new Properties();
        try (Connection connection = getConnection(properties, loader)) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(
                            Integer.parseInt(
                                    getSetting(properties, loader,
                                            "rabbit.properties", "rabbit.interval")
                            )
                    )
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
    } catch (SchedulerException | SQLException | ClassNotFoundException | InterruptedException throwables) {
            throwables.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO rabbit(created_date) VALUES (?)"
            )) {
                statement.setDate(1, new Date(System.currentTimeMillis()));
                statement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
