package de.unikiel.npr.thorup.algs;

import de.unikiel.npr.thorup.ds.PriorityQueue;
import de.unikiel.npr.thorup.ds.PriorityQueueItem;
import de.unikiel.npr.thorup.ds.graph.Edge;
import de.unikiel.npr.thorup.ds.graph.Graph;

/**
 * An implementation of <i>Dijkstra</i>'s single-source shortest paths
 * algorithm.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 */
public class Dijkstra {
	/**
	 * The predecessors of all vertices of the checked graph on their way to
	 * the source vertex.
	 */
	private int[] predecessors;
	
	/**
	 * The distances of all vertices of the checked graph from the source
	 * vertex.
	 */
	private int[] distances;
	

	/**
	 * Iterates the passed weighted graph, computing the paths from the passed
	 * source vertex to all others, including their corresponding distances.
	 * It is imperative that u is a vertex of g. The results of the algorithm
	 * can be accessed using {@link #getDistances()} and
	 * {@link #getPredecessors()} as soon as the algorithm has finished.
	 * 
	 * @param g
	 * 		the graph to run the algorithm on
	 * @param u
	 * 		the source vertex
	 * @param q 
	 * 		the priority queue used for maintaining the order the vertices are
	 * 		visited in
	 * @throws IllegalArgumentException
	 * 		if the passed graph is <code>null</code>
	 * @throws IllegalArgumentException
	 * 		if <code>u</code> is not a vertex of <code>g</code>
	 * @see #getDistances()
	 * @see #getPredecessors()
	 */
	@SuppressWarnings("unchecked")
	public void findShortestPaths(Graph<? extends Edge> g, int u,
		PriorityQueue q) throws IllegalArgumentException {
		
		// check arguments
		if (g == null) {
			String errorMessage = "The passed graph musn't be null.";
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (u < 0 || u >= g.getNumberOfVertices()){
			String errorMessage = "The vertex with index " + u +
				" is not within the passed graph.";
			throw new IllegalArgumentException(errorMessage);
		}
		
		// get the number of vertices of the passed graph
		int n = g.getNumberOfVertices();
		
		// initialize help arrays
		int[] colors = new int[n];
		predecessors = new int[n];
		distances = new int[n];
		
		int white = 0;
		int gray = 1;
		int black = 2;
		
		for (int v = 0; v < n; v++) {
			colors[v] = white;
			predecessors[v] = -1;
			distances[v] = -1;
		}
		
		// mark the start vertex as visited
		colors[u] = gray;
		
		// initialize the priority queue
		PriorityQueueItem<Integer>[] items = new PriorityQueueItem[n];
		
		PriorityQueueItem<Integer> item = q.insert(u, 0);
		items[u] = item;
		
		// extend distance tree until border is empty
		while (!q.isEmpty()) {
			// get next vertex for the distance tree and set its distance
			item = q.deleteMin();
			int d = (int)item.getKey();
			int v = (int)item.getItem();
			distances[v] = d;
			
			// mark that vertex as visited
			colors[v] = black;
			
			// update border and border approximation
			int[] a = g.getArrayOfAdjacentVertices(v);
			int[] da = g.getArrayOfIncidentEdgeWeights(v);
			
			for (int i = 0; i < a.length; i++) {
				// get next neighbor
				int w = a[i];
				
				// update approximation
				int dw = d + da[i];
				
				// update entry if necessary
				if (colors[w] == white ||
					(colors[w] == gray && items[w].getKey() > dw)) {
					colors[w] = gray;
					predecessors[w] = v;
					
					if (items[w] == null) {
						items[w] = q.insert(w, dw);
					} else {
						q.decreaseKeyTo(items[w], dw);
					}
				}
			}
		}
	}
	
	
	/**
	 * Gets the predecessors of all vertices of the checked graph on their way
	 * to the source vertex.
	 * 
	 * @return
	 * 		the predecessors of all vertices of the checked graph on their way
	 * 		to the source vertex
	 */
	public int[] getPredecessors() {
		return predecessors;
	}

	/**
	 * Gets the distances of all vertices of the checked graph from the source
	 * vertex.
	 * 
	 * @return
	 * 		the distances of all vertices of the checked graph from the source
	 * 		vertex
	 */
	public int[] getDistances() {
		return distances;
	}
}
