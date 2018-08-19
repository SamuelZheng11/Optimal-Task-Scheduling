package parallelprocesses;

import java.util.EventListener;

/**
 * to be implemented by anything that needs to know about things that happen to the pilot BFS search
 */
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
