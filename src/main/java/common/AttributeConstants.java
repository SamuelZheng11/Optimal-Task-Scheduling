package common;

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

