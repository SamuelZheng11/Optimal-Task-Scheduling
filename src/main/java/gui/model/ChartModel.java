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

    public ChartModel(int ProcessorNum){

    }

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

    @Override
    public void update(State state){

    }

}
