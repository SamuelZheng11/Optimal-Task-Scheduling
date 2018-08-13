package parallelprocesses;

import common.State;
import exception_classes.RecursionManagerException;

public class RecursionStore {
    private RecursionStore recursionStore;
    private State bestState;
    private Integer numberOfProcessors;
    private Integer linearScheduleTime;

    private RecursionStore(int noOfProc, int linearScheduleTime){
        this.linearScheduleTime = linearScheduleTime;
        this.numberOfProcessors = noOfProc;
    }

    public RecursionStore constructRecursionStoreSingleton(int noOfProc, int linearScheduleTime){
        if(recursionStore == null){
            this.recursionStore = new RecursionStore(noOfProc, linearScheduleTime);
        }
        return this.recursionStore;
    }

    public void processPotentialBestState(State potentialBestState){
        if(potentialBestState.getHeuristicValue() < this.bestState.getHeuristicValue() || this.bestState == null){
            this.bestState = potentialBestState;
        }
    }

    public void setNumberOfProcessors(int noOfProc){
        if(this.numberOfProcessors != null) {
            throw new RecursionManagerException("Attempted to set the number of processors when it has already been set");
        } else {
            this.numberOfProcessors = noOfProc;
        }
    }

    public int getLinearScheduleTime(){
        return this.linearScheduleTime;
    }

    public int getNumberOfProcessors(){
        return this.numberOfProcessors;
    }

    public double getBestStateHeuristic(){
        return this.bestState.getHeuristicValue();
    }
}
