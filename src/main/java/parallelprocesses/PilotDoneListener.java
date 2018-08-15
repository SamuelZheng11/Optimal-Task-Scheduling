package parallelprocesses;

import java.util.EventListener;

public interface PilotDoneListener extends EventListener {

    /**
     * method that is called when the pilot run has completed
     */
    void handlePilotRunHasCompleted();
}
