package gui.view;


import gui.controller.MainController;
import gui.model.ChartModel;
import gui.model.StatisticsModel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

public class MainScreen {

    MainController _controller;

    public MainScreen(Stage primaryStage, StatisticsModel model) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainScreen.fxml"));
        Pane pane = loader.load();

        _controller = new MainController(model);
        loader.setController(_controller);

        primaryStage.setTitle("Parallel Task Scheduler");

        Canvas canvas = new Canvas(1600, 900);
        ChartScreen chart = new ChartScreen(canvas.getGraphicsContext2D());

        // For mocking change to null and update with new method
        chart.drawChart(new ChartModel());

        pane.getChildren().add(canvas);

        primaryStage.setScene(new Scene(pane, 1600, 900));
        primaryStage.show();
    }



}
