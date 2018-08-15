package parallelprocesses;


import common.*;

import common.DependencyGraph;
import common.State;
import common.TaskDependencyNode;
import cost_function.CostFunctionService;

import exception_classes.RecursiveWorkerException;
import gui.model.StatisticsModel;
import gui.view.MainScreen;
import javafx.application.Application;
import javafx.concurrent.Task;

import javafx.stage.Stage;
import org.apache.commons.cli.*;


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

    private static CommandLine _commands;

    private static final int DEFAULT_NUMBER_OF_PROCESSORS = 1;

    private static final int DEFAULT_BOOST_MULTIPLIER = 5;

    private static final String DEFAULT_OUTPUT_ENDING_NAME = "-output";

    private static final String OUTPUT_FILE_FORMAT = ".dot";

    private static int numberOfBranchesCompleted;

    private static int totalNumberOfStateTreeBranches;

    private ExecutorService _pool;


    public void start(Stage primaryStage) throws Exception {


        StatisticsModel model = new StatisticsModel();

        _commands = getCommands();


        Task task = new Task<Void>() {
            @Override
            public Void call() {
                InitialiseScheduling(model);
                return null;
            }
        };

        new Thread(task).start();

        if( getCommandLine().hasOption("v")){
            MainScreen mainScreen = new MainScreen(primaryStage, model);
        }

    }



    private CommandLine getCommands() throws ParseException
    {
        Options options = new Options();
        options.addOption("p", true, "The number of processors for the algorithm to run on");
        options.addOption("v", "Whether to visualise the search");
        options.addOption("o", true,"The output file");
        options.addOption("b", true,"Initial boost Value");
        CommandLineParser parser = new DefaultParser();
        String[] params = new String[getParameters().getRaw().size()];
        params = getParameters().getRaw().toArray(params);
        return parser.parse(options, params);
    }



    public void InitialiseScheduling(StatisticsModel model) {
        CommandLine commands = null;
        try {
            commands = getCommands();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DependencyGraph dg = DependencyGraph.getGraph();
        dg.setFilePath(commands.getArgs()[0]);
        //todo parsing of command line args to graph parsing function
        dg.parse();
        System.out.println("Calculating schedule, Please wait ...");


        int numberOfCores;
        if(commands.getOptionValue('p') != null){
            numberOfCores = Integer.valueOf(commands.getOptionValue('p'));
        } else {
            numberOfCores = DEFAULT_NUMBER_OF_PROCESSORS;
        }

        int boostMultiplier;
        if(commands.getOptionValue('b') != null){
            boostMultiplier = Integer.valueOf(commands.getOptionValue('b'));
        } else {
            boostMultiplier = DEFAULT_BOOST_MULTIPLIER;
        }

        _pool = Executors.newFixedThreadPool(numberOfCores);

        State bestFoundSoln = dg.initialState(numberOfCores);
        List<TaskDependencyNode> freeTasks = dg.getFreeTasks(null);

        RecursionStore.constructRecursionStoreSingleton(Integer.valueOf(commands.getArgs()[1]), bestFoundSoln.getJobListDuration()[0], dg.getNodes().size(), numberOfCores);
        RecursionStore.processPotentialBestState(bestFoundSoln);
        RecursionStore.pushStateTreeQueue(new StateTreeBranch(generateInitalState(RecursionStore.getBestStateHeuristic()), freeTasks, 0));

        PilotRecursiveWorker pilot = new PilotRecursiveWorker(boostMultiplier, this);
        pilot.recurse();


    }



    //The recursion to find the optimal schedule
    // Arguments in order are numProc: The number of processors, freeTasks: the initially free tasks of the
    // dependancy tree (all roots of the tree), depth(0 to start), state(null to start),
    // bestFoundState: representation of the greedy algo best found soln,
    // numTasks: total number of tasks to be scheudled,
    // linearScheduleTime: The total time it would take if this was all on processor (no comms delays)
//    public void recursion(int numProc, List<TaskDependencyNode> freeTasks, int depth, State state, int numTasks, int linearScheduleTime) {
//        Double bestFoundHeuristic = RecursionStore.getBestStateHeuristic();
//
//        if(state == null) {
//            ArrayList<List<Job>> jobList = new ArrayList<List<Job>>(numProc);
//            for (int i = 0; i < numProc; i++) {
//                jobList.add(new ArrayList<Job>());
//            }
//            int[] procDur = new int[numProc];
//            java.util.Arrays.fill(procDur, 0);
//            state = new State(jobList, procDur, Math.floorDiv(linearScheduleTime, numProc));
//        }
//        //If there are available tasks to schedule
//        if (freeTasks.size() > 0) {
//            //For each available task, try scheduling it on a processor
//            for (int i = 0; i < freeTasks.size(); i++) {
//                //if the current processor and the next processor are empty, skip the current one (all empty processors are equivalent)
//                for (int j = 0; j < numProc; j++) {
//                    if (j < numProc-1 && state.getJobListDuration()[j] == 0 && state.getJobListDuration()[j + 1] == 0) {
//                        continue;
//                    }
//                    depth++;
//                    TaskDependencyNode currentNode = freeTasks.get(i);
//                    List<TaskDependencyNode> prospectiveFreeTasks = new ArrayList<>(freeTasks);
//
//                    //add all children of the task to the freetask list and remove the task
//                    for (int k = 0; k < currentNode._children.size(); k++) {
//                        TaskDependencyNode child = currentNode._children.get(k)._child;
//                        int numUnresolvedParents = child._parents.size();
//                        boolean hasConsideredCurrentNodeAsParent = false;
//
//                        for (int l = 0; l <state.getJobLists().size(); l++) {
//                            for (int m = 0; m < child._parents.size(); m++) {
//                                if (!hasConsideredCurrentNodeAsParent && currentNode == child._parents.get(m)._parent){
//                                    numUnresolvedParents--;
//                                    hasConsideredCurrentNodeAsParent = true;
//                                }
//                                for (int n = 0; n < state.getJobLists().get(l).size(); n++) {
//                                    if (state.getJobLists().get(l).get(n) instanceof TaskJob && ((TaskJob) state.getJobLists().get(l).get(n)).getNode() == child._parents.get(m)._parent){
//                                        numUnresolvedParents--;
//                                    }
//                                }
//
//                            }
//                            if (numUnresolvedParents == 0 ){
//                                break;
//                            }
//                        }
//                        if (numUnresolvedParents == 0) {
//                            prospectiveFreeTasks.add(currentNode._children.get(k)._child);
//                        }
//                    }
//                    prospectiveFreeTasks.remove(currentNode);
//                    //create the new state with the task scheduled to evaluate pass to the recursion
//                    State newState = new CostFunctionService().scheduleNode(currentNode, j, state, linearScheduleTime);
//
//                    //if this state is complete and better than existing best, update.
//                    if (newState.getHeuristicValue() <= bestFoundHeuristic && depth == numTasks) {
//                        RecursionStore.processPotentialBestState(newState);
//                    }
//                    //if possibly better and not complete, recurse.
//                    else if (newState.getHeuristicValue() <= bestFoundHeuristic && depth < numTasks) {
//                        recursion(numProc, prospectiveFreeTasks, depth, newState, numTasks, linearScheduleTime);
//                    }
//                    depth--;
//                }
//            }
//        }
//    }

    public static CommandLine getCommandLine(){
        return _commands;
    }

    private static State generateInitalState(double initialHeuristic){
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

        Set<RecursiveWorker> callables = new HashSet<>();
        this.totalNumberOfStateTreeBranches = RecursionStore.getTaskQueueSize();
        while ( RecursionStore.getTaskQueueSize() > 0) {
            callables.add(new RecursiveWorker(RecursionStore.pollStateTreeQueue(), this));
        }

        if(callables.size() == 0){
            throw new RecursiveWorkerException("No tasks are assigned to the thread call-ables");
        }

        try{
            _pool.invokeAll(callables);
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }

        _pool.shutdown();
    }

    @Override
    public synchronized void handleThreadRecursionHasCompleted() {
        //ensure that all branches have been explored before writing output
        this.numberOfBranchesCompleted++;
        if(this.numberOfBranchesCompleted != this.totalNumberOfStateTreeBranches ){
            return;
        }

        CommandLine commands = null;
        DependencyGraph dg = DependencyGraph.getGraph();
        try {
            commands = getCommands();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String outputName = commands.getOptionValue('o');

        State bs = RecursionStore.getBestState();

        if(outputName == null){
            String[] outputNameWithFileDirectory = dg.getFilePath().split(".dot")[0].split("/");
            outputName = outputNameWithFileDirectory[outputNameWithFileDirectory.length-1] + DEFAULT_OUTPUT_ENDING_NAME + OUTPUT_FILE_FORMAT;
        } else {
            outputName += OUTPUT_FILE_FORMAT;
        }

        try {
            dg.generateOutput(RecursionStore.getBestState(), outputName);
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("Finished");
        //todo call algorithm and pass the model
        System.exit(0);
    }
}