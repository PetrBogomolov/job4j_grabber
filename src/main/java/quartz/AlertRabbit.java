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
    private final Properties properties = new Properties();

    public Properties getProperties() {
        return properties;
    }

    public void getSetting(String fileSittings) {
        ClassLoader loader = AlertRabbit.class.getClassLoader();
        try {
            properties.load(loader.getResourceAsStream(fileSittings));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(properties.getProperty("driver"));
        return DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("login"),
                properties.getProperty("password")
        );
    }

    public static void main(String[] args) {
        AlertRabbit alertRabbit = new AlertRabbit();
        alertRabbit.getSetting("rabbit.properties");
        try (Connection connection = alertRabbit.getConnection()) {
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
                                  alertRabbit.getProperties().getProperty("rabbit.interval")
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
    } catch (SchedulerException | SQLException
                | ClassNotFoundException | InterruptedException troubles) {
            troubles.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            Connection connection =
                    (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO rabbit(created_date) VALUES (?)"
            )) {
                statement.setDate(1, new Date(System.currentTimeMillis()));
                statement.execute();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
