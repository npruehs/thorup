package de.unikiel.npr.thorup.ds.graph;

/**
 * A graph G = (V, E) with V being the set of vertices of this graph,
 * and E being the set of edges with an integer edge weight function
 * d: E -> Z.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 * @param <T>
 * 		the type of the edges of this graph
 */
public interface WeightedGraph<T extends WeightedEdge> extends Graph<T> {
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
	 * @exception IllegalArgumentException
	 * 		if <code>i</code> or <code>j</code> are not between 0 and the
	 * 		number of vertices of this graph
	 * @exception IllegalArgumentException
	 * 		if there is no edge between the vertices with the indices
	 * 		<code>i</code> and <code>j</code> within this graph
	 */
	public int getEdgeWeight(int i, int j) throws IllegalArgumentException;
}
