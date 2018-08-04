package gui.controller;

import gui.model.StatisticsModel;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;

public class MainController {

    StatisticsModel _model;

    public MainController(StatisticsModel model){
        _model = model;
    }

    public CategoryDataset getData(){

        // todo implement logic

        final double[][] data = new double[][] {
                {1.0, 43.0, 35.0, 58.0, 54.0, 77.0, 71.0, 89.0},
                {54.0, 75.0, 63.0, 83.0, 43.0, 46.0, 27.0, 13.0},
                {41.0, 33.0, 22.0, 34.0, 62.0, 32.0, 42.0, 34.0}
        };
        return DatasetUtilities.createCategoryDataset("Series ", "", data);

    };

}
