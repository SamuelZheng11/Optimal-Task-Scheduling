package sample.common;

import java.util.ArrayList;
import java.util.List;

//Node of the acyclic Task Dependency Tree
public class TaskDependencyNode {
    public int _duration;
    public List<TaskDependencyEdge> _children;
    public List<TaskDependencyEdge> _parents;
    public final String _name;

    public TaskDependencyNode(int duration, List<TaskDependencyEdge> children, List<TaskDependencyEdge> parents, String name){
        _duration = duration;
        _children = children;
        _parents = parents;
        _name = name;
    }

    public TaskDependencyNode(int duration, String name){
        _duration = duration;
        _name = name;
        _parents = new ArrayList<TaskDependencyEdge>();
        _children = new ArrayList<TaskDependencyEdge>();
    }

    @Override
    public String toString() {
        return "Node: " + _name + " with " + _children.size() + " outgoing edges";
    }

    @Override
    public boolean equals(Object obj){
        if(obj == this){
            return true;
        }
        if(!(obj instanceof TaskDependencyNode)){
            return false;
        }
        TaskDependencyNode tdn = (TaskDependencyNode) obj;
        return (this._name.equals(tdn._name));
    }

}

