package parallelprocesses;

import common.State;
import common.TaskDependencyNode;

import java.util.List;

public class StateTreeBranch implements Comparable<StateTreeBranch>{
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

    @Override
    public int compareTo(StateTreeBranch stateTreeBranch) {
        if(this.stateSnapshot.getHeuristicValue() == stateTreeBranch.stateSnapshot.getHeuristicValue()){
            return 0;
        } else if(this.stateSnapshot.getHeuristicValue() > stateTreeBranch.stateSnapshot.getHeuristicValue()){
            return 1;
        } else {
            return -1;
        }
    }
}
