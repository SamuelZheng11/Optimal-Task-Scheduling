package gui.model;

import common.State;

public class StatisticsModel {

    // Now using the mediator pattern

    final private ChartModel _chartModel;
    private State _state;
    private boolean _running = true;
    private boolean _updated = false;
    private long _startTime;
    private long _threadNumber;

    public StatisticsModel(ChartModel chartModel){
        _chartModel = chartModel;

    }

    public synchronized void updateState(State state){
        _state = state;
        _chartModel.updateState(state);
    }

    public synchronized State getState(){
        return _state;
    }

    public synchronized ChartModel getChartModel(){
        return _chartModel;
    }

    public synchronized boolean getRunning(){
        return _running;
    }

    public synchronized void setRunning(boolean running){
        _running = running;
    }

    public synchronized boolean getUpdated(){
        return _updated;
    }

    public synchronized void setUpdated(boolean updated){
        _updated = updated;
    }

    public synchronized long getStartTime(){
        return _startTime;
    }

    public synchronized void setStartTime(long startTime){
        _startTime = startTime;
    }

    public synchronized long getThreadNumber(){
        return _threadNumber;
    }

    public synchronized void setThreadNumber(long threadNumber){
        _threadNumber = threadNumber;
    }


}
