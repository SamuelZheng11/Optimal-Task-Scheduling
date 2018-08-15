package parallelprocesses;

public enum Defaults {
    SUFFIX("-output"), FORMAT(".dot"), MAXTHREADS("1"), OUTPUT("DEFAULT");

    private final String TEXT;

    private Defaults(String text){
        TEXT = text;
    }

    @Override
    public String toString(){
        return TEXT;
    }
}
