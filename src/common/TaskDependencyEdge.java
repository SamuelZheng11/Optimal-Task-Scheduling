package sample.common;

//Edge of the acyclic task dependency tree
public class TaskDependencyEdge {
    public TaskDependencyNode _parent;
    public TaskDependencyNode _child;
    public int _communicationDelay;

    public TaskDependencyEdge(TaskDependencyNode parent, TaskDependencyNode child, int communicationDelay){
        _parent = parent;
        _child = child;
        _communicationDelay = communicationDelay;
    }
}
