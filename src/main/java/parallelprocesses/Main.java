package parallelprocesses;

import common.*;
import cost_function.CostFunctionService;

import exception_classes.RecursiveWorkerException;
import gui.model.StatisticsModel;
import gui.view.MainScreen;
import javafx.application.Application;
import javafx.concurrent.Task;

import javafx.stage.Stage;
import org.apache.commons.cli.*;
import parser.ArgumentParser;
import parser.KernelParser;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main extends Application implements PilotDoneListener, RecursiveDoneListener {

    public static void main(String[] args) {
        launch(args);
    }

    private ArgumentParser _argumentsParser;

    private static int numberOfBranchesCompleted;

    private static int totalNumberOfStateTreeBranches;

    private ExecutorService _pool;


    public void start(Stage primaryStage) throws Exception {


        StatisticsModel model = new StatisticsModel();
        _argumentsParser = new KernelParser(this);
        validateArguments();


        Task task = new Task<Void>() {
            @Override
            public Void call() {
                InitialiseScheduling(model);
                return null;
            }
        };

        new Thread(task).start();

        if (_argumentsParser.displayVisuals()) {
            MainScreen mainScreen = new MainScreen(primaryStage, model);
        }

    }


    public void InitialiseScheduling(StatisticsModel model) {
        DependencyGraph dg = DependencyGraph.getGraph();
        dg.setFilePath(_argumentsParser.getFilePath());
        //todo parsing of command line args to graph parsing function
        dg.parse();
        System.out.println("Calculating schedule, Please wait ...");


        State bestFoundSoln = dg.initialState(_argumentsParser.getProcessorNo());
        List<TaskDependencyNode> freeTasks = dg.getFreeTasks(null);

        RecursionStore.constructRecursionStoreSingleton(_argumentsParser.getProcessorNo(), bestFoundSoln.getJobListDuration()[0], dg.getNodes().size(), _argumentsParser.getMaxThreads());
        RecursionStore.processPotentialBestState(bestFoundSoln);
        RecursionStore.pushStateTreeQueue(new StateTreeBranch(generateInitalState(RecursionStore.getBestStateHeuristic()), freeTasks, 0));

        PilotRecursiveWorker pilot = new PilotRecursiveWorker(_argumentsParser.getBoostMultiplier(), this);

        _pool = Executors.newFixedThreadPool(_argumentsParser.getMaxThreads());
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
        this.totalNumberOfStateTreeBranches = RecursionStore.getTaskQueueSize();
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
        if (this.numberOfBranchesCompleted != this.totalNumberOfStateTreeBranches) {
            return;
        }
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

        System.out.println("Finished");
        System.exit(0);
    }

    private void validateArguments() {
        _argumentsParser.getFilePath();
        _argumentsParser.getProcessorNo();
    }
}