package gui.listeners;

/**
 * interface to be implemented by anything that need to be notified that the model (stats or chart) has changed
 */
public interface ModelChangeListener {
    public void update();
}
