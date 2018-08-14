package parallelprocesses;

import common.Job;
import common.State;
import common.TaskDependencyNode;
import common.TaskJob;
import cost_function.CostFunctionService;

import java.util.ArrayList;
import java.util.List;

public class PilotRecursiveWorker extends RecursiveWorker {
    private int maxNumberOfScheduledNodes;
    private int nodesScheduled;
    
    public PilotRecursiveWorker(RecursionStore recursionStore, List<TaskDependencyNode> withFreeTasks, int atDepth, State withState, int withNumTasks, int maxNumberOfScheduledNodes) {
        super(recursionStore, withFreeTasks, atDepth, withState, withNumTasks);
        this.maxNumberOfScheduledNodes = maxNumberOfScheduledNodes;
    }

    public void pilotRecurse(List<TaskDependencyNode> freeTasks, State state, int numTasks) {
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
                    this.nodesScheduled++;
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
                    if (newState.getHeuristicValue() <= recursionStore.getBestStateHeuristic() && this.nodesScheduled == this.maxNumberOfScheduledNodes) {
                        RecursionStore.processPotentialBestState(newState);
                    }
                    //if possibly better and not complete, recurse.
                    else if (newState.getHeuristicValue() <= recursionStore.getBestStateHeuristic() && this.nodesScheduled < this.maxNumberOfScheduledNodes) {
                        pilotRecurse(prospectiveFreeTasks, newState, numTasks);
                    }
                }
            }
        }
    }

}
