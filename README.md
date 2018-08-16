# 306_Parallel_Processes

This repository is used for storing code artefacts related to the first project of SoftEng 306 2018, The Parallel Scheduling Problem.
### Contributors
The 5 architects who worked on solving this problems are:

**Samuel Zheng** : **GitHub Username**: samuelzheng11, **UID**: 634715579, **UPI**: szhe560

**Edwar Zhang** : **GitHub Username**: Eddiwar, **UID**: 78468327, **UPI**: ezha219

**Alex Morgan** : **GitHub Username**: AlexRMorgan, **UID**: 5577546, **UPI**: amor789

**Cameron Scoular** : **GitHub Username**: cameronscoular, **UID**: 139876164, **UPI**: csco768

**Andrew Fairweather** : **GitHub Username**: theandrewfairweather, **UID**: 518641205, **UPI**: afai640

## The Scheduling Problem
The problem description is given a certain number of processors and a number of tasks to scheduled on the processor, 
what is the solution that gives the earliest finish time of all tasks on the processors.

This problem is known as an non-deterministic non-polynomial hard problem (NP hard). This means that it is a very difficult problem
that is at least as difficult as the harded problems in its catagory of problems.

Our solution solves this by giving the least worst solution.

## Running the Application
To run our application:
1. Download jar file (or build a jar from the repository). 
2. the open a terminal in the location where the jar is.
3. run the command "__java -jar 306_Parallel_Processes_Team_5 [name of the .DOT file that contains the tasks you want to find a minimal schedule for] [Integer representing the of processors]__"

Optional parameters:

This represents the maximum number of threads the algorithm is allowed to use.
"__-p [Integer representing the number of cores on the machine or the maximum number of threads]__"


"__-b [Integer representing the boosting value]__"

This is a parameter only to be used by advanced users who know excatly how wide or narrow they want the search to be. (depending on the complexity of the graph, this parameter can speed up the search significantly).

"__-o [Name of the output file]__"

This parameter represents what you want the output graph .dot file to be called.

"__-v__"

This parameter is to be used if the user wants the algorithm's search to be visualised.
