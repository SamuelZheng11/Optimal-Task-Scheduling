package main;

import javafx.application.Application;
import javafx.stage.Stage;
import common.DependencyGraph;
import org.apache.commons.cli.*;

public class Main extends Application {
    private DependencyGraph dg = DependencyGraph.getGraph();

    @Override
    public void start(Stage primaryStage) throws Exception{

        CommandLine commands = getCommands();
        dg.parse();


    }

    private CommandLine getCommands() throws ParseException
    {
        Options options = new Options();
        options.addOption("p", true, "The number of processors for the algorithm to run on");
        options.addOption("v", "Whether to visualise the search");
        options.addOption("o", true,"The output file");
        CommandLineParser parser = new DefaultParser();
        String[] params = new String[getParameters().getRaw().size()];
        params = getParameters().getRaw().toArray(params);
        return parser.parse(options, params);
    }


    public static void main(String[] args) {
        launch(args);
    }

}
