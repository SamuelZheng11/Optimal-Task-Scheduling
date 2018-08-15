package gui.view;


import gui.controller.MainController;
import gui.model.ChartModel;
import gui.model.StatisticsModel;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.util.Duration;

import java.io.IOException;

public class MainScreen {

    MainController _controller;
    private Timeline timeline;
    private DoubleProperty timeSeconds = new SimpleDoubleProperty(),
            splitTimeSeconds = new SimpleDoubleProperty();
    private Duration time = Duration.ZERO, splitTime = Duration.ZERO;
    private LongProperty memoryBytes = new SimpleLongProperty();

    public MainScreen(Stage primaryStage, StatisticsModel model) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainScreen.fxml"));
        Pane pane = loader.load();

        //Components for statistics
        VBox vbox = new VBox(20);
        Label timerTitleLabel = new Label("Time(secs)");
        Label timerLabel = new Label();
        Label memoryTitleLabel = new Label("Memory Used(MB)");
        Label memoryLabel = new Label();

        VBox memoryBox = new VBox(10);
        VBox timeBox = new VBox(10);


        timerLabel.textProperty().bind(timeSeconds.asString());
        timerLabel.setAlignment(Pos.CENTER);

        memoryLabel.textProperty().bind(memoryBytes.asString());

        timeBox.getChildren().addAll(timerTitleLabel,timerLabel);
        memoryBox.getChildren().addAll(memoryTitleLabel,memoryLabel);

        _controller = new MainController(model);
        loader.setController(_controller);

        primaryStage.setTitle("Parallel Task Scheduler");

        Canvas canvas = new Canvas(600, 500);
        ChartScreen chart = new ChartScreen(canvas.getGraphicsContext2D());

        vbox.relocate(canvas.getWidth(),0);

        // For mocking change to null and update with new method
        chart.drawChart(new ChartModel());

        vbox.getChildren().addAll(timeBox, memoryBox);

        pane.getChildren().add(canvas);
        pane.getChildren().add(vbox);

        primaryStage.setScene(new Scene(pane, 1600, 900));
        primaryStage.show();
        startTimer();
    }

    public void startTimer(){
        if (timeline != null) {
            splitTime = Duration.ZERO;
            splitTimeSeconds.set(splitTime.toSeconds());
        } else {
            timeline = new Timeline(
                    new KeyFrame(Duration.millis(100),
                            t -> {
                                Duration duration = ((KeyFrame)t.getSource()).getTime();
                                time = time.add(duration);
                                splitTime = splitTime.add(duration);
                                timeSeconds.set(time.toSeconds());
                                splitTimeSeconds.set(splitTime.toSeconds());
                            })
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }

    public void stopTimer(){
        timeline.stop();
    }



}
