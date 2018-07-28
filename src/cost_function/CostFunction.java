package cost_function;

import exception_classes.CostFunctionException;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class CostFunction {

    ArrayList currentState;
    Node nodeToAdd;
    ArrayList previousState;


    private CostFunction(ArrayList currentState, Node nextScheduledNode, ArrayList previousState) {
        this.currentState = currentState;
        this.nodeToAdd = nextScheduledNode;
        this.previousState = previousState;
    }

    public ArrayList calculateBestPath(ArrayList state) {
        return null;
    }
}
