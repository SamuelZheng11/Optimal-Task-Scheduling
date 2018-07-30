package cost_function;

import common.*;
import exception_classes.CostFunctionException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CostFunctionService {
    private static final int FIRST_INDEX = 0;
    private static final int INDEX_OFF_SET = 1;
    private State _state;

    public State scheduleNode(TaskDependencyNode node, Job[] onProcessor, State withCurrentState) {

        _state = withCurrentState;
        List<Job> processor = new ArrayList<>(Arrays.asList(onProcessor));
        // identify what processor the caller has send us
        int processorNumber = 0;
        if (onProcessor.length > 0) {
            processorNumber = this.identifyProcessor(onProcessor[FIRST_INDEX]);
        } else {
            processorNumber = this.identifyProcessor(null);
        }

        // check if the node has any parents
        if (node._parents.length == 0) {
            // returns the state with the new task on the processor
            processor.add(new TaskJob(node._duration, node._name, node));
            _state.getJobLists()[processorNumber] = processor.toArray(new Job[processor.size()]);
            return _state;
        }

        TaskDependencyNode[] parentNodes = {};
        int[] parentCommDelayEdges = {};
        for(int i = 0; i < node._parents.length; i++){
            parentNodes[i] = node._parents[i]._parent;
            parentCommDelayEdges[i] = node._parents[i]._communicationDelay;
        }

        // otherwise attempt to find the parent with the lowest communication time
        int lowestCostToSchedule = this.calculateCostToSchedule(new ArrayList<TaskDependencyNode>(Arrays.asList(parentNodes)), parentCommDelayEdges);


        // check that the lowest communication time is not infinity
        if (lowestCostToSchedule == Integer.MAX_VALUE){
            throw new CostFunctionException("Lowest Communication Time have value infinity");
        }

        // add to processor with communication delay
        processor.add(new DelayJob(lowestCostToSchedule));
        processor.add(new TaskJob(lowestCostToSchedule, node._name, node));
        _state.getJobLists()[processorNumber] = processor.toArray(new Job[processor.size()]);
        return _state;
    }

    private int calculateCostToSchedule(ArrayList<TaskDependencyNode> parents, int[] commDelay) {

        // find the parent's completion time on other processors
        int[] costOfParentOnProcessor = {};
        boolean[] parentFound = {};
        for (int i = 0; i < _state.getJobLists().length; i++){
            for (int j = 0; j < _state.getJobLists()[i].length; j++){
                costOfParentOnProcessor[i] += _state.getJobLists()[i][j].getDuration();
                if (_state.getJobLists()[i][j] instanceof TaskJob) {
                    TaskJob potentialParentJob = (TaskJob) _state.getJobLists()[i][j];
                    if (parents.contains(potentialParentJob.getNode())) {
                        parentFound[i] = true;
                        costOfParentOnProcessor[i] += commDelay[parents.indexOf(parents.contains(potentialParentJob.getNode()))];
                        break;
                    }
                }
            }
        }

        // calculate best cost to schedule with
        int lowestCost = Integer.MAX_VALUE;
        for(int i = 0; i < costOfParentOnProcessor.length; i++){
            if(parentFound[i] == true && costOfParentOnProcessor[i] < lowestCost){
                lowestCost = costOfParentOnProcessor[i];
            }
        }

        return lowestCost;
    }

    private int identifyProcessor(Job firstTaskOnProcessor) {

        boolean enableShortCircuit = (firstTaskOnProcessor == null);
        for (int i = 0; i < _state.getJobLists().length; i++){
            if (enableShortCircuit && _state.getJobLists()[i].length == 0) {
                // if the first index is null and we want to find a processor with no tasks on it then return the processor number
                return i;
            } else if (_state.getJobLists()[FIRST_INDEX][i] == firstTaskOnProcessor) {
                // otherwise try to identify the processor number
                return i;
            }
        }

        throw new CostFunctionException("Index of processor was not found");
    }
}
