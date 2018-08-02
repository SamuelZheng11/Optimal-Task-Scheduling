package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependencyGraph {

    private static DependencyGraph _dg;
    private Map<String,TaskDependencyNode> _nodes = new HashMap<String,TaskDependencyNode>();
    private List<TaskDependencyNode> _freeTasks = new ArrayList<TaskDependencyNode>();
    private List<TaskDependencyNode> _scheduledNodes = new ArrayList<TaskDependencyNode>();

    private DependencyGraph(){
    }

    public static DependencyGraph getGraph(){
        if(_dg == null){
            _dg = new DependencyGraph();
        }
        return _dg;
    }

    public List<TaskDependencyNode> getCandidate(){
        return null;
    }

    public Map<String,TaskDependencyNode> getNodes() {
        return _nodes; // Needed for testing
    }

    public void addNode(TaskDependencyNode node){
        _nodes.put(node._name, node);
    }


    /**
     * Returns a list of TaskDependencyNodes which are 'free' i.e are independent
     * or all its predecessors have already been scheduled.
     * @param scheduledNode if null, will return root/independent nodes.
     *                      else it will remove specified node from list and add it to list
     *                      provided it is not already in and yet to be scheduled.
     * @return TaskDependencyNode list of free nodes that can be scheduled.
     */
    public List<TaskDependencyNode> getFreeTasks(TaskDependencyNode scheduledNode) {
        try {
            if (scheduledNode == null) { // List will contain nodes without parents (roots)
                for (Map.Entry<String, TaskDependencyNode> entry : _nodes.entrySet()) {
                    if (entry.getValue()._parents.isEmpty()) {
                        _freeTasks.add(entry.getValue()); // add root nodes (nodes without parents)
                    }
                }
            } else {
                _freeTasks.remove(scheduledNode);
                _scheduledNodes.add(scheduledNode);
                for (TaskDependencyEdge nextNodes : scheduledNode._children) {
                    if(_freeTasks.contains(nextNodes._child)) {
                        continue; //skip if list already contains node
                    }
                    else if (_scheduledNodes.contains(nextNodes._child)){
                        continue; //skip if the node has already been scheduled.
                    }
                    else {
                        _freeTasks.add(nextNodes._child);
                    }
                }
            }
        } catch (NoSuchElementException e) {
                e.printStackTrace();
            }

        return _freeTasks;
    }

    //    Parsing Assumptions
    //    A node occurs in the dot file prior to any of its edges either to or from
    //    A edge always contains the '-' character as a part of the arrow symbol
    //    The first line is always some file header containing the word "{"
    //    The last line prior to null contains "}"
    public void parse() {
        Pattern WEIGHTPATTERN = Pattern.compile("Weight=(\\d+)");
        Pattern NODENAMEPATTERN = Pattern.compile("\t(\\d+)\t");
        Pattern EDGESOURCEPATTERN = Pattern.compile("\t(\\d+) ");
        Pattern EDGEDESTPATTERN = Pattern.compile(" (\\d+)\t");

        Matcher _weightMatcher;
        Matcher _nodeNameMatcher;
        Matcher _edgeSourcePatternMatcher;
        Matcher _edgeDestPatternMatcher;

        TaskDependencyNode previous;
        try {
            String filePath = "Input/example-input-graphs/Nodes_9_SeriesParallel.dot";
            BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
            String s = br.readLine();
            while (s != null) {
                if (s.contains("{") || s.contains("}") ) {
                }else if(s.contains("-")){
                    _edgeSourcePatternMatcher = EDGESOURCEPATTERN.matcher(s);
                    _edgeSourcePatternMatcher.find();
                    _edgeDestPatternMatcher = EDGEDESTPATTERN.matcher(s);
                    _edgeDestPatternMatcher.find();
                    _weightMatcher = WEIGHTPATTERN.matcher(s);
                    _weightMatcher.find();
                    _nodes.get(_edgeDestPatternMatcher.group(1))._parents.add(new TaskDependencyEdge(_nodes.get(_edgeSourcePatternMatcher.group(1)),_nodes.get(_edgeDestPatternMatcher.group(1)),Integer.valueOf(_weightMatcher.group(1))));
                    _nodes.get(_edgeSourcePatternMatcher.group(1))._children.add(new TaskDependencyEdge(_nodes.get(_edgeSourcePatternMatcher.group(1)),_nodes.get(_edgeDestPatternMatcher.group(1)),Integer.valueOf(_weightMatcher.group(1))));
                }else {
                    _nodeNameMatcher = NODENAMEPATTERN.matcher(s);
                    _nodeNameMatcher.find();
                    _weightMatcher = WEIGHTPATTERN.matcher(s);
                    _weightMatcher.find();
                    previous = new TaskDependencyNode(Integer.valueOf(_weightMatcher.group(1)), _nodeNameMatcher.group(1));
                    _nodes.put(previous._name,previous);
                }
                s = br.readLine();
            }
            for (Map.Entry<String, TaskDependencyNode> entry : _nodes.entrySet())
            {
                System.out.println(entry.getKey() + "/" + entry.getValue());
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
