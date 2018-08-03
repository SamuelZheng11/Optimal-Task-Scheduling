package common;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;
import java.io.IOException;
import java.util.*;

public class DependencyGraph {

    private final String WEIGHT = "Weight";
    private Graph g = new DefaultGraph("g");
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

    /**
     * Using the graph stream library this method creates a graph stream graph and converts the .dot file into the internal graph structure
     */
    public void parse(){
        String filePath = "Input/example-input-graphs/Nodes_7_OutTree.dot";
        FileSource fs = new FileSourceDOT();
        fs.addSink(g);
        try {
            fs.readAll(filePath);
            convert();
//            Iterator<Node> iter = g.getNodeIterator();
//            while(iter.hasNext()){
//                 Node test = iter.next();
//                 Iterator<Edge> iter2 = test.getEdgeIterator();
//                 while(iter2.hasNext()){
//                     Edge edge = iter2.next();
//                     for(String s : edge.getAttributeKeySet()){
//                         System.out.println(s);
//                     }
//                     edge.addAttribute("delay", new Integer(10));
//                     System.out.println(edge.getAttribute("delay") + "");
//                 }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This convert method is called from parse(). Taking the graph stream graph structure and then translating it to our internal graph structure.
     */
    private void convert(){
        try {
            for (Node n : g.getNodeSet()) {
                _nodes.put(n.getId(), new TaskDependencyNode(((Double) n.getAttribute(WEIGHT)).intValue(), n.getId()));
            }
            for (Node n : g.getNodeSet()) {
                TaskDependencyNode node = _nodes.get(n.getId());
                for (Edge e : n.getEdgeSet()) {
                    if (e.getSourceNode().equals(n)) {
                        node._children.add(new TaskDependencyEdge(_nodes.get(n.getId()), _nodes.get(e.getTargetNode().getId()), ((Double) e.getAttribute(WEIGHT)).intValue()));
                    } else {
                        node._parents.add(new TaskDependencyEdge(_nodes.get(e.getSourceNode().getId()), _nodes.get(n.getId()), ((Double) e.getAttribute(WEIGHT)).intValue()));
                    }
                }
            }
            for (Map.Entry<String, TaskDependencyNode> entry : _nodes.entrySet()) {
                System.out.println(entry.getKey() + "/" + entry.getValue());
            }
        }catch(ClassCastException e){
            e.printStackTrace();
            System.out.println("Edwar your daring Double type force casting broke");
        }
    }
}
