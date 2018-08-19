package gui.model;

import common.*;
import gui.listeners.ModelChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to hold information regarding a chart on the GUI when the user enables search visualisation
 * In this case it is a pie chart
 */
public class ChartModel {

    private int _processorNum;
    private State _state;
    private int _maxTime = - 1;
    private List<ModelChangeListener> _listeners = new ArrayList<>();

    public ChartModel(int ProcessorNum){
        _processorNum = ProcessorNum;
    }

    public synchronized int getProcessorNumber(){
        return _processorNum;
    }

    public synchronized double getMaximumTime(){

        if(_maxTime == -1){

            int newMaxTime = 0;

            for(int i = 0; i < getJobList().size(); i++){
                int procMaxTime = 0;

                for(int j = 0; j < getJobList().get(i).size(); j++){
                    procMaxTime += getJobList().get(i).get(j).getDuration();
                }

                if(procMaxTime > newMaxTime){
                    newMaxTime = procMaxTime;
                }
            }

            _maxTime = newMaxTime;
            return _maxTime;

        }else{
            return _maxTime;
        }


    }

    public synchronized List<List<Job>> getJobList(){
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


    public synchronized void updateState(State state){
        _state = state;
    }

////////////////////////////////////////////////////////// MOCKING
    /*
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

    */
    ///////////////////////////////////////////////////////


}
