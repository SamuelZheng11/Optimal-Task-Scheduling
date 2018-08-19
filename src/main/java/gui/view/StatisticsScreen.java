package gui.view;

import gui.model.StatisticCell;
import gui.model.StatisticsModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.Duration;

/**
 * This class renders the entire statistics table on the GUI when the search visualisation is enabled
 */
public class StatisticsScreen {

    TableView _tableView;
    TableColumn _statName;
    TableColumn _statField;

    ObservableList<StatisticCell> _data;

    /**
     * create the table itself for the render
     * @param tableView
     */
    public StatisticsScreen(TableView tableView){

        _tableView = tableView;
        _tableView.setEditable(false);

        _statName = new TableColumn("");
        _statName.setCellValueFactory(new PropertyValueFactory<>("statName"));
        _statName.setMinWidth(250);
        _statName.setMaxWidth(250);

        _statField = new TableColumn("");
        _statField.setCellValueFactory(new PropertyValueFactory<>("statField"));
        _statField.setMinWidth(116);
        _statField.setMaxWidth(116);

        _data = FXCollections.observableArrayList(
                new StatisticCell("Input Graph", ""),
                new StatisticCell("Time Elapsed", ""),
                new StatisticCell("Status", ""),
                new StatisticCell("Number of processors", ""),
                new StatisticCell("Number of threads", ""),
                new StatisticCell("Greedy Schedule Time", ""),
                new StatisticCell("Current best schedule time", ""),
                new StatisticCell("Total Number of Branches", ""),
                new StatisticCell("Branches Searched", "")
        );

        _tableView.setItems(_data);

        _tableView.getColumns().addAll(_statName, _statField);
    }

    /**
     * when the controller tries to update the view it calls this method to get the new information from the mediator pattern
     * @param model
     */
    public void updateStatisticsScreen(StatisticsModel model){

        _data.get(0).statField = model.getInputGraphName();
        setTime(model.getStartTime());
        if(model.getRunning()){
            _data.get(2).statField = "Processing";
        }else{
            _data.get(2).statField = "Complete";
        }

        _data.get(3).statField = Integer.toString(model.getState().getJobLists().size());
        _data.get(4).statField = Long.toString(model.getThreadNumber());
        if(model.getGreedyChartModel() != null){
            _data.get(5).statField = Double.toString(model.getGreedyChartModel().getMaximumTime());
        }
        _data.get(6).statField = Double.toString(model.getState().getHeuristicValue());
        _data.get(7).statField = Long.toString(model.getTotalBranch());
        _data.get(8).statField = Long.toString(model.getBranchesSearched());

        _tableView.refresh();
    }

    /**
     * Used to calculate the amount of time passed. This method is called by updateStatisticsScreen to get the time that has passed
     * @param startTime
     */
    private void setTime(long startTime){

        long nanoseconds = System.nanoTime() - startTime;

        long remainingTime = nanoseconds;
        StringBuilder sb = new StringBuilder();
        long seconds = remainingTime / 1000000000;
        long days = seconds / (3600 * 24);
        append(sb, days, "d");
        seconds -= (days * 3600 * 24);
        long hours = seconds / 3600;
        append(sb, hours, "h");
        seconds -= (hours * 3600);
        long minutes = seconds / 60;
        append(sb, minutes, "m");
        seconds -= (minutes * 60);
        append(sb, seconds, "s");
        long nanos = remainingTime % 1000;
        append(sb, nanos, "ms");

        _data.get(1).statField = sb.toString();


    }

    /**
     * used to format the time string on the statistic model
     * @param sb stringBuilder class object
     * @param value the value of the time unit (eg 1, 2, 100)
     * @param text the suffix of the time unit (s(econds), h(our), d(ays))
     */
    private void append(StringBuilder sb, long value, String text) {
        if (value > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(value).append(text);
        }
    }
}
