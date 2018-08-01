package gui.controller;

import gui.model.StatisticsModel;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

public class MainController {

    StatisticsModel _model;

    public MainController(StatisticsModel model){
        _model = model;
    }

    public IntervalCategoryDataset getData(){

        // todo implement logic

        final TaskSeries schedule = new TaskSeries("Schedule");
        final TaskSeries schedule2 = new TaskSeries("Schedule2");

        schedule.add(new Task("Processor 1", new SimpleTimePeriod(0, 2)));
        schedule.add(new Task("Processor 1", new SimpleTimePeriod(3, 5)));
        schedule.add(new Task("Processor 2", new SimpleTimePeriod(1, 4)));

        schedule2.add(new Task("Processor 1", new SimpleTimePeriod(0, 2)));


        final TaskSeriesCollection collection = new TaskSeriesCollection();
        collection.add(schedule);
        collection.add(schedule2);


        return collection;

    };

}
