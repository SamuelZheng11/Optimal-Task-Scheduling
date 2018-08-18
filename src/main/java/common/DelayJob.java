package common;

public class DelayJob implements Job {

    private final int _duration;

    public DelayJob(int duration){
        _duration = duration;
    }

    public int getDuration() {
        return _duration;
    }

    @Override
    public String toString(){
        return "Delay";
    }
}
