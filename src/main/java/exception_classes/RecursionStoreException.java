package exception_classes;
/**
 * when an exception occurs in the recursion store class this exception is thrown
 */
public class RecursionStoreException extends RuntimeException {
    public RecursionStoreException(String msg){
        super(msg);
    }
}
