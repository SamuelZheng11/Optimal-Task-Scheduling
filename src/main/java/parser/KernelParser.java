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
            return _maxThreads;
        }else{
            return 1;
        }

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
    public int getBoostMultiplier() {
        if(_commands.getOptionValue('b') != null){
            _boostMultiplier = Integer.valueOf(_commands.getOptionValue('b'));
        }
        return _boostMultiplier;
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
        options.addOption("b",true,"Initial boost Value");
        DefaultParser parser = new DefaultParser();
        String[] params = new String[getParameters(application).getRaw().size()];
        params = getParameters(application).getRaw().toArray(params);
        return parser.parse(options, params);
    }

    @Override
    public String getFilePath() {
        try {
            return _commands.getArgs()[0];
            //Catching exception e is god awful practice but this is the exception thrown by apache cli
        }catch (Exception e){
            System.out.println("Please specify the input filepath.");
            System.out.println("Usage: java -jar JARNAME InputFilePath Processors [-v] [-p]");
            System.exit(1);
        }
        return _commands.getArgs()[0];
    }

    @Override
    public int getProcessorNo(){
        try{
            return Integer.valueOf(_commands.getArgs()[1]);
        }catch(NumberFormatException e){
            System.out.println("Please specify the number of processors the tasks are to be scheduled on after the input filepath.");
            System.out.println("Usage: java -jar JARNAME InputFilePath Processors [-v] [-p]");
            System.exit(1);
        }
        catch (Exception e){
            System.out.println("Please specify the number of processors the tasks are to be scheduled on after the input filepath.");
            System.out.println("Usage: java -jar JARNAME InputFilePath Processors [-v] [-p]");
            System.exit(1);
        }
        return Integer.valueOf(_commands.getArgs()[1]);
    }
}
