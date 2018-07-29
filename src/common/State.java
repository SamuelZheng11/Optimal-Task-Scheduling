package sample.common;

public class State {
//  Joblists consists of p arrays, each with an ordered list of Jobs for that processor, in the order which they are to
//  be carried out
    private final Job[][] _jobLists;
//  JobListDuration is an array of length p, with each value corresponding to the time at which the final task of its
//  corresponding processor(of the same index as in JobLists) will complete
    private final int[] _jobListDuration;
    private final int _heuristicValue;

    public State(Job[][] jobLists, int[] jobListDuration, int heuristicValue){
        _jobLists = jobLists;
        _jobListDuration = jobListDuration;
        _heuristicValue = heuristicValue;
    }

    public Job[][] getJobLists(){
        return _jobLists;
    }

    public int[] getJobListDuration(){
        return _jobListDuration;
    }

    public int getHeuristicValue(){
        return _heuristicValue;
    }
}
