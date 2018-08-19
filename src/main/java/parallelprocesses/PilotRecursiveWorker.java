package parallelprocesses;

import common.State;
import common.TaskDependencyNode;
import common.TaskJob;
import cost_function.CostFunctionService;

import java.util.ArrayList;
import java.util.List;

/**
 * this class represents the initial search of the state tree to expand the number of branches so that it can be parallelised easily
 */
public class PilotRecursiveWorker implements Runnable {
    private int recursiveDepth = 0;

    private int stateTreeStopValue;
    private PilotDoneListener listener;

    public PilotRecursiveWorker(int processorMultiplierWeight, PilotDoneListener listener) {
        this.stateTreeStopValue = processorMultiplierWeight * RecursionStore.getNumberOfCores();
        this.listener = listener;
    }

    /**
     * called when the runnable is scheduled on a thread to be run
     */
    @Override
    public void run() {
        try {
            recurse();
        } catch (Exception e) {
            listener.handleThreadException(e);
        }
    }

    private void recurse() {
        this.recursiveDepth++;

        // pop next state-tree branch for expansion
        StateTreeBranch stb = RecursionStore.pollStateTreeQueue();

        // check if the best state has been found by the pilot
        if (stb == null) {
            done();
            return;
        }

        List<TaskDependencyNode> freeTasks = stb.getFreeNodes();

        // check if the stopping condition has been met
        if (RecursionStore.getTaskQueueSize() < this.stateTreeStopValue && stb != null) {
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

                    //create the new state with the task scheduled to evaluate pass to the recursion
                    State newState = new CostFunctionService().scheduleNode(currentNode, j, state, RecursionStore.getLinearScheduleTime());

                    if (newState.getHeuristicValue() < RecursionStore.getBestStateHeuristic() && !RecursionStore.hasExplored(newState.getByteArray())) {
                        int tasksScheduled = stb.getNumTaskScheduled() + 1;
                        for (int k = 0; k < currentNode._children.size(); k++) {
                            TaskDependencyNode child = currentNode._children.get(k)._child;
                            int numUnresolvedParents = child._parents.size();

                            for (int l = 0; l < child._parents.size(); l++) {
                                if (currentNode == child._parents.get(l)._parent) {
                                    numUnresolvedParents--;
                                    continue;
                                }
                                boolean parentFound = false;
                                for (int m = 0; m < state.getJobLists().size(); m++) {
                                    for (int n = 0; n < state.getJobLists().get(m).size(); n++) {
                                        if (state.getJobLists().get(m).get(n) instanceof TaskJob &&
                                                ((TaskJob) state.getJobLists().get(m).get(n)).getNode() == child._parents.get(l)._parent) {
                                            numUnresolvedParents--;
                                            parentFound = true;
                                        }
                                    }
                                }
                                if (!parentFound) {
                                    break;
                                }
                            }
                            if (numUnresolvedParents == 0) {
                                if (!prospectiveFreeTasks.contains(currentNode._children.get(k)._child)) {
                                    prospectiveFreeTasks.add(currentNode._children.get(k)._child);
                                }
                            }
                        }

                        //if this state is complete and better than existing best, update
                        if (tasksScheduled == RecursionStore.getNumberOfTasksTotal()) {
                            RecursionStore.processPotentialBestState(newState);
                        } else if (tasksScheduled < RecursionStore.getNumberOfTasksTotal()) {
                            RecursionStore.pushStateTreeQueue(new StateTreeBranch(newState, prospectiveFreeTasks, tasksScheduled));
                        }
                        tasksScheduled--;
                    }
                }
            }
            this.recurse();
        }

        this.recursiveDepth--;
        if (this.recursiveDepth == 0) {
            done();
        }
    }

    /**
     * method called to notify listeners that the task is finished
     */
    private void done() {
        RecursionStore.clearHashSet();
        this.listener.handlePilotRunHasCompleted();
    }
}
