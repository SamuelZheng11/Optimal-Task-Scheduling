package common;

/**
 * constants that are used in the DOT/GraphStream format
 */
public enum AttributeConstants {

    WEIGHT("Weight");

    private final String TEXT;

    private AttributeConstants(String text){
        TEXT = text;
    }

    @Override
    public String toString(){
        return TEXT;
    }
}

