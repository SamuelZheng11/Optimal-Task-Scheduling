package gui.listeners;

import common.State;

/**
 * Interface to be implemented by anything that needs to be notified when the Recursion store changes (the recursion
 * store is the main point of interest when it comes to the algorithm producing new valuable information )
 */
public interface AlgorithmListener {
    public void update(State state);
}
