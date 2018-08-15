package gui.controller;

import gui.model.StatisticsModel;
import gui.view.PieChartScreen;
import javafx.concurrent.Task;

public class MainController {

    StatisticsModel _model;


    public void setModel(StatisticsModel model){
        _model = model;
    }

    public void startPieChart(PieChartScreen pieChartScreen){

        Task task = new Task<Void>() {
            @Override
            public Void call() {
                while(true){
                    pieChartScreen.UpdatePieChart();
                }
            }
        };

        new Thread(task).start();



    }


}
