package parallelprocesses;

import common.State;
import common.TaskDependencyNode;

import java.util.List;

public class RecursiveAlgorithmAdapter extends Main {

    public State callAlgorithm(int numProc, List<TaskDependencyNode> freeTasks, int depth, State state, State bestFoundState, int numTasks, int linearScheduleTime){
        return recursion(numProc, freeTasks, depth, state, bestFoundState, numTasks, linearScheduleTime);
    }
}
