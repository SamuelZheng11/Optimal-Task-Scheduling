package main;

import common.DependencyGraph;
import common.State;
import common.TaskDependencyNode;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private DependencyGraph dg = DependencyGraph.getGraph();

    @Override
    public void start(Stage primaryStage) throws Exception{
        dg.parse();
    }


    public static void main(String[] args) {
        launch(args);
    }

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
    }
}
