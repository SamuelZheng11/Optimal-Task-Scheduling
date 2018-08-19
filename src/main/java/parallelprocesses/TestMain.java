package parallelprocesses;

import common.*;
import exception_classes.RecursiveWorkerException;
import gui.model.ChartModel;
import gui.model.StatisticsModel;
import gui.view.MainScreen;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import parser.ArgumentParser;
import parser.KernelParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestMain extends Main implements PilotDoneListener, RecursiveDoneListener, GreedySearchListener {

    public static void main(String[] args) {
        FILEPATH = args[0];
        PROCESSORS = Integer.valueOf(args[1]);
        TestMain tm = new TestMain();
        tm.start();
    }

    private static int PROCESSORS;
    private static String FILEPATH;
    private static int numberOfBranchesCompleted;

    private static int _totalNumberOfStateTreeBranches;

    private static int _maxThreads = 1;

    private ExecutorService _pool;

    private long _startTime;

    private static boolean complete = false;

    public void start(){
//        DependencyGraph dg = DependencyGraph.getGraph();
//
//        //Parse the graph so that our data is ready for use in any point post this line.
//        dg.setFilePath(FILEPATH);
//        dg.parse();


        //ChartModel cModel = new ChartModel(_argumentsParser.getProcessorNo(), DependencyGraph.getGraph().getLinearScheduleDuration());
        ChartModel cModel = new ChartModel(PROCESSORS);
        StatisticsModel sModel = new StatisticsModel(cModel);
        sModel.setStartTime(System.nanoTime());

        InitialiseScheduling(sModel);
        return;
    }



    public void InitialiseScheduling(StatisticsModel model) {
        DependencyGraph dg = DependencyGraph.getGraph();
        dg.setFilePath(FILEPATH);
        //todo parsing of command line args to graph parsing function
        dg.parse();
        System.out.println("Calculating schedule, Please wait ...");

        // initialise store and thread pool
        RecursionStore.constructRecursionStoreSingleton(model, PROCESSORS, dg.remainingCosts(), dg.getNodes().size(), _maxThreads);
        _pool = Executors.newFixedThreadPool(_maxThreads);

        GreedyState greedyState = new GreedyState(dg, this);
        _pool.execute(greedyState);

        RecursionStore.setMaxThreads(_maxThreads);
        try {
            _pool.awaitTermination(10, TimeUnit.SECONDS);
            System.out.println("pause");
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static State generateInitalState(double initialHeuristic) {
        ArrayList<List<Job>> jobList = new ArrayList<>(RecursionStore.getNumberOfProcessors());
        for (int i = 0; i < RecursionStore.getNumberOfProcessors(); i++) {
            jobList.add(new ArrayList<>());
        }
        int[] procDur = new int[RecursionStore.getNumberOfProcessors()];
        java.util.Arrays.fill(procDur, 0);
        return new State(jobList, procDur, initialHeuristic);
    }

    @Override
    public void handleGreedySearchHasCompleted(State greedyState) {
        // set up the results from the greedy search to be used for the optimal search
        List<TaskDependencyNode> freeTasks = DependencyGraph.getGraph().getFreeTasks(null);
        RecursionStore.processPotentialBestState(greedyState);
        RecursionStore.pushStateTreeQueue(new StateTreeBranch(generateInitalState(RecursionStore.getBestStateHeuristic()), freeTasks, 0));

        // if the number of processors is one, then schedule everything on on recursive worker
        if(_maxThreads == Integer.valueOf(Defaults.MAXTHREADS.toString())){
            _totalNumberOfStateTreeBranches = RecursionStore.getTaskQueueSize();
            RecursiveWorker singleWorker = new RecursiveWorker(RecursionStore.pollStateTreeQueue(), this);
            _pool.submit(singleWorker);
            return;
        }

        PilotRecursiveWorker pilot = new PilotRecursiveWorker(5, this);
        _pool.submit(pilot);
    }

    @Override
    public void handlePilotRunHasCompleted() {
        if(RecursionStore.getTaskQueueSize() < 5 * _maxThreads){
            generateOutputAndClose();
            return;
        }

        Set<RecursiveWorker> callables = new HashSet<>();
        this._totalNumberOfStateTreeBranches = RecursionStore.getTaskQueueSize();
        while (RecursionStore.getTaskQueueSize() > 0) {
            callables.add(new RecursiveWorker(RecursionStore.pollStateTreeQueue(), this));
        }

        if (callables.size() == 0) {
            throw new RecursiveWorkerException("No tasks are assigned to the thread call-ables");
        }

        try {
            _pool.invokeAll(callables);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public synchronized void handleThreadRecursionHasCompleted() {
        //ensure that all branches have been explored before writing output
        this.numberOfBranchesCompleted++;
        if (this.numberOfBranchesCompleted != this._totalNumberOfStateTreeBranches) {
            return;
        }
        // write output file
        generateOutputAndClose();
        _pool.shutdown();
    }

    public void generateOutputAndClose(){
        DependencyGraph dg = DependencyGraph.getGraph();
        String outputName = "test-output";

        try {
            dg.generateOutput(RecursionStore.getBestState(), outputName);
        }catch (IOException e){
            e.printStackTrace();
        }

        RecursionStore.finishGuiProcessing();
        System.out.println("Finished");
    }


    @Override
    public void handleThreadException(Exception e){
        System.out.println("A thread has thrown an uncaught exception");
        e.printStackTrace();
        System.out.println(1);
    }

    public static boolean isComplete(){
        return complete;
    }
}
