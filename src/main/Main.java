package main;

import javafx.application.Application;
import javafx.stage.Stage;
import common.DependencyGraph;

public class Main extends Application {
    private DependencyGraph dg = DependencyGraph.getGraph();

    @Override
    public void start(Stage primaryStage) throws Exception{
        dg.parse();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
