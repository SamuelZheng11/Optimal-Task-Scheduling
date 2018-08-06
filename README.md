# 306_Parallel_Processes

This repository is used for storing code artefacts related to the first project of SoftEng 306 2018, The Parallel Scheduling Problem.
### Contributors
The 5 architects who worked on solving this problems are:

**Samuel Zheng** : **GitHub Username**: samuelzheng11, **UID**: 634715579, **UPI**: szhe560

**Edwar Zhang** : **GitHub Username**: Eddiwar, **UID**: 78468327, **UPI**: ezha219

**Alex Morgan** : **GitHub Username**: X, **UID**: X, **UPI**: X

**Cameron Scoular** : **GitHub Username**: X, **UID**: X, **UPI**: X

**Andrew Fairweather** : **GitHub Username**: X, **UID**: X, **UPI**: X

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
3. run the command "java -jar 306_Parallel_Processes_Team_5 [name of the .DOT file that contains the tasks you want to find a minimal schedule for] -p [Integer representing the of processors] -o [Name of the output file]"

NOTE: As this program is still under development, The jar and the file containing the tasks must be in the same place and must directory for an output to be generated. In addtion the view (-v) parameter is not configured to display a view as it is not a requirement for milestone 1
