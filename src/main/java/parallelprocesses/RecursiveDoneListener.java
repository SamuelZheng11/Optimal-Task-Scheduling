package parallelprocesses;

public interface RecursiveDoneListener {

    /**
     * called when the thread is done with the job to notify the listener
     */
    void handleThreadRecursionHasCompleted();

    /**
     * called when the thread has an uncaught exception and notifies the listener
     * @param e the exception object
     */
    void handleThreadException(Exception e);
}
