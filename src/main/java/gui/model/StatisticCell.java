package gui.model;

/**
 * This class is used for the purposes of representing a single cell to display data on the statistic model class
 */
public class StatisticCell {

    public String statName;
    public String statField;

    public StatisticCell(String name, String field){
        statName = name;
        statField = field;
    }

    public String getStatName(){
        return statName;
    }

    public void setStatName(String name){
        statName = statName;
    }

    public String getStatField(){
        return statField;
    }

    public void setStatField(String field){
        statField = statField;
    }

}
