# Thorup

Mikkel Thorup found the first deterministic algorithm to solve the classic single-source shortest paths problem for undirected graphs with positive integer weights in linear time and space. The algorithm requires a hierarchical bucketing structure for identifying the order the vertices have to be visited in without breaking this time bound, thus avoiding the sorting bottleneck of the algorithm proposed by Dijkstra in 1959.

Initializing the buckets takes a lot of time, compared to the search time of the algorithm. However, as soon as all data structures have been prepared, they don’t need to be computed again – this makes Thorup’s algorithm very attractive for repetitive queries.

My Bachelor Thesis consists of a Java implementation of the algorithms by Thorup and Dijkstra and their descriptions, as well as the comparison of their running times.
