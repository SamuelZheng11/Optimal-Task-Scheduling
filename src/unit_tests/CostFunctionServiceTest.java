package unit_tests;

import common.*;
import javafx.concurrent.Task;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import cost_function.CostFunctionService;

import java.util.ArrayList;
import java.util.List;

public class CostFunctionServiceTest {

    State currentState;
    List<Job> targetProcessor;
    List<Job> secondaryProcessor;

    List<TaskDependencyNode> _nodes = new ArrayList<>();
    final static int SUM_OF_ALL_NODES = 28;

    @Before
    public void setUp() throws Exception {
        // set up jobLists
        this.targetProcessor = new ArrayList<>();
        this.secondaryProcessor = new ArrayList<>();
        List<List<Job>> jobList = new ArrayList<>();
        jobList.add(this.targetProcessor);
        jobList.add(this.secondaryProcessor);

        this.currentState = new State(jobList, new int[]{0,0}, 0);

        TaskDependencyNode rootNode = new TaskDependencyNode(1, new TaskDependencyEdge[]{}, new TaskDependencyEdge[]{}, "root node");
        TaskDependencyNode node1 = new TaskDependencyNode(2, new TaskDependencyEdge[]{}, new TaskDependencyEdge[]{}, "Level 1 node");
        TaskDependencyNode node2 = new TaskDependencyNode(3, new TaskDependencyEdge[]{}, new TaskDependencyEdge[]{}, "level 2 node 1");
        TaskDependencyNode node3 = new TaskDependencyNode(4, new TaskDependencyEdge[]{}, new TaskDependencyEdge[]{}, "level 2 node 2");
        TaskDependencyNode node4 = new TaskDependencyNode(5, new TaskDependencyEdge[]{}, new TaskDependencyEdge[]{}, "level 3 node");
        TaskDependencyNode node5 = new TaskDependencyNode(6, new TaskDependencyEdge[]{}, new TaskDependencyEdge[]{}, "level 4 node");
        TaskDependencyNode isolatedNode = new TaskDependencyNode(7, new TaskDependencyEdge[]{}, new TaskDependencyEdge[]{}, "level 3 node");

        _nodes.add(rootNode);
        _nodes.add(node1);
        _nodes.add(node2);
        _nodes.add(node3);
        _nodes.add(node4);
        _nodes.add(node5);
        _nodes.add(isolatedNode);

        rootNode._children = new TaskDependencyEdge[]{new TaskDependencyEdge(rootNode, node1, 1)};
        node1._parents = new TaskDependencyEdge[]{new TaskDependencyEdge(rootNode, rootNode, 1)};
        node1._children = new TaskDependencyEdge[]{new TaskDependencyEdge(node1, node2, 1), new TaskDependencyEdge(node1, node3, 1)};
        node2._parents = new TaskDependencyEdge[]{new TaskDependencyEdge(node1, node2, 1)};
        node2._children = new TaskDependencyEdge[]{new TaskDependencyEdge(node2, node3, 1), new TaskDependencyEdge(node2, node4, 1)};
        node3._parents = new TaskDependencyEdge[]{new TaskDependencyEdge(node1, node3, 1),new TaskDependencyEdge(node2, node3, 1)};
        node4._parents = new TaskDependencyEdge[]{new TaskDependencyEdge(node2, node4, 1)};
        node4._children = new TaskDependencyEdge[]{new TaskDependencyEdge(node4, node5, 1)};
        node5._parents = new TaskDependencyEdge[]{new TaskDependencyEdge(node4, node5, 1)};
    }

    @After
    public void cleanUp() {
        this.currentState = null;
        this.targetProcessor = null;
        this._nodes = null;
    }

    @Test
    public void shouldScheduleFirstOrIsolatedJob() {
        State result = new CostFunctionService().scheduleNode(_nodes.get(0), 0, this.currentState, this.SUM_OF_ALL_NODES);

        // check that the Job scheduled is a TaskJob
        try{
            TaskJob scheduledJob =(TaskJob) result.getJobLists().get(0).get(0);

            // check that the scheduled TaskJob is the root node
            Assert.assertEquals(scheduledJob.getNode(), _nodes.get(0));
            Assert.assertEquals(result.getJobListDuration()[0], 1);
            Assert.assertTrue(result.getHeuristicValue() == ((float)27)/2);


            Assert.assertNotSame(result.getJobLists(), this.currentState.getJobLists());
            Assert.assertNotSame(result.getJobLists().get(0), this.currentState.getJobLists().get(0));
            Assert.assertNotSame(result.getJobListDuration(), this.currentState.getJobListDuration());
        } catch (ClassCastException cce) {
            Assert.fail();
        }
    }

