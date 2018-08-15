package gui.view;


import gui.controller.MainController;
import gui.model.ChartModel;
import gui.model.StatisticsModel;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

public class MainScreen {

    MainController _controller;

    public MainScreen(Stage primaryStage, StatisticsModel model) throws IOException {

        _controller = new MainController();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainScreen.fxml"));
        loader.setController(_controller);
        SplitPane pane = loader.load();


        primaryStage.setTitle("Parallel Task Scheduler");

        Canvas canvas = ((Canvas)loader.getNamespace().get("chartCanvas"));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        ChartScreen chart = new ChartScreen(gc);

        // For mocking change to null and update with new method
        chart.drawChart(new ChartModel());

        primaryStage.setScene(new Scene(pane, 1280, 720));
        primaryStage.show();
    }



}
