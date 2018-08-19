package parser;

/**
 * interface used by the parser test class
 */
public class TestParser implements ArgumentParser {

    @Override
    public int getProcessorNo() {
        return 0;
    }

    @Override
    public String getFileSuffix() {
        return null;
    }

    @Override
    public String getOutputFileFormat() {
        return null;
    }

    @Override
    public String getOutputFileName() {
        return null;
    }

    @Override
    public String getFilePath() {
        return null;
    }

    @Override
    public boolean displayVisuals() {
        return false;
    }

    @Override
    public int getMaxThreads() {
        return 1;
    }

    @Override
    public int getBoostMultiplier() {
        return 5;
    }

    public void setProcessorNo(int processors) {

    }

    public void setFileSuffix() {

    }

    public void setOutputFileFormat() {

    }
}
