package parallelprocesses;

import common.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

//public class RecursiveAlgorithmTest {
//
//    private DependencyGraph dg;
//    private RecursiveAlgorithmAdapter adapter = new RecursiveAlgorithmAdapter();
//
//    @Before()
//    public void setUpDependencyGraphParams(){
//        dg = new DependencyGraphAdapter();
//    }
//
//    @Test
//    public void shouldScheduleBetterThanGreedyAndWorstThanHeuristicBestOnGraph7() {
//        dg.setFilePath("Input/example-input-graphs/Nodes_7_OutTree.dot");
//        //todo parsing of command line args to graph parsing function
//        dg.parse();
//        List<TaskDependencyNode> freeTasks = dg.getFreeTasks(null);
//        State bestFoundSoln = dg.initialState(2);
//        int costOfAllProcessorsOnOneProcessor = bestFoundSoln.getJobListDuration()[0];
//
//        bestFoundSoln = adapter.callAlgorithm(
//                bestFoundSoln.getJobListDuration().length,
//                freeTasks,
//                0,
//                null,
//                bestFoundSoln,
//                dg.getNodes().size(),
//                bestFoundSoln.getJobListDuration()[0]);
//
//        int algoirthmScheduleFinishTime = bestFoundSoln.getJobListDuration()[0];
//        for (int i = 1; i < bestFoundSoln.getJobListDuration().length; i++) {
//            if(algoirthmScheduleFinishTime < bestFoundSoln.getJobListDuration()[i]) {
//                algoirthmScheduleFinishTime = bestFoundSoln.getJobListDuration()[i];
//            }
//        }
//
//        Assert.assertTrue(algoirthmScheduleFinishTime >= ((double)costOfAllProcessorsOnOneProcessor)/2);
//        Assert.assertTrue(algoirthmScheduleFinishTime <= ((double)costOfAllProcessorsOnOneProcessor));
//    }
//
//    @Test
//    public void shouldScheduleBetterThanGreedyAndWorstThanHeuristicBestOnGraph8() {
//        dg.setFilePath("Input/example-input-graphs/Nodes_9_SeriesParallel.dot");
//        //todo parsing of command line args to graph parsing function
//        dg.parse();
//        List<TaskDependencyNode> freeTasks = dg.getFreeTasks(null);
//        State bestFoundSoln = dg.initialState(2);
//        int costOfAllProcessorsOnOneProcessor = bestFoundSoln.getJobListDuration()[0];
//
//        bestFoundSoln = adapter.callAlgorithm(
//                bestFoundSoln.getJobListDuration().length,
//                freeTasks,
//                0,
//                null,
//                bestFoundSoln,
//                dg.getNodes().size(),
//                bestFoundSoln.getJobListDuration()[0]);
//
//        int algoirthmScheduleFinishTime = bestFoundSoln.getJobListDuration()[0];
//        for (int i = 1; i < bestFoundSoln.getJobListDuration().length; i++) {
//            if(algoirthmScheduleFinishTime < bestFoundSoln.getJobListDuration()[i]) {
//                algoirthmScheduleFinishTime = bestFoundSoln.getJobListDuration()[i];
//            }
//        }
//
//        Assert.assertTrue(algoirthmScheduleFinishTime >= ((double)costOfAllProcessorsOnOneProcessor)/2);
//        Assert.assertTrue(algoirthmScheduleFinishTime <= ((double)costOfAllProcessorsOnOneProcessor));
//    }
//
//    @Test
//    public void shouldScheduleBetterThanGreedyAndWorstThanHeuristicBestOnGraph9() {
//        dg.setFilePath("Input/example-input-graphs/Nodes_9_SeriesParallel.dot");
//        //todo parsing of command line args to graph parsing function
//        dg.parse();
//        List<TaskDependencyNode> freeTasks = dg.getFreeTasks(null);
//        State bestFoundSoln = dg.initialState(2);
//        int costOfAllProcessorsOnOneProcessor = bestFoundSoln.getJobListDuration()[0];
//
//        bestFoundSoln = adapter.callAlgorithm(
//                bestFoundSoln.getJobListDuration().length,
//                freeTasks,
//                0,
//                null,
//                bestFoundSoln,
//                dg.getNodes().size(),
//                bestFoundSoln.getJobListDuration()[0]);
//
//        int algoirthmScheduleFinishTime = bestFoundSoln.getJobListDuration()[0];
//        for (int i = 1; i < bestFoundSoln.getJobListDuration().length; i++) {
//            if(algoirthmScheduleFinishTime < bestFoundSoln.getJobListDuration()[i]) {
//                algoirthmScheduleFinishTime = bestFoundSoln.getJobListDuration()[i];
//            }
//        }
//
//        Assert.assertTrue(algoirthmScheduleFinishTime >= ((double)costOfAllProcessorsOnOneProcessor)/2);
//        Assert.assertTrue(algoirthmScheduleFinishTime <= ((double)costOfAllProcessorsOnOneProcessor));
//    }
//
//    @Test
//    public void shouldNotScheduleDuplicateNodesOnGraph7() {
//        dg.setFilePath("Input/example-input-graphs/Nodes_7_OutTree.dot");
//        //todo parsing of command line args to graph parsing function
//        dg.parse();
//        List<TaskDependencyNode> freeTasks = dg.getFreeTasks(null);
//        State bestFoundSoln = dg.initialState(2);
//
//        bestFoundSoln = adapter.callAlgorithm(
//                bestFoundSoln.getJobListDuration().length,
//                freeTasks,
//                0,
//                null,
//                bestFoundSoln,
//                dg.getNodes().size(),
//                bestFoundSoln.getJobListDuration()[0]);
//
//        boolean hasScheduledDuplicateNode = false;
//        HashMap<TaskDependencyNode, Boolean> mapOfScheduledNodes = new HashMap<>();
//        for (int i = 1; i < bestFoundSoln.getJobLists().size(); i++) {
//            for (int j = 0; j < bestFoundSoln.getJobLists().get(i).size(); j++) {
//                if( bestFoundSoln.getJobLists().get(i).get(j) instanceof TaskJob) {
//                    TaskJob taskJob = (TaskJob) bestFoundSoln.getJobLists().get(i).get(j);
//                    if(mapOfScheduledNodes.containsValue(taskJob.getNode())){
//                        hasScheduledDuplicateNode = true;
//                        break;
//                    }
//                    mapOfScheduledNodes.put(((TaskJob) bestFoundSoln.getJobLists().get(i).get(j)).getNode(), false);
//                }
//            }
//            if(hasScheduledDuplicateNode){
//                break;
//            }
//        }
//
//        Assert.assertFalse(hasScheduledDuplicateNode);
//    }
//
//    @Test
//    public void shouldNotScheduleDuplicateNodesOnGraph8() {
//        dg.setFilePath("Input/example-input-graphs/Nodes_8_Random.dot");
//        //todo parsing of command line args to graph parsing function
//        dg.parse();
//        List<TaskDependencyNode> freeTasks = dg.getFreeTasks(null);
//        State bestFoundSoln = dg.initialState(2);
//
//        bestFoundSoln = adapter.callAlgorithm(
//                bestFoundSoln.getJobListDuration().length,
//                freeTasks,
//                0,
//                null,
//                bestFoundSoln,
//                dg.getNodes().size(),
//                bestFoundSoln.getJobListDuration()[0]);
//
//        boolean hasScheduledDuplicateNode = false;
//        HashMap<TaskDependencyNode, Boolean> mapOfScheduledNodes = new HashMap<>();
//        for (int i = 1; i < bestFoundSoln.getJobLists().size(); i++) {
//            for (int j = 0; j < bestFoundSoln.getJobLists().get(i).size(); j++) {
//                if( bestFoundSoln.getJobLists().get(i).get(j) instanceof TaskJob) {
//                    TaskJob taskJob = (TaskJob) bestFoundSoln.getJobLists().get(i).get(j);
//                    if(mapOfScheduledNodes.containsValue(taskJob.getNode())){
//                        hasScheduledDuplicateNode = true;
//                        break;
//                    }
//                    mapOfScheduledNodes.put(((TaskJob) bestFoundSoln.getJobLists().get(i).get(j)).getNode(), false);
//                }
//            }
//            if(hasScheduledDuplicateNode){
//                break;
//            }
//        }
//
//        Assert.assertFalse(hasScheduledDuplicateNode);
//    }
//
//    @Test
//    public void shouldNotScheduleDuplicateNodesOnGraph9() {
//        dg.setFilePath("Input/example-input-graphs/Nodes_9_SeriesParallel.dot");
//        //todo parsing of command line args to graph parsing function
//        dg.parse();
//        List<TaskDependencyNode> freeTasks = dg.getFreeTasks(null);
//        State bestFoundSoln = dg.initialState(2);
//
//        bestFoundSoln = adapter.callAlgorithm(
//                bestFoundSoln.getJobListDuration().length,
//                freeTasks,
//                0,
//                null,
//                bestFoundSoln,
//                dg.getNodes().size(),
//                bestFoundSoln.getJobListDuration()[0]);
//
//        boolean hasScheduledDuplicateNode = false;
//        HashMap<TaskDependencyNode, Boolean> mapOfScheduledNodes = new HashMap<>();
//        for (int i = 1; i < bestFoundSoln.getJobLists().size(); i++) {
//            for (int j = 0; j < bestFoundSoln.getJobLists().get(i).size(); j++) {
//                if( bestFoundSoln.getJobLists().get(i).get(j) instanceof TaskJob) {
//                    TaskJob taskJob = (TaskJob) bestFoundSoln.getJobLists().get(i).get(j);
//                    if(mapOfScheduledNodes.containsValue(taskJob.getNode())){
//                        hasScheduledDuplicateNode = true;
//                        break;
//                    }
//                    mapOfScheduledNodes.put(((TaskJob) bestFoundSoln.getJobLists().get(i).get(j)).getNode(), false);
//                }
//            }
//            if(hasScheduledDuplicateNode){
//                break;
//            }
//        }
//
//        Assert.assertFalse(hasScheduledDuplicateNode);
//    }
//
//    @Test
//    public void shouldNotScheduleDuplicateNodesOnGraph10() {
//        dg.setFilePath("Input/example-input-graphs/Nodes_10_Random.dot");
//        //todo parsing of command line args to graph parsing function
//        dg.parse();
//        List<TaskDependencyNode> freeTasks = dg.getFreeTasks(null);
//        State bestFoundSoln = dg.initialState(2);
//
//        bestFoundSoln = adapter.callAlgorithm(
//                bestFoundSoln.getJobListDuration().length,
//                freeTasks,
//                0,
//                null,
//                bestFoundSoln,
//                dg.getNodes().size(),
//                bestFoundSoln.getJobListDuration()[0]);
//
//        boolean hasScheduledDuplicateNode = false;
//        HashMap<TaskDependencyNode, Boolean> mapOfScheduledNodes = new HashMap<>();
//        for (int i = 1; i < bestFoundSoln.getJobLists().size(); i++) {
//            for (int j = 0; j < bestFoundSoln.getJobLists().get(i).size(); j++) {
//                if( bestFoundSoln.getJobLists().get(i).get(j) instanceof TaskJob) {
//                    TaskJob taskJob = (TaskJob) bestFoundSoln.getJobLists().get(i).get(j);
//                    if(mapOfScheduledNodes.containsValue(taskJob.getNode())){
//                        hasScheduledDuplicateNode = true;
//                        break;
//                    }
//                    mapOfScheduledNodes.put(((TaskJob) bestFoundSoln.getJobLists().get(i).get(j)).getNode(), false);
//                }
//            }
//            if(hasScheduledDuplicateNode){
//                break;
//            }
//        }
//
//        Assert.assertFalse(hasScheduledDuplicateNode);
//    }
//
//    @Test
//    public void shouldNotScheduleDuplicateNodesOnGraph11() {
//        dg.setFilePath("Input/example-input-graphs/Nodes_11_OutTree.dot");
//        //todo parsing of command line args to graph parsing function
//        dg.parse();
//        List<TaskDependencyNode> freeTasks = dg.getFreeTasks(null);
//        State bestFoundSoln = dg.initialState(2);
//
//        bestFoundSoln = adapter.callAlgorithm(
//                bestFoundSoln.getJobListDuration().length,
//                freeTasks,
//                0,
//                null,
//                bestFoundSoln,
//                dg.getNodes().size(),
//                bestFoundSoln.getJobListDuration()[0]);
//
//        boolean hasScheduledDuplicateNode = false;
//        HashMap<TaskDependencyNode, Boolean> mapOfScheduledNodes = new HashMap<>();
//        for (int i = 1; i < bestFoundSoln.getJobLists().size(); i++) {
//            for (int j = 0; j < bestFoundSoln.getJobLists().get(i).size(); j++) {
//                if( bestFoundSoln.getJobLists().get(i).get(j) instanceof TaskJob) {
//                    TaskJob taskJob = (TaskJob) bestFoundSoln.getJobLists().get(i).get(j);
//                    if(mapOfScheduledNodes.containsValue(taskJob.getNode())){
//                        hasScheduledDuplicateNode = true;
//                        break;
//                    }
//                    mapOfScheduledNodes.put(((TaskJob) bestFoundSoln.getJobLists().get(i).get(j)).getNode(), false);
//                }
//            }
//            if(hasScheduledDuplicateNode){
//                break;
//            }
//        }
//
//        Assert.assertFalse(hasScheduledDuplicateNode);
//    }
//}
