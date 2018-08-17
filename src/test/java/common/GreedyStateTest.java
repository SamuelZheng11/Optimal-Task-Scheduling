package common;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GreedyStateTest {

    private static DependencyGraph _dg = DependencyGraph.getGraph();
    private Map<String, TaskDependencyNode> _nodes;
    private List<TaskDependencyNode> _freeTasks;
    private final static String FILEPATH = "Input/example-input-graphs/Nodes_8_Random.dot";

    @BeforeClass
    public static void setGraph(){
        _dg.setFilePath(FILEPATH);
    }

    @Test
    public void createFileTest() throws IOException {
        _dg.parse();

//        TaskDependencyNode nodeAwithEdge = new TaskDependencyNode(2 , aChildren, aParents, "a");
//        TaskDependencyNode nodeBwithEdge = new TaskDependencyNode(3, bChildren, bParents, "b");
//        TaskDependencyNode nodeCwithEdge = new TaskDependencyNode(3, cChildren, cParents,"c");
//        TaskDependencyNode nodeDwithEdge = new TaskDependencyNode(2, dChildren, dParents,"d");
//
//
//        TaskDependencyNode nodeA = new TaskDependencyNode(2 , "a");
//        TaskDependencyNode nodeB = new TaskDependencyNode(3, "b");
//        TaskDependencyNode nodeC = new TaskDependencyNode(3, "c");
//        TaskDependencyNode nodeD = new TaskDependencyNode(2, "d");
//
//        TaskDependencyEdge edgeAB = new TaskDependencyEdge(nodeA, nodeB, 1);
//        TaskDependencyEdge edgeAC = new TaskDependencyEdge(nodeA, nodeC, 2);
//        TaskDependencyEdge edgeBD = new TaskDependencyEdge(nodeB, nodeD, 2);
//        TaskDependencyEdge edgeCD = new TaskDependencyEdge(nodeC, nodeD, 1);
//
//        List<TaskDependencyEdge> aChildren = new ArrayList<TaskDependencyEdge>();
//        aChildren.add(edgeAB);
//        aChildren.add(edgeAC);
//
//        List<TaskDependencyEdge> bChildren = new ArrayList<TaskDependencyEdge>();
//        bChildren.add(edgeBD);
//
//        List<TaskDependencyEdge> cChildren = new ArrayList<TaskDependencyEdge>();
//        cChildren.add(edgeCD);
//
//        List<TaskDependencyEdge> dChildren = new ArrayList<TaskDependencyEdge>();
//
//        List<TaskDependencyEdge> aParents = new ArrayList<TaskDependencyEdge>();
//
//        List<TaskDependencyEdge> bParents = new ArrayList<TaskDependencyEdge>();
//        bParents.add(edgeAB);
//
//        List<TaskDependencyEdge> cParents = new ArrayList<TaskDependencyEdge>();
//        cParents.add(edgeAC);
//
//        List<TaskDependencyEdge> dParents = new ArrayList<TaskDependencyEdge>();
//        dParents.add(edgeBD);
//        dParents.add(edgeCD);

        String inputFileName = "test.dot";

//        GreedyState greedyState = new GreedyState();
//        State state = greedyState.getInitialState(_dg, 2);

//        _dg.generateOutput(state, inputFileName);

    }
}
