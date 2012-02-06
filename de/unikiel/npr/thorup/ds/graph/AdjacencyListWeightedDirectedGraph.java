package de.unikiel.npr.thorup.ds.graph;

/**
 * An implementation of a weighted, directed graph using an adjacency list
 * for encoding the set of edges.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 * @param <T>
 * 		the type of the edges of this graph
 */
public class AdjacencyListWeightedDirectedGraph<T extends WeightedEdge>
	extends AdjacencyListUnweightedDirectedGraph<T> implements WeightedGraph<T> {

	/**
	 * Constructs a new weighted, directed graph with <code>n</code> vertices
	 * and no edges. Use {@link #addEdge(Edge)} in order to add edges.
	 * 
	 * @param n
	 * 		the number of vertices of the new graph
	 * @throws IllegalArgumentException
	 * 		if <code>n</code> is less than one
	 * @see #addEdge(Edge)
	 */
	public AdjacencyListWeightedDirectedGraph(int n)
		throws IllegalArgumentException {
		
		super(n);
	}
	
	/**
	 * Returns the weight of the edge between the vertices with the indices
	 * <code>i</code> and <code>j</code> within this graph, if there is one, and
	 * throws an {@link IllegalArgumentException}, otherwise.
	 * 
	 * @param i
	 * 		the index of the start vertex to check
	 * @param j
	 * 		the index of the end vertex to check
	 * @return
	 * 		the weight of the edge between the vertices with the indices
	 * 		<code>i</code> and	<code>j</code> within this graph, if there is
	 * 		one
	 * @throws IllegalArgumentException
	 * 		if <code>i</code> or <code>j</code> are not between 0 and the
	 * 		number of vertices of this graph
	 * @throws IllegalArgumentException
	 * 		if there is no edge between the vertices with the indices
	 * 		<code>i</code> and <code>j</code> within this graph
	 */
	public int getEdgeWeight(int i, int j) throws IllegalArgumentException {
		// get the edge between the vertices with the indices i and j
		T e = getEdge(i, j);
		
		// return its weight, if possible
		if (e != null) {
			return e.getWeight();
		}
		
		String errorMessage = "There is no edge between the vertices with " +
				"the indices " + i + " and " + j + " within this graph.";
		throw new IllegalArgumentException(errorMessage);
	}
	
	/**
	 * Returns an array containing the weights of all edges that are
	 * incident to the vertex with the index <code>i</code> within this graph.
	 * 
	 * @param i
	 * 		the index of the vertex to get the edge weights of
	 * @return
	 * 		an array containing the weights of all edges that are incident to
	 * 		the vertex with the index <code>i</code> within this graph
	 * @throws IllegalArgumentException
	 * 		if <code>i</code> is not between 0 and the number of vertices of
	 * 		this graph
	 */
	public int[] getArrayOfIncidentEdgeWeights(int i)
			throws IllegalArgumentException {
		
		// check the passed vertex index
		if (i < 0 || i >= numberOfVertices) {
			String errorMessage =
				"Allows vertex indizes are 0.."
				+ (numberOfVertices - 1) + ".";
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		// get the number of vertices adjacent to vertex i
		int n = adjacencyList[i].size();
		
		// create new edge weight array and fill with 1s
		int[] edgeWeights = new int[n];
		
		for (int v = 0; v < n; v++) {
			edgeWeights[v] = adjacencyList[i].get(v).getWeight();
		}
		
		return edgeWeights;
	}
}
