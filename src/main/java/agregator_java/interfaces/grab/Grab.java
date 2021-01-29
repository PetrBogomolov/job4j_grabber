package agregator_java.interfaces.grab;

import agregator_java.interfaces.parse.Parse;
import agregator_java.interfaces.store.Store;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public interface Grab {

    /*
     *  запускает приложение с заданной периодичностью
     */
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
