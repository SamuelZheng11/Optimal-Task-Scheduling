package test;

import common.DependencyGraph;
import common.TaskDependencyNode;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
}