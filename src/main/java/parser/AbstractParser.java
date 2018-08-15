package parser;

import parallelprocesses.Defaults;


public abstract class AbstractParser implements ArgumentParser {
    protected int _processor;
    protected String _format = Defaults.FORMAT.toString();
    protected String _suffix = Defaults.SUFFIX.toString();
    protected String _output;
    protected String _filePath;
    protected int _maxThreads = Integer.valueOf(Defaults.MAXTHREADS.toString());

    @Override
    public int getProcessorNo() {
        return _processor;
    }

    @Override
    public abstract String getOutputFileName();


    @Override
    public String getFileSuffix() {
        return _suffix;
    }

    @Override
    public String getOutputFileFormat() {
        return _format;
    }

    @Override
    public abstract String getFilePath();

    @Override
    public abstract boolean displayVisuals();

    @Override
    public int getMaxThreads(){
        return _maxThreads;
    };
}