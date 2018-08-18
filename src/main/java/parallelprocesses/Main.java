package parallelprocesses;

import common.*;

import gui.model.ChartModel;
import exception_classes.RecursiveWorkerException;
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

public class Main extends Application implements PilotDoneListener, RecursiveDoneListener {

    public static void main(String[] args) {
        launch(args);
    }

    private ArgumentParser _argumentsParser;

    private static int numberOfBranchesCompleted;

    private static int _totalNumberOfStateTreeBranches;

    private static int _maxThreads;

    private ExecutorService _pool;

    private long _startTime;

    public void start(Stage primaryStage) throws Exception {
        DependencyGraph dg = DependencyGraph.getGraph();

        //Validate out arguments and make sure they are correct
        _argumentsParser = new KernelParser(this);
        validateArguments();
        _maxThreads = _argumentsParser.getMaxThreads();

        //Parse the graph so that our data is ready for use in any point post this line.
        dg.setFilePath(_argumentsParser.getFilePath());
        dg.parse();


        //ChartModel cModel = new ChartModel(_argumentsParser.getProcessorNo(), DependencyGraph.getGraph().getLinearScheduleDuration());
        ChartModel cModel = new ChartModel(_argumentsParser.getProcessorNo());
        StatisticsModel sModel = new StatisticsModel(cModel);
        sModel.setStartTime(System.nanoTime());

        Task task = new Task<Void>() {
            @Override
            public Void call() {
                InitialiseScheduling(sModel);
                return null;
            }
        };

        new Thread(task).start();

        if( _argumentsParser.displayVisuals()){
            MainScreen mainScreen = new MainScreen(primaryStage, sModel);
        }

    }


    public void InitialiseScheduling(StatisticsModel model) {
        _startTime = System.nanoTime();
        DependencyGraph dg = DependencyGraph.getGraph();
        System.out.println("Calculating schedule, Please wait ...");

        State bestFoundSoln = dg.initialState(_argumentsParser.getProcessorNo());
        List<TaskDependencyNode> freeTasks = dg.getFreeTasks(null);

        // initialise store and thread pool
        RecursionStore.constructRecursionStoreSingleton(model, _argumentsParser.getProcessorNo(), bestFoundSoln.getJobListDuration()[0], dg.getNodes().size(), _argumentsParser.getMaxThreads());
        RecursionStore.processPotentialBestState(bestFoundSoln);
        RecursionStore.pushStateTreeQueue(new StateTreeBranch(generateInitalState(RecursionStore.getBestStateHeuristic()), freeTasks, 0));
        _pool = Executors.newFixedThreadPool(_argumentsParser.getMaxThreads());

        // if the number of processors is one, then schedule everything on on recursive worker
        if(_argumentsParser.getMaxThreads() == Integer.valueOf(Defaults.MAXTHREADS.toString())){
            _totalNumberOfStateTreeBranches = RecursionStore.getTaskQueueSize();
            RecursiveWorker singleWorker = new RecursiveWorker(RecursionStore.pollStateTreeQueue(), this);
            _pool.submit(singleWorker);
            return;
        }

        PilotRecursiveWorker pilot = new PilotRecursiveWorker(_argumentsParser.getBoostMultiplier(), this);
        _pool.submit(pilot);
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
    public void handlePilotRunHasCompleted() {
        if(RecursionStore.getTaskQueueSize() < _argumentsParser.getBoostMultiplier() * _argumentsParser.getMaxThreads()){
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

        _pool.shutdown();
    }

    @Override
    public synchronized void handleThreadRecursionHasCompleted() {
        //ensure that all branches have been explored before writing output
        this.numberOfBranchesCompleted++;
        if (this.numberOfBranchesCompleted != this._totalNumberOfStateTreeBranches) {
            return;
        }
        System.out.println((System.nanoTime() - _startTime) / 1000000000.0);
        generateOutputAndClose();
    }

    public void generateOutputAndClose(){
        DependencyGraph dg = DependencyGraph.getGraph();
        String outputName = _argumentsParser.getOutputFileName();

        try {
            dg.generateOutput(RecursionStore.getBestState(), outputName);
        }catch (IOException e){
            e.printStackTrace();
        }

        RecursionStore.finishGuiProcessing();
        System.out.println("Finished");

        

    }

    private void validateArguments() {
        _argumentsParser.getFilePath();
        _argumentsParser.getProcessorNo();
    }

    @Override
    public void handleThreadException(Exception e){
        System.out.println("A thread has thrown an uncaught exception");
        e.printStackTrace();
        System.out.println(1);
    }
}