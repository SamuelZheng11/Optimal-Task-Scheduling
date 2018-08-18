package common;

import java.util.List;

public class State {
//  Joblists consists of p arrays, each with an ordered list of Jobs for that processor, in the order which they are to
//  be carried out
    private final List<List<Job>> _jobLists;
//  JobListDuration is an array of length p, with each value corresponding to the time at which the final task of its
//  corresponding processor(of the same index as in JobLists) will complete
    private final int[] _jobListDuration;
    private final double _heuristicValue;

    public int getSumOfScheduledTasks() {
        return _sumOfScheduledTasks;
    }

    private final int _sumOfScheduledTasks;

    public State(List<List<Job>> jobLists, int[] jobListDuration, double heuristicValue, int sumOfScheduledTasks){
        _jobLists = jobLists;
        _jobListDuration = jobListDuration;
        _heuristicValue = heuristicValue;
        _sumOfScheduledTasks = sumOfScheduledTasks;
    }

    public State add(TaskDependencyNode node, int i){
        return null;
    }

    public List<List<Job>> getJobLists(){
        return _jobLists;
    }

    public int[] getJobListDuration(){
        return _jobListDuration;
    }

    public double getHeuristicValue(){
        return _heuristicValue;
    }
}
