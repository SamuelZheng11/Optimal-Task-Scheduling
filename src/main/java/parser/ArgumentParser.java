package parser;

public interface ArgumentParser {
    public int getProcessorNo();

    public String getFileSuffix();

    public String getOutputFileFormat();

    public String getOutputFileName();

    public String getFilePath();

    public boolean displayVisuals();

    public int getMaxThreads();
}
