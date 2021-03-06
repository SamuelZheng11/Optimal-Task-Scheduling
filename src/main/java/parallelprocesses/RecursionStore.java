package parallelprocesses;

import common.State;
import exception_classes.RecursionStoreException;
import gui.model.StatisticsModel;
import org.graphstream.graph.Graph;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * This class is used to store information about the status of the algorithm. the main point of interest about it is that it stores
 * the number of branches in the state tree after the BFS search and also holds the best found schedule
 */
public class RecursionStore {
    private static RecursionStore recursionStore;
    private static State bestState;
    private static int SET_RESET_SIZE = 80000;
    private static Integer numberOfProcessors;
    private static Integer linearScheduleTime;
    private static Integer numberOfTasks;
    private static Integer numberOfCores;
    private static Queue<StateTreeBranch> stb = new PriorityQueue<>();
    public static Set<byte[]> exploredStates = new HashSet<>();
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

    public static RecursionStore getRecursionStore(){
        return recursionStore;
    }

    public static synchronized void processPotentialBestState(State potentialBestState){
        if( bestState == null || potentialBestState.getHeuristicValue() < bestState.getHeuristicValue()){
            bestState = potentialBestState;
            updateBestFoundState();
            clearHashSet();
        }
    }

    public static synchronized void finishGuiProcessing(){
        statisticsModel.setRunning(false);
    }

    public static synchronized void setMaxThreads(int maxThreads){
        statisticsModel.setThreadNumber(maxThreads);
    }

    public static synchronized void setGreedyState(State greedyState){
        statisticsModel.updateGreedyState(greedyState);
    }

    public static synchronized void setInputGraph(Graph graph){
        statisticsModel.setInputGraph(graph);
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

    /**
     * used to check if a state has been explored by comparing its byte string
     * @param stateByteForm
     * @return
     */
    public static boolean hasExplored(byte[] stateByteForm){
        if(exploredStates.contains(stateByteForm)){
            return true;
        } else if(exploredStates.size() > SET_RESET_SIZE){
            clearHashSet();
        }
        exploredStates.add(stateByteForm);
        return false;
    }

    public static void clearHashSet(){
        exploredStates.clear();
    }

    public static void publishTotalBranches(){
        statisticsModel.setTotalBranches(getTaskQueueSize());
    }

    public static void updateBranchesComplete(long branchesComplete){
        statisticsModel.setBranchesSearched(branchesComplete);
    }
}
