package main;

import common.DependencyGraph;
import common.State;
import common.TaskDependencyNode;
import gui.model.StatisticsModel;
import gui.view.MainScreen;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.concurrent.Task;

public class Main extends Application {
    private DependencyGraph dg = DependencyGraph.getGraph();

    @Override
    public void start(Stage primaryStage) throws Exception{


        StatisticsModel model = new StatisticsModel();

        Task task = new Task<Void>() {
            @Override public Void call() {
                InitialiseScheduling(model);
                return null;
            }
        };

        new Thread(task).start();


        MainScreen mainScreen = new MainScreen(primaryStage, model);

    }


    public static void main(String[] args) {
        launch(args);
    }

    public void InitialiseScheduling(StatisticsModel model){

        //Gets command line arguments
        Application.Parameters parameters = getParameters();

        //todo parsing of command line args to graph parsing function
        dg.parse();

        //todo call algorithm and pass the model

    }

    // todo refactor this to different class
    public State recursion(int numProc, int depth, State state, State bestFoundState, int numTasks){
        TaskDependencyNode[] freeTasks = free(state);
        if (freeTasks.length>0){
            for (int i = 0; i < freeTasks.length; i++) {
                for (int j = 0; j < numProc; j++) {
                    depth++;
                    State newState = state.add(freeTasks[i], j);
                    if (newState.getHeuristicValue()<= bestFoundState.getHeuristicValue() && depth == numTasks){
                        bestFoundState = newState;
                    }
                    else if (newState.getHeuristicValue() <= bestFoundState.getHeuristicValue() && depth < numTasks) {
                        State foundState = recursion(numProc, depth, newState, bestFoundState, numTasks);
                        if (foundState.getHeuristicValue() <= bestFoundState.getHeuristicValue()) {
                            bestFoundState = foundState;
                        }
                    }
                    depth--;
                }
            }
            return bestFoundState;
        }
        return null;
    }

    public TaskDependencyNode[] free(State state){
        return null;
    }


}
