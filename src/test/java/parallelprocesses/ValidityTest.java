package parallelprocesses;


import common.*;
import javafx.concurrent.Task;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertTrue;

public class ValidityTest {

    @Test
    public void testAll(){
        TestMain.main(new String[]{"Input/example-input-graphs/Nodes_11_OutTree.dot", "2"});
        Graph g = DependencyGraph.getGraph().getInputGraph();
        State s = RecursionStore.getBestState();
        assertTrue(isValid(s, g));

    }

    public boolean isValid(State state, Graph graph){
        PriorityQueue<ScheduledJob> toBeCheckedJobs = new PriorityQueue<ScheduledJob>();
        Map<String, ScheduledJob> checkedJobs = new HashMap();
        List<TaskDependencyNode> dependencies = new ArrayList<>();
        for(int i = 0; i < state.getJobLists().size(); i++){
            List<Job> tempJobList = state.getJobLists().get(i);
            int start = 0;
            for(Job j : tempJobList){
                if(j instanceof TaskJob){
                    ScheduledJob sj = new ScheduledJob((TaskJob) j, start, i);
                    toBeCheckedJobs.add(sj);
                }
                start += j.getDuration();
            }
        }

        while(toBeCheckedJobs.peek() != null){
            ScheduledJob sj = toBeCheckedJobs.poll();
            checkedJobs.put(sj._taskJob.getName(), sj);
            if(sj._startTime == 0){
                if(!sj._taskJob.getNode()._parents.isEmpty()){
                    return false;
                }
            }else{
                for(TaskDependencyEdge e : sj._taskJob.getNode()._parents){
                    dependencies.add(e._parent);
                }
                for(TaskDependencyNode n : dependencies){
                    if(!checkedJobs.containsKey(n._name)){
                        return false;
                    }
                    ScheduledJob dependency = checkedJobs.get(n._name);
                    if(dependency._processorNo != sj._processorNo && sj._startTime < dependency._finishTime){
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
