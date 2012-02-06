package de.unikiel.npr.thorup.ds.graph;

import java.util.ArrayList;

/**
 * A graph G = (V, E) with V being the set of vertices of this graph,
 * and E being the set of edges.
 *  
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 * @param <T>
 * 		the type of the edges of this graph
 */
public interface Graph<T extends Edge> {
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
	boolean hasEdge(int i, int j) throws IllegalArgumentException;
	
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
	T getEdge(int i, int j) throws IllegalArgumentException;
	
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
	int[] getArrayOfAdjacentVertices(int i) throws IllegalArgumentException;
	
	/**
	 * Returns an array containing the weights of all edges that are
	 * incident to the vertex with the index <code>i</code> within this graph.
	 * For unweighted graphs, all entries should be 1.
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
	int[] getArrayOfIncidentEdgeWeights(int i) throws IllegalArgumentException;
	
	/**
	 * Returns the number of vertices of this graph.
	 * 
	 * @return
	 * 		the number of vertices of this graph
	 */
	int getNumberOfVertices();
	
	/**
	 * Returns the number of edges of this graph.
	 * 
	 * @return
	 * 		the number of edges of this graph
	 */
	int getNumberOfEdges();
	
	/**
	 * Returns the list of edges that are incident to the vertex with the
	 * passed index.
	 * 
	 * @param v
	 * 		the index of the vertex to get all incident edges of
	 * @return
	 * 		the list of edges that are incident to the vertex with the
	 * 		passed index
	 */
	ArrayList<T> getIncidentEdges(int v);
}
