package parser;

/**
 * interface to be implemented by any argument parser which handles command line arguments
 */
public interface ArgumentParser {
    public int getProcessorNo();

    public String getFileSuffix();

    public String getOutputFileFormat();

    public String getOutputFileName();

    public String getFilePath();

    public boolean displayVisuals();

    public int getMaxThreads();

    public int getBoostMultiplier();
}
