package gui.view;


import gui.controller.MainController;
import gui.model.ChartModel;
import gui.model.StatisticsModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

public class MainScreen {

    MainController _controller;
    private IntegerProperty timeSeconds = new SimpleIntegerProperty(0);

    public MainScreen(Stage primaryStage, StatisticsModel model) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainScreen.fxml"));
        Pane pane = loader.load();

        //Components for statistics
        VBox vbox = new VBox();
        Label timerLabel = new Label();
        timerLabel.textProperty().bind(timeSeconds.asString());

        vbox.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        vbox.setPrefWidth(100);
        vbox.setPrefHeight(500);


        _controller = new MainController(model);
        loader.setController(_controller);

        primaryStage.setTitle("Parallel Task Scheduler");

        Canvas canvas = new Canvas(600, 500);
        ChartScreen chart = new ChartScreen(canvas.getGraphicsContext2D());

        vbox.relocate(canvas.getWidth(),0);

        // For mocking change to null and update with new method
        chart.drawChart(new ChartModel());

        pane.getChildren().add(canvas);
        pane.getChildren().add(vbox);

        primaryStage.setScene(new Scene(pane, 1600, 900));
        primaryStage.show();
    }

    public void startTimer(){

    }

    public void stopTimer(){

    }



}
