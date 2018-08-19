package gui.controller;

import gui.model.StatisticsModel;
import gui.view.ChartScreen;
import gui.view.PieChartScreen;
import gui.view.StatisticsScreen;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import parallelprocesses.Main;
import sun.util.resources.cldr.mas.CalendarData_mas_KE;


public class MainController {

    StatisticsModel _model;
    Main _main;

    public void setModel(StatisticsModel model, Main main) {
        _model = model;
        _main = main;
    }

    public void initStatistics(PieChartScreen pieChartScreen, StatisticsScreen statisticsScreen, ChartScreen chartScreen) {

        chartScreen.drawChart(_model.getChartModel());
        statisticsScreen.updateStatisticsScreen(_model);
        pieChartScreen.updatePieChart();

        new Thread(() -> {
            while (_model.getRunning()) {
                try {
                    Thread.sleep(100); // Wait for 500 msec before updating
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(_model.getUpdated()){
                    Platform.runLater(() -> chartScreen.drawChart(_model.getChartModel()));// Update on JavaFX Application Thread
                    _model.setUpdated(false);
                }

                Platform.runLater(() -> statisticsScreen.updateStatisticsScreen(_model));
            }
            _main.generateOutputAndClose();
            System.out.println("Finished");
        }).start();

        new Thread(() -> {
            while(_model.getRunning()) {
                try {
                    Thread.sleep(100); // Wait for 500 msec before updating
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> pieChartScreen.updatePieChart());
            }
        }).start();
    }
}






