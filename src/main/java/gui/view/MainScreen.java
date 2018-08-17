package gui.view;


import gui.controller.MainController;
import gui.listeners.ModelChangeListener;
import gui.model.ChartModel;
import gui.model.StatisticsModel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.PieChart;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

public class MainScreen implements ModelChangeListener {

    MainController _controller;
    private ChartScreen _chart;
    private ChartModel _chartModel;

    public MainScreen(Stage primaryStage, StatisticsModel statModel, ChartModel chartModel) throws IOException {

        _controller = new MainController();
        _chartModel = chartModel;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainScreen.fxml"));
        loader.setController(_controller);
        SplitPane pane = loader.load();


        primaryStage.setTitle("Parallel Task Scheduler");

        Canvas canvas = ((Canvas)loader.getNamespace().get("chartCanvas"));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        _chart = new ChartScreen(gc);

        PieChart pieChart = ((PieChart)loader.getNamespace().get("pieChart"));
        PieChartScreen pieChartScreen = new PieChartScreen(pieChart);


        TableView tableView = ((TableView)loader.getNamespace().get("statisticsTable"));
        StatisticsScreen statisticsScreen = new StatisticsScreen(tableView);
        _controller.startStatistics(pieChartScreen, statisticsScreen);

        // For mocking change to null and update with new method
        _chart.drawChart(chartModel);

        primaryStage.setScene(new Scene(pane, 1280, 720));
        primaryStage.show();
    }

    public void update(){
        _chart.drawChart(_chartModel);
    }


}
