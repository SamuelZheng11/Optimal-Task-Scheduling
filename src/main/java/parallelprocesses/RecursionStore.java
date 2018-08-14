package parallelprocesses;

import common.State;
import exception_classes.RecursionManagerException;

public class RecursionStore {
    private RecursionStore recursionStore;
    private static State bestState;
    private static Integer numberOfProcessors;
    private static Integer linearScheduleTime;

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

    public static void processPotentialBestState(State potentialBestState){
        if(potentialBestState.getHeuristicValue() < bestState.getHeuristicValue() || bestState == null){
            bestState = potentialBestState;
            updateBestFoundState();
        }
    }

    public int getLinearScheduleTime(){
        return this.linearScheduleTime;
    }

    public int getNumberOfProcessors(){
        return this.numberOfProcessors;
    }

    public static double getBestStateHeuristic(){
        return bestState.getHeuristicValue();
    }

    private static void updateBestFoundState(){
        //TODO: call the gui display method inside this method
    }

    public static State getBestState(){
        return bestState;
    }
}
