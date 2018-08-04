package common;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class DependencyGraph {

    private final String WEIGHT = "Weight";
    private String _filePath = "Input/example-input-graphs/Nodes_7_OutTree.dot";
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

    public void setFilePath(String path){
        _filePath = path;
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
     * The summation of all the remaining tasks processing costs (excluding communication cost)
     * @return cost.
     */
    public int remainingCosts(){
        int cost = 0;
        for(Map.Entry<String, TaskDependencyNode> node : _nodes.entrySet()){
            if(!_scheduledNodes.contains(node.getValue())){
                cost += node.getValue()._duration;
            }
        }
        return cost;
    }

    /**
     * Using the graph stream library this method creates a graph stream graph and converts the .dot file into the internal graph structure
     */
    public void parse(){
        FileSource fs = new FileSourceDOT();
        fs.addSink(g);
        try {
            fs.readAll(_filePath);
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
//            for (Map.Entry<String, TaskDependencyNode> entry : _nodes.entrySet()) {
//                System.out.println(entry.getKey() + "/" + entry.getValue());
//            }
        }catch(ClassCastException e){
            e.printStackTrace();
            System.out.println("Edwar your daring Double type force casting broke");
        }
    }

    /**
     * This method will create a dot file to represent the optimal found state. The dot file is created in the current directory.
     * @param optimalState State object which contains the optimal solution.
     * @param inputFileName The file name (with extension .dot) of the original input file.
     * @throws IOException
     */
    public void generateOutput(State optimalState, String inputFileName) throws IOException {
        int processorNumber = 0;
        List<List<Job>> optimalLists = optimalState.getJobLists();

        String outputName = "output" + inputFileName; // File name (inc. extension) of output file.

        String fileData = "digraph \"" + "output" + inputFileName + "\" {" + System.lineSeparator();
        Files.write(Paths.get(outputName), fileData.getBytes()); //File creation

        for (List<Job> processors : optimalLists) { //Iterate through each processor
            processorNumber++;
            int startTime = 0;
            for (Job jobs : processors) { //Iterate through each job in the processor
                if (jobs instanceof DelayJob ) {
                    startTime += jobs.getDuration();
                    continue;
                }
                else if (jobs instanceof  TaskJob) {
                    TaskJob task = (TaskJob) jobs;
//                    try(BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
//                        writer.newLine();
//                        String output = String.format(" %s   [Weight=%d,Start=%d,Processor=%d];", task.getName(), task.getDuration(), startTime, processorNumber);
//                        writer.write(output);
//                    } catch(IOException e) {
//                        e.printStackTrace();
//                    }
                    try {
                        String output = String.format("\t%s\t [Weight=%d,Start=%d,Processor=%d];", task.getName(), task.getDuration(), startTime, processorNumber) + System.lineSeparator();
                        Files.write(Paths.get(outputName), output.getBytes(), StandardOpenOption.APPEND);
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    startTime += task.getDuration();
                }
            }
        }

        for (List<Job> processors : optimalLists) {
            for (Job jobs : processors) {
                if (jobs instanceof DelayJob ) {
                    continue;
                }
                else if (jobs instanceof  TaskJob) {
                    TaskJob task = (TaskJob) jobs;
                    TaskDependencyNode node = task.getNode();

                    String parentName;
                    String childName;
                    int weight;

                    parentName = node._name;

                    for (TaskDependencyEdge edges : node._children) {
                        childName = edges._child._name;
                        weight = edges._communicationDelay;
                        try {
                            String output = String.format("\t%s -> %s\t [Weight=%d];", parentName, childName, weight) + System.lineSeparator();
                            Files.write(Paths.get(outputName), output.getBytes(), StandardOpenOption.APPEND);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

//        for (Map.Entry<String, TaskDependencyNode> entry : _nodes.entrySet())
//        {
//            String parentName;
//            String childName;
//            int weight;
//
//            parentName = entry.getValue()._name;
//            for (TaskDependencyEdge edges : entry.getValue()._children) {
//
//                childName = edges._child._name;
//                weight = edges._communicationDelay;
//
////                try(BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
////                    writer.newLine();
////                    String output = String.format(" %s -> %s  [Weight=%d];", parentName, childName, weight);
////                    writer.write(output);
////                } catch(IOException e) {
////                    e.printStackTrace();
////                }
//                try {
//                    System.out.println("Hello");
//                    String output = String.format("\t%s -> %s\t [Weight=%d];", parentName, childName, weight) + System.lineSeparator();
//                    Files.write(Paths.get("output.dot"), output.getBytes(), StandardOpenOption.APPEND);
//                }catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        Path path = Paths.get("output.dot");
//        try(BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
//            writer.newLine();
//            writer.write("}");
//        } catch(IOException e) {
//            e.printStackTrace();
//        }
        try {
            Files.write(Paths.get(outputName), "}".getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}