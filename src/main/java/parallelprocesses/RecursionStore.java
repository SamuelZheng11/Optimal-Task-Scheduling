package parallelprocesses;

import common.State;
import exception_classes.RecursionStoreException;
import gui.model.StatisticsModel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RecursionStore {
    private static RecursionStore recursionStore;
    private static State bestState;
    private static Integer numberOfProcessors;
    private static Integer linearScheduleTime;
    private static Integer numberOfTasks;
    private static Integer numberOfCores;
    private static Queue<StateTreeBranch> stb = new ConcurrentLinkedQueue<>();
    private static StatisticsModel statisticsModel;

    private RecursionStore(StatisticsModel model, int noOfProc, int linearScheduleTime, int numTasks, int numOfCores){
        RecursionStore.linearScheduleTime = linearScheduleTime;
        RecursionStore.numberOfProcessors = noOfProc;
        RecursionStore.numberOfTasks = numTasks;
        RecursionStore.numberOfCores = numOfCores;
        RecursionStore.statisticsModel = model;
    }

    public static void constructRecursionStoreSingleton(StatisticsModel model, int noOfProc, int linearScheduleTime, int numTasks, int numOfCores){
        if(recursionStore == null){
            recursionStore = new RecursionStore(model, noOfProc, linearScheduleTime, numTasks, numOfCores);
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

    public static synchronized void finishGuiProcessing(){
        statisticsModel.setRunning(false);
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
        statisticsModel.updateState(bestState);
        statisticsModel.setUpdated(true);
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
