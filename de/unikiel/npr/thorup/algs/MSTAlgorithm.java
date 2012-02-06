package de.unikiel.npr.thorup.algs;

import de.unikiel.npr.thorup.ds.graph.WeightedEdge;
import de.unikiel.npr.thorup.ds.graph.WeightedGraph;

/**
 * An algorithm for the computation of minimum spanning trees.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 */
public interface MSTAlgorithm {
	/**
	 * Computes and returns a minimum spanning tree of the passed weighted
	 * graph.
	 * 
	 * @param g
	 * 		the graph to compute a minimum spanning tree of
	 * @return
	 * 		a minimum spanning tree of g
	 * @throws IllegalArgumentException
	 * 		if the passed graph is <code>null</code>
	 */
	public WeightedGraph<? extends WeightedEdge> findSolution
		(WeightedGraph<? extends WeightedEdge> g)
		throws IllegalArgumentException;
}
