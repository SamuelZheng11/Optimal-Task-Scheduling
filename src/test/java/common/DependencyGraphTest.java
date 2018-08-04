package common;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class DependencyGraphTest {

    //TESTED USING Nodes_9_SeriesParallel.dot
    private static DependencyGraph _dg = DependencyGraph.getGraph();
    private Map<String, TaskDependencyNode> _nodes;
    private List<TaskDependencyNode> _freeTasks;
    private final static String FILEPATH = "Input/example-input-graphs/Nodes_9_SeriesParallel.dot";

    @BeforeClass
    public static void setGraph(){
        _dg.setFilePath(FILEPATH);
    }

    // If input null, return only root nodes (no parents)
    @Test
    public void getFreeTasks1() {
        _dg.parse();
        _nodes = _dg.getNodes();
        _freeTasks = _dg.getFreeTasks(null);
        List<TaskDependencyNode> correctTasks = new ArrayList<TaskDependencyNode>();
        correctTasks.add(_nodes.get("0"));
        assertEquals(_freeTasks, correctTasks);
    }

    // Test for scheduled node that it removes it from list and adds children nodes (multiple children).
    @Test
    public void getFreeTasks2() {
        _dg.parse();
        _nodes = _dg.getNodes();

        _freeTasks = _dg.getFreeTasks(_nodes.get("0"));
        List<TaskDependencyNode> correctTasks = new ArrayList<TaskDependencyNode>();
        correctTasks.add(_nodes.get("2"));
        correctTasks.add(_nodes.get("3"));
        correctTasks.add(_nodes.get("4"));
        assertEquals(correctTasks, _freeTasks);
    }

    // As above but single child.
    @Test
    public void getFreeTasks3() {
        _dg.parse();
        _nodes = _dg.getNodes();
        _freeTasks = _dg.getFreeTasks(_nodes.get("4"));
        List<TaskDependencyNode> correctTasks = new ArrayList<TaskDependencyNode>();
        correctTasks.add(_nodes.get("2"));
        correctTasks.add(_nodes.get("3"));
        correctTasks.add(_nodes.get("1"));

        assertEquals(_freeTasks, correctTasks);

    }

    // If node has multiple parents it will only be added into the list only once.
    @Test
    public void getFreeTasks4() {
        _dg.parse();
        _nodes = _dg.getNodes();
        _freeTasks = _dg.getFreeTasks(_nodes.get("3"));

        List<TaskDependencyNode> correctTasks = new ArrayList<TaskDependencyNode>();
        correctTasks.add(_nodes.get("2"));
        correctTasks.add(_nodes.get("1"));

        assertEquals(_freeTasks, correctTasks);
    }


    @Test
    public void getFreeTasks5() {
        _dg.parse();
        _nodes = _dg.getNodes();
        _freeTasks = _dg.getFreeTasks(_nodes.get("2"));

        List<TaskDependencyNode> correctTasks = new ArrayList<TaskDependencyNode>();
        correctTasks.add(_nodes.get("1"));
        correctTasks.add(_nodes.get("6"));
        correctTasks.add(_nodes.get("7"));
        correctTasks.add(_nodes.get("8"));

        assertEquals(_freeTasks, correctTasks);
    }

    @Test
    public void getFreeTasks6() {
        _dg.parse();
        _nodes = _dg.getNodes();
        _freeTasks = _dg.getFreeTasks(_nodes.get("8"));

        List<TaskDependencyNode> correctTasks = new ArrayList<TaskDependencyNode>();
        correctTasks.add(_nodes.get("1"));
        correctTasks.add(_nodes.get("6"));
        correctTasks.add(_nodes.get("7"));
        correctTasks.add(_nodes.get("5"));

        assertEquals(_freeTasks, correctTasks);
    }

    @Test
    public void getFreeTasks7() {
        _dg.parse();
        _nodes = _dg.getNodes();
        _freeTasks = _dg.getFreeTasks(_nodes.get("1"));

        List<TaskDependencyNode> correctTasks = new ArrayList<TaskDependencyNode>();
        correctTasks.add(_nodes.get("6"));
        correctTasks.add(_nodes.get("7"));
        correctTasks.add(_nodes.get("5"));

        assertEquals(_freeTasks, correctTasks);
    }

    @Test
    public void getFreeTasks8() {
        _dg.parse();
        _nodes = _dg.getNodes();
        _freeTasks = _dg.getFreeTasks(_nodes.get("6"));

        List<TaskDependencyNode> correctTasks = new ArrayList<TaskDependencyNode>();

        correctTasks.add(_nodes.get("7"));
        correctTasks.add(_nodes.get("5"));

        assertEquals(_freeTasks, correctTasks);
    }

    @Test
    public void getFreeTasks9() {
        _dg.parse();
        _nodes = _dg.getNodes();
        _freeTasks = _dg.getFreeTasks(_nodes.get("5"));

        List<TaskDependencyNode> correctTasks = new ArrayList<TaskDependencyNode>();

        correctTasks.add(_nodes.get("7"));


        assertEquals(_freeTasks, correctTasks);
    }

    @Test
    public void getFreeTasks10() {
        _dg.parse();
        _nodes = _dg.getNodes();
        _freeTasks = _dg.getFreeTasks(_nodes.get("7"));

        List<TaskDependencyNode> correctTasks = new ArrayList<TaskDependencyNode>();

        assertEquals(_freeTasks, correctTasks);
    }


    @Test
    public void createFileTest() throws IOException {

        TaskDependencyNode nodeA = new TaskDependencyNode(2 , "a");
        TaskDependencyNode nodeB = new TaskDependencyNode(3, "b");
        TaskDependencyNode nodeC = new TaskDependencyNode(3, "c");
        TaskDependencyNode nodeD = new TaskDependencyNode(2, "d");

        TaskDependencyEdge edgeAB = new TaskDependencyEdge(nodeA, nodeB, 1);
        TaskDependencyEdge edgeAC = new TaskDependencyEdge(nodeA, nodeC, 2);
        TaskDependencyEdge edgeBD = new TaskDependencyEdge(nodeB, nodeD, 2);
        TaskDependencyEdge edgeCD = new TaskDependencyEdge(nodeC, nodeD, 1);

        List<TaskDependencyEdge> aChildren = new ArrayList<TaskDependencyEdge>();
        aChildren.add(edgeAB);
        aChildren.add(edgeAC);

        List<TaskDependencyEdge> bChildren = new ArrayList<TaskDependencyEdge>();
        bChildren.add(edgeBD);

        List<TaskDependencyEdge> cChildren = new ArrayList<TaskDependencyEdge>();
        cChildren.add(edgeCD);

        List<TaskDependencyEdge> dChildren = new ArrayList<TaskDependencyEdge>();

        List<TaskDependencyEdge> aParents = new ArrayList<TaskDependencyEdge>();

        List<TaskDependencyEdge> bParents = new ArrayList<TaskDependencyEdge>();
        bParents.add(edgeAB);

        List<TaskDependencyEdge> cParents = new ArrayList<TaskDependencyEdge>();
        cParents.add(edgeAC);

        List<TaskDependencyEdge> dParents = new ArrayList<TaskDependencyEdge>();
        dParents.add(edgeBD);
        dParents.add(edgeCD);

        TaskDependencyNode nodeAwithEdge = new TaskDependencyNode(2 , aChildren, aParents, "a");
        TaskDependencyNode nodeBwithEdge = new TaskDependencyNode(3, bChildren, bParents, "b");
        TaskDependencyNode nodeCwithEdge = new TaskDependencyNode(3, cChildren, cParents,"c");
        TaskDependencyNode nodeDwithEdge = new TaskDependencyNode(2, dChildren, dParents,"d");

        TaskJob taskA = new TaskJob(2, "a", nodeAwithEdge);
        TaskJob taskB = new TaskJob(3, "b", nodeBwithEdge);
        TaskJob taskC = new TaskJob(3, "c", nodeCwithEdge);
        TaskJob taskD = new TaskJob(2, "d", nodeDwithEdge);
        DelayJob delay = new DelayJob(4);

        List processor1 = new ArrayList<Job>();

        processor1.add(taskA);
        processor1.add(taskB);

        List processor2  = new ArrayList<Job>();

        processor2.add(delay);
        processor2.add(taskC);
        processor2.add(taskD);

        List<List<Job>> finalList = new ArrayList<List<Job>>();

        finalList.add(processor1);
        finalList.add(processor2);

        int[] joblist = new int[5];
        int huristic = 5;

        State state = new State(finalList, joblist, huristic);

        String inputFileName = "test.dot";

        _dg.generateOutput(state, inputFileName);


    }
}