package parallelprocesses;


import common.DependencyGraph;
import common.State;
import javafx.concurrent.Task;
import org.graphstream.graph.Graph;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ValidityTest {
    private DependencyGraph _dg = DependencyGraph.getGraph();

    @Test
    public void testAll(){
        TestMain.main(new String[]{"Input/example-input-graphs/Nodes_7_OutTree.dot", "2"});

        System.out.println("Control Returned");
        Graph g = DependencyGraph.getGraph().getInputGraph();
        State s = RecursionStore.getBestState();
//        assertTrue(true);
    }

    private void isValid(State state, Graph graph){
    }
}
