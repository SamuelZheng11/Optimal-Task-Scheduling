package gui.view;

import gui.model.StatisticCell;
import gui.model.StatisticsModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.Duration;


public class StatisticsScreen {

    TableView _tableView;
    TableColumn _statName;
    TableColumn _statField;

    ObservableList<StatisticCell> _data;

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
                new StatisticCell("Time Elapsed", ""),
                new StatisticCell("Number of processors", ""),
                new StatisticCell("Number of threads", ""),
                new StatisticCell("Greedy Schedule Time", ""),
                new StatisticCell("Current best schedule time", "")
        );

        _tableView.setItems(_data);

        _tableView.getColumns().addAll(_statName, _statField);
    }

    public void updateStatisticsScreen(StatisticsModel model){

        setTime(model.getStartTime());
        _data.get(1).statField = Integer.toString(model.getState().getJobLists().size());
        _data.get(2).statField = Long.toString(model.getThreadNumber());



        _tableView.refresh();
    }

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

        _data.get(0).statField = sb.toString();


    }

    private void append(StringBuilder sb, long value, String text) {
        if (value > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(value).append(text);
        }
    }





}
