package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;
import org.graphstream.stream.file.FileSourceFactory;
import org.graphstream.stream.file.dot.DOTParser;
import sample.common.DependencyGraph;

import java.io.*;

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
