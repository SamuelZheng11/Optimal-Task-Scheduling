package unit_tests;

import common.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import cost_function.CostFunctionService;

public class CostFunctionServiceTest {

    State currentState;
    Job[] targetProcessor;
    TaskDependencyNode node;

    @Before
    public void setUp() throws Exception {
        // declare two processors with jobs in them
        this.targetProcessor = new TaskJob[]{
                new TaskJob( 4, "Job 1", new TaskDependencyNode(4, new TaskDependencyEdge[2], new TaskDependencyEdge[2], "Task 1")),
                new TaskJob( 3, "Job 2", new TaskDependencyNode(3, new TaskDependencyEdge[2], new TaskDependencyEdge[2], "Task 2")),
        };
        Job[] secondProcessor = new TaskJob[]{
                new TaskJob( 5, "Job 3", new TaskDependencyNode(4, new TaskDependencyEdge[2], new TaskDependencyEdge[2], "Task 3")),
                new TaskJob( 6, "Job 4", new TaskDependencyNode(3, new TaskDependencyEdge[2], new TaskDependencyEdge[2], "Task 4")),
        };

        // define a job array for the State object
        Job[][] jobList = new Job[][] {
                {this.targetProcessor[0], this.targetProcessor[1]},
                {secondProcessor[0], secondProcessor[1]}
        };

        // define the durationList for the State object
        int[] durationList = new int[]{ 7, 11};

        // setup the fields that will be used in the tests
        this.node = new TaskDependencyNode(10, new TaskDependencyEdge[2], new TaskDependencyEdge[2], "Task 5");
        this.currentState = new State(jobList, durationList, 20);
    }

    @Test
    public void shouldScheduleFirstJob() {
        this.targetProcessor = new TaskJob[] {};
        Job[][] jobList = new Job[][]{this.targetProcessor};
        this.currentState = new State(jobList, new int[]{0}, 0);
        TaskDependencyNode rootNode = new TaskDependencyNode(1, new TaskDependencyEdge[]{}, new TaskDependencyEdge[]{}, "root node");

        State result = new CostFunctionService().scheduleNode(rootNode, this.targetProcessor, this.currentState);

        // check that the Job scheduled is a TaskJob
        try{
            System.out.println(result.getJobLists()[0][0]);
            TaskJob scheduledJob =(TaskJob) result.getJobLists()[0][0];

            // check that the scheduled TaskJob is the root node
            Assert.assertEquals(scheduledJob.getNode(), rootNode);
        } catch (ClassCastException cce) {
            Assert.fail();
        }
    }

    @Test
    public void shouldReturnOnSecondaryProcessorWithNoDelayOnJob() {

    }

    @Test
    public void shouldReturnOnSecondaryProcessorWithDelayOnJob() {

    }

    @Test
    public void shouldReturnTargetProcessorWithNoDelayOnJob() {

    }

    @Test
    public void shouldReturnTargetProcessorWithDelayOnJob() {

    }

    @Test
    public void shouldScheduleLastJob() {

    }
}
