package parallelprocesses;

import common.*;
import cost_function.CostFunctionService;

import gui.model.ChartModel;
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
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private ArgumentParser _argumentsParser;

    static int counter = 0;

    public void start(Stage primaryStage) throws Exception {
        _argumentsParser = new KernelParser(this);

        StatisticsModel sModel = new StatisticsModel();
        ChartModel cModel = new ChartModel(_argumentsParser.getProcessorNo());



        Task task = new Task<Void>() {
            @Override
            public Void call() {
                InitialiseScheduling(sModel);
                return null;
            }
        };

        new Thread(task).start();

        if( _argumentsParser.displayVisuals()){
            MainScreen mainScreen = new MainScreen(primaryStage, sModel, cModel);
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

        bestFoundSoln = recursion(_argumentsParser.getProcessorNo(), freeTasks, 0, null, bestFoundSoln, dg.getNodes().size(), bestFoundSoln.getJobListDuration()[0]);

        String outputName = _argumentsParser.getOutputFileName();

        try {
            dg.generateOutput(bestFoundSoln, outputName);
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("Finished");
        //todo call algorithm and pass the model

    }



    //The recursion to find the optimal schedule
    // Arguments in order are numProc: The number of processors, freeTasks: the initially free tasks of the
    // dependancy tree (all roots of the tree), depth(0 to start), state(null to start),
    // bestFoundState: representation of the greedy algo best found soln,
    // numTasks: total number of tasks to be scheudled,
    // linearScheduleTime: The total time it would take if this was all on processor (no comms delays)
    public State recursion(int numProc, List<TaskDependencyNode> freeTasks, int depth, State state, State bestFoundState, int numTasks, int linearScheduleTime) {
        if(state == null) {
            ArrayList<List<Job>> jobList = new ArrayList<List<Job>>(numProc);
            for (int i = 0; i < numProc; i++) {
                jobList.add(new ArrayList<Job>());
            }
            int[] procDur = new int[numProc];
            java.util.Arrays.fill(procDur, 0);
            state = new State(jobList, procDur, Math.floorDiv(linearScheduleTime, numProc));
        }
        //If there are available tasks to schedule
        if (freeTasks.size() > 0) {
            //For each available task, try scheduling it on a processor
            for (int i = 0; i < freeTasks.size(); i++) {
                //if the current processor and the next processor are empty, skip the current one (all empty processors are equivalent)
                for (int j = 0; j < numProc; j++) {
                    if (j < numProc-1 && state.getJobListDuration()[j] == 0 && state.getJobListDuration()[j + 1] == 0) {
                        continue;
                    }
                    depth++;
                    TaskDependencyNode currentNode = freeTasks.get(i);
                    List<TaskDependencyNode> prospectiveFreeTasks = new ArrayList<>(freeTasks);

                    //add all children of the task to the freetask list and remove the task
                    for (int k = 0; k < currentNode._children.size(); k++) {
                        TaskDependencyNode child = currentNode._children.get(k)._child;
                        int numUnresolvedParents = child._parents.size();
                        boolean hasConsideredCurrentNodeAsParent = false;

                        for (int l = 0; l <state.getJobLists().size(); l++) {
                            for (int m = 0; m < child._parents.size(); m++) {
                                if (!hasConsideredCurrentNodeAsParent && currentNode == child._parents.get(m)._parent){
                                    numUnresolvedParents--;
                                    hasConsideredCurrentNodeAsParent = true;
                                }
                                for (int n = 0; n < state.getJobLists().get(l).size(); n++) {
                                    if (state.getJobLists().get(l).get(n) instanceof TaskJob && ((TaskJob) state.getJobLists().get(l).get(n)).getNode() == child._parents.get(m)._parent){
                                        numUnresolvedParents--;
                                    }
                                }

                            }
                            if (numUnresolvedParents == 0 ){
                                break;
                            }
                        }
                        if (numUnresolvedParents == 0) {
                            prospectiveFreeTasks.add(currentNode._children.get(k)._child);
                        }
                    }
                    prospectiveFreeTasks.remove(currentNode);
                    //create the new state with the task scheduled to evaluate pass to the recursion
                    State newState = new CostFunctionService().scheduleNode(currentNode, j, state, linearScheduleTime);

                    //if this state is complete and better than existing best, update.
                    if (newState.getHeuristicValue() <= bestFoundState.getHeuristicValue() && depth == numTasks) {
                        bestFoundState = newState;
                    }
                    //if possibly better and not complete, recurse.
                    else if (newState.getHeuristicValue() <= bestFoundState.getHeuristicValue() && depth < numTasks) {
                        State foundState = recursion(numProc, prospectiveFreeTasks, depth, newState, bestFoundState, numTasks, linearScheduleTime);
                        //Recursion will always return a complete state. If this is better, update.
                        if (foundState.getHeuristicValue() <= bestFoundState.getHeuristicValue()) {
                            bestFoundState = foundState;
                        }
                    }
                    depth--;
                }
            }
        }
        return bestFoundState;
    }

}