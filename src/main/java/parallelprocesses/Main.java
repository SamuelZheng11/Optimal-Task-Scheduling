package parallelprocesses;

import common.*;

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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * this class is used like a template pattern to schedule an optimal output
 */
public class Main extends Application implements PilotDoneListener, RecursiveDoneListener, GreedySearchListener {

    /**
     * launches the application
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    private ArgumentParser _argumentsParser;

    private static int numberOfBranchesCompleted;

    private static int _totalNumberOfStateTreeBranches;

    private static int _maxThreads;

    private ExecutorService _pool;

    private StatisticsModel _sModel;

    private long _startTime;

    /**
     * entry point for FX projects
     * @param primaryStage
     * @throws Exception
     */
    public void start(Stage primaryStage) throws Exception {
        DependencyGraph dg = DependencyGraph.getGraph();

        //Validate out arguments and make sure they are correct
        _argumentsParser = new KernelParser(this);
        validateArguments();
        _maxThreads = _argumentsParser.getMaxThreads();

        //Parse the graph so that our data is ready for use in any point post this line.
        dg.setFilePath(_argumentsParser.getFilePath());
        dg.parse();

        //set up statistics
        ChartModel cModel = new ChartModel(_argumentsParser.getProcessorNo());
        _sModel = new StatisticsModel(cModel, _argumentsParser.getFilePath());
        _sModel.setStartTime(System.nanoTime());

        // run the algorithm
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                InitialiseScheduling(_sModel);
                return null;
            }
        };

        new Thread(task).start();

        // renders the visualisation if nessasary
        if (_argumentsParser.displayVisuals()) {
            MainScreen mainScreen = new MainScreen(primaryStage, _sModel, this);
        }

    }

    /**
     * Starts the seraching part scheduling search of the program and runs the greedy algorithm
     * @param model
     */
    public void InitialiseScheduling(StatisticsModel model) {
        DependencyGraph dg = DependencyGraph.getGraph();

        dg.setFilePath(_argumentsParser.getFilePath());
        dg.parse();
        System.out.println("Calculating schedule, Please wait ...");

        // initialise store and thread pool
        RecursionStore.constructRecursionStoreSingleton(model, _argumentsParser.getProcessorNo(), dg.remainingCosts(), dg.getNodes().size(), _argumentsParser.getMaxThreads());
        _pool = Executors.newFixedThreadPool(_argumentsParser.getMaxThreads());

        //start greedy search
        GreedyState greedyState = new GreedyState(dg, this);
        _pool.execute(greedyState);

        RecursionStore.setMaxThreads(_maxThreads);
    }

    /**
     * method used to generate the entry nodes inside the search
     * @param initialHeuristic
     * @return
     */
    private static State generateInitialState(double initialHeuristic) {
        ArrayList<List<Job>> jobList = new ArrayList<>(RecursionStore.getNumberOfProcessors());
        for (int i = 0; i < RecursionStore.getNumberOfProcessors(); i++) {
            jobList.add(new ArrayList<>());
        }
        int[] procDur = new int[RecursionStore.getNumberOfProcessors()];
        java.util.Arrays.fill(procDur, 0);
        return new State(jobList, procDur, initialHeuristic, 0);
    }

    /**
     * gets notified when the greedy search is done to start the BFS search
     * @param greedyState
     */
    @Override
    public void handleGreedySearchHasCompleted(State greedyState) {
        // set up the results from the greedy search to be used for the optimal search
        List<TaskDependencyNode> freeTasks = DependencyGraph.getGraph().getFreeTasks(null);
        RecursionStore.processPotentialBestState(greedyState);
        RecursionStore.pushStateTreeQueue(new StateTreeBranch(generateInitialState(RecursionStore.getBestStateHeuristic()), freeTasks, 0));

        PilotRecursiveWorker pilot = new PilotRecursiveWorker(_argumentsParser.getBoostMultiplier(), this);
        _pool.submit(pilot);
    }

    /**
     * Method used to start the parallelisation search after the BFS pilot search is done
     */
    @Override
    public void handlePilotRunHasCompleted() {
        RecursionStore.publishTotalBranches();
        if (RecursionStore.getTaskQueueSize() < _argumentsParser.getBoostMultiplier() * _argumentsParser.getMaxThreads()) {
            generateOutputAndClose();
            return;
        }

        this._totalNumberOfStateTreeBranches = RecursionStore.getTaskQueueSize();
        while (RecursionStore.getTaskQueueSize() > 0) {
            _pool.submit(new RecursiveWorker(RecursionStore.pollStateTreeQueue(), this));
        }
    }

    /**
     * when a thread i finished with a runnable it calls here which waits until all tasks are finished before moving on
     */
    @Override
    public synchronized void handleThreadRecursionHasCompleted() {
        //ensure that all branches have been explored before writing output
        this.numberOfBranchesCompleted++;
        RecursionStore.updateBranchesComplete(this.numberOfBranchesCompleted);
        if (this.numberOfBranchesCompleted != this._totalNumberOfStateTreeBranches) {
            return;
        }
        // write output file
        generateOutputAndClose();
    }

    /**
     * used to write and output file and close the program
     */
    public void generateOutputAndClose() {

        RecursionStore.finishGuiProcessing();

        DependencyGraph dg = DependencyGraph.getGraph();
        String outputName = _argumentsParser.getOutputFileName();

        try {
            dg.generateOutput(RecursionStore.getBestState(), outputName);
            System.out.println("Finished");
            if(!_argumentsParser.displayVisuals()){
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void validateArguments() {
        _argumentsParser.getFilePath();
        _argumentsParser.getProcessorNo();
    }

    @Override
    public void handleThreadException(Exception e) {
        System.out.println("A thread has thrown an uncaught exception");
        e.printStackTrace();
        System.out.println(1);
    }
}
