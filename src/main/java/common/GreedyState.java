package common;

import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class GreedyState {

    private State _currentState;
    private List<TaskDependencyNode> _freeTasks;

    public State getInitialState(DependencyGraph dg, int numProc) {

        _freeTasks = dg.getFreeTasks(null); // Get all root nodes

        TaskDependencyNode firstNode = _freeTasks.get(0);
        TaskJob firstJob = new TaskJob(firstNode._duration, firstNode._name, firstNode);

        List<Job> processor1 = new ArrayList<Job>();
        processor1.add(firstJob);

        //while there are still tasks to be scheduled.
        while (_freeTasks.size() > 0) {
            for (TaskDependencyNode node:
                 _freeTasks) {

            }
        }




        return null;
    }


}
