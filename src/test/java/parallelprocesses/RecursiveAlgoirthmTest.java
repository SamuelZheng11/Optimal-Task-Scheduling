package parallelprocesses;

import common.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class RecursiveAlgoirthmTest {

    DependencyGraph dg = DependencyGraph.getGraph();

    @Test
    public void shouldScheduleBetterThanGreedyAndWorstThanHeuristicBestOnGraph7() {
        dg.setFilePath("Input/example-input-graphs/Nodes_7_OutTree.dot");
        //todo parsing of command line args to graph parsing function
        dg.parse();
        List<TaskDependencyNode> freeTasks = dg.getFreeTasks(null);
        State bestFoundSoln = dg.initialState(2);
        int costOfAllProcessorsOnOneProcessor = bestFoundSoln.getJobListDuration()[0];

//        bestFoundSoln = recursion(bestFoundSoln.getJobListDuration().length, freeTasks, 0, null, bestFoundSoln, dg.getNodes().size(), bestFoundSoln.getJobListDuration()[0]);

        int algoirthmScheduleFinishTime = Integer.MAX_VALUE;
        for (int i = 0; i < bestFoundSoln.getJobListDuration().length; i++) {
            if(algoirthmScheduleFinishTime > bestFoundSoln.getJobListDuration()[i]) {
                algoirthmScheduleFinishTime = bestFoundSoln.getJobListDuration()[i];
            }
        }

        Assert.assertTrue(algoirthmScheduleFinishTime >= ((double)costOfAllProcessorsOnOneProcessor)/2);
        Assert.assertTrue(algoirthmScheduleFinishTime <= ((double)costOfAllProcessorsOnOneProcessor));
    }

    @Test
    public void shouldScheduleBetterThanGreedyAndWorstThanHeuristicBestOnGraph9() {
        dg.setFilePath("Input/example-input-graphs/Nodes_9_SeriesParallel.dot");
        //todo parsing of command line args to graph parsing function
        dg.parse();
        List<TaskDependencyNode> freeTasks = dg.getFreeTasks(null);
        State bestFoundSoln = dg.initialState(2);
        int costOfAllProcessorsOnOneProcessor = bestFoundSoln.getJobListDuration()[0];

//        bestFoundSoln = recursion(bestFoundSoln.getJobListDuration().length, freeTasks, 0, null, bestFoundSoln, dg.getNodes().size(), bestFoundSoln.getJobListDuration()[0]);

        int algoirthmScheduleFinishTime = Integer.MAX_VALUE;
        for (int i = 0; i < bestFoundSoln.getJobListDuration().length; i++) {
            if(algoirthmScheduleFinishTime > bestFoundSoln.getJobListDuration()[i]) {
                algoirthmScheduleFinishTime = bestFoundSoln.getJobListDuration()[i];
            }
        }

        Assert.assertTrue(algoirthmScheduleFinishTime >= ((double)costOfAllProcessorsOnOneProcessor)/2);
        Assert.assertTrue(algoirthmScheduleFinishTime <= ((double)costOfAllProcessorsOnOneProcessor));
    }

}
