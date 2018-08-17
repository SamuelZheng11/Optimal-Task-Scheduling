package parallelprocesses;

import common.State;
import common.TaskDependencyNode;

import java.util.List;

public class StateTreeBranch {
    private State stateSnapshot;
    private List<TaskDependencyNode> freeNodes;
    private int tasksScheduled;

    public StateTreeBranch(State snapshot, List<TaskDependencyNode> freeNodes, int tasksScheduled){
        this.stateSnapshot = snapshot;
        this.freeNodes = freeNodes;
        this.tasksScheduled = tasksScheduled;
    }

    public List<TaskDependencyNode> getFreeNodes(){
        return this.freeNodes;
    }

    public State getStateSnapshot(){
        return this.stateSnapshot;
    }

    public int getNumTaskScheduled(){
        return this.tasksScheduled;
    }
}
