package gui.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;


public class PieChartScreen {


    private PieChart _pieChart;
    private ObservableList<PieChart.Data> _data;

    public PieChartScreen(PieChart pieChart ){
        _pieChart = pieChart;
        pieChart.setTitle("JVM Memory Usage");
        _data = FXCollections.observableArrayList(
                new PieChart.Data("Free Memory", 1),
                new PieChart.Data("Used Memory", 0)
        );
        _pieChart.setData(_data);
        _pieChart.setLabelsVisible(true);
    }

    public void updatePieChart(){

        int mb = 1024*1024;

        Runtime runtime = Runtime.getRuntime();

        long freeMemory = runtime.freeMemory() / mb;

        long totalMemoryUsed = runtime.totalMemory() / mb;

        _data.get(0).setName("Free Memory\n" + freeMemory + " Mb");
        _data.get(0).setPieValue(freeMemory);

        _data.get(1).setName("Used Memory\n" + totalMemoryUsed + " Mb");
        _data.get(1).setPieValue(totalMemoryUsed);

    }


}
