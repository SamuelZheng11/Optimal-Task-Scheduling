package cost_function;

import common.*;
import exception_classes.CostFunctionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CostFunctionService {
    private static final int FIRST_INDEX = 0;
    private State state;
    private ArrayList<Job> processor;
    private int inputProcessorCompletionTime = 0;
    private int heuristicSum = 0;

    public State scheduleNode(TaskDependencyNode node, int onProcessorNumber, State withCurrentState, int costOfAllNodes) {
        // generate a deep copy of the input state
        this.generateDeepCopy(withCurrentState, onProcessorNumber);

        ArrayList<TaskDependencyNode> parentNodes = new ArrayList<>();
        ArrayList<Integer> parentCommDelayEdges = new ArrayList<>();
        for(int i = 0; i < node._parents.length; i++){
            parentNodes.add(node._parents[i]._parent);
            parentCommDelayEdges.add(node._parents[i]._communicationDelay);
        }

        // check if the current processor contains any parent jobs
        boolean parentOnProcessor = isParentOnProcessor(this.processor.toArray(new Job[this.processor.size()]), parentNodes);

        // check if the node has any parents
        if (parentNodes.size() == 0) {
            // returns the state with the new task on the processor
            this.processor.add(new TaskJob(node._duration, node._name, node));
            this.state.getJobLists()[onProcessorNumber] = processor.toArray(new Job[processor.size()]);
            this.state.getJobListDuration()[onProcessorNumber] += node._duration;
            return new State(this.state.getJobLists(), this.state.getJobListDuration(), costOfAllNodes - heuristicSum - node._duration);
        }

        // otherwise attempt to find the parent with the lowest communication time
        int lowestCostToSchedule;
        if(!parentOnProcessor) {
            lowestCostToSchedule = this.calculateCostToSchedule(parentNodes, parentCommDelayEdges, onProcessorNumber);
        } else {
            lowestCostToSchedule = 0;
        }

        // check that the lowest communication time is not infinity
        if (lowestCostToSchedule == Integer.MAX_VALUE){
            throw new CostFunctionException("Lowest Communication Time have value infinity");
        }

        // add to processor with communication delay
        if(!parentOnProcessor && lowestCostToSchedule > this.inputProcessorCompletionTime) {
            processor.add(new DelayJob(lowestCostToSchedule - this.inputProcessorCompletionTime));
            this.state.getJobListDuration()[onProcessorNumber] += lowestCostToSchedule - this.inputProcessorCompletionTime;
            this.heuristicSum += (lowestCostToSchedule - this.inputProcessorCompletionTime);
        }
        processor.add(new TaskJob(lowestCostToSchedule, node._name, node));
        this.state.getJobLists()[onProcessorNumber] = processor.toArray(new Job[processor.size()]);
        this.state.getJobListDuration()[onProcessorNumber] += node._duration;

        return new State(this.state.getJobLists(), this.state.getJobListDuration(), costOfAllNodes - heuristicSum - node._duration);
    }

    private int calculateCostToSchedule(ArrayList<TaskDependencyNode> parents, ArrayList<Integer> commDealy, int skipProcessorNumber) {

        // find the parent's completion time on other processors
        List<Integer> buildingCostForProcessor = new ArrayList<>(Collections.nCopies(this.state.getJobLists().length, 0));
        List<Integer> currentBestForProcessor = new ArrayList<>(Collections.nCopies(this.state.getJobLists().length, Integer.MAX_VALUE));
        List<Boolean> parentFound = new ArrayList<>(Collections.nCopies(this.state.getJobLists().length, Boolean.FALSE));

        for (int i = 0; i < this.state.getJobLists().length; i++){

            // skip the row if it has already been check by the outer method
            if(skipProcessorNumber == i) {
                continue;
            }

            currentBestForProcessor.set(i, Integer.MAX_VALUE);

            for (int j = 0; j < this.state.getJobLists()[i].length; j++){
                buildingCostForProcessor.set(i, buildingCostForProcessor.get(i) + this.state.getJobLists()[i][j].getDuration());
                this.heuristicSum += this.state.getJobLists()[i][j].getDuration();
                if (this.state.getJobLists()[i][j].getClass() == TaskJob.class) {
                    TaskJob potentialParentJob = (TaskJob) this.state.getJobLists()[i][j];

                    // check if the TaskJob's node is a parent of the node
                    if (parents.contains(potentialParentJob.getNode())) {
                        // only update the current best cost for the processor if the cost of the processor up to the parent's node + the parents node's communication delay is
                        // less than the current best
                        if (currentBestForProcessor.get(i) > buildingCostForProcessor.get(i) + commDealy.get(parents.indexOf(potentialParentJob.getNode()))) {
                            currentBestForProcessor.set(i, buildingCostForProcessor.get(i) + commDealy.get(parents.indexOf(potentialParentJob.getNode())));
                            parentFound.set(i, true);
                        }
                    }
                }
            }
        }

        // calculate best cost to schedule with
        int lowestCost = Integer.MAX_VALUE;
        for(int i = 0; i < currentBestForProcessor.size(); i++){
            // skip the row if it has already been check by the outer method
            if(skipProcessorNumber == i) {
                continue;
            }

            // get the lowest cost out of all the processors
            if(parentFound.get(i) == true && currentBestForProcessor.get(i) < lowestCost){
                lowestCost = currentBestForProcessor.get(i);
            }
        }

        // sum up heuristic cost for all foreign processors
//        for(int i = 0; i < buildingCostForProcessor.size(); i++) {
//            this.heuristicSum += buildingCostForProcessor.get(i);
//        }

        return lowestCost;
    }

    private boolean isParentOnProcessor(Job[] onProcessor, ArrayList<TaskDependencyNode> parentNodes) {
        boolean result = false;
        // check if a parent of the node to be scheduled is on the current processor
        // sum up the cost of tasks on the processor for the heuristic cost
        for (int i = 0; i < onProcessor.length; i++) {
            this.heuristicSum += onProcessor[i].getDuration();
            this.inputProcessorCompletionTime += onProcessor[i].getDuration();
            for (int j = 0; j < parentNodes.size(); j++) {
                if(onProcessor[i] instanceof TaskJob) {
                    TaskJob job = (TaskJob) onProcessor[i];
                    if (job.getNode() == parentNodes.get(j)) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    private void generateDeepCopy(State inputState, int inputProcessorIndex) {
        Job[][] jobs = {{},{}};

        // create deep copy as java does not do deep copying :(
        for(int i = 0; i < inputState.getJobLists().length; i++){
            jobs[i] = Arrays.copyOf(inputState.getJobLists()[i], inputState.getJobLists()[i].length);
            if (inputProcessorIndex == i){
                this.processor = new ArrayList<>(Arrays.asList(jobs[i]));
            }
        }
        this.state = new State( jobs, Arrays.copyOf(inputState.getJobListDuration(), inputState.getJobListDuration().length), 0);
    }
}
