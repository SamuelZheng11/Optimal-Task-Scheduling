package main;

import com.sun.deploy.util.ArrayUtil;
import common.DependencyGraph;
import common.State;
import common.TaskDependencyNode;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class Main extends Application {
    private DependencyGraph dg = DependencyGraph.getGraph();

    @Override
    public void start(Stage primaryStage) throws Exception{
        dg.parse();
    }


    public static void main(String[] args) {
        launch(args);
    }

    //The recursion to find the optimal schedule
    public State recursion(int numProc, List<TaskDependencyNode> freeTasks, int depth, State state, State bestFoundState, int numTasks, int linearScheduleTime){
        //If there are available tasks to schedule
        if (freeTasks.size()>0){
            //For each available task, try scheduling it on a processor
            for (int i = 0; i < freeTasks.size(); i++) {
                //if the current processor and the next processor are empty, skip the current one (all empty processors are equivalent)
                if (i<freeTasks.size()-1 && state.getJobListDuration()[i]==0 && state.getJobListDuration()[i+1]==0){
                    i++;
                }
                for (int j = 0; j < numProc; j++) {
                    depth++;
                    TaskDependencyNode currentNode = freeTasks.get(i);

                    //add all children of the task to the freetask list and remove the task
                    for (int k = 0; k < currentNode._children.size(); k++) {
                        freeTasks.add(currentNode._children.get(k)._child);
                    }
                    freeTasks.remove(currentNode);
                    //create the new state with the task scheduled to evaluate pass to the recursion
                    State newState = state.add(currentNode, j, linearScheduleTime);

                    //if this state is complete and better than existing best, update.
                    if (newState.getHeuristicValue()<= bestFoundState.getHeuristicValue() && depth == numTasks){
                        bestFoundState = newState;
                    }
                    //if possibly better and not complete, recurse.
                    else if (newState.getHeuristicValue() <= bestFoundState.getHeuristicValue() && depth < numTasks) {
                        State foundState = recursion(numProc, freeTasks, depth, newState, bestFoundState, numTasks, linearScheduleTime);
                        //Recursion will always return a complete state. If this is better, update.
                        if (foundState.getHeuristicValue() <= bestFoundState.getHeuristicValue()) {
                            bestFoundState = foundState;
                        }
                    }
                    //undo changes to freetasks for next iteration
                    for (int k = 0; k < currentNode._children.size(); k++) {
                        freeTasks.remove(currentNode._children.get(k)._child);
                    }
                    freeTasks.add(currentNode);
                    depth--;
                }
            }
            return bestFoundState;
        }
    }
}
