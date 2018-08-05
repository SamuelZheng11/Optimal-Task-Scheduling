package cost_function;

import common.*;
import cost_function.CostFunctionService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CostFunctionServiceTest {

    State currentState;
    List<Job> targetProcessor;
    List<Job> secondaryProcessor;

    List<TaskDependencyNode> _nodes = new ArrayList<>();
    final static int SUM_OF_ALL_NODES = 36;

    @Before
    public void setUp() throws Exception {
        // set up jobLists
        this.targetProcessor = new ArrayList<>();
        this.secondaryProcessor = new ArrayList<>();
        List<List<Job>> jobList = new ArrayList<>();
        jobList.add(this.targetProcessor);
        jobList.add(this.secondaryProcessor);

        this.currentState = new State(jobList, new int[]{0,0}, 0);

        TaskDependencyNode rootNode = new TaskDependencyNode(1, new ArrayList<>(), new ArrayList<>(), "root node");
        TaskDependencyNode node1 = new TaskDependencyNode(2, new ArrayList<>(), new ArrayList<>(), "Level 1 node");
        TaskDependencyNode node2 = new TaskDependencyNode(3, new ArrayList<>(), new ArrayList<>(), "level 2 node 1");
        TaskDependencyNode node3 = new TaskDependencyNode(4, new ArrayList<>(), new ArrayList<>(), "level 2 node 2");
        TaskDependencyNode node4 = new TaskDependencyNode(5, new ArrayList<>(), new ArrayList<>(), "level 3 node");
        TaskDependencyNode node5 = new TaskDependencyNode(6, new ArrayList<>(), new ArrayList<>(), "level 4 node");
        TaskDependencyNode isolatedNode = new TaskDependencyNode(7, new ArrayList<>(), new ArrayList<>(), "level 3 node");
        TaskDependencyNode node7 = new TaskDependencyNode(8, new ArrayList<>(), new ArrayList<>(), "level 2 node 3");

        _nodes.add(rootNode);
        _nodes.add(node1);
        _nodes.add(node2);
        _nodes.add(node3);
        _nodes.add(node4);
        _nodes.add(node5);
        _nodes.add(isolatedNode);
        _nodes.add(node7);

        rootNode._children.add(new TaskDependencyEdge(rootNode, node1, 1));
        node1._parents.add(new TaskDependencyEdge(rootNode, rootNode, 1));
        node1._children.add(new TaskDependencyEdge(node1, node2, 1));
        node1._children.add(new TaskDependencyEdge(node1, node3, 1));
        node2._parents.add(new TaskDependencyEdge(node1, node2, 1));
        node2._children.add(new TaskDependencyEdge(node2, node3, 1));
        node2._children.add(new TaskDependencyEdge(node2, node4, 1));
        node3._parents.add(new TaskDependencyEdge(node1, node3, 1));
        node3._parents.add(new TaskDependencyEdge(node2, node3, 1));
        node4._parents.add(new TaskDependencyEdge(node2, node4, 1));
        node4._children.add(new TaskDependencyEdge(node4, node5, 1));
        node5._parents.add(new TaskDependencyEdge(node4, node5, 1));
        node7._parents.add(new TaskDependencyEdge(node1, node7, 2));
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
            Assert.assertTrue(result.getHeuristicValue() == 18.5);


            Assert.assertNotSame(result.getJobLists(), this.currentState.getJobLists());
            Assert.assertNotSame(result.getJobLists().get(0), this.currentState.getJobLists().get(0));
            Assert.assertNotSame(result.getJobListDuration(), this.currentState.getJobListDuration());
        } catch (ClassCastException cce) {
            Assert.fail();
        }
    }

    @Test
    public void shouldReturnTargetProcessorWithNoDelay() {

        TaskJob rootNodeJob = new TaskJob(_nodes.get(0)._duration, _nodes.get(0)._name, _nodes.get(0));

        this.targetProcessor.add(rootNodeJob);

        this.currentState.getJobListDuration()[0] += rootNodeJob.getDuration();

        State result = new CostFunctionService().scheduleNode(_nodes.get(1), 0, this.currentState, this.SUM_OF_ALL_NODES);

        // check that the Job scheduled is a TaskJob
        try{
            TaskJob scheduledJob = (TaskJob) result.getJobLists().get(0).get(1);

            // check that the scheduled TaskJob is the root node
            Assert.assertEquals(scheduledJob.getNode(), _nodes.get(1));
            Assert.assertEquals(result.getJobListDuration()[0], 3);
            Assert.assertTrue(result.getHeuristicValue() == 19.5);


            Assert.assertNotSame(result.getJobLists(), this.currentState.getJobLists());
            Assert.assertNotSame(result.getJobLists().get(0), this.currentState.getJobLists().get(0));
            Assert.assertSame(result.getJobLists().get(0).get(0), rootNodeJob);
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
            Assert.assertTrue(result.getHeuristicValue() == 20.5);

            Assert.assertNotSame(result.getJobLists(), this.currentState.getJobLists());
            Assert.assertNotSame(result.getJobLists().get(0), this.currentState.getJobLists().get(0));
            Assert.assertSame(result.getJobLists().get(1).get(0), rootJob);
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
            Assert.assertTrue(result.getHeuristicValue() == 20.5);

            Assert.assertNotSame(result.getJobLists(), this.currentState.getJobLists());
            Assert.assertNotSame(result.getJobLists().get(0), this.currentState.getJobLists().get(0));
            Assert.assertSame(result.getJobLists().get(1).get(0), rootJob);
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
        Job job7 = new TaskJob(_nodes.get(7)._duration, _nodes.get(7)._name, _nodes.get(7));


        this.targetProcessor.add(rootJob);
        this.targetProcessor.add(job1);
        this.targetProcessor.add(job2);
        this.targetProcessor.add(job3);
        this.targetProcessor.add(job4);
        this.targetProcessor.add(job7);

        this.currentState.getJobListDuration()[0] += ((TaskJob) rootJob).getNode()._duration;
        this.currentState.getJobListDuration()[0] += ((TaskJob) job1).getNode()._duration;
        this.currentState.getJobListDuration()[0] += ((TaskJob) job2).getNode()._duration;
        this.currentState.getJobListDuration()[0] += ((TaskJob) job3).getNode()._duration;
        this.currentState.getJobListDuration()[0] += ((TaskJob) job4).getNode()._duration;
        this.currentState.getJobListDuration()[0] += ((TaskJob) job7).getNode()._duration;


        State result = new CostFunctionService().scheduleNode(_nodes.get(5), 0, this.currentState, this.SUM_OF_ALL_NODES);

        // check that the Job scheduled is a TaskJob
        try{
            TaskJob scheduledJob =(TaskJob) result.getJobLists().get(0).get(6);
            Assert.assertTrue(result.getHeuristicValue() == 32.5);

            // check that the scheduled TaskJob is the root node
            Assert.assertEquals(scheduledJob.getNode(), _nodes.get(5));
            Assert.assertEquals(result.getJobListDuration()[0], 29);
        } catch (ClassCastException cce) {
            Assert.fail();
        }
    }

    @Test
    public void shouldScheduleChildWithMultipleParentsAndDelay() {
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
            Assert.assertEquals(result.getJobListDuration()[0], (_nodes.get(3)._duration + _nodes.get(0)._duration + _nodes.get(1)._duration + 1 + _nodes.get(2)._duration));
            Assert.assertTrue((result.getHeuristicValue() == 24.0));

            // check memory efficiency
            Assert.assertNotSame(result.getJobLists(), this.currentState.getJobLists());
            Assert.assertNotSame(result.getJobLists().get(0), this.currentState.getJobLists().get(0));
            Assert.assertSame(result.getJobLists().get(1).get(0), rootJob);
            Assert.assertNotSame(result.getJobListDuration(), this.currentState.getJobListDuration());
        } catch (ClassCastException cce) {
            Assert.fail();
        }
    }

    @Test
    public void shouldScheduleChildWithParentsThatHasMultipleChildrenAndDelay() {
        Job rootJob = new TaskJob(_nodes.get(0)._duration, _nodes.get(0)._name, _nodes.get(0));
        Job job1 = new TaskJob(_nodes.get(1)._duration, _nodes.get(1)._name, _nodes.get(1));
        Job job2 = new TaskJob(_nodes.get(2)._duration, _nodes.get(2)._name, _nodes.get(2));

        this.secondaryProcessor.add(rootJob);
        this.secondaryProcessor.add(job1);
        this.secondaryProcessor.add(job2);

        this.currentState.getJobListDuration()[1] += ((TaskJob) rootJob).getNode()._duration;
        this.currentState.getJobListDuration()[1] += ((TaskJob) job1).getNode()._duration;
        this.currentState.getJobListDuration()[1] += ((TaskJob) job2).getNode()._duration;

        State result = new CostFunctionService().scheduleNode(_nodes.get(7), 0, this.currentState, this.SUM_OF_ALL_NODES);

        // check that the Job scheduled is a TaskJob
        try{
            TaskJob scheduledJob =(TaskJob) result.getJobLists().get(0).get(1);

            // check that the scheduled TaskJob is the root node
            Assert.assertEquals(scheduledJob.getNode(), _nodes.get(7));
            Assert.assertEquals(result.getJobListDuration()[0], 13);
            System.out.println(result.getHeuristicValue());
            Assert.assertTrue(result.getHeuristicValue() == 24.0);


            Assert.assertNotSame(result.getJobLists(), this.currentState.getJobLists());
            Assert.assertNotSame(result.getJobLists().get(0), this.currentState.getJobLists().get(0));
            Assert.assertSame(result.getJobLists().get(1).get(0), rootJob);
            Assert.assertNotSame(result.getJobListDuration(), this.currentState.getJobListDuration());
        } catch (ClassCastException cce) {
            Assert.fail();
        }
    }
}
