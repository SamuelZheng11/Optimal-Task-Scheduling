package gui.model;

import common.*;
import gui.listeners.AlgorithmListener;
import gui.listeners.ModelChangeListener;
import parser.ArgumentParser;

import java.util.ArrayList;
import java.util.List;

public class ChartModel implements AlgorithmListener {

    private int _processorNum;
    private State _state;
    private int _maxTime;
    private List<ModelChangeListener> _listeners = new ArrayList<>();

    public ChartModel(int ProcessorNum, int maxTime){
        _processorNum = ProcessorNum;
        _maxTime = maxTime;
    }
/*
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
*/
    @Override
    public void update(State state){
        _state = state;
        for(ModelChangeListener l : _listeners){
            l.update();
        }
    }
//////////////////////////////////////////////////////////
    public int getProcessorNumber(){
        return 2;
    }

    public double getMaximumTime(){
        return 375;
    }

    public List<List<Job>> getJobList(){
        List<List<Job>> list = new ArrayList<List<Job>>();
        List<Job> jobList = new ArrayList<Job>();
        List<Job> jobList2 = new ArrayList<Job>();

        jobList.add(new DelayJob(125));
        jobList.add(new TaskJob(100, "node 1" ,null));


        jobList2.add(new TaskJob(200, "node 2" ,null));
        jobList2.add(new DelayJob(150));
        jobList2.add(new TaskJob(25, "node 3" ,null));

        list.add(jobList);
        list.add(jobList2);


        return list;
    }
    ///////////////////////////////////////////////////////

    public void addListener(ModelChangeListener listener){
        _listeners.add(listener);
    }

}
