package gui.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class PieChartScreen {

    PieChart _pieChart;
    ObservableList<PieChart.Data> _data;

    public PieChartScreen(PieChart pieChart){
        _pieChart = pieChart;
        pieChart.setTitle("JVM Memory Usage");
        _data = FXCollections.observableArrayList(
                new PieChart.Data("Maximum Memory", 1),
                new PieChart.Data("Used Memory", 0)
        );
        _pieChart.setData(_data);
    }

    public void updatePieChart(){

        int mb = 1024*1024;

        Runtime runtime = Runtime.getRuntime();

        double freeMemory = runtime.freeMemory() / mb;

        double totalMemoryUsed = runtime.totalMemory() / mb;

        _data.get(0).setPieValue(totalMemoryUsed);
        _data.get(1).setPieValue(freeMemory);

    }


}
