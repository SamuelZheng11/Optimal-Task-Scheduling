package common;

import java.util.List;

/**
 * The class that represents the an entire state during the search and building up of the schedule. Each state object
 * represents a schedule whether it be incomplete or complete
 */
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


    public List<List<Job>> getJobLists(){
        return _jobLists;
    }

    public int[] getJobListDuration(){
        return _jobListDuration;
    }

    public double getHeuristicValue(){
        return _heuristicValue;
    }

    /**
     * uses the toString override to generate the unique identifier for the state (uses a small amount of space when its in
     * the byte array)
     * @return
     */
    public byte[] getByteArray(){
        char[] charArrayForm = this.toString().toCharArray();
        byte[] byteArray = new byte[charArrayForm.length];
        for (int i = 0; i < charArrayForm.length; i++) {
            int ascii = charArrayForm[i];
            byteArray[i] = (byte)ascii;
        }
        return byteArray;
    }

    /**
     * Override to string method to uniquely identify the state string value
     * @return
     */
    @Override
    public String toString(){
        String stateString = "";
        int subject =  -1;
        for (int processor = 0; processor < this.getJobLists().size(); processor++) {
            int[] nextAccInfo = this.getNextAccedingIndexInJobList(subject);
            if(nextAccInfo[0] == -1){
                break;
            }
            // generate string value of the string with a "E" character to signify the end of the processor
            for (int i = 0; i < this.getJobLists().get(nextAccInfo[0]).size(); i++) {
                stateString = stateString.concat(this.getJobLists().get(nextAccInfo[0]).get(i).toString());
            }
            stateString = stateString.concat("E");
            subject = nextAccInfo[1];
        }
        return stateString;
    }

    /**
     * Method that is called to help the toString method build the unique identifier for the state object
     * @param subject
     * @return
     */
    public int[] getNextAccedingIndexInJobList(int subject){
        int nextAcceding = Integer.MAX_VALUE;
        int nextAccedingIndex = -1;
        //iterate though each processor list in acceding order and get the index and value at that processor
        for (int processor = 0; processor < this.getJobLists().size(); processor++) {
            if(this.getJobLists().get(processor).size() != 0 ){
                if(this.getJobLists().get(processor).get(0) instanceof TaskJob) {
                    int potentialNextAcc = Integer.valueOf(((TaskJob) this.getJobLists().get(processor).get(0)).getName());
                    if( potentialNextAcc > subject && potentialNextAcc < nextAcceding){
                        nextAccedingIndex = processor;
                        nextAcceding = potentialNextAcc;
                    }
                } else {
                    int potentialNextAcc = Integer.valueOf(((TaskJob) this.getJobLists().get(processor).get(1)).getName());
                    if( potentialNextAcc > subject && potentialNextAcc < nextAcceding){
                        nextAccedingIndex = processor;
                        nextAcceding = potentialNextAcc;
                    }
                }
            }
        }
        //return map of the next acceding index and its value
        return new int[]{nextAccedingIndex, nextAcceding};
    }
}
