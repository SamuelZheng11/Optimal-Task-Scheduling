package gui.controller;

import gui.model.StatisticsModel;
import gui.view.PieChartScreen;
import gui.view.StatisticsScreen;
import javafx.concurrent.Task;

public class MainController {

    StatisticsModel _model;


    public void setModel(StatisticsModel model){
        _model = model;
    }

    public void startStatistics(PieChartScreen pieChartScreen, StatisticsScreen statisticsScreen){

        Task task = new Task<Void>() {
            @Override
            public Void call() {
                while(true){
                    statisticsScreen.updateStatisticsScreen();
                    pieChartScreen.updatePieChart();
                }
            }
        };

        new Thread(task).start();
    }


}
