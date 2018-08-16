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
                    tasksScheduled++;
                    TaskDependencyNode currentNode = freeTasks.get(i);
                    List<TaskDependencyNode> prospectiveFreeTasks = new ArrayList<>(freeTasks);

                    //add all children of the task to the free task list and remove the task
                    for (int k = 0; k < currentNode._children.size(); k++) {
                        TaskDependencyNode child = currentNode._children.get(k)._child;
                        int numUnresolvedParents = child._parents.size();

                        for (int l = 0; l <child._parents.size(); l++) {
                            if (currentNode == child._parents.get(l)._parent){
                                numUnresolvedParents--;
                                continue;
                            }
                            // if at any point a parent has been found break out of job list search and look at next parent
                            nestedLoop:
                            for (int m = 0; m < state.getJobLists().size(); m++) {
                                for (int n = 0; n < state.getJobLists().get(m).size(); n++) {
                                    if (state.getJobLists().get(m).get(n) instanceof TaskJob &&
                                            ((TaskJob) state.getJobLists().get(m).get(n)).getNode() == child._parents.get(l)._parent){
                                        System.out.println(child._parents.get(l)._parent);
                                        numUnresolvedParents--;
                                        break nestedLoop;
                                    }
                                }
                            }
                            if (numUnresolvedParents == 0 ){
                                break;
                            }
                        }
                        if (numUnresolvedParents == 0) {
                            if(prospectiveFreeTasks.contains(currentNode._children.get(k)._child)){
                                System.out.println("breaker");
                            }
                            prospectiveFreeTasks.add(currentNode._children.get(k)._child);
                        }
                    }
                    prospectiveFreeTasks.remove(currentNode);

                    //create the new state with the task scheduled to evaluate pass to the recursion
                    State newState = new CostFunctionService().scheduleNode(currentNode, j, state, RecursionStore.getLinearScheduleTime());

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

        this.recurse(
                new ArrayList<>(this.freeTasks),
                new State(this.state.getJobLists(), this.state.getJobListDuration(), this.state.getHeuristicValue()),
                new Integer(this.tasksScheduled));
        done();
        return 0;
    }

    public void done(){
        this.listener.handleThreadRecursionHasCompleted();
    }
}
