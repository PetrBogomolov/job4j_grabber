package agregator;

import agregator.interfaces.grab.Grabber;
import agregator.interfaces.parse.SqlRuParse;
import org.quartz.SchedulerException;

public class Run {
    public static void main(String[] args) throws SchedulerException {
        Grabber grab = new Grabber();
        grab.setConfig("agregator.properties");
        grab.init(new SqlRuParse(), grab.getStore(), grab.getScheduler());
    }
}
