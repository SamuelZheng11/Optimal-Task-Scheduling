package sample.common;

//Node of the acyclic Task Dependency Tree
public class TaskDependencyNode {
    public int _duration;
    public TaskDependencyEdge[] _children;
    public TaskDependencyEdge[] _parents;
    public final String _name;

    public TaskDependencyNode(int duration, TaskDependencyEdge[] children, TaskDependencyEdge[] parents, String name){
        _duration = duration;
        _children = children;
        _parents = parents;
        _name = name;
    }

}

