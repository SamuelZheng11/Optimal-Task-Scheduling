package parallelprocesses;

/**
 * enum that holds the default values if they are not specified in the command line
 */
public enum Defaults {
    SUFFIX("-output"), FORMAT(".dot"), MAXTHREADS("1"), OUTPUT("DEFAULT"), BOOSTMULTIPLIER("100");

    private final String TEXT;

    private Defaults(String text){
        TEXT = text;
    }

    @Override
    public String toString(){
        return TEXT;
    }
}
