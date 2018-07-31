package common;

public final class TaskJob implements Job {

    private final int _duration;
    private final String _name;
//  reference to the task in the dependency tree for looking up parents
    private final TaskDependencyNode _node;

    public TaskJob(int duration, String name, TaskDependencyNode node){
        _duration = duration;
        _name = name;
        _node = node;
    }

    public TaskDependencyNode getNode() {
        return _node;
    }

    public int getDuration() {
        return _duration;
    }

    public String getName(){
        return _name;
    }
}
