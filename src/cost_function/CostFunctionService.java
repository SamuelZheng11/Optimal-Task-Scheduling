package cost_function;

import common.*;
import exception_classes.CostFunctionException;
import javafx.concurrent.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CostFunctionService {
    private static final int FIRST_INDEX = 0;
    private State _state;
    private int inputProcessorCompletionTime = 0;
    private int heuristicSum = 0;

    public State scheduleNode(TaskDependencyNode node, Job[] onProcessor, State withCurrentState, int costOfAllNodes) {

        _state = withCurrentState;
        List<Job> processor = new ArrayList<>(Arrays.asList(onProcessor));
        // identify what processor the caller has send us
        int processorNumber = 0;
        if (onProcessor.length > 0) {
            processorNumber = this.identifyProcessor(onProcessor[FIRST_INDEX]);
        } else {
            processorNumber = this.identifyProcessor(null);
        }

        ArrayList<TaskDependencyNode> parentNodes = new ArrayList<>();
        ArrayList<Integer> parentCommDelayEdges = new ArrayList<>();
        for(int i = 0; i < node._parents.length; i++){
            parentNodes.add(node._parents[i]._parent);
            parentCommDelayEdges.add(node._parents[i]._communicationDelay);
        }

        // check if the current processor contains any parent jobs
        boolean parentOnProcessor = isParentOnProcessor(onProcessor, parentNodes);

        // check if the node has any parents
        if (parentNodes.size() == 0) {
            // returns the state with the new task on the processor
            processor.add(new TaskJob(node._duration, node._name, node));
            _state.getJobLists()[processorNumber] = processor.toArray(new Job[processor.size()]);
            _state.getJobListDuration()[processorNumber] += node._duration;
            return new State(_state.getJobLists(), _state.getJobListDuration(), costOfAllNodes - heuristicSum - node._duration);
        }

        // otherwise attempt to find the parent with the lowest communication time
        int lowestCostToSchedule;
        if(!parentOnProcessor) {
            lowestCostToSchedule = this.calculateCostToSchedule(parentNodes, parentCommDelayEdges, processorNumber);
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
            _state.getJobListDuration()[processorNumber] += lowestCostToSchedule - this.inputProcessorCompletionTime;
            System.out.println(this.heuristicSum);
            this.heuristicSum += (lowestCostToSchedule - this.inputProcessorCompletionTime);
            System.out.println(this.heuristicSum);
        }
        processor.add(new TaskJob(lowestCostToSchedule, node._name, node));
        _state.getJobLists()[processorNumber] = processor.toArray(new Job[processor.size()]);
        _state.getJobListDuration()[processorNumber] += node._duration;

        return new State(_state.getJobLists(), _state.getJobListDuration(), costOfAllNodes - heuristicSum - node._duration);
    }

    private int calculateCostToSchedule(ArrayList<TaskDependencyNode> parents, ArrayList<Integer> commDealy, int skipProcessorNumber) {

        // find the parent's completion time on other processors
        List<Integer> buildingCostForProcessor = new ArrayList<>(Collections.nCopies(_state.getJobLists().length, 0));
        List<Integer> currentBestForProcessor = new ArrayList<>(Collections.nCopies(_state.getJobLists().length, 0));
        List<Boolean> parentFound = new ArrayList<>(Collections.nCopies(_state.getJobLists().length, Boolean.FALSE));

        for (int i = 0; i < _state.getJobLists().length; i++){

            // skip the row if it has already been check by the outer method
            if(skipProcessorNumber == i) {
                continue;
            }

            currentBestForProcessor.set(i, Integer.MAX_VALUE);

            for (int j = 0; j < _state.getJobLists()[i].length; j++){
                buildingCostForProcessor.set(i, buildingCostForProcessor.get(i) + 1);

                if (_state.getJobLists()[i][j].getClass() == TaskJob.class) {
                    TaskJob potentialParentJob = (TaskJob) _state.getJobLists()[i][j];

                    if (parents.contains(potentialParentJob.getNode())) {
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

        // calculate the current heuristic cost
        for(int i = 0; i < buildingCostForProcessor.size(); i++) {
            this.heuristicSum += buildingCostForProcessor.get(i);
        }

        return lowestCost;
    }

    private boolean isParentOnProcessor(Job[] onProcessor, ArrayList<TaskDependencyNode> parentNodes) {
        boolean result = false;
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

    private int identifyProcessor(Job firstJobOnProcessor) {

        boolean enableShortCircuit = (firstJobOnProcessor == null);
        for (int i = 0; i < _state.getJobLists().length; i++){
            if (enableShortCircuit && _state.getJobLists()[i].length == 0) {
                // if the first index is null and we want to find a processor with no tasks on it then return the processor number
                return i;
            } else if (_state.getJobLists()[FIRST_INDEX][i] == firstJobOnProcessor) {
                // otherwise try to identify the processor number
                return i;
            }
        }

        throw new CostFunctionException("Index of processor was not found");
    }
}
