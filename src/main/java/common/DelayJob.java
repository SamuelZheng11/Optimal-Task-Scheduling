package common;

/**
 * This class is used to represent a wait time on a processors as a parents is processed
 */
public class DelayJob implements Job {

    private final int _duration;

    /**
     * constructor for generating a wait job object
     * @param duration: the amount of time the wait job takes
     */
    public DelayJob(int duration){
        _duration = duration;
    }

    public int getDuration() {
        return _duration;
    }

    /**
     * Override to string method to identify what types of Job this is as the Delay Job implemnts the Job interface
     * @return String
     */
    @Override
    public String toString(){
        return "D";
    }
}
