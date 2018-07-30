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

import java.io.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        String filePath = "Input/example-input-graphs/Nodes_7_OutTree.dot";
        try {
            FileSource fs = new FileSourceDOT();
            Graph g = new DefaultGraph("Graph");
            fs.addSink(g);
            fs.readAll(filePath);
            g.display();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

}
