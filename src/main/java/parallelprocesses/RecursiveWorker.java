package parallelprocesses;

import common.Job;
import common.State;
import common.TaskDependencyNode;
import common.TaskJob;
import cost_function.CostFunctionService;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class RecursiveWorker implements Runnable {
    protected static RecursionStore recursionStore;
    protected static int linearScheduleTime;
    protected static int numProc;
    protected State state;
    protected List<TaskDependencyNode> freeTasks;
    protected int numTasks;
    protected int depth;

    public RecursiveWorker(RecursionStore recursionStore, List<TaskDependencyNode> withFreeTasks, int atDepth, State withState, int withNumTasks) {
        this.recursionStore = recursionStore;
        this.freeTasks = withFreeTasks;
        this.depth = atDepth;
        this.state = withState;
        this.numTasks = withNumTasks;
    }

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    //The recursion to find the optimal schedule
    // Arguments in order are, freeTasks: the initially free tasks of the
    // dependency tree (all roots of the tree), depth(0 to start), state(null to start),
    // bestFoundState: representation of the greedy algo best found soln,
    // numTasks: total number of tasks to be scheduled,
    // in addition fields used are numProc: The number of processors,
    // linearScheduleTime: The total time it would take if this was all on processor (no comms delays),
    // recursionStore used to house the constant information
    public void recurse(List<TaskDependencyNode> freeTasks, int depth, State state, int numTasks) {
        if(state == null) {
            ArrayList<List<Job>> jobList = new ArrayList<List<Job>>(this.numProc);
            for (int i = 0; i < this.numProc; i++) {
                jobList.add(new ArrayList<Job>());
            }
            int[] procDur = new int[this.numProc];
            java.util.Arrays.fill(procDur, 0);
            state = new State(jobList, procDur, Math.floorDiv(this.linearScheduleTime, this.numProc));
        }
        //If there are available tasks to schedule
        if (freeTasks.size() > 0) {
            //For each available task, try scheduling it on a processor
            for (int i = 0; i < freeTasks.size(); i++) {
                //if the current processor and the next processor are empty, skip the current one (all empty processors are equivalent)
                for (int j = 0; j < this.numProc; j++) {
                    if (j < this.numProc-1 && state.getJobListDuration()[j] == 0 && state.getJobListDuration()[j + 1] == 0) {
                        continue;
                    }
                    depth++;
                    TaskDependencyNode currentNode = freeTasks.get(i);
                    List<TaskDependencyNode> prospectiveFreeTasks = new ArrayList<>(freeTasks);

                    //add all children of the task to the freetask list and remove the task
                    for (int k = 0; k < currentNode._children.size(); k++) {
                        TaskDependencyNode child = currentNode._children.get(k)._child;
                        int numUnresolvedParents = child._parents.size();
                        boolean hasConsideredCurrentNodeAsParent = false;

                        for (int l = 0; l <state.getJobLists().size(); l++) {
                            for (int m = 0; m < child._parents.size(); m++) {
                                if (!hasConsideredCurrentNodeAsParent && currentNode == child._parents.get(m)._parent){
                                    numUnresolvedParents--;
                                    hasConsideredCurrentNodeAsParent = true;
                                }
                                for (int n = 0; n < state.getJobLists().get(l).size(); n++) {
                                    if (state.getJobLists().get(l).get(n) instanceof TaskJob && ((TaskJob) state.getJobLists().get(l).get(n)).getNode() == child._parents.get(m)._parent){
                                        numUnresolvedParents--;
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
                    State newState = new CostFunctionService().scheduleNode(currentNode, j, state, this.linearScheduleTime);

                    //if this state is complete and better than existing best, update.
                    if (newState.getHeuristicValue() <= recursionStore.getBestStateHeuristic() && depth == numTasks) {
                        RecursionStore.processPotentialBestState(newState);
                    }
                    //if possibly better and not complete, recurse.
                    else if (newState.getHeuristicValue() <= recursionStore.getBestStateHeuristic() && depth < numTasks) {
                        recurse(prospectiveFreeTasks, depth, newState, numTasks);
                    }
                    depth--;
                }
            }
        }
    }

    @Override
    public void run() {
        this.numProc = recursionStore.getNumberOfProcessors();
        this.linearScheduleTime = recursionStore.getLinearScheduleTime();
        this.recurse(this.freeTasks, this.depth, this.state, this.numTasks);
    }
}
