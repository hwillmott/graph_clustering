graph_clustering
================
This project consists of two main applications, one to pull the data
from freebase.com and print it into a text file in a format suited
to the graph clustering application (can be found in the freebase folder) 
and the second is the actual graph clustering application (found in 
the GraphClustering folder).

The freebase application is a single python file, which, when run,
will pull the specified data from freebase.com in JSON format and
print it out to a text file.

The graph clustering application is a main java file, GraphHandler.java,
which uses several other java classes to do everything. Graph.java 
holds the implementation of the graph and the graph clustering algorithms.
To run GraphHandler.java, the main method must be altered to suit your
needs, and Graph.java may need to be altered, depending on what data 
you wish to print. Both are heavily commented, so it shouldn't be
very difficult.
