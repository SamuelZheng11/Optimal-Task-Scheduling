package gui.view;


import gui.controller.MainController;
import gui.listeners.ModelChangeListener;
import gui.model.ChartModel;
import gui.model.StatisticsModel;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.PieChart;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class MainScreen{

    MainController _controller;
    private ChartScreen _chart;
    private ChartScreen _greedyChart;

    public MainScreen(Stage primaryStage, StatisticsModel statModel) throws IOException {

        _controller = new MainController();
        _controller.setModel(statModel);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainScreen.fxml"));
        loader.setController(_controller);
        SplitPane pane = loader.load();

        primaryStage.setTitle("Parallel Task Scheduler");

        Canvas canvas = ((Canvas)loader.getNamespace().get("chartCanvas"));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        _chart = new ChartScreen(gc);
        _chart.drawChart(statModel.getChartModel());

        Canvas greedyCanvas = ((Canvas)loader.getNamespace().get("greedyChartCanvas"));
        GraphicsContext gc2 = greedyCanvas.getGraphicsContext2D();
        _greedyChart = new ChartScreen(gc2);
        _greedyChart.drawChart(statModel.getChartModel());

        PieChart pieChart = ((PieChart)loader.getNamespace().get("pieChart"));
        PieChartScreen pieChartScreen = new PieChartScreen(pieChart);

        TableView tableView = ((TableView)loader.getNamespace().get("statisticsTable"));
        StatisticsScreen statisticsScreen = new StatisticsScreen(tableView);

        Scene scene = new Scene(pane, 1280, 720);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });



        _controller.initStatistics(pieChartScreen, statisticsScreen, _chart);

    }



}
