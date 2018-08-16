package common;

import javafx.concurrent.Task;

import java.util.*;

public class GreedyState {

    private State _currentState;
    private List<TaskDependencyNode> _freeTasks;
    private Map<Integer, List<Job>> _processors;
    private Map<Integer, List<TaskDependencyNode>> _scheduledNodes;
    private Map<TaskDependencyNode, Integer> _nodeStartTime;
    private int _numFreeTasks;
    private int _numProc;
    private DependencyGraph _dg;
    private int _numNodes;



    public State getInitialState(DependencyGraph dg, int numProc) {
        _numProc = numProc;
        _dg = dg;
        _numNodes = _dg.getNodes().size();
        //Create map with number of processors specified as input
        _processors = new HashMap<Integer, List<Job>>();

        for(int i = 1; i <= _numProc; i++) {
            _processors.put(i, new ArrayList<Job>());
            _scheduledNodes.put(i, new ArrayList<TaskDependencyNode>());
        }

        _freeTasks = _dg.getFreeTasks(null); // Get all root nodes

        // Create task for first node and add to first processor
        TaskDependencyNode firstNode = _freeTasks.get(0);
        TaskJob firstJob = new TaskJob(firstNode._duration, firstNode._name, firstNode);
        _processors.get(1).add(firstJob);
        _scheduledNodes.get(1).add(firstNode);
        _nodeStartTime.put(firstNode, 0);


        _freeTasks = _dg.getFreeTasks(firstNode); //update free tasks


        while (_freeTasks.size() > 0) {
            addNode();
        }

        return null;
    }

    public void addNode() {
        TaskDependencyNode nodeToAdd = _freeTasks.get(0); //get first node in priority
        TaskJob jobToAdd = new TaskJob(nodeToAdd._duration, nodeToAdd._name, nodeToAdd); // create next job based on priority



        //iterate through all parents of the node to add
        for (TaskDependencyEdge parentEdge:
             nodeToAdd._parents) {
            int[] earliestStarrTimeArr = new int[_numProc];

            int parentNodeEndTime = _nodeStartTime.get(parentEdge._parent) + parentEdge._parent._duration; //get starttime(parent node) + weight(parent node)


            for(int i = 1; i <= _numProc; i++) { //iterate through each processor and get earliest start time based on precedence check
                int earliestStartTime = parentNodeEndTime;
                for (Job jobs:
                     _processors.get(i)) {
                    if(!_scheduledNodes.get(i).contains(parentEdge._parent)) {
                        earliestStartTime += parentEdge._communicationDelay; //add comm delay if proc(i) =/= proc(j)
                    }


//                    if(jobs instanceof TaskJob) {
//                        TaskJob task = (TaskJob) jobs;
//                        if(task.getNode().equals(parentEdge._parent)) {
//                            earliestStartTime += task.getDuration();
//                            break;
//                        }
//                    }
//                    else if(jobs instanceof DelayJob) {
//                        earliestStartTime += jobs.getDuration();
//                        continue;
//                    }
//                    else if(jobs instanceof TaskJob) {
//                        TaskJob task = (TaskJob) jobs;
//                        if(!task.getNode().equals(parentEdge._parent)) {
//                            earliestStartTime += task.getDuration();
//                            continue;
//                        }
//                    }
//                    else {
//                        continue;
//                    }

                }

            }
        }
    }

    public void checkParent(TaskDependencyNode checkNode) {

    }


}
