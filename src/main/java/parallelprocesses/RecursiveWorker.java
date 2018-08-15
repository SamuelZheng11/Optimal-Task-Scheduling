package parallelprocesses;

import common.State;
import common.TaskDependencyNode;
import common.TaskJob;
import cost_function.CostFunctionService;
import exception_classes.RecursiveWorkerException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RecursiveWorker implements Callable<Integer> {

    private List<TaskDependencyNode> freeTasks;
    private State state;
    private int tasksScheduled;
    private RecursiveDoneListener listener;

    public RecursiveWorker(StateTreeBranch branch, RecursiveDoneListener listener) {
        if(branch.getStateSnapshot() == null){
            throw new RecursiveWorkerException("No state snapshot found in the StateTreeBranch object");
        }
        this.freeTasks = branch.getFreeNodes();
        this.state = branch.getStateSnapshot();
        this.tasksScheduled = branch.getNumTaskScheduled();
        this.listener = listener;
    }

    //The recursion to find the optimal schedule
    // Arguments in order are, freeTasks: the initially free tasks of the
    // dependency tree (all roots of the tree), depth(0 to start), state(null to start),
    // bestFoundState: representation of the greedy algo best found soln,
    // numTasks: total number of tasks to be scheduled,
    // in addition fields used are numProc: The number of processors,
    // linearScheduleTime: The total time it would take if this was all on processor (no comms delays),
    // recursionStore used to house the constant information
    public void recurse(List<TaskDependencyNode> freeTasks, State state, int tasksScheduled) {
        //If there are available tasks to schedule
        if (freeTasks.size() > 0) {
            //For each available task, try scheduling it on a processor
            for (int i = 0; i < freeTasks.size(); i++) {
                //if the current processor and the next processor are empty, skip the current one (all empty processors are equivalent)
                for (int j = 0; j < RecursionStore.getNumberOfProcessors(); j++) {
                    if (j < RecursionStore.getNumberOfProcessors()-1 && state.getJobListDuration()[j] == 0 && state.getJobListDuration()[j + 1] == 0) {
                        continue;
                    }
                    TaskDependencyNode currentNode = freeTasks.get(i);
                    List<TaskDependencyNode> prospectiveFreeTasks = new ArrayList<>(freeTasks);

                    //add all children of the task to the freetask list and remove the task
                    for (int k = 0; k < currentNode._children.size(); k++) {
                        TaskDependencyNode child = currentNode._children.get(k)._child;
                        int numUnresolvedParents = child._parents.size();

                        for (int l = 0; l <state.getJobLists().size(); l++) {
                            for (int m = 0; m < child._parents.size(); m++) {
                                if (currentNode == child._parents.get(m)._parent){
                                    numUnresolvedParents--;
                                    continue;
                                } else {
                                    for (int n = 0; n < state.getJobLists().get(l).size(); n++) {
                                        if (state.getJobLists().get(l).get(n) instanceof TaskJob &&
                                                ((TaskJob) state.getJobLists().get(l).get(n)).getNode() == child._parents.get(m)._parent){
                                            numUnresolvedParents--;
                                        }
                                    }
                                }

                            }
                            if (numUnresolvedParents == 0 ){
                                break;
                            }
                        }
                        if (numUnresolvedParents == 0) {
                            prospectiveFreeTasks.add(currentNode._children.get(k)._child);
                        }
                    }
                    prospectiveFreeTasks.remove(currentNode);
                    //create the new state with the task scheduled to evaluate pass to the recursion
                    State newState = new CostFunctionService().scheduleNode(currentNode, j, state, RecursionStore.getLinearScheduleTime());
                    tasksScheduled++;

                    //if this state is complete and better than existing best, update.
                    if (newState.getHeuristicValue() < RecursionStore.getBestStateHeuristic() && tasksScheduled == RecursionStore.getNumberOfTasksTotal()) {
                        RecursionStore.processPotentialBestState(newState);
                    }
                    //if possibly better and not complete, recurse.
                    else if (newState.getHeuristicValue() < RecursionStore.getBestStateHeuristic() && tasksScheduled < RecursionStore.getNumberOfTasksTotal()) {
                        recurse(prospectiveFreeTasks, newState, tasksScheduled);
                    }
                    tasksScheduled--;
                }
            }
        }
    }

    @Override
    public Integer call() throws Exception {
        try {
            this.recurse(new ArrayList<>(this.freeTasks), new State(this.state.getJobLists(), this.state.getJobListDuration(), this.state.getHeuristicValue()), new Integer(this.tasksScheduled));
        } catch( Exception e){
            throw new RecursiveWorkerException("uncaught exception thrown in a recursive worker");
        }

        done();
        return 0;
    }

    public void done(){
        this.listener.handleThreadRecursionHasCompleted();
    }
}
