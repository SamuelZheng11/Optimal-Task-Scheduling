package common;

public interface GreedySearchListener {

    /**
     * method that is called when the greedy search run has completed
     */
    void handleGreedySearchHasCompleted(State greedyState);

    /**
     * called when the thread has an uncaught exception and notifies the listener
     * @param e the exception object
     */
    void handleThreadException(Exception e);
}
