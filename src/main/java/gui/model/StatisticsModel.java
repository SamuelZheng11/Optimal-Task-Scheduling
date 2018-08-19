package gui.model;

import common.State;
import org.graphstream.graph.Graph;

public class StatisticsModel {

    // Now using the mediator pattern

    final private ChartModel _chartModel;
    private ChartModel _greedyChartModel;
    private State _state;
    private boolean _running = true;
    private boolean _updated = false;
    private long _startTime;
    private long _threadNumber;
    private Graph _inputGraph;
    private long _totalBranches;
    private long _branchesSearched;
    private String _graphName;

    public StatisticsModel(ChartModel chartModel, String graphName){
        _chartModel = chartModel;
        _greedyChartModel = new ChartModel(chartModel.getProcessorNumber());
        _graphName = graphName;
    }

    public synchronized void updateState(State state){
        _state = state;
        _chartModel.updateState(state);
    }

    public void updateGreedyState(State greedyState){
        _greedyChartModel.updateState(greedyState);
    }

    public ChartModel getGreedyChartModel(){
        return _greedyChartModel;
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

    public synchronized void setTotalBranches(long totalBranches) { _totalBranches = totalBranches; }

    public synchronized long getTotalBranch() { return _totalBranches; }

    public synchronized void setBranchesSearched(long totalBranches) { _branchesSearched = totalBranches; }

    public synchronized long getBranchesSearched() { return _branchesSearched; }

    public void setInputGraph(Graph inputGraph){
        _inputGraph = inputGraph;
    }

    public String getInputGraphName(){ return _graphName; }

    public Graph getInputGraph(){
        return _inputGraph;
    }

}
