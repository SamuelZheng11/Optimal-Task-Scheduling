package parallelprocesses;

import common.Job;
import common.State;
import common.TaskDependencyNode;
import common.TaskJob;
import cost_function.CostFunctionService;
import pt.runtime.TaskID;
import pt.runtime.TaskIDGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ParallelController {
    private static ParallelController parallelController;
    private static State _bestFoundState;

    private ParallelController() {
    }

    public static ParallelController getParallelController(){
        if(parallelController == null) {
            parallelController = new ParallelController();
        }
        return parallelController;
    }

    public State findOptimalSchedule(int numProc, List<TaskDependencyNode> freeTasks, int depth, State state, State bestFoundState, int numTasks, int linearScheduleTime){
        return recursion(numProc, freeTasks, depth, state, bestFoundState, numTasks, linearScheduleTime);
    }

    //The recursion to find the optimal schedule
    // Arguments in order are numProc: The number of processors, freeTasks: the initially free tasks of the
    // dependancy tree (all roots of the tree), depth(0 to start), state(null to start),
    // bestFoundState: representation of the greedy algo best found soln,
    // numTasks: total number of tasks to be scheudled,
    // linearScheduleTime: The total time it would take if this was all on processor (no comms delays)
    private State recursion(int numProc, List<TaskDependencyNode> freeTasks, int depth, State state, State bestFoundState, int numTasks, int linearScheduleTime) {
        if(state == null) {
            ArrayList<List<Job>> jobList = new ArrayList<List<Job>>(numProc);
            for (int i = 0; i < numProc; i++) {
                jobList.add(new ArrayList<Job>());
            }
            int[] procDur = new int[numProc];
            java.util.Arrays.fill(procDur, 0);
            state = new State(jobList, procDur, Math.floorDiv(linearScheduleTime, numProc));
        }
        //If there are available tasks to schedule
        if (freeTasks.size() > 0) {
            //For each available task, try scheduling it on a processor
            for (int i = 0; i < freeTasks.size(); i++) {
                //if the current processor and the next processor are empty, skip the current one (all empty processors are equivalent)
                for (int j = 0; j < numProc; j++) {
                    if (j < numProc-1 && state.getJobListDuration()[j] == 0 && state.getJobListDuration()[j + 1] == 0) {
                        continue;
                    }
                    depth++;
                    TaskDependencyNode currentNode = freeTasks.get(i);
                    List<TaskDependencyNode> prospectiveFreeTasks = new ArrayList<>(freeTasks);

                    TaskIDGroup childrenTasks = new TaskIDGroup(currentNode._children.size());
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
                            childrenTasks.add();
                        }
                    }
                    prospectiveFreeTasks.remove(currentNode);
                    //create the new state with the task scheduled to evaluate pass to the recursion
                    State newState = new CostFunctionService().scheduleNode(currentNode, j, state, linearScheduleTime);

                    //if this state is complete and better than existing best, update.
                    if (newState.getHeuristicValue() <= bestFoundState.getHeuristicValue() && depth == numTasks) {
                        updateBestFoundState(newState);
                    }
                    //if possibly better and not complete, recurse.
                    else if (newState.getHeuristicValue() <= bestFoundState.getHeuristicValue() && depth < numTasks) {
                        try{
                            childrenTasks.waitTillFinished();
                        }catch (ExecutionException ee) {
                            ee.printStackTrace();
                        }catch (InterruptedException ie){
                            ie.printStackTrace();
                        }
                        //Recursion will always return a complete state. If this is better, update.
                        if (foundState.getHeuristicValue() <= bestFoundState.getHeuristicValue()) {
                            this.updateBestFoundState(foundState);
                        }
                    }
                    depth--;
                }
            }
        }
        return bestFoundState;
    }

    private TaskID<Void> doRecursion(TaskDependencyNode targetNode, State withState, int withDepth){
        State foundState = recursion(numProc, prospectiveFreeTasks, withDepth, withState, bestFoundState, numTasks, linearScheduleTime);

        return
    }

    private void updateBestFoundState(State bestFoundState){
        //TODO: call the gui display method inside this method
        _bestFoundState = bestFoundState;
    }
}
