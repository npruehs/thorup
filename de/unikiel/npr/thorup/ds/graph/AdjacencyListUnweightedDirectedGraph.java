package de.unikiel.npr.thorup.ds.graph;

import java.util.ArrayList;

/**
 * An implementation of an unweighted, directed graph using an adjacency list
 * for encoding the set of edges.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 * @param <T>
 * 		the type of the edges of this graph
 */
public class AdjacencyListUnweightedDirectedGraph<T extends Edge>
	implements Graph<T> {
	
	/**
	 * The number of vertices of this graph.
	 */
	int numberOfVertices;
	
	/**
	 * The number of edges of this graph.
	 */
	int numberOfEdges;
	
	/**
	 * The adjacency list encoding the set of edges of this graph.
	 */
	ArrayList<T>[] adjacencyList;
	
	
	/**
	 * Constructs a new unweighted, directed graph with <code>n</code> vertices
	 * and no edges. Use {@link #addEdge(Edge)} in order to add edges.
	 * 
	 * @param n
	 * 		the number of vertices of the new graph
	 * @throws IllegalArgumentException
	 * 		if <code>n</code> is less than one
	 * @see #addEdge(Edge)
	 */
	@SuppressWarnings("unchecked")
	public AdjacencyListUnweightedDirectedGraph(int n)
		throws IllegalArgumentException {
		
		// check the passed number of vertices
		if (n < 1) {
			String errorMessage = "n must be greater than or equal to 1.";
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		// remember the number of vertices of this graph
		numberOfVertices = n;

		// initially there are no edges
		adjacencyList = new ArrayList[n];
		
		for (int i = 0; i < n; i++) {
			adjacencyList[i] = new ArrayList<T>();				
		}
	}
	
	
	/**
	 * Adds the passed edge to this graph, if there is no edge between
	 * its source and target vertices in this graph yet. An
	 * <code>IllegalArgumentException</code> is thrown, otherwise.
	 * 
	 * @param edge
	 * 		the edge to add
	 * @throws IllegalArgumentException
	 * 		if the source or target of the passed edge are not between 0 and the
	 * 		number of vertices of this graph
	 * @throws IllegalArgumentException
	 * 		if there already is an edge between the source and target vertices
	 * 		of the passed one
	 */
	public void addEdge(T edge) throws IllegalArgumentException {
		// check the passed vertex indices
		if (edge.getSource() < 0 || edge.getSource() >= numberOfVertices ||
			edge.getTarget() < 0 || edge.getTarget() >= numberOfVertices) {
			
			String errorMessage =
				"Allows vertex indizes are 0.."
				+ (numberOfVertices - 1) + ".";
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		/*
		 * update the number of edges if there has been no edge between the
		 * passed vertices yet, only
		 */
		if (hasEdge(edge.getSource(), edge.getTarget())) {
			String errorMessage =
				"There already is an edge between the vertices " +
				edge.getSource() + " and " + edge.getTarget() +
				" in this graph."; 
			
			throw new IllegalArgumentException(errorMessage);			
		}
		
		adjacencyList[edge.getSource()].add(edge);
		numberOfEdges++;
	}
	
	/**
	 * Returns true, if there is an edge between the vertices with the indices
	 * <code>i</code> and <code>j</code> within this graph, and false otherwise.
	 * 
	 * @param i
	 * 		the index of the start vertex to check
	 * @param j
	 * 		the index of the end vertex to check
	 * @return
	 * 		true, if there is an edge between the vertices with the indices
	 * 		<code>i</code> and <code>j</code> within this graph, and<br>
	 * 		false, otherwise
	 * @throws IllegalArgumentException
	 * 		if <code>i</code> or <code>j</code> are not between 0 and the
	 * 		number of vertices of this graph
	 */
	public boolean hasEdge(int i, int j) throws IllegalArgumentException {
		return (getEdge(i, j) != null);
	}

	/**
	 * Returns the edge between the vertices with the indices <code>i</code> and
	 * <code>j</code> within this graph, if there is one, and <code>null</code>
	 * otherwise.
	 * 
	 * @param i
	 * 		the index of the start vertex to get the edge of
	 * @param j
	 * 		the index of the end vertex to get the edge of
	 * @return
	 * 		the edge between the vertices with the indices <code>i</code> and
	 * 		<code>j</code> within this graph, if there is one, and
	 * 		<code>null</code> otherwise
	 * @throws IllegalArgumentException
	 * 		if <code>i</code> or <code>j</code> are not between 0 and the
	 * 		number of vertices of this graph
	 */
	public T getEdge(int i, int j) throws IllegalArgumentException {
		// check the passed vertex indizes
		if (i < 0 || i >= numberOfVertices || j < 0 || j >= numberOfVertices) {
			String errorMessage =
				"Allows vertex indizes are 0.."
				+ (numberOfVertices - 1) + ".";
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (T e : adjacencyList[i]) {
			if (e.getTarget() == j) {
				return e;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns an array containing the indices of all vertices that are
	 * adjacent to the vertex with the index <code>i</code> within this graph.
	 * 
	 * @param i
	 * 		the index of the vertex to get the adjacent vertices of
	 * @return
	 * 		an array containing the indices of all vertices that are adjacent to
	 * 		the vertex with the index <code>i</code> within this graph
	 * @throws IllegalArgumentException
	 * 		if <code>i</code> is not between 0 and the number of vertices of
	 * 		this graph
	 */
	public int[] getArrayOfAdjacentVertices(int i)
		throws IllegalArgumentException {
		
		// check the passed vertex index
		if (i < 0 || i >= numberOfVertices) {
			String errorMessage =
				"Allows vertex indizes are 0.."
				+ (numberOfVertices - 1) + ".";
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		// convert the adjacency list to an array of appropriate size
		int n = adjacencyList[i].size();
		int[] a = new int[n];
		
		for (int v = 0; v < n; v++) {
			a[v] = adjacencyList[i].get(v).getTarget();
		}
		
		return a;
	}

	/**
	 * Returns an array with the length of the number of edges that are
	 * incident to the vertex with the index <code>i</code> within this graph,
	 * with every entry set to <code>1</code> as this graph is unweighted.
	 * 
	 * @param i
	 * 		the index of the vertex to get the edge weights of
	 * @return
	 * 		an array with the length of the number of edges that are
	 * 		incident to the vertex with the index <code>i</code> within this
	 * 		graph, with every entry set to <code>1</code>
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
		
		for (int j = 0; j < n; j++) {
			edgeWeights[j] = 1;
		}
		
		return edgeWeights;
	}
	
	/**
	 * Returns the list of edges that are incident to the vertex with the
	 * passed index.<br>
	 * <br>
	 * <i>Note that the passed list is backed by the adjacency list of this
	 * graph; thus modifying the passed list will change the edges incident
	 * to <code>v</code></i>.
	 * 
	 * @param v
	 * 		the index of the vertex to get all incident edges of
	 * @return
	 * 		the list of edges that are incident to the vertex with the
	 * 		passed index
	 */
	public ArrayList<T> getIncidentEdges(int v) {
		// check the passed vertex index
		if (v < 0 || v >= numberOfVertices) {
			String errorMessage =
				"Allows vertex indizes are 0.."
				+ (numberOfVertices - 1) + ".";
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		return adjacencyList[v];
	}
	
	/**
	 * Returns the number of vertices of this graph.
	 * 
	 * @return
	 * 		the number of vertices of this graph
	 */
	public int getNumberOfVertices() {
		return numberOfVertices;
	}
	
	/**
	 * Returns the number of edges of this graph.
	 * 
	 * @return
	 * 		the number of edges of this graph
	 */
	public int getNumberOfEdges() {
		return numberOfEdges;
	}
	
	/**
	 * Returns <code>true</code>, if this graph equals the passed one, and
	 * <code>false</code> otherwise. Requires that the equality of edges
	 * of this graph is correclty definied.<br>
	 * <br>
	 * Two graphs are considered equals, if the following two conditions hold:
	 * <ul>
	 * 		<li>their number of vertices are equal, and</li>
	 * 		<li>their edge sets mutual include each other</li>
	 * </ul>
	 * 
	 * @param other
	 * 		the graph to check for equality
	 * @return
	 * 		<code>true</code>, if this graph equals the passed one, and<br>
	 * 		<code>false</code>, otherwise
	 */
	public boolean equals(AdjacencyListUnweightedDirectedGraph<T> other) {
		return (numberOfVertices == other.numberOfVertices &&
				numberOfEdges == other.numberOfEdges &&
				edgeSetsAreEqual(other));
		
	}
	
	/**
	 * Checks if the edge sets of this graph and the passed one mutual include
	 * each other.
	 * 
	 * @param other
	 * 		the graph to check the edge set of
	 * @return
	 * 		<code>true</code>, if the edge sets of this graph and the passed one
	 * 		mutual include each other, and<br>
	 * 		<code>false</code>, otherwise
	 */
	private boolean edgeSetsAreEqual
		(AdjacencyListUnweightedDirectedGraph<T> other) {
		
		for (ArrayList<T> singleAdjacencyList : adjacencyList) {
			for (T edge : singleAdjacencyList) {
				T otherEdge = other.getEdge(edge.source, edge.target);
				
				if (otherEdge == null || !(edge.equals(otherEdge))) {
					return false;
				}
			}
		}
		
		for (ArrayList<T> singleAdjacencyList : other.adjacencyList) {
			for (T edge : singleAdjacencyList) {
				T otherEdge = getEdge(edge.source, edge.target);
				
				if (otherEdge == null || !(edge.equals(otherEdge))) {
					return false;
				}
			}
		}
		
		return true;
	}
}
