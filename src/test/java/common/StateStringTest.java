package common;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StateStringTest {

    @Test
    public void shouldReturnStateStringAsEmptyString(){
        State state = new State(new ArrayList<>(), new int[]{}, 0, 0);
        Assert.assertEquals("", state.toString());
    }

    @Test
    public void shouldReturnStateStringWithSingleNode(){
        List<List<Job>> jobList = new ArrayList<>();
        TaskDependencyNode node = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(0));
        jobList.add(new ArrayList<>());
        jobList.get(0).add(new TaskJob(0, node._name, node));
        State state = new State(jobList, new int[]{}, 0, 0);
        Assert.assertEquals("0", state.toString());
    }

    @Test
    public void shouldReturnStateStringDelayWithTaskNode(){
        List<List<Job>> jobList = new ArrayList<>();
        TaskDependencyNode node = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(0));
        jobList.add(new ArrayList<>());
        jobList.get(0).add(new DelayJob(0));
        jobList.get(0).add(new TaskJob(0, node._name, node));
        State state = new State(jobList, new int[]{}, 0, 0);
        Assert.assertEquals("Delay0", state.toString());
    }

    @Test
    public void shouldHaveTwoStateStringsBeEqualButNotSame(){
        List<List<Job>> jobList1 = new ArrayList<>();
        TaskDependencyNode node1 = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(0));
        jobList1.add(new ArrayList<>());
        jobList1.get(0).add(new DelayJob(0));
        jobList1.get(0).add(new TaskJob(0, node1._name, node1));
        State state1 = new State(jobList1, new int[]{}, 0, 0);

        List<List<Job>> jobList2 = new ArrayList<>();
        TaskDependencyNode node2 = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(0));
        jobList2.add(new ArrayList<>());
        jobList2.get(0).add(new DelayJob(0));
        jobList2.get(0).add(new TaskJob(0, node2._name, node2));
        State state2 = new State(jobList2, new int[]{}, 0, 0);

        Assert.assertEquals("Delay0", state1.toString());
        Assert.assertEquals("Delay0", state2.toString());
        Assert.assertEquals(state1.toString(), state2.toString());
        Assert.assertNotSame(state1, state2);
    }

    @Test
    public void shouldHaveTwoStateStringsNotEqual(){
        List<List<Job>> jobList1 = new ArrayList<>();
        TaskDependencyNode node1 = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(0));
        jobList1.add(new ArrayList<>());
        jobList1.get(0).add(new DelayJob(0));
        jobList1.get(0).add(new TaskJob(0, node1._name, node1));
        State state1 = new State(jobList1, new int[]{}, 0, 0);

        List<List<Job>> jobList2 = new ArrayList<>();
        TaskDependencyNode node2 = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(1));
        jobList2.add(new ArrayList<>());
        jobList2.get(0).add(new DelayJob(0));
        jobList2.get(0).add(new TaskJob(0, node2._name, node2));
        State state2 = new State(jobList2, new int[]{}, 0, 0);

        Assert.assertEquals("Delay0", state1.toString());
        Assert.assertEquals("Delay1", state2.toString());
        Assert.assertNotEquals(state1.toString(), state2.toString());
        Assert.assertNotSame(state1, state2);
    }

    @Test
    public void shouldHaveTwoComplexStateStringsNotEqual(){
        List<List<Job>> jobList1 = new ArrayList<>();
        TaskDependencyNode node0 = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(0));
        TaskDependencyNode node2 = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(2));
        TaskDependencyNode node4 = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(4));
        TaskDependencyNode node6 = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(6));
        TaskDependencyNode node8 = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(8));
        jobList1.add(new ArrayList<>());
        jobList1.get(0).add(new TaskJob(0, node0._name, node0));
        jobList1.get(0).add(new DelayJob(0));
        jobList1.get(0).add(new TaskJob(0, node2._name, node2));
        jobList1.get(0).add(new TaskJob(0, node4._name, node4));
        jobList1.get(0).add(new DelayJob(0));
        jobList1.get(0).add(new DelayJob(0));
        jobList1.add(new ArrayList<>());
        jobList1.get(1).add(new TaskJob(0, node6._name, node6));
        jobList1.get(1).add(new TaskJob(0, node8._name, node8));
        jobList1.add(new ArrayList<>());
        jobList1.get(2).add(new DelayJob(0));
        jobList1.get(2).add(new TaskJob(0, node4._name, node4));

        State state1 = new State(jobList1, new int[]{}, 0, 0);

        List<List<Job>> jobList2 = new ArrayList<>();
        TaskDependencyNode node1 = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(1));
        TaskDependencyNode node3 = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(3));
        TaskDependencyNode node7 = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(7));
        TaskDependencyNode node9 = new TaskDependencyNode(0, new ArrayList<>(), new ArrayList<>(), String.valueOf(9));
        jobList2.add(new ArrayList<>());
        jobList2.get(0).add(new TaskJob(0, node1._name, node1));
        jobList2.get(0).add(new DelayJob(0));
        jobList2.get(0).add(new TaskJob(0, node3._name, node3));
        jobList2.get(0).add(new TaskJob(0, node0._name, node0));
        jobList2.get(0).add(new DelayJob(0));
        jobList2.get(0).add(new DelayJob(0));
        jobList2.add(new ArrayList<>());
        jobList2.get(1).add(new DelayJob(0));
        jobList2.get(1).add(new TaskJob(0, node7._name, node7));
        jobList2.add(new ArrayList<>());
        jobList2.get(2).add(new TaskJob(0, node9._name, node9));
        jobList2.get(2).add(new DelayJob(0));

        State state2 = new State(jobList2, new int[]{}, 0, 0);

        Assert.assertEquals("0Delay24DelayDelayDelay468", state1.toString());
        Assert.assertEquals("1Delay30DelayDelayDelay79Delay", state2.toString());
        Assert.assertNotEquals(state1.toString(), state2.toString());
        Assert.assertNotSame(state1, state2);
    }
}
