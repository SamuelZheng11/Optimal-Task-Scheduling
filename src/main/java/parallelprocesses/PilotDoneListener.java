package parallelprocesses;

import java.util.EventListener;

public interface PilotDoneListener extends EventListener {

    /**
     * method that is called when the pilot run has completed
     */
    void handlePilotRunHasCompleted();

    /**
     * called when the thread has an uncaught exception and notifies the listener
     * @param e the exception object
     */
    void handleThreadException(Exception e);
}
