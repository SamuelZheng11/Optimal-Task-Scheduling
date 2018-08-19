package common;

import javafx.concurrent.Task;
import parallelprocesses.RecursionStore;

import java.util.*;

/**
 * this runnable class is responsible when executed to calculate the greedy schedule given an input graph
 */
public class GreedyState implements Runnable {

    private List<TaskDependencyNode> _freeTasks;
    private Map<Integer, List<Job>> _processors;
    private Map<Integer, List<TaskDependencyNode>> _scheduledNodes;
    private Map<TaskDependencyNode, Integer> _nodeStartTime;
    private int _numProc;
    private DependencyGraph _dg;
    private GreedySearchListener _listener;

    public GreedyState(DependencyGraph dg, GreedySearchListener listener){
        _dg = dg;
        _numProc = RecursionStore.getNumberOfProcessors();
        _listener = listener;
    }

    /**
     * called to get the greedy schedule
     * @return State object representing the greedy schedule
     */
    private State calculateInitialState() {
        //Create map with number of processors specified as input
        _processors = new HashMap<>();
        _scheduledNodes = new HashMap<>();
        _nodeStartTime = new HashMap<>();
        int scheduledTasks = 0;

        for(int i = 1; i <= _numProc; i++) {
            _processors.put(i, new ArrayList<>());
            _scheduledNodes.put(i, new ArrayList<>());
        }

        _freeTasks = _dg.getFreeTasks(null); // Get all root nodes

        // Create task for first node and add to first processor
        TaskDependencyNode firstNode = _freeTasks.get(0);
        TaskJob firstJob = new TaskJob(firstNode._duration, firstNode._name, firstNode);
        _processors.get(1).add(firstJob);
        scheduledTasks += firstJob.getDuration();
        _scheduledNodes.get(1).add(firstNode);
        _nodeStartTime.put(firstNode, 0);


        _freeTasks = _dg.getFreeTasks(firstNode); //update free tasks


        while (_freeTasks.size() > 0) {
            scheduledTasks += _freeTasks.get(0)._duration;
            addNode();
        }
        List<List<Job>> processorList = new ArrayList(_numProc);
        for (int i = 1; i <= _numProc; i++) {
            processorList.add(_processors.get(i));
        }

        int[] durationArr = new int[_numProc];
        int counter = 0;
        for (List<Job> processors : processorList) { //Iterate through each processor
            int duration = 0;
            for (Job jobs : processors) {
                duration += jobs.getDuration();
            }
            durationArr[counter] = duration;
            counter++;
        }

        int heuristic = durationArr[0];
        for(int i = 1; i < durationArr.length; i++) {
            if(durationArr[i] > heuristic) {
                heuristic = durationArr[i];
            }
        }

        State state = new State(processorList, durationArr, heuristic, scheduledTasks);

        return state;
    }

    /**
     * called by the calculateInitialState method to add the next node on to the schedule
     * when calculating the initial greedy schedule
     */
    private void addNode() {
        TaskDependencyNode nodeToAdd = _freeTasks.get(0); //get first node in priority
        _freeTasks = _dg.getFreeTasks(nodeToAdd);

        int[] bestProcStartTimes = new int[_numProc];
        int counter = 0;
        //iterate through all parents of the node to add
        for (TaskDependencyEdge parentEdge:
             nodeToAdd._parents) {

            int[] nodeEarliestStartTimeArr = new int[_numProc];
            int[] procEarliestStartTimeArr = new int[_numProc];
            int parentNodeEndTime = _nodeStartTime.get(parentEdge._parent) + parentEdge._parent._duration; //get starttime(parent node) + weight(parent node)

            for (int i = 0; i < _numProc; i++) { //iterate through each processor and get earliest start time based on precedence check
                int nodeEarliestStartTime = parentNodeEndTime;
                int procEarliestStartTime = 0;
                if (!_scheduledNodes.get(i + 1).contains(parentEdge._parent)) {
                    nodeEarliestStartTime += parentEdge._communicationDelay; //add comm delay if proc(node i) =/= proc(node j)
                }
                for (Job jobs :
                        _processors.get(i + 1)) {
                    procEarliestStartTime += jobs.getDuration(); //get actual possible start time of proc i.

                }
                nodeEarliestStartTimeArr[i] = nodeEarliestStartTime;
                procEarliestStartTimeArr[i] = procEarliestStartTime;
                if (procEarliestStartTimeArr[i] <= nodeEarliestStartTimeArr[i]) {
                    procEarliestStartTimeArr[i] = nodeEarliestStartTimeArr[i]; //update earliest possible start time for node
                }
            }

            if (counter == 0) {
                for (int i = 0; i < _numProc; i++) {
                    bestProcStartTimes[i] = procEarliestStartTimeArr[i];
                }
            } else {
                for (int i = 0; i < _numProc; i++)
                    if (procEarliestStartTimeArr[i] > bestProcStartTimes[i]) {
                        bestProcStartTimes[i] = procEarliestStartTimeArr[i];
                    }
            }
            counter++;
        }

        //Find lowest start time for the node
        int minumumStartTime = bestProcStartTimes[0];

        int bestProcessor = 1;
        for (int i = 0; i <bestProcStartTimes.length; i++) {
            if(bestProcStartTimes[i] < minumumStartTime) {
                minumumStartTime = bestProcStartTimes[i];
                bestProcessor = i+1;
            }
        }
        int endTime = 0; //current end time for the best processor
        for(Job jobs:
            _processors.get(bestProcessor)) {
            endTime += jobs.getDuration();
        }
        int idleTime = minumumStartTime - endTime;


        if(idleTime > 0) {
            DelayJob delay = new DelayJob(idleTime);
            _processors.get(bestProcessor).add(delay);
        }
        TaskJob task = new TaskJob(nodeToAdd._duration, nodeToAdd._name, nodeToAdd);
        _processors.get(bestProcessor).add(task);
        _scheduledNodes.get(bestProcessor).add(nodeToAdd);

        int nodeStartTime = 0;

        for(Job jobs:
                _processors.get(bestProcessor)) {
            nodeStartTime += jobs.getDuration();
        }
        nodeStartTime -= nodeToAdd._duration;
        _nodeStartTime.put(nodeToAdd, nodeStartTime);


    }

    /**
     * Method that is called when the runnable is executed
     */
    @Override
    public void run() {
        try{
            State greedyState = this.calculateInitialState();
            //start greedy search
            RecursionStore.setGreedyState(greedyState);
            this._listener.handleGreedySearchHasCompleted(greedyState);
        } catch (Exception e){
            this._listener.handleThreadException(e);
        }
    }
}
