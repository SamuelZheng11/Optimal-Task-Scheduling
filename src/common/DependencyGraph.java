package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

    public void generateOutput(State optimalState) throws IOException {
        int processorNumber = 0;
        List<List<Job>> optimalLists = optimalState.getJobLists();

        String fileData = "digraph \"output\" {" + System.lineSeparator();
        Files.write(Paths.get("output.dot"), fileData.getBytes());

        for (List<Job> processors : optimalLists) {
            processorNumber++;
            int startTime = 0;
            for (Job jobs : processors) {

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
                        Files.write(Paths.get("output.dot"), output.getBytes(), StandardOpenOption.APPEND);
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

                    System.out.println("Parent is: " +parentName);

                    for (TaskDependencyEdge edges : node._children) {
                        System.out.println(node._children);
                        childName = edges._child._name;
                        System.out.println("Child is: " +childName);
                        weight = edges._communicationDelay;

                        try {

                            String output = String.format("\t%s -> %s\t [Weight=%d];", parentName, childName, weight) + System.lineSeparator();
                            Files.write(Paths.get("output.dot"), output.getBytes(), StandardOpenOption.APPEND);
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
            Files.write(Paths.get("output.dot"), "}".getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
