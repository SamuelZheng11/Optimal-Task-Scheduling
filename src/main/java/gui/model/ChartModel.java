package gui.model;

import com.sun.jdi.connect.Connector;
import common.*;
import gui.listeners.AlgorithmListener;
import parser.ArgumentParser;

import java.util.ArrayList;
import java.util.List;

public class ChartModel implements AlgorithmListener {

    private int _processorNum;
    private State _state;
    private int _maxTime;

    public ChartModel(int ProcessorNum, int maxTime){
        _processorNum = ProcessorNum;
        _maxTime = maxTime;
    }

    public int getProcessorNumber(){
        return _processorNum;
    }

    public double getMaximumTime(){
        return _maxTime;
    }

    public List<List<Job>> getJobList(){
        if(_state == null){
            List<List<Job>> temp = new ArrayList<>();
            for(int i = 0; i < _processorNum; i++){
                ArrayList<Job> alj = new ArrayList<>();
                alj.add(new DelayJob(1));
                temp.add(alj);
            }
            return temp;
        }else{
            return _state.getJobLists();
        }
    }

    @Override
    public void update(State state){
        _state = state;
    }

}
