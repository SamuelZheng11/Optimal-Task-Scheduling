package parallelprocesses;

import common.State;
import common.TaskDependencyNode;
import common.TaskJob;
import cost_function.CostFunctionService;

import java.util.ArrayList;
import java.util.List;

public class PilotRecursiveWorker {
    private int recursiveDepth = 0;

    private Integer depth = 0;
    private int stateTreeStopValue;
    private PilotDoneListener listener;

    public PilotRecursiveWorker(int processorMultiplierWeight, PilotDoneListener listener) {
        this.stateTreeStopValue = processorMultiplierWeight * RecursionStore.getNumberOfCores();
        this.listener = listener;
    }

    public void recurse() {
        this.recursiveDepth++;
        // pop next state-tree branch for expansion
        StateTreeBranch stb = RecursionStore.pollStateTreeQueue();
        List<TaskDependencyNode> freeTasks = stb.getFreeNodes();

        // check if the stopping condition has been met
        if (RecursionStore.getTaskQueueSize() < this.stateTreeStopValue) {
            //For each available task, try scheduling it on a processor
            for (int i = 0; i < freeTasks.size(); i++) {
                //if the current processor and the next processor are empty, skip the current one (all empty processors are equivalent)
                TaskDependencyNode currentNode = freeTasks.get(i);
                List<TaskDependencyNode> prospectiveFreeTasks = new ArrayList<>(freeTasks);
                prospectiveFreeTasks.remove(currentNode);
                State state = stb.getStateSnapshot();

                for (int j = 0; j < RecursionStore.getNumberOfProcessors(); j++) {
                    if (j < RecursionStore.getNumberOfProcessors() - 1 && state.getJobListDuration()[j] == 0 && state.getJobListDuration()[j + 1] == 0) {
                        continue;
                    }

                    int tasksScheduled = stb.getNumTaskScheduled() + 1;
                    for (int k = 0; k < currentNode._children.size(); k++) {
                        TaskDependencyNode child = currentNode._children.get(k)._child;
                        int numUnresolvedParents = child._parents.size();
                        boolean hasConsideredCurrentNodeAsParent = false;

                        for (int l = 0; l < state.getJobLists().size(); l++) {
                            for (int m = 0; m < child._parents.size(); m++) {
                                if (!hasConsideredCurrentNodeAsParent && currentNode == child._parents.get(m)._parent) {
                                    numUnresolvedParents--;
                                    hasConsideredCurrentNodeAsParent = true;
                                }
                                for (int n = 0; n < state.getJobLists().get(l).size(); n++) {
                                    if (state.getJobLists().get(l).get(n) instanceof TaskJob && ((TaskJob) state.getJobLists().get(l).get(n)).getNode() == child._parents.get(m)._parent) {
                                        numUnresolvedParents--;
                                    }
                                }

                            }
                            if (numUnresolvedParents == 0) {
                                break;
                            }
                        }
                        if (numUnresolvedParents == 0) {
                            prospectiveFreeTasks.add(currentNode._children.get(k)._child);
                        }
                    }

                    //create the new state with the task scheduled to evaluate pass to the recursion
                    State newState = new CostFunctionService().scheduleNode(currentNode, j, state, RecursionStore.getLinearScheduleTime());
                    //if this state is complete and better than existing best, update
                    if (newState.getHeuristicValue() <= RecursionStore.getBestStateHeuristic() && tasksScheduled == RecursionStore.getNumberOfTasksTotal()) {
                        RecursionStore.processPotentialBestState(newState);
                    } else if (newState.getHeuristicValue() <= RecursionStore.getBestStateHeuristic() && tasksScheduled < RecursionStore.getNumberOfTasksTotal()) {
                        RecursionStore.pushStateTreeQueue(new StateTreeBranch(newState, prospectiveFreeTasks, tasksScheduled));
                    }
                }
            }
        this.recurse();
        }
        this.recursiveDepth--;
        if(this.recursiveDepth == 0){
            done();
        }
    }

    private void done(){
        this.listener.handlePilotRunHasCompleted();
    }
}
