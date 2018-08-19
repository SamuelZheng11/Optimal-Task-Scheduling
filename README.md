# The Original Sinnens

## Optimal Task Scheduling

This repository is used for storing code artefacts related to the first project of SoftEng 306 which lasts 6 weeks starting on the 16 July 2018, The Parallel Scheduling Problem.
### Contributors
The 5 architects who worked on solving this problems are:
A photo of the team can also be found under the code tab

**Samuel Zheng** : **GitHub Username**: samuelzheng11, **UID**: 634715579, **UPI**: szhe560 (Leader) (center)

**Edwar Zhang** : **GitHub Username**: Eddiwar, **UID**: 78468327, **UPI**: ezha219 (close Right)

**Alex Morgan** : **GitHub Username**: AlexRMorgan, **UID**: 5577546, **UPI**: amor789 (far left)

**Cameron Scoular** : **GitHub Username**: cameronscoular, **UID**: 139876164, **UPI**: csco768 (far right)

**Andrew Fairweather** : **GitHub Username**: theandrewfairweather, **UID**: 518641205, **UPI**: afai640 (close left)

## The Scheduling Problem
The problem description is given a certain number of processors and a number of tasks to scheduled on the processor, 
what is the solution that gives the earliest finish time of all tasks on the processors.

This problem is known as an non-deterministic non-polynomial hard problem (NP hard). This means that it is a very difficult problem
that is at least as difficult as the harded problems in its catagory of problems.

Our solution solves this by giving the least worst solution.

## Running the Application
To run our application:
1. Download jar file from the code tab (or build a jar from the repository). 
2. the open a terminal in the location where the jar is.
3. run the command "__java -jar 306_Parallel_Processes_Team_5 [name of the .DOT file that contains the tasks you want to find a minimal schedule for] [Integer representing the of processors]__"

Optional parameters:

Parameter representing the maximum number of threads the algorithm is allowed to use.

"__-p [Integer representing the number of cores on the machine or the maximum number of threads]__"

Parameter only to be used by advanced users who know excatly how wide or narrow they want the search to be. (depending on the complexity of the graph, this parameter can speed up the search significantly).

"__-b [Integer representing the boosting value]__"

Parameter representing what you want the output graph .dot file to be called. (see wiki on command lines for general values to use)

"__-o [Name of the output file]__"

Parameter to specify if the user wants the algorithm's search to be visualised.

"__-v__"

## Where to find detail description of project
For code information, high level information is found in the Wiki tab of this repository

For the project problems and bugs and issue tracking, the github issues board was used to document them

For the project deadlines and planning they can be found in both the "Planning and Notes for Project" folder as well as the Projects tab

The final jar to be marked is called "Planning and Notes for Project" under the code section

Communication between teams mainly happend either in person or electronically, meeting discussion can be found inside the "Planning and Notes for Project" folder. Plans, decisions and background research can also be found inside that folder
