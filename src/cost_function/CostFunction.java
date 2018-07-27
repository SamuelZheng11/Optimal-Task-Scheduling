package cost_function;

import exception_classes.CostFunctionException;

import java.util.ArrayList;

public class CostFunction {

    private static final CostFunction instance = new CostFunction();

    private CostFunction() {
    }

    public static CostFunction getCostFunctionInstance() {
        if (instance == null) {
            throw new CostFunctionException("The CostFunction singleton instance is null");
        }
        return instance;
    }

    public ArrayList calculateBestPath(ArrayList graph) {
        return null;
    }
}
