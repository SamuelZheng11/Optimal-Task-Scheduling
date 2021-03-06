package cost_function;

import common.*;
import exception_classes.CostFunctionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class is a service class which can be called by the recursive algorithm to determine add a node onto a processor
 */
public class CostFunctionService {
    private State state;
    private int inputProcessorCompletionTime = 0;
    private int processorThatFinishesLast = 0;
    private List<Boolean> parentsFound;
            
    /**
     *  This function is called by the recursive algorithm to get a state with new node scheduled and an associated 'cost'
     *  value of having that node scheduled on a given processor to compare against the current state to determine if
     *  its better.
     *
     * @param node: the target node that you want to schedule
     * @param onProcessorNumber: the target processor you want to schedule your target node on
     * @param withCurrentState: the current state (before) the node has been scheduled, this is needed for the function
     *                        determine when to schedule the node based on which other processors has the target node's
     *                        parents
     * @param costOfAllNodes: the cost of all the the node's durations summed together. This is used to calculate the
     *                      heuristic of the state with the new node scheduled on the target processor
     * @return: returns a state with the target node scheduled on the target processors with a new cost heuristic
     */
    public State scheduleNode(TaskDependencyNode node, int onProcessorNumber, State withCurrentState, int costOfAllNodes) {
        // generate a deep copy of the input state
        this.generateDeepCopy(withCurrentState, node);
        this.parentsFound = new ArrayList<>(Collections.nCopies(node._parents.size(), Boolean.FALSE));

        // generate the arrays that will be used to easily find a parent and its communication delay with the target node
        ArrayList<TaskDependencyNode> parentNodes = new ArrayList<>();
        ArrayList<Integer> parentCommDelayEdges = new ArrayList<>();
        for(int i = 0; i < node._parents.size(); i++){
            parentNodes.add(node._parents.get(i)._parent);
            parentCommDelayEdges.add(node._parents.get(i)._communicationDelay);
        }

        // check if the current processor contains any parent jobs
        this.processTargetProcessor(this.state.getJobLists().get(onProcessorNumber), parentNodes);
        boolean allParentOnTargetProcessor = this.parentsFound.stream().allMatch(foundParent -> foundParent);

        // check if the node has any parents
        if (parentNodes.size() == 0) {

            // returns the state with the new task on the processor
            this.state.getJobLists().get(onProcessorNumber).add(new TaskJob(node._duration, node._name, node));
            this.state.getJobListDuration()[onProcessorNumber] += node._duration;
            this.determineProcessorThatFinishesLast();

            double fullTime = this.state.getJobListDuration()[this.processorThatFinishesLast];
            double freeSpace = 0;
            for (int i = 0; i < this.state.getJobListDuration().length; i++) {
                if (i == this.processorThatFinishesLast){
                    continue;
                }
                freeSpace += fullTime-this.state.getJobListDuration()[i];
            }
            double heuristicValue = fullTime;
            double costOfNodesAfterGapsFilled = (double)costOfAllNodes - (this.state.getSumOfScheduledTasks() + freeSpace);
            if (costOfNodesAfterGapsFilled > 0){
                heuristicValue += costOfNodesAfterGapsFilled/withCurrentState.getJobLists().size();
            }


            // return state early with node scheduled as it has not parents
            return new State(
                    this.state.getJobLists(),
                    this.state.getJobListDuration(),
                    heuristicValue,
                    this.state.getSumOfScheduledTasks()
            );
        }

        // otherwise attempt to find the parent with the lowest communication time
        int costOfSchedulingNode;
        if(!allParentOnTargetProcessor) {
            costOfSchedulingNode = this.calculateCostToSchedule(parentNodes, parentCommDelayEdges, onProcessorNumber);
        } else {
            costOfSchedulingNode = 0;
        }

        // check that the lowest communication time is not infinity
        if (costOfSchedulingNode == -1){
            throw new CostFunctionException("Not all parents have been scheduled yet");
        }

        // add to processor with communication delay
        if(!allParentOnTargetProcessor && costOfSchedulingNode > this.inputProcessorCompletionTime) {
            this.state.getJobLists().get(onProcessorNumber).add(new DelayJob(costOfSchedulingNode - this.inputProcessorCompletionTime));
            this.state.getJobListDuration()[onProcessorNumber] += costOfSchedulingNode - this.inputProcessorCompletionTime;
        }

        // schedule the node and update job lists for returning state
        this.state.getJobLists().get(onProcessorNumber).add(new TaskJob(node._duration, node._name, node));
        this.state.getJobListDuration()[onProcessorNumber] += node._duration;
        this.determineProcessorThatFinishesLast();

        double fullTime = this.state.getJobListDuration()[this.processorThatFinishesLast];
        double freeSpace = 0;
        for (int i = 0; i < this.state.getJobListDuration().length; i++) {
            if (i == this.processorThatFinishesLast){
                continue;
            }
            freeSpace += fullTime-this.state.getJobListDuration()[i];
        }
        double heuristicValue = fullTime;
        double costOfNodesAfterGapsFilled = (double)costOfAllNodes - this.state.getSumOfScheduledTasks() - freeSpace;
        if (costOfNodesAfterGapsFilled > 0){
            heuristicValue += costOfNodesAfterGapsFilled/withCurrentState.getJobLists().size();
        }

        return new State(
                this.state.getJobLists(),
                this.state.getJobListDuration(),
                heuristicValue,
                this.state.getSumOfScheduledTasks()
        );
    }

