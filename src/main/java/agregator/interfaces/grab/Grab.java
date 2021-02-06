package agregator.interfaces.grab;

import agregator.interfaces.parse.Parse;
import agregator.interfaces.store.Store;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public interface Grab {

    /*
     *  запускает приложение с заданной периодичностью
     */
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