    @Test
    public void shouldReturnTargetProcessorWithNoDelay() {

        TaskJob rootNodeTask = new TaskJob(_nodes.get(0)._duration, _nodes.get(0)._name, _nodes.get(0));

        this.targetProcessor.add(rootNodeTask);

        this.currentState.getJobListDuration()[0] += rootNodeTask.getDuration();

        State result = new CostFunctionService().scheduleNode(_nodes.get(1), 0, this.currentState, this.SUM_OF_ALL_NODES);

        // check that the Job scheduled is a TaskJob
        try{
            TaskJob scheduledJob = (TaskJob) result.getJobLists().get(0).get(1);

            // check that the scheduled TaskJob is the root node
            Assert.assertEquals(scheduledJob.getNode(), _nodes.get(1));
            Assert.assertEquals(result.getJobListDuration()[0], 3);
            Assert.assertTrue((result.getHeuristicValue() == ((float)25)/2));


            Assert.assertNotSame(result.getJobLists(), this.currentState.getJobLists());
            Assert.assertNotSame(result.getJobLists().get(0), this.currentState.getJobLists().get(0));
            Assert.assertNotSame(result.getJobListDuration(), this.currentState.getJobListDuration());
        } catch (ClassCastException cce) {
            Assert.fail();
        }
    }

    @Test
    public void shouldScheduleNodeWithDelay() {

        Job rootJob = new TaskJob(1, _nodes.get(0)._name, _nodes.get(0));

        this.secondaryProcessor.add(rootJob);

        this.currentState.getJobListDuration()[1] += ((TaskJob) rootJob).getNode()._duration;

        State result = new CostFunctionService().scheduleNode(_nodes.get(1), 0, this.currentState, this.SUM_OF_ALL_NODES);

        // check that the Job scheduled is a TaskJob
        try{

            TaskJob scheduledJob =(TaskJob) result.getJobLists().get(0).get(1);

            // check that the scheduled TaskJob is the root node
            Assert.assertEquals(scheduledJob.getNode(), _nodes.get(1));

            Assert.assertEquals(result.getJobListDuration()[0], 4);
            Assert.assertEquals(result.getJobListDuration()[1], 1);

            Assert.assertNotSame(result.getJobLists(), this.currentState.getJobLists());
            Assert.assertNotSame(result.getJobLists().get(0), this.currentState.getJobLists().get(0));
            Assert.assertNotSame(result.getJobListDuration(), this.currentState.getJobListDuration());
        } catch (ClassCastException cce) {
            Assert.fail();
        }
    }

    @Test
    public void shouldScheduleDelayJob() {

        Job rootJob = new TaskJob(_nodes.get(0)._duration, _nodes.get(0)._name, _nodes.get(0));

        this.secondaryProcessor.add(rootJob);

        this.currentState.getJobListDuration()[1] += ((TaskJob) rootJob).getNode()._duration;

        State result = new CostFunctionService().scheduleNode(_nodes.get(1), 0, this.currentState, this.SUM_OF_ALL_NODES);

        // check that the Job scheduled is a TaskJob
        try{

            // check that the job scheduled is a delay job and has the correct length
            Assert.assertEquals(result.getJobLists().get(0).get(0).getClass(), DelayJob.class);
            Assert.assertEquals(result.getJobLists().get(0).get(0).getDuration(), 2);
            Assert.assertTrue((result.getHeuristicValue() == ((float)23)/2));

            Assert.assertNotSame(result.getJobLists(), this.currentState.getJobLists());
            Assert.assertNotSame(result.getJobLists().get(0), this.currentState.getJobLists().get(0));
            Assert.assertNotSame(result.getJobListDuration(), this.currentState.getJobListDuration());
        } catch (ClassCastException cce) {
            Assert.fail();
        }
    }

