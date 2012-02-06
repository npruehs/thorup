package de.unikiel.npr.thorup.algs;

import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import de.unikiel.npr.thorup.ds.graph.AdjacencyListWeightedDirectedGraph;
import de.unikiel.npr.thorup.ds.graph.WeightedEdge;
import de.unikiel.npr.thorup.ds.graph.WeightedGraph;

/**
 * An implementation of <i>Prim</i>'s algorithm for the computation of minimum
 * spanning trees.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 07/31/09
 */
public class Prim implements MSTAlgorithm {
	/**
	 * Computes and returns a minimum spanning tree of the passed weighted,
	 * undirected graph in <i>O(n<sup>2</sup>)</i>.
	 * 
	 * @param g
	 * 		the graph to compute a minimum spanning tree of
	 * @return
	 * 		a minimum spanning tree of g
	 * @throws IllegalArgumentException
	 * 		if the passed graph is <code>null</code>
	 */
	public AdjacencyListWeightedDirectedGraph<WeightedEdge>
		findSolution(WeightedGraph<? extends WeightedEdge> g) {
		
		// check arguments
		if (g == null) {
			String errorMessage = "The passed graph musn't be null.";
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		// prepare constructing the minimum spanning tree
		AdjacencyListWeightedDirectedGraph<WeightedEdge> mst =
			new AdjacencyListWeightedDirectedGraph<WeightedEdge>
				(g.getNumberOfVertices());

		// 1.
		LinkedHashSet<Integer> u = new LinkedHashSet<Integer>();
		u.add(0);
		
		Hashtable<Integer, Integer> closest = new Hashtable<Integer, Integer>();
		
		for (int v = 0; v < g.getNumberOfVertices(); v++) {
			if (g.hasEdge(0, v)) {
				closest.put(v, 0);
			}
		}
		
		// 2.
		while (true) {
			LinkedList<WeightedEdge> potentialEdges =
				new LinkedList<WeightedEdge>();
	
			for (int v = 0; v < g.getNumberOfVertices(); v++) {
				if (u.contains(v) || !closest.containsKey(v)) { 
					continue;
				}
				
				// get all edges (v,closest(v))
				WeightedEdge e = (WeightedEdge) g.getEdge(v, closest.get(v));
				if (e != null) {
					potentialEdges.add(new WeightedEdge
							(v,
							 closest.get(v),
							 e.getWeight()));
				}
			}
			
			// get edge with minimal weight
			WeightedEdge e = potentialEdges.getFirst();
			
			for (WeightedEdge other : potentialEdges) {
				if (other.getWeight() < e.getWeight()) {
					e = other;
				}
			}
			
			int v0 = e.getSource();
			
			// 3.
			mst.addEdge(new WeightedEdge
					(e.getSource(),
					 e.getTarget(),
					 e.getWeight()));
			mst.addEdge(new WeightedEdge(
					 e.getTarget(),
					 e.getSource(),
					 e.getWeight()));
			u.add(v0);
			
			// 4.
			if (u.size() == g.getNumberOfVertices()) {
				return mst;
			}
			
			// 5.
			for (int v = 0; v < g.getNumberOfVertices(); v++) {
				if (u.contains(v) || !g.hasEdge(v, v0)) {
					continue;
				}
				
				if (!closest.containsKey(v)) {
					closest.put(v, v0);
				} else {
					WeightedEdge e1 = g.getEdge(v, closest.get(v));
					WeightedEdge e2 = g.getEdge(v, v0);
					
					if (e1.getWeight() > e2.getWeight()) {
						closest.put(v, v0);
					}
				}
			}
		}
	}
}