    /**
     *  This method is an internal method special to the CostFunctionService. It is used to determine the cost of
     *  schedule a node given that not all of its parents are on target processor.
     * @param parents: all the parent nodes of the target node supplied in the scheduleNode method signature.
     * @param commDealy: all the communication delays associated with the parent nodes of the target node
     * @param skipProcessorNumber: the processors that we know is guaranteed to NOT contain all of the parent processors
     * @return: An integer representing the total amount of time waiting before the target node can be scheduled from
     * very first node
     */
    private int calculateCostToSchedule(ArrayList<TaskDependencyNode> parents, ArrayList<Integer> commDealy, int skipProcessorNumber) {

        // find the parent's completion time on other processors
        List<Integer> buildingCostForProcessor = new ArrayList<>(Collections.nCopies(this.state.getJobLists().size(), 0));
        List<Integer> longestReadyTime = new ArrayList<>(Collections.nCopies(this.state.getJobLists().size(), 0));
        List<Boolean> parentFound = new ArrayList<>(Collections.nCopies(this.state.getJobLists().size(), Boolean.FALSE));

        for (int i = 0; i < this.state.getJobLists().size(); i++){

            // skip the row if it has already been check by the outer method
            if(skipProcessorNumber == i) {
                continue;
            }

            longestReadyTime.set(i, 0);

            for (int j = 0; j < this.state.getJobLists().get(i).size(); j++){
                buildingCostForProcessor.set(i, buildingCostForProcessor.get(i) + this.state.getJobLists().get(i).get(j).getDuration());
                if (this.state.getJobLists().get(i).get(j).getClass() == TaskJob.class) {
                    TaskJob potentialParentJob = (TaskJob) this.state.getJobLists().get(i).get(j);
                    // check if the TaskJob's node is a parent of the node
                    if (parents.contains(potentialParentJob.getNode())) {
                        // only update the current best cost for the processor if the cost of the processor up to the
                        // parent's node + the parents node's communication delay is less than the current best
                        if (longestReadyTime.get(i) < buildingCostForProcessor.get(i) + commDealy.get(parents.indexOf(potentialParentJob.getNode()))) {
                            longestReadyTime.set(i, buildingCostForProcessor.get(i) + commDealy.get(parents.indexOf(potentialParentJob.getNode())));
                            parentFound.set(i, true);
                        }
                    }
                }
            }
        }

        // calculate best cost to schedule with
        int costOfSchedulingNode = -1;
        for(int i = 0; i < longestReadyTime.size(); i++){
            // skip the row if it has already been check by the outer method
            if(skipProcessorNumber == i) {
                continue;
            }

            // get the lowest cost out of all the processors
            if(parentFound.get(i) && longestReadyTime.get(i) > costOfSchedulingNode){
                costOfSchedulingNode = longestReadyTime.get(i);
            }
        }

        return costOfSchedulingNode;
    }

    /**
     * This method is an internal method special to the CostFunctionService. It check the target processors (the one
     * you want to schedule the node on) determining which parents the target node have already been scheduled on the
     * target processor
     * @param onProcessor: the target processors passed in on the schedule node processor
     * @param parentNodes: all the parent nodes that are associated with the target node
     */
    private void processTargetProcessor(List<Job> onProcessor, ArrayList<TaskDependencyNode> parentNodes) {
        // check if a parent of the node to be scheduled is on the current processor
        // sum up the cost of tasks on the processor for the heuristic cost
        for (int i = 0; i < onProcessor.size(); i++) {
            this.inputProcessorCompletionTime += onProcessor.get(i).getDuration();
            for (TaskDependencyNode parentNode: parentNodes) {
                if(onProcessor.get(i) instanceof TaskJob) {
                    TaskJob job = (TaskJob) onProcessor.get(i);
                    if (job.getNode() == parentNode) {
                        this.parentsFound.set(parentNodes.indexOf(parentNode), true);
                    }
                }
            }
        }
    }

    /**
     * This method is an internal method special to the CostFunctionService. because Java does not do deep copying, and
     * the recursive algorithm can modify the state that was passed into the method signature, a separate state needs to
     * be passed in.
     * @param inputState: the input state that was passed into the method signature
     */
    private void generateDeepCopy(State inputState, TaskDependencyNode node) {
        List<List<Job>> jobs = new ArrayList<>(inputState.getJobLists().size());

        // create deep copy as java does not do deep copying
        for(int i = 0; i < inputState.getJobLists().size(); i++){
            jobs.add(new ArrayList<>());
            jobs.set(i, new ArrayList<>(inputState.getJobLists().get(i)));
        }
        this.state = new State( jobs, Arrays.copyOf(inputState.getJobListDuration(), inputState.getJobListDuration().length), 0, inputState.getSumOfScheduledTasks()+node._duration);
    }

    private void determineProcessorThatFinishesLast() {
        for (int i = 0; i < this.state.getJobLists().size(); i++) {
            if(this.state.getJobListDuration()[this.processorThatFinishesLast] < this.state.getJobListDuration()[i]) {
                this.processorThatFinishesLast = i;
            }
        }
    }
}
