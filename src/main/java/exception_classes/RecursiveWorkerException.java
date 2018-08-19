package exception_classes;
/**
 * when an exception occurs in the recursion worker class this exception is thrown
 */
public class RecursiveWorkerException extends RuntimeException {
    public RecursiveWorkerException(String msg) {
        super(msg);
    }
}
