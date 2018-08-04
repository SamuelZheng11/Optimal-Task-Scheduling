package common;

import javafx.concurrent.Task;

import java.util.*;

public class GreedyState {

    private State _currentState;
    private List<TaskDependencyNode> _freeTasks;
    private Map<Integer, List<Job>> _processors;
    private int _numFreeTasks;
    private DependencyGraph _dg;
    private int _numNodes;

    public void topologicalSortUtil(int v, boolean visited[], Stack stack) {
        // Mark the current node as visited.
        visited[v] = true;
        Integer i;

        // Recur for all the vertices adjacent to this
        // vertex


    }

    public void topologicalSort() {
        Stack stack = new Stack();

        //Mark all vertices as not visited
        boolean visited[] = new boolean[_numNodes];
        for (int i = 0; i < _numNodes; i++) {
            visited[i] = false;
        }

        // Call the recursive helper function to store
        // Topological Sort starting from all vertices
        // one by one
        for (int i = 0; i < _numNodes; i++) {
            if(visited[i] = false) {
                topologicalSortUtil(i, visited, stack);
            }
        }

    }

    public State getInitialState(DependencyGraph dg, int numProc) {

        _dg = dg;
        _numNodes = _dg.getNodes().size()
        //Create map with number of processors specified as input
        _processors = new HashMap<Integer, List<Job>>();
        for(int i = 1; i <= numProc; i++) {
            _processors.put(i, new ArrayList<Job>());
        }

        _freeTasks = dg.getFreeTasks(null); // Get all root nodes

        // Create task for first node and add to first processor
        TaskDependencyNode firstNode = _freeTasks.get(0);
        TaskJob firstJob = new TaskJob(firstNode._duration, firstNode._name, firstNode);
        _processors.get(1).add(firstJob);

        // add delay job to all other processors

        for (int i:
             _processors.keySet()) {
            if (i == 1) {
                continue;
            }
            else {
                DelayJob delayJob = new DelayJob(firstNode._duration);
                _processors.get(i).add(delayJob);
            }
        }

        //remove first node and add children
        _freeTasks = dg.getFreeTasks(firstNode);

        _numFreeTasks = _freeTasks.size();

        //while there are still tasks to be scheduled.
        while (_numFreeTasks > 0) {
            for (TaskDependencyNode node:
                 _freeTasks) {
                    int earliestStartTime;
                    int bestProcessor;

                    for

            }
        }




        return null;
    }


}
