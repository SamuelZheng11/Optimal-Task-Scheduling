package gui.view;

import gui.model.StatisticCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


public class StatisticsScreen {

    TableView _tableView;

    public StatisticsScreen(TableView tableView){

        _tableView = tableView;
        tableView.setEditable(false);

        TableColumn statName = new TableColumn("");
        statName.setCellValueFactory(new PropertyValueFactory<>("statName"));
        statName.setMinWidth(250);
        statName.setMaxWidth(250);

        TableColumn stat = new TableColumn("");
        stat.setCellValueFactory(new PropertyValueFactory<>("statField"));
        stat.setMinWidth(116);
        stat.setMaxWidth(116);

        ObservableList<StatisticCell> data = FXCollections.observableArrayList(
                new StatisticCell("Time Elapsed", ""),
                new StatisticCell("Number of processors", ""),
                new StatisticCell("Number of threads", ""),
                new StatisticCell("Number of branches pruned", ""),
                new StatisticCell("Current best schedule time", "")
        );

        _tableView.setItems(data);

        _tableView.getColumns().addAll(statName, stat);
    }

    public void updateStatisticsScreen(){


    }



}
