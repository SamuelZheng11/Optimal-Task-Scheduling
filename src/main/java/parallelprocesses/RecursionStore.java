package parallelprocesses;

import common.State;
import exception_classes.RecursionStoreException;

import java.util.PriorityQueue;
import java.util.Queue;

public class RecursionStore {
    private static RecursionStore recursionStore;
    private static State bestState;
    private static Integer numberOfProcessors;
    private static Integer linearScheduleTime;
    private static Integer numberOfTasks;
    private static Integer numberOfCores;
    private static Queue<StateTreeBranch> stb = new PriorityQueue<>();

    private RecursionStore(int noOfProc, int linearScheduleTime, int numTasks, int numOfCores){
        RecursionStore.linearScheduleTime = linearScheduleTime;
        RecursionStore.numberOfProcessors = noOfProc;
        RecursionStore.numberOfTasks = numTasks;
        RecursionStore.numberOfCores = numOfCores;
    }

    public static void constructRecursionStoreSingleton(int noOfProc, int linearScheduleTime, int numTasks, int numOfCores){
        if(recursionStore == null){
            recursionStore = new RecursionStore(noOfProc, linearScheduleTime, numTasks, numOfCores);
        } else {
            throw new RecursionStoreException("Attempted to re-assign store parameters that should never change");
        }
    }

    public static synchronized void processPotentialBestState(State potentialBestState){
        if( bestState == null || potentialBestState.getHeuristicValue() < bestState.getHeuristicValue()){
            bestState = potentialBestState;
            updateBestFoundState();
        }
    }

    public static int getNumberOfTasksTotal(){
        return RecursionStore.numberOfTasks;
    }

    public static int getLinearScheduleTime(){
        return RecursionStore.linearScheduleTime;
    }

    public static int getNumberOfProcessors(){
        return RecursionStore.numberOfProcessors;
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

    public static int getTaskQueueSize(){
        return stb.size();
    }

    public static StateTreeBranch pollStateTreeQueue(){
        return stb.poll();
    }

    public static void pushStateTreeQueue(StateTreeBranch tp){
        stb.add(tp);
    }

    public static int getNumberOfCores(){
        return RecursionStore.numberOfCores;
    }
}
