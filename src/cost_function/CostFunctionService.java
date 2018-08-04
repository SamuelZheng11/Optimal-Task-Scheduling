package cost_function;

import common.*;
import exception_classes.CostFunctionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CostFunctionService {
    private State state;
    private int inputProcessorCompletionTime = 0;
    private double heuristicSum = 0;
    private List<Boolean> parentsFound;
    private TaskDependencyNode targetNode;

    public State scheduleNode(TaskDependencyNode node, int onProcessorNumber, State withCurrentState, int costOfAllNodes) {

        // generate a deep copy of the input state
        this.generateDeepCopy(withCurrentState);
        this.parentsFound = new ArrayList<>(Collections.nCopies(node._parents.size(), Boolean.FALSE));

        // generate the arrays that will be used to easily find a parent and its communication delay with the target node
        ArrayList<TaskDependencyNode> parentNodes = new ArrayList<>();
        ArrayList<Integer> parentCommDelayEdges = new ArrayList<>();
        for(int i = 0; i < node._parents.size(); i++){
            parentNodes.add(node._parents.get(i)._parent);
            parentCommDelayEdges.add(node._parents.get(i)._communicationDelay);
        }

        // check if the current processor contains any parent jobs
        this.processTargetProcessor(this.state.getJobLists().get(onProcessorNumber), parentNodes);
        boolean allParentOnTargetProcessor = this.parentsFound.stream().allMatch(foundParent -> foundParent);


        // check if the node has any parents
        if (parentNodes.size() == 0) {
            // returns the state with the new task on the processor
            this.state.getJobLists().get(onProcessorNumber).add(new TaskJob(node._duration, node._name, node));
            this.state.getJobListDuration()[onProcessorNumber] += node._duration;
            return new State(this.state.getJobLists(), this.state.getJobListDuration(), (costOfAllNodes - heuristicSum - node._duration)/withCurrentState.getJobLists().size());
        }

        // otherwise attempt to find the parent with the lowest communication time
        int costOfSchedulingNode;
        if(!allParentOnTargetProcessor) {
            costOfSchedulingNode = this.calculateCostToSchedule(parentNodes, parentCommDelayEdges, onProcessorNumber);
        } else {
            costOfSchedulingNode = 0;
        }

        // check that the lowest communication time is not infinity
        if (costOfSchedulingNode == Integer.MAX_VALUE){
            throw new CostFunctionException("Lowest Communication Time have value infinity");
        }

        // add to processor with communication delay
        if(!allParentOnTargetProcessor && costOfSchedulingNode > this.inputProcessorCompletionTime) {
            this.state.getJobLists().get(onProcessorNumber).add(new DelayJob(costOfSchedulingNode - this.inputProcessorCompletionTime));
            this.state.getJobListDuration()[onProcessorNumber] += costOfSchedulingNode - this.inputProcessorCompletionTime;
            this.heuristicSum += (costOfSchedulingNode - this.inputProcessorCompletionTime);
        }

        // schedule the node and update job lists for returning state
        this.state.getJobLists().get(onProcessorNumber).add(new TaskJob(costOfSchedulingNode, node._name, node));
        this.state.getJobListDuration()[onProcessorNumber] += node._duration;

        return new State(this.state.getJobLists(), this.state.getJobListDuration(), (costOfAllNodes - heuristicSum - node._duration)/withCurrentState.getJobLists().size());
    }

    private int calculateCostToSchedule(ArrayList<TaskDependencyNode> parents, ArrayList<Integer> commDealy, int skipProcessorNumber) {

        // find the parent's completion time on other processors
        List<Integer> buildingCostForProcessor = new ArrayList<>(Collections.nCopies(this.state.getJobLists().size(), 0));
        List<Integer> currentBestForProcessor = new ArrayList<>(Collections.nCopies(this.state.getJobLists().size(), 0));
        List<Boolean> parentFound = new ArrayList<>(Collections.nCopies(this.state.getJobLists().size(), Boolean.FALSE));

        for (int i = 0; i < this.state.getJobLists().size(); i++){

            // skip the row if it has already been check by the outer method
            if(skipProcessorNumber == i) {
                continue;
            }

            currentBestForProcessor.set(i, 0);

            for (int j = 0; j < this.state.getJobLists().get(i).size(); j++){
                buildingCostForProcessor.set(i, buildingCostForProcessor.get(i) + this.state.getJobLists().get(i).get(j).getDuration());
                this.heuristicSum += this.state.getJobLists().get(i).get(j).getDuration();
                if (this.state.getJobLists().get(i).get(j).getClass() == TaskJob.class) {
                    TaskJob potentialParentJob = (TaskJob) this.state.getJobLists().get(i).get(j);

                    // check if the TaskJob's node is a parent of the node
                    if (parents.contains(potentialParentJob.getNode())) {
                        // only update the current best cost for the processor if the cost of the processor up to the parent's node + the parents node's communication delay is
                        // less than the current best

                        if (currentBestForProcessor.get(i) < buildingCostForProcessor.get(i) + commDealy.get(parents.indexOf(potentialParentJob.getNode()))) {
                            currentBestForProcessor.set(i, buildingCostForProcessor.get(i) + commDealy.get(parents.indexOf(potentialParentJob.getNode())));
                            parentFound.set(i, true);
                        }
                    }
                }
            }
        }

        // calculate best cost to schedule with
        int costOfSchedulingNode = Integer.MAX_VALUE;
        for(int i = 0; i < currentBestForProcessor.size(); i++){
            // skip the row if it has already been check by the outer method
            if(skipProcessorNumber == i) {
                continue;
            }

            // get the lowest cost out of all the processors
            if(parentFound.get(i) && currentBestForProcessor.get(i) < costOfSchedulingNode){
                costOfSchedulingNode = currentBestForProcessor.get(i);
            }
        }

        return costOfSchedulingNode;
    }

    private void processTargetProcessor(List<Job> onProcessor, ArrayList<TaskDependencyNode> parentNodes) {
        // check if a parent of the node to be scheduled is on the current processor
        // sum up the cost of tasks on the processor for the heuristic cost
        for (int i = 0; i < onProcessor.size(); i++) {
            this.heuristicSum += onProcessor.get(i).getDuration();
            this.inputProcessorCompletionTime += onProcessor.get(i).getDuration();
            for (TaskDependencyNode parentNode: parentNodes) {
                if(onProcessor.get(i) instanceof TaskJob) {
                    TaskJob job = (TaskJob) onProcessor.get(i);
                    if (job.getNode() == parentNode) {
                        this.parentsFound.set(parentNodes.indexOf(parentNode), true);
                    }
                }
            }
        }
    }

    private void generateDeepCopy(State inputState) {
        List<List<Job>> jobs = new ArrayList<>(inputState.getJobLists().size());

        // create deep copy as java does not do deep copying
        for(int i = 0; i < inputState.getJobLists().size(); i++){
            jobs.add(new ArrayList<>());
            jobs.set(i, new ArrayList<>(inputState.getJobLists().get(i)));
        }
        this.state = new State( jobs, Arrays.copyOf(inputState.getJobListDuration(), inputState.getJobListDuration().length), 0);
    }
}
