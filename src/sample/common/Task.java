package sample.common;

public final class Task implements Job {

    private final int _duration;
    private final String _name;

    public Task(int duration, String name){
        _duration = duration;
        _name = name;
    }

    public int getDuration() {
        return _duration;
    }

    public String getName(){
        return _name;
    }
}
