package parallelprocesses;


import common.DependencyGraph;
import common.Job;
import common.State;
import javafx.concurrent.Task;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.assertTrue;

public class ValidityTest {
    private DependencyGraph _dg = DependencyGraph.getGraph();

    @Test
    public void testAll(){
        TestMain.main(new String[]{"Input/example-input-graphs/Nodes_7_OutTree.dot", "2"});

        System.out.println("Control Returned");
        Graph g = DependencyGraph.getGraph().getInputGraph();
        State s = RecursionStore.getBestState();

    }

    private boolean isValid(State state, Graph graph){
        List<Node> freeNodes = new ArrayList<Node>();
        for (Node n : graph.getNodeSet()){
            if(n.getInDegree() == 0){
                freeNodes.add(n);
            }
        }


        for(int i = 0; i < state.getJobLists().size(); i++){

        }
    }
}
