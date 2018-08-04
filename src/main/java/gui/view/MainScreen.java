package gui.view;


import gui.controller.MainController;
import gui.model.StatisticsModel;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;


import javax.swing.*;
import java.io.IOException;

public class MainScreen {

    MainController _controller;

    public MainScreen(Stage primaryStage, StatisticsModel model) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainScreen.fxml"));

        Pane pane = loader.load();

        _controller = new MainController(model);
        loader.setController(_controller);

        primaryStage.setTitle("Parallel Task Scheduler");

        SwingNode swingNode = new SwingNode();
        createGanttChart(swingNode);
        pane.getChildren().add(swingNode);

        primaryStage.setScene(new Scene(pane, 1600, 900));
        primaryStage.show();
    }

    public void createGanttChart(SwingNode swingNode){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                CategoryDataset data = _controller.getData();
                final JFreeChart chart = ChartFactory.createStackedBarChart(
                        "Current Optimal Schedule",  // chart title
                        "Processor",                  // domain axis label
                        "Time",                     // range axis label
                        data,                     // data
                        PlotOrientation.HORIZONTAL,    // the plot orientation
                        false,                        // legend
                        true,                        // tooltips
                        false                        // urls
                );


                swingNode.setContent(new ChartPanel(chart));

            }
        });
    }

}
