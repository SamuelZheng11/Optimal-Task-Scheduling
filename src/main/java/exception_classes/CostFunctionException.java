package exception_classes;

/**
 * when an exception occurs in the cost function class this exception is thrown
 */
public class CostFunctionException extends RuntimeException {
    public CostFunctionException(String msg) {
        super(msg);
    }
}