    @Test
    public void shouldScheduleLastJob() {

        Job rootJob = new TaskJob( _nodes.get(0)._duration, _nodes.get(0)._name, _nodes.get(0));
        Job job1 = new TaskJob( _nodes.get(1)._duration, _nodes.get(1)._name, _nodes.get(1));
        Job job2 = new TaskJob(_nodes.get(2)._duration, _nodes.get(2)._name, _nodes.get(2));
        Job job3 = new TaskJob(_nodes.get(3)._duration, _nodes.get(3)._name, _nodes.get(3));
        Job job4 = new TaskJob(_nodes.get(4)._duration, _nodes.get(4)._name, _nodes.get(4));
        Job job5 = new TaskJob(_nodes.get(5)._duration, _nodes.get(5)._name, _nodes.get(5));


        this.targetProcessor.add(rootJob);
        this.targetProcessor.add(job1);
        this.targetProcessor.add(job2);
        this.targetProcessor.add(job3);
        this.targetProcessor.add(job4);
        this.targetProcessor.add(job5);

        this.currentState.getJobListDuration()[0] += ((TaskJob) rootJob).getNode()._duration;
        this.currentState.getJobListDuration()[0] += ((TaskJob) job1).getNode()._duration;
        this.currentState.getJobListDuration()[0] += ((TaskJob) job2).getNode()._duration;
        this.currentState.getJobListDuration()[0] += ((TaskJob) job3).getNode()._duration;
        this.currentState.getJobListDuration()[0] += ((TaskJob) job4).getNode()._duration;


        State result = new CostFunctionService().scheduleNode(_nodes.get(5), 0, this.currentState, this.SUM_OF_ALL_NODES);

        // check that the Job scheduled is a TaskJob
        try{
            TaskJob scheduledJob =(TaskJob) result.getJobLists().get(0).get(5);

            // check that the scheduled TaskJob is the root node
            Assert.assertEquals(scheduledJob.getNode(), _nodes.get(5));
            int p1FinishTime = 0;
            for(int i = 0; i < _nodes.size()-1; i++) {
                p1FinishTime += _nodes.get(i)._duration;
            }
            Assert.assertEquals(result.getJobListDuration()[0], p1FinishTime);
        } catch (ClassCastException cce) {
            Assert.fail();
        }
    }

    @Test
    public void shouldScheduleParentWithMultipleChildrenWithDelay() {
        Job rootJob = new TaskJob(_nodes.get(0)._duration, _nodes.get(0)._name, _nodes.get(0));
        Job job1 = new TaskJob(_nodes.get(1)._duration, _nodes.get(1)._name, _nodes.get(1));
        Job job2 = new TaskJob(_nodes.get(2)._duration, _nodes.get(2)._name, _nodes.get(2));

        this.secondaryProcessor.add(rootJob);
        this.secondaryProcessor.add(job1);
        this.secondaryProcessor.add(job2);

        this.currentState.getJobListDuration()[1] += ((TaskJob) rootJob).getNode()._duration;
        this.currentState.getJobListDuration()[1] += ((TaskJob) job1).getNode()._duration;
        this.currentState.getJobListDuration()[1] += ((TaskJob) job2).getNode()._duration;

        State result = new CostFunctionService().scheduleNode(_nodes.get(3), 0, this.currentState, this.SUM_OF_ALL_NODES);

        // check that the Job scheduled is a TaskJob
        try{
            TaskJob scheduledJob =(TaskJob) result.getJobLists().get(0).get(1);

            // check that the scheduled TaskJob is the root node
            Assert.assertEquals(scheduledJob.getNode(), _nodes.get(3));
            Assert.assertEquals(result.getJobListDuration()[0], (_nodes.get(3)._duration + _nodes.get(0)._duration + _nodes.get(1)._duration + 1));
            Assert.assertTrue((result.getHeuristicValue() == ((float)this.SUM_OF_ALL_NODES - (_nodes.get(3)._duration + 1 + 2*(_nodes.get(0)._duration + _nodes.get(1)._duration) + _nodes.get(2)._duration))/2));


            Assert.assertNotSame(result.getJobLists(), this.currentState.getJobLists());
            Assert.assertNotSame(result.getJobLists().get(0), this.currentState.getJobLists().get(0));
            Assert.assertNotSame(result.getJobListDuration(), this.currentState.getJobListDuration());
        } catch (ClassCastException cce) {
            Assert.fail();
        }
    }
}
