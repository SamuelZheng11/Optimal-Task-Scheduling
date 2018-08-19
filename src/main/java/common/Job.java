package common;

/**
 * Interface that is to be implemented by anything that is considered to be a job on the processors (in this case
 * it would be either a delay job or a task job)
 */
public interface Job {

    public abstract int getDuration();
}
