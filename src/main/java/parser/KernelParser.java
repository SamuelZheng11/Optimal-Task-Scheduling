package parser;

import javafx.application.Application;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import static com.sun.javafx.application.ParametersImpl.getParameters;

public class KernelParser extends AbstractParser {

    private CommandLine _commands;

    public KernelParser(Application application){
        try {
            _commands = getCommands(application);
            _filePath = _commands.getArgs()[0];
        }catch(ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean displayVisuals() {
        return _commands.hasOption('v');
    }

    @Override
    public int getMaxThreads() {
        if(_commands.getOptionValue('p') != null){
            _maxThreads = Integer.valueOf(_commands.getOptionValue('p'));
        }
        return _maxThreads;
    }

    @Override
    public String getOutputFileName() {
        String outputName = _commands.getOptionValue('o');

        if(outputName == null){
            String[] outputNameWithFileDirectory = _filePath.split(".dot")[0].split("/");
            outputName = outputNameWithFileDirectory[outputNameWithFileDirectory.length-1] + _suffix + _format;
        } else {
            outputName += _format;
        }

        return outputName;
    }

    @Override
    public String getFileSuffix() {
        return _suffix;
    }

    @Override
    public String getOutputFileFormat() {
        return _format;
    }

    private CommandLine getCommands(Application application) throws ParseException
    {
        Options options = new Options();
        options.addOption("p", true, "The number of processors for the algorithm to run on");
        options.addOption("v", "Whether to visualise the search");
        options.addOption("o", true,"The output file");
        DefaultParser parser = new DefaultParser();
        String[] params = new String[getParameters(application).getRaw().size()];
        params = getParameters(application).getRaw().toArray(params);
        return parser.parse(options, params);
    }

    @Override
    public String getFilePath() {
        return _commands.getArgs()[0];
    }

    @Override
    public int getProcessorNo(){
        return Integer.valueOf(_commands.getArgs()[1]);
    }
}
