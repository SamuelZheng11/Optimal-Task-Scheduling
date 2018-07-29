package cost_function;

import exception_classes.CostFunctionException;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class CostFunctionService {

    TaskJob node;
    TaskJob[] Processor;
    State currentState


    private CostFunctionService(Job node, Job[] Processor, State currentState) {
        this.node = node;
        this.Processor = Processor;
        this.currentState = currentState;
    }

    public ArrayList calculateBestPath(ArrayList state) {
        this.node.getParent();
    }
}
