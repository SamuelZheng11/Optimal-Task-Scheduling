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
        _scheduledNodes = new HashMap<Integer, List<TaskDependencyNode>>();
        _nodeStartTime = new HashMap<TaskDependencyNode, Integer>();

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
        List<List<Job>> processorList = new ArrayList(_numProc);
        for (int i = 1; i <= _numProc; i++) {
            processorList.add(_processors.get(i));
        }

        int[] durationArr = new int[_numProc];
        int counter = 0;
        for (List<Job> processors : processorList) { //Iterate through each processor
           // System.out.println("Looking at processor number " + counter);
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

        State state = new State(processorList, durationArr, heuristic);

        return state;
    }

    public void addNode() {
        TaskDependencyNode nodeToAdd = _freeTasks.get(0); //get first node in priority
        _freeTasks = _dg.getFreeTasks(nodeToAdd);
        TaskJob jobToAdd = new TaskJob(nodeToAdd._duration, nodeToAdd._name, nodeToAdd); // create next job based on priority

        //System.out.println("Attempting to add node: " + nodeToAdd._name);

        int[] bestProcStartTimes = new int[_numProc];
        int counter = 0;
        //iterate through all parents of the node to add
        for (TaskDependencyEdge parentEdge:
             nodeToAdd._parents) {

          //  System.out.println("Looking at parent node: " + parentEdge._parent._name);

            int[] nodeEarliestStartTimeArr = new int[_numProc];
            int[] procEarliestStartTimeArr = new int[_numProc];
            int parentNodeEndTime = _nodeStartTime.get(parentEdge._parent) + parentEdge._parent._duration; //get starttime(parent node) + weight(parent node)

           // System.out.println("Parent node end time is: " + Integer.toString(parentNodeEndTime));

            for (int i = 0; i < _numProc; i++) { //iterate through each processor and get earliest start time based on precedence check
                int nodeEarliestStartTime = parentNodeEndTime;
                int procEarliestStartTime = 0;
                if (!_scheduledNodes.get(i + 1).contains(parentEdge._parent)) {
                  //  System.out.println("Processor number " + Integer.toString(i + 1) + "does not contain parent node");
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
//            if (nodeToAdd._name.equals("7")) {
//             //   System.out.println("EARLIEST START TIME FOR NODE " + nodeToAdd._name + " on processor " + Integer.toString(1) + " is " + procEarliestStartTimeArr[0]);
//              //  System.out.println("EARLIEST START TIME FOR NODE " + nodeToAdd._name + " on processor " + Integer.toString(2) + " is " + procEarliestStartTimeArr[1]);
//
//            }

            if (counter == 0) {
                for (int i = 0; i < _numProc; i++) {
                   // System.out.println(Integer.toString(procEarliestStartTimeArr[i]));
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

//        for(int i = 0; i< bestProcStartTimes.length; i++) {
//            System.out.print(Integer.toString(bestProcStartTimes[i])+ " ");
//        }

        //Find lowest start time for the node
        int minumumStartTime = bestProcStartTimes[0];

        int bestProcessor = 1;
        for (int i = 0; i <bestProcStartTimes.length; i++) {
            if(bestProcStartTimes[i] < minumumStartTime) {
                minumumStartTime = bestProcStartTimes[i];
                bestProcessor = i+1;
            }
        }
      //  System.out.println("MOST OPTIMAL start time is: " + Integer.toString(minumumStartTime) + " on processor " + Integer.toString(bestProcessor));
        int endTime = 0; //current end time for the best processor
        for(Job jobs:
            _processors.get(bestProcessor)) {
            endTime += jobs.getDuration();
        }
        int idleTime = minumumStartTime - endTime;
      //  System.out.println("Current end time is " + Integer.toString(endTime));
     //   System.out.println("Current idle time is " + Integer.toString(idleTime));

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
     //   System.out.println(Integer.toString(nodeStartTime));
        _nodeStartTime.put(nodeToAdd, nodeStartTime);


    }
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