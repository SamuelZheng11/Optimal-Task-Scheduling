package sample.common;

public class Delay implements Job {

    private final int _duration;

    public Delay(int duration){
        _duration = duration;
    }

    public int getDuration() {
        return _duration;
    }
}
