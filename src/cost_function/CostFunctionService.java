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
        List<Job> processor = new ArrayList<Job>(Arrays.asList(onProcessor));
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

        // otherwise attempt to find the parent with the lowest communication time
        //TODO: room for optimisation by ranking the parents and children by lowest to highest communication delay reduces for loop time
        int lowestCommunicationTime = Integer.MAX_VALUE;
        for (int i = 0; i < node._parents.length; i++) {
            int tempCommunicationTime = this.calculateCommunicationTime(node, node._parents[i]._parent, onProcessor);

            // stop the loop if any communication time return 0 as that's the best time we will have when scheduling on the processor
            if (tempCommunicationTime == 0) {
                lowestCommunicationTime = tempCommunicationTime;
                break;
            }
        }

        // check that the lowest communication time is not infinity
        if (lowestCommunicationTime == Integer.MAX_VALUE){
            throw new CostFunctionException("Lowest Communication Time have value infinity");
        }

        // add to processor with communication delay
        processor.add(new DelayJob(lowestCommunicationTime));
        processor.add(new TaskJob(lowestCommunicationTime, node._name, node));
        _state.getJobLists()[processorNumber] = processor.toArray(new Job[processor.size()]);
        return _state;
    }

    private int calculateCommunicationTime(TaskDependencyNode ofNode, TaskDependencyNode withParentNode, Job[] onProcessor) {


        for (int i = 0; i < onProcessor.length; i++){
            if (onProcessor[i] == withParentNode) {
                return 0;
            }
        }

        int[] cumulativeParentOnProcessorCost = {};
        boolean[] parentFound = {};

        for (int i = 0; i < _state.getJobLists()[0].length; i++){
            for (int j = 0; j < _state.getJobLists()[1].length; j++){
                cumulativeParentOnProcessorCost[j] += _state.getJobLists()[i][j].getDuration();
                if (_state.getJobLists()[i][j] == withParentNode) {
                    parentFound[j] = true;
                }
            }
        }

        int lowestCommunicationTime = Integer.MAX_VALUE;
        for(int i = 0; i < cumulativeParentOnProcessorCost.length; i++){
            if(parentFound[i] == true && cumulativeParentOnProcessorCost[i] < lowestCommunicationTime){
                lowestCommunicationTime = cumulativeParentOnProcessorCost[i];
            }
        }

        return lowestCommunicationTime;
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
